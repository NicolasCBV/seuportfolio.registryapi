package com.seuportfolio.registryapi.modules.organizations.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrganizationDTO {

	@NotNull(message = "O nome da organização não pode estar vázio")
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
		min = 10,
		message = "A descrição da organização precisa conter no mínimo 10 caracteres"
	)
	@Size(
		max = 120,
		message = "A descrição da organização precisa ser menor que 121 caracteres"
	)
	@NotNull(message = "A descrição da organização não pode estar vázia")
	private String description;

	@NotNull(message = "A organização deve ter pelo menos 1 tag")
	private List<
		@Size(
			min = 2,
			max = 60,
			message = "As tags devem ter no mínimo 2 e no máximo 60 caracteres"
		) String
	> tags;
}
