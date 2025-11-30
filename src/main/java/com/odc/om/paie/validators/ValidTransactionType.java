package com.odc.om.paie.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidTransactionTypeValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTransactionType {
    String message() default "Le type de transaction doit être DEPOSIT, WITHDRAWAL ou TRANSFER";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}