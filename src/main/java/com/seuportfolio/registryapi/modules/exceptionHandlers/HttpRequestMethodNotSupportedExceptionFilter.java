package com.seuportfolio.registryapi.modules.exceptionHandlers;

import com.seuportfolio.registryapi.utils.RestControllerAdvice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class HttpRequestMethodNotSupportedExceptionFilter {

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<Object> exec() {
		return new ResponseEntity<>(null, new HttpHeaders(), 405);
	}
}
