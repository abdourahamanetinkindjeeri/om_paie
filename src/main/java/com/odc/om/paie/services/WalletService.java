package com.odc.om.paie.services;

import com.odc.om.paie.authenticated.user.User;
import com.odc.om.paie.authenticated.user.UserRepository;
import com.odc.om.paie.web.mobile.dto.WalletRequestDTO;
import com.odc.om.paie.web.mobile.dto.WalletResponseDTO;
import com.odc.om.paie.entities.Merchant;
import com.odc.om.paie.entities.Wallet;
import com.odc.om.paie.exceptions.DuplicateMainWalletException;
import com.odc.om.paie.exceptions.MerchantNotFoundException;
import com.odc.om.paie.exceptions.UserNotFoundException;
import com.odc.om.paie.exceptions.WalletNotFoundException;
import com.odc.om.paie.repositories.MerchantRepository;
import com.odc.om.paie.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;

    public WalletResponseDTO createWallet(WalletRequestDTO request) {
        log.info("Creating wallet for owner type: {}, owner id: {}", request.getOwnerType(), request.getOwnerId());

        Wallet wallet = buildWalletFromRequest(request);
        validateUniqueMainWallet(wallet);

        Wallet savedWallet = walletRepository.save(wallet);
        log.info("Wallet created with id: {}", savedWallet.getId());

        return mapToResponseDTO(savedWallet);
    }

    public WalletResponseDTO getWalletById(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with id: " + walletId));
        return mapToResponseDTO(wallet);
    }

    public List<WalletResponseDTO> getWalletsByUser(User user) {
        return walletRepository.findByUser(user).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<WalletResponseDTO> getWalletsByMerchant(Merchant merchant) {
        return walletRepository.findByMerchant(merchant).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public WalletResponseDTO getMainWalletByUser(User user) {
        Wallet wallet = walletRepository.findMainWalletByUser(user)
                .orElseThrow(() -> new WalletNotFoundException("No main wallet found for user: " + user.getId()));
        return mapToResponseDTO(wallet);
    }

    public WalletResponseDTO getMainWalletByMerchant(Merchant merchant) {
        Wallet wallet = walletRepository.findMainWalletByMerchant(merchant)
                .orElseThrow(() -> new WalletNotFoundException("No main wallet found for merchant: " + merchant.getId()));
        return mapToResponseDTO(wallet);
    }

    public List<WalletResponseDTO> getSecondaryWalletsByUser(User user) {
        return walletRepository.findSecondaryWalletsByUser(user).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<WalletResponseDTO> getSecondaryWalletsByMerchant(Merchant merchant) {
        return walletRepository.findSecondaryWalletsByMerchant(merchant).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public Wallet getWalletEntityById(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with id: " + walletId));
    }

    private Wallet buildWalletFromRequest(WalletRequestDTO request) {
        Wallet.WalletBuilder builder = Wallet.builder()
                .currency(request.getCurrency())
                .isMain(request.getIsMain() != null ? request.getIsMain() : false);

        if ("USER".equalsIgnoreCase(request.getOwnerType())) {
            User user = userRepository.findById(UUID.fromString(request.getOwnerId()))
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.getOwnerId()));
            builder.user(user);
        } else if ("MERCHANT".equalsIgnoreCase(request.getOwnerType())) {
            Merchant merchant = merchantRepository.findById(UUID.fromString(request.getOwnerId()))
                    .orElseThrow(() -> new MerchantNotFoundException("Merchant not found with id: " + request.getOwnerId()));
            builder.merchant(merchant);
        } else {
            throw new IllegalArgumentException("Invalid owner type: " + request.getOwnerType());
        }

        return builder.build();
    }

    private void validateUniqueMainWallet(Wallet wallet) {
        if (Boolean.TRUE.equals(wallet.getIsMain())) {
            if (wallet.belongsToUser()) {
                boolean exists = walletRepository.existsByUserAndIsMain(wallet.getUser(), true);
                if (exists) {
                    throw new DuplicateMainWalletException("User already has a main wallet");
                }
            } else if (wallet.belongsToMerchant()) {
                boolean exists = walletRepository.existsByMerchantAndIsMain(wallet.getMerchant(), true);
                if (exists) {
                    throw new DuplicateMainWalletException("Merchant already has a main wallet");
                }
            }
        }
    }

    private WalletResponseDTO mapToResponseDTO(Wallet wallet) {
        String ownerType = wallet.belongsToUser() ? "USER" : "MERCHANT";
        String ownerId = wallet.getOwnerId();
        String ownerName = wallet.belongsToUser()
                ? wallet.getUser().getFirstname() + " " + wallet.getUser().getLastname()
                : wallet.getMerchant().getNom();

        return WalletResponseDTO.builder()
                .id(wallet.getId())
                .ownerType(ownerType)
                .ownerId(ownerId)
                .ownerName(ownerName)
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .isMain(wallet.getIsMain())
                .createdAt(wallet.getCreationDate())
                .updatedAt(wallet.getLastModifiedDate())
                .build();
    }
}