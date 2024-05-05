package com.seuportfolio.registryapi.modules.exceptionHandlers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.seuportfolio.registryapi.utils.RestControllerAdvice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class JwtVerificationExceptionFilter {

	@ExceptionHandler(JWTVerificationException.class)
	public ResponseEntity<Object> exec() {
		return new ResponseEntity<>(
			null,
			new HttpHeaders(),
			HttpStatus.FORBIDDEN
		);
	}
}
