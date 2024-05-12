package com.seuportfolio.registryapi.modules.organizations.presentation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationChangesDTO {

	@Size(
		min = 2,
		message = "O nome da organização precisa ter no mínimo 2 caracteres"
	)
	@Size(
		max = 64,
		message = "O nome da organização precisa ter menos que 65 caracteres"
	)
	private String name;

	@Size(
		min = 8,
		message = "O link do site da organização precisa ter pelo menos 8 caracteres"
	)
	@Size(
		min = 8,
		message = "O link do site da organização precisa ter no máximo 255 caracteres"
	)
	private String siteUrl;

	@Size(
		min = 10,
		message = "A descrição da organização precisa conter no mínimo 10 caracteres"
	)
	@Size(
		max = 120,
		message = "A descrição da organização precisa ser menor que 121 caracteres"
	)
	private String description;
}
