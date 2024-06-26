package com.seuportfolio.registryapi.modules.exceptionHandlers;

import com.seuportfolio.registryapi.utils.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class HttpMessageNotReadableExceptionFilter {

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> exec() {
		return ResponseEntity.badRequest().build();
	}
}
