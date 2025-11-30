package com.odc.om.paie.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.odc.om.paie.web.mobile.dto.TransactionRequestDTO;
import com.odc.om.paie.entities.Transaction;
import com.odc.om.paie.entities.TransactionStatus;
import com.odc.om.paie.entities.TransactionType;
import com.odc.om.paie.entities.Wallet;
import com.odc.om.paie.exceptions.InsufficientBalanceException;
import com.odc.om.paie.exceptions.WalletNotFoundException;
import com.odc.om.paie.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component("withdrawal")
@RequiredArgsConstructor
@Slf4j
public class WithdrawalStrategy implements TransactionStrategy {

    private final WalletRepository walletRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Transaction execute(TransactionRequestDTO request) {
        log.info("Executing withdrawal transaction for wallet: {}", request.getWalletId());

        // Get wallet
        Wallet wallet = walletRepository.findById(UUID.fromString(request.getWalletId()))
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + request.getWalletId()));

        // Check sufficient balance
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal. Available: " + wallet.getBalance());
        }

        // Create transaction
        Transaction transaction = Transaction.builder()
                .wallet(wallet)
                .type(TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .status(TransactionStatus.COMPLETED)
                .reference(generateReference())
                .build();

        // Add metadata
        String meta = null;
        if (request.getDescription() != null || request.getMeta() != null) {
            try {
                if (request.getMeta() != null) {
                    meta = request.getMeta();
                } else if (request.getDescription() != null) {
                    meta = "{\"description\":\"" + request.getDescription() + "\"}";
                }
            } catch (Exception e) {
                log.warn("Failed to process meta data: {}", e.getMessage());
            }
        }
        transaction.setMeta(meta);

        // Update wallet balance
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        log.info("Withdrawal completed. New balance: {}", wallet.getBalance());
        return transaction;
    }

    private String generateReference() {
        return "WTH-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}