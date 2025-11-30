package com.odc.om.paie.entities;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
@EqualsAndHashCode(callSuper=false)
public class Transaction extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(unique = true)
    private String reference;

    @Column(columnDefinition = "TEXT")
    private String meta;

    // For transfers: the destination wallet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_wallet_id")
    private Wallet destinationWallet;

    // Helper methods
    public boolean isDeposit() {
        return type == TransactionType.DEPOSIT;
    }

    public boolean isWithdrawal() {
        return type == TransactionType.WITHDRAWAL;
    }

    public boolean isTransfer() {
        return type == TransactionType.TRANSFER;
    }

    public boolean isCompleted() {
        return status == TransactionStatus.COMPLETED;
    }

    public boolean isPending() {
        return status == TransactionStatus.PENDING;
    }
}