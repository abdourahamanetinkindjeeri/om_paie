package com.odc.om.paie.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odc.om.paie.web.mobile.dto.TransactionRequestDTO;
import com.odc.om.paie.entities.Transaction;
import com.odc.om.paie.entities.TransactionStatus;
import com.odc.om.paie.entities.TransactionType;
import com.odc.om.paie.entities.Wallet;
import com.odc.om.paie.exceptions.InsufficientBalanceException;
import com.odc.om.paie.exceptions.InvalidTransactionException;
import com.odc.om.paie.exceptions.WalletNotFoundException;
import com.odc.om.paie.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component("transfer")
@RequiredArgsConstructor
@Slf4j
public class TransferStrategy implements TransactionStrategy {

    private final WalletRepository walletRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Transaction execute(TransactionRequestDTO request) {
        log.info("Executing transfer transaction from wallet: {} to wallet: {}",
                request.getWalletId(), request.getDestinationWalletId());

        if (request.getDestinationWalletId() == null) {
            throw new InvalidTransactionException("Destination wallet is required for transfer");
        }

        // Get source wallet
        Wallet sourceWallet = walletRepository.findById(UUID.fromString(request.getWalletId()))
                .orElseThrow(() -> new WalletNotFoundException("Source wallet not found: " + request.getWalletId()));

        // Get destination wallet
        Wallet destinationWallet = walletRepository.findById(UUID.fromString(request.getDestinationWalletId()))
                .orElseThrow(() -> new WalletNotFoundException("Destination wallet not found: " + request.getDestinationWalletId()));

        // Check sufficient balance
        if (sourceWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for transfer. Available: " + sourceWallet.getBalance());
        }

        // Create transaction for source wallet
        Transaction transaction = Transaction.builder()
                .wallet(sourceWallet)
                .destinationWallet(destinationWallet)
                .type(TransactionType.TRANSFER)
                .amount(request.getAmount())
                .status(TransactionStatus.COMPLETED)
                .reference(generateReference())
                .build();

        // Add metadata
        String meta = null;
        try {
            StringBuilder metaBuilder = new StringBuilder();
            metaBuilder.append("{\"destinationWalletId\":\"").append(request.getDestinationWalletId()).append("\"");
            if (request.getDescription() != null) {
                metaBuilder.append(",\"description\":\"").append(request.getDescription()).append("\"");
            }
            if (request.getMeta() != null) {
                metaBuilder.append(",").append(request.getMeta().substring(1, request.getMeta().length() - 1)); // Remove outer braces and add
            }
            metaBuilder.append("}");
            meta = metaBuilder.toString();
        } catch (Exception e) {
            log.warn("Failed to process meta data: {}", e.getMessage());
            meta = "{\"destinationWalletId\":\"" + request.getDestinationWalletId() + "\"}";
        }
        transaction.setMeta(meta);

        // Update wallet balances
        sourceWallet.setBalance(sourceWallet.getBalance().subtract(request.getAmount()));
        destinationWallet.setBalance(destinationWallet.getBalance().add(request.getAmount()));

        walletRepository.save(sourceWallet);
        walletRepository.save(destinationWallet);

        log.info("Transfer completed. Source balance: {}, Destination balance: {}",
                sourceWallet.getBalance(), destinationWallet.getBalance());
        return transaction;
    }

    private String generateReference() {
        return "TRF-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}