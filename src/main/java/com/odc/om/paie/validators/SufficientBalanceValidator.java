package com.odc.om.paie.validators;

import com.odc.om.paie.web.mobile.dto.TransactionRequestDTO;
import com.odc.om.paie.repositories.WalletRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SufficientBalanceValidator implements ConstraintValidator<SufficientBalance, TransactionRequestDTO> {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public void initialize(SufficientBalance constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(TransactionRequestDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getAmount() == null) {
            return true; // Let other validators handle null checks
        }

        // Only check for withdrawal and transfer
        if (!"WITHDRAWAL".equalsIgnoreCase(dto.getType()) && !"TRANSFER".equalsIgnoreCase(dto.getType())) {
            return true; // Deposits don't need balance check
        }

        try {
            var wallet = walletRepository.findById(UUID.fromString(dto.getWalletId())).orElse(null);
            if (wallet == null) {
                return false; // Wallet not found
            }

            // Check if balance is sufficient
            return wallet.getBalance().compareTo(dto.getAmount()) >= 0;
        } catch (Exception e) {
            return false;
        }
    }
}