package com.seuportfolio.registryapi.modules.exceptionHandlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.seuportfolio.registryapi.utils.RestControllerAdvice;

@RestControllerAdvice
public class MethodArgumentNotValidExceptionFilter {
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> exec(
		MethodArgumentNotValidException e
	) {
		List<String> errors = e.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(FieldError::getDefaultMessage)
			.collect(Collectors.toList());

		Map<String, List<String>> body = new HashMap<>();
		body.put("message", errors);

		return ResponseEntity.badRequest().body(body);
	}
}
