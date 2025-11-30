package com.odc.om.paie.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class PositiveAmountValidator implements ConstraintValidator<PositiveAmount, BigDecimal> {

    @Override
    public void initialize(PositiveAmount constraintAnnotation) {
        // Initialization if needed
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }
}