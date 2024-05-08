package com.seuportfolio.registryapi.modules.exceptionHandlers;

import com.seuportfolio.registryapi.utils.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class HandlerMethodValidationExceptionFilter {

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<Object> exec() {
		return ResponseEntity.badRequest().build();
	}
}
