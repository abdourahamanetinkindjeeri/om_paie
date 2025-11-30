package com.odc.om.paie.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class ValidTransactionTypeValidator implements ConstraintValidator<ValidTransactionType, String> {

    private static final String[] VALID_TYPES = {"DEPOSIT", "WITHDRAWAL", "TRANSFER"};

    @Override
    public void initialize(ValidTransactionType constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return Arrays.asList(VALID_TYPES).contains(value.toUpperCase());
    }
}