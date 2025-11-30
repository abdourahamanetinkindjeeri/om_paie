package com.odc.om.paie.web.mobile.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {

    private UUID id;
    private String walletId;
    private String type;
    private BigDecimal amount;
    private String status;
    private String reference;
    private String destinationWalletId;
    private String description;
    private String meta;
    private Instant createdAt;
    private Instant updatedAt;
}