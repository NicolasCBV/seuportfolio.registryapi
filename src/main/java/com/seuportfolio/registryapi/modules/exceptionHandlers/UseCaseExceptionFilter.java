package com.seuportfolio.registryapi.modules.exceptionHandlers;

import com.seuportfolio.registryapi.utils.RestControllerAdvice;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.errors.enums.UseCaseTagEnum;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class UseCaseExceptionFilter {

	record TagMap(UseCaseTagEnum tag, HttpStatus statusCode) {}

	private TagMap[] tagMapArr = {
		new TagMap(UseCaseTagEnum.CONTENT_NOT_FOUND, HttpStatus.NOT_FOUND),
	};

	@ExceptionHandler(UseCaseException.class)
	public ResponseEntity<Object> exec(UseCaseException e) {
		for (TagMap tagMap : this.tagMapArr) {
			if (e.getTag() == tagMap.tag()) return new ResponseEntity<>(
				null,
				new HttpHeaders(),
				tagMap.statusCode()
			);
		}

		return ResponseEntity.internalServerError().build();
	}
}
