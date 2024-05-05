package com.seuportfolio.registryapi.modules.exceptionHandlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.seuportfolio.registryapi.modules.exceptionHandlers.dto.InternalServerErrorDTO;
import com.seuportfolio.registryapi.utils.RestControllerAdvice;

@RestControllerAdvice
public class InternalServerErrorFilter {
	private String stacktrace;

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> exec(Exception e) {
		// if(this.stacktrace != "always")
		// 	return this.defaultInternalServerError();

		var body = InternalServerErrorDTO.builder()
			.message(e.getMessage())
			.stack(e.getStackTrace().toString())
			.build();

		return ResponseEntity.internalServerError().body(body);
	}

	private ResponseEntity<Object> defaultInternalServerError() {
		String msg = "Ooops! Parece que alguma coisa aconteceu nos bastidores!";

		Map<String, String> map = new HashMap<String, String>();
		map.put("message", msg);

		return ResponseEntity.internalServerError().body(msg);
	}
}
