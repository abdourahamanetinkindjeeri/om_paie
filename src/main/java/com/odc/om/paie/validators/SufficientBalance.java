package com.odc.om.paie.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SufficientBalanceValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SufficientBalance {
    String message() default "Solde insuffisant pour cette transaction";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}