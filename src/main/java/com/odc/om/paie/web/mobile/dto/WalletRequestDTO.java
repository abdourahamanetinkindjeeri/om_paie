package com.odc.om.paie.web.mobile.dto;

import com.odc.om.paie.validators.UniqueMainWallet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@UniqueMainWallet
public class WalletRequestDTO {

    @NotBlank(message = "Le type de propriétaire est requis")
    private String ownerType; // "USER" or "MERCHANT"

    @NotNull(message = "L'ID du propriétaire est requis")
    private String ownerId;

    @NotBlank(message = "La devise est requise")
    private String currency;

    private Boolean isMain;
}