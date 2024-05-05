package com.seuportfolio.registryapi.modules.exceptionHandlers;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.seuportfolio.registryapi.utils.RestControllerAdvice;

@RestControllerAdvice
public class HttpMessageNotReadableExceptionFilter {
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleMessageNotReadable() {
		return ResponseEntity.badRequest().build();
	}
}
