package com.seuportfolio.registryapi.modules.exceptionHandlers;

import com.seuportfolio.registryapi.modules.exceptionHandlers.dto.InternalServerErrorDTO;
import com.seuportfolio.registryapi.utils.RestControllerAdvice;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class InternalServerErrorFilter {

	@Value("${server.error.include-stacktrace}")
	private String stacktrace;

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> exec(Exception e) {
		if (
			!this.stacktrace.equals("always")
		) return this.defaultInternalServerError();

		var body = InternalServerErrorDTO.builder()
			.message(e.getMessage())
			.stack(Arrays.toString(e.getStackTrace()))
			.build();

		return ResponseEntity.internalServerError().body(body);
	}

	private ResponseEntity<Object> defaultInternalServerError() {
		String msg = "Ooops! Parece que alguma coisa aconteceu nos bastidores!";

		Map<String, String> map = new HashMap<>();
		map.put("message", msg);

		return ResponseEntity.internalServerError().body(msg);
	}
}
