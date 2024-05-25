package com.seuportfolio.registryapi.modules.projects.presentation.dto;

import com.seuportfolio.registryapi.modules.projects.presentation.dto.customValidators.ProjectMinimalFields;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.customValidators.StateConstraint;
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
public class CreateProjectDTO implements ProjectMinimalFields {

	@NotNull(message = "O nome da organização não pode estar vázio")
	@Size(
		min = 2,
		message = "O nome do projeto precisa ter no mínimo 2 caracteres"
	)
	@Size(
		max = 64,
		message = "O nome do projeto precisa ter menos que 65 caracteres"
	)
	private String name;

	@Size(
		min = 10,
		message = "A descrição do projeto precisa conter no mínimo 10 caracteres"
	)
	@Size(
		max = 120,
		message = "A descrição do projeto precisa ser menor que 121 caracteres"
	)
	@NotNull(message = "A descrição do projeto não pode estar vázia")
	private String description;

	@NotNull(message = "O estado do projeto não pode ser nulo")
	@StateConstraint
	private String state;

	@NotNull(message = "O projeto deve ter pelo menos 1 tag")
	private List<
		@Size(
			min = 2,
			max = 60,
			message = "As tags devem ter no mínimo 2 e no máximo 60 caracteres"
		) String
	> tags;
}
