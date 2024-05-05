package com.seuportfolio.registryapi.modules.user.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {

	@JsonProperty("fullName")
	@Size(
		min = 2,
		message = "O campo \"nome completo\" deve ter no mínimo 2 caracteres ou mais"
	)
	@Size(
		max = 100,
		message = "O campo \"nome completo\" deve ter no máximo 100 caracteres"
	)
	private String fullName;

	@JsonProperty("description")
	@Size(
		min = 1,
		message = "O campo \"descrição\" deve ter no mínimo 1 caracter"
	)
	@Size(
		max = 100,
		message = "O campo \"descrição\" deve ter no máximo 120 caracteres"
	)
	private String description;
}
