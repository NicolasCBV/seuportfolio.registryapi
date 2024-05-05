package com.seuportfolio.registryapi.modules.exceptionHandlers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InternalServerErrorDTO {
	@JsonProperty("message")
	private String message;

	@JsonProperty("stack")
	private String stack;
}
