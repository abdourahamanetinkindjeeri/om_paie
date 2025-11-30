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
public class WalletResponseDTO {

    private UUID id;
    private String ownerType;
    private String ownerId;
    private String ownerName;
    private BigDecimal balance;
    private String currency;
    private Boolean isMain;
    private Instant createdAt;
    private Instant updatedAt;
}