package com.seuportfolio.registryapi.modules.projects.presentation.dto;

import com.seuportfolio.registryapi.modules.projects.presentation.dto.customValidators.ProjectMinimalFields;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.customValidators.StateConstraint;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectChangesDTO implements ProjectMinimalFields {

	@Size(
		min = 2,
		message = "O nome da projeto precisa ter no mínimo 2 caracteres"
	)
	@Size(
		max = 64,
		message = "O nome da projeto precisa ter menos que 65 caracteres"
	)
	private String name;

	@Size(
		min = 10,
		message = "A descrição da projeto precisa conter no mínimo 10 caracteres"
	)
	@Size(
		max = 120,
		message = "A descrição da projeto precisa ser menor que 121 caracteres"
	)
	private String description;

	@StateConstraint
	private String state;
}
