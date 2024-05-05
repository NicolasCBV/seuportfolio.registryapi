package com.seuportfolio.registryapi.modules.exceptionHandlers;

import com.seuportfolio.registryapi.utils.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class DataIntegrityViolationExceptionFilter {

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> exec(DataIntegrityViolationException e) {
		if (
			!e.getMessage().contains("duplicate key value") &&
			!e.getMessage().contains("Unique index or primary key violation")
		) {
			Map<String, String> body = new HashMap<>();
			body.put(
				"message",
				"Ooops! Parece que alguma coisa aconteceu nos bastidores!"
			);
			return ResponseEntity.internalServerError().body(body);
		}

		Map<String, String> body = new HashMap<>();
		body.put("message", "Entidade já existe");

		return new ResponseEntity<>(body, null, HttpStatus.CONFLICT);
	}
}
