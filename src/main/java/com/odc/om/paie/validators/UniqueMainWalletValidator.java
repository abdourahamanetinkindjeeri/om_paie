package com.odc.om.paie.validators;

import com.odc.om.paie.web.mobile.dto.WalletRequestDTO;
import com.odc.om.paie.repositories.WalletRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueMainWalletValidator implements ConstraintValidator<UniqueMainWallet, WalletRequestDTO> {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public void initialize(UniqueMainWallet constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(WalletRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null || !Boolean.TRUE.equals(dto.getIsMain())) {
            return true; // Not a main wallet, so no constraint
        }

        try {
            // This is a simplified check - in a real implementation,
            // you would need to resolve the owner entity and check
            // if a main wallet already exists for that owner
            return true; // For now, allow - will be handled in service layer
        } catch (Exception e) {
            return false;
        }
    }
}