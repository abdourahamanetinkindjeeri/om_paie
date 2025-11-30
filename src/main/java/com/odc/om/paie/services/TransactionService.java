package com.odc.om.paie.services;

import com.odc.om.paie.web.mobile.dto.TransactionRequestDTO;
import com.odc.om.paie.web.mobile.dto.TransactionResponseDTO;
import com.odc.om.paie.entities.*;
import com.odc.om.paie.exceptions.InvalidTransactionException;
import com.odc.om.paie.exceptions.WalletNotFoundException;
import com.odc.om.paie.repositories.TransactionRepository;
import com.odc.om.paie.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final Map<String, TransactionStrategy> transactionStrategies;

    @Transactional
    public TransactionResponseDTO processTransaction(TransactionRequestDTO request) {
        log.info("Processing transaction: {}", request);

        // Validate transaction type
        TransactionType type = TransactionType.valueOf(request.getType().toUpperCase());

        // Get transaction strategy
        TransactionStrategy strategy = transactionStrategies.get(type.name().toLowerCase());
        if (strategy == null) {
            throw new InvalidTransactionException("Unsupported transaction type: " + type);
        }

        // Execute transaction using strategy pattern
        Transaction transaction = strategy.execute(request);

        // Save transaction
        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Transaction processed successfully with id: {}", savedTransaction.getId());
        return mapToResponseDTO(savedTransaction);
    }

    public TransactionResponseDTO getTransactionById(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new InvalidTransactionException("Transaction not found with id: " + transactionId));
        return mapToResponseDTO(transaction);
    }

    public List<TransactionResponseDTO> getTransactionsByWallet(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with id: " + walletId));

        return transactionRepository.findByWalletOrderByCreationDateDesc(wallet).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransactionByReference(String reference) {
        Transaction transaction = transactionRepository.findByReference(reference)
                .orElseThrow(() -> new InvalidTransactionException("Transaction not found with reference: " + reference));
        return mapToResponseDTO(transaction);
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        String description = null;
        if (transaction.getMeta() != null) {
            try {
                // Simple extraction of description from JSON string
                String meta = transaction.getMeta();
                if (meta.contains("\"description\"")) {
                    int start = meta.indexOf("\"description\":\"") + 15;
                    int end = meta.indexOf("\"", start);
                    if (start > 14 && end > start) {
                        description = meta.substring(start, end);
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to extract description from meta: {}", e.getMessage());
            }
        }

        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .walletId(transaction.getWallet().getId().toString())
                .type(transaction.getType().name())
                .amount(transaction.getAmount())
                .status(transaction.getStatus().name())
                .reference(transaction.getReference())
                .destinationWalletId(transaction.getDestinationWallet() != null ?
                        transaction.getDestinationWallet().getId().toString() : null)
                .description(description)
                .meta(transaction.getMeta())
                .createdAt(transaction.getCreationDate())
                .updatedAt(transaction.getLastModifiedDate())
                .build();
    }
}