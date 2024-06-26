package com.seuportfolio.registryapi.modules.user.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

	@JsonProperty("email")
	@NotNull(message = "O campo \"email\" não pode estar vázio")
	@Email(message = "Ooops! Parece que este email é inválido")
	@Size(
		min = 3,
		message = "O campo \"email\" deve ter no mínimo 3 caracteres"
	)
	@Size(
		max = 320,
		message = "O campo \"email\" deve ter no máximo 320 caracteres"
	)
	private String email;

	@JsonProperty("password")
	@NotNull(message = "O campo \"password\" não pode estar vázio")
	@Size(
		min = 6,
		message = "O campo \"password\" deve ter no mínimo 6 caracteres"
	)
	@Size(
		max = 255,
		message = "O campo \"password\" deve ter no máximo 255 caracteres"
	)
	private String password;
}
