package com.seuportfolio.registryapi.modules.projects.presentation.dto.customValidators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StateValidator
	implements ConstraintValidator<StateConstraint, String> {

	@Override
	public boolean isValid(String val, ConstraintValidatorContext ctx) {
		if (!"finished".equals(val) && !"in_progress".equals(val)) return false;
		return true;
	}

	@Override
	public void initialize(StateConstraint constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
	}
}
