package com.seuportfolio.registryapi.modules.projects.presentation.dto.customValidators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StateValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface StateConstraint {
	String message() default "O estado precisa estar entre os seguintes valores: \"finished\" ou \"in_progress\"";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
