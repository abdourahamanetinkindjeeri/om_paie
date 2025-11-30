package com.odc.om.paie.web.mobile.dto;

import com.odc.om.paie.validators.PositiveAmount;
import com.odc.om.paie.validators.SufficientBalance;
import com.odc.om.paie.validators.ValidTransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SufficientBalance
public class TransactionRequestDTO {

    @NotBlank(message = "L'ID du wallet est requis")
    private String walletId;

    @NotNull(message = "Le type de transaction est requis")
    @ValidTransactionType
    private String type; // "DEPOSIT", "WITHDRAWAL", "TRANSFER"

    @NotNull(message = "Le montant est requis")
    @PositiveAmount
    private BigDecimal amount;

    private String destinationWalletId; // Pour les transferts

    private String description;

    private String meta; // Données JSON supplémentaires (sous forme de String)
}