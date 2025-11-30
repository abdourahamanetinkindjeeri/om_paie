package com.odc.om.paie.services;

import com.odc.om.paie.authenticated.user.User;
import com.odc.om.paie.authenticated.user.UserRepository;
import com.odc.om.paie.web.mobile.dto.WalletRequestDTO;
import com.odc.om.paie.web.mobile.dto.WalletResponseDTO;
import com.odc.om.paie.entities.Merchant;
import com.odc.om.paie.entities.Wallet;
import com.odc.om.paie.exceptions.DuplicateMainWalletException;
import com.odc.om.paie.repositories.MerchantRepository;
import com.odc.om.paie.repositories.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private WalletService walletService;

    private User testUser;
    private Merchant testMerchant;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstname("John")
                .lastname("Doe")
                .build();
        // Set id manually since AbstractEntity doesn't expose it in builder
        testUser.setId(UUID.randomUUID());

        testMerchant = Merchant.builder()
                .nom("Test Merchant")
                .code("TEST001")
                .build();
        testMerchant.setId(UUID.randomUUID());

        testWallet = Wallet.builder()
                .user(testUser)
                .balance(java.math.BigDecimal.valueOf(1000))
                .currency("XOF")
                .isMain(true)
                .build();
        testWallet.setId(UUID.randomUUID());
    }

    @Test
    void createWallet_UserWallet_ShouldCreateSuccessfully() {
        // Given
        WalletRequestDTO request = WalletRequestDTO.builder()
                .ownerType("USER")
                .ownerId(testUser.getId().toString())
                .currency("XOF")
                .isMain(true)
                .build();

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(walletRepository.existsByUserAndIsMain(testUser, true)).thenReturn(false);
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);

        // When
        WalletResponseDTO response = walletService.createWallet(request);

        // Then
        assertNotNull(response);
        assertEquals(testWallet.getId(), response.getId());
        assertEquals("USER", response.getOwnerType());
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    void createWallet_DuplicateMainWallet_ShouldThrowException() {
        // Given
        WalletRequestDTO request = WalletRequestDTO.builder()
                .ownerType("USER")
                .ownerId(testUser.getId().toString())
                .currency("XOF")
                .isMain(true)
                .build();

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(walletRepository.existsByUserAndIsMain(testUser, true)).thenReturn(true);

        // When & Then
        assertThrows(DuplicateMainWalletException.class, () -> walletService.createWallet(request));
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void getWalletById_ExistingWallet_ShouldReturnWallet() {
        // Given
        when(walletRepository.findById(testWallet.getId())).thenReturn(Optional.of(testWallet));

        // When
        WalletResponseDTO response = walletService.getWalletById(testWallet.getId());

        // Then
        assertNotNull(response);
        assertEquals(testWallet.getId(), response.getId());
    }
}