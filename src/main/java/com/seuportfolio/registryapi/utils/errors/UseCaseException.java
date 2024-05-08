package com.seuportfolio.registryapi.utils.errors;

import com.seuportfolio.registryapi.utils.errors.enums.UseCaseTagEnum;
import lombok.Getter;

@Getter
public class UseCaseException extends Exception {

	final UseCaseTagEnum tag;

	public UseCaseException(String messageErr, UseCaseTagEnum tag) {
		super(messageErr);
		this.tag = tag;
	}
}
