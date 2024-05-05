package com.seuportfolio.registryapi.modules.exceptionHandlers;

import com.seuportfolio.registryapi.utils.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class NotFoundExceptionFilter {

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<Object> exec() {
		Map<String, String> body = new HashMap<>();
		body.put("message", "Conteúdo não encontrado");
		return new ResponseEntity<>(
			body,
			new HttpHeaders(),
			HttpStatus.NOT_FOUND
		);
	}
}
