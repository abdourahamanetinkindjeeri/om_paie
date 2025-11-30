package com.odc.om.paie.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueMainWalletValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueMainWallet {
    String message() default "Un utilisateur ou marchand ne peut avoir qu'un seul wallet principal";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}