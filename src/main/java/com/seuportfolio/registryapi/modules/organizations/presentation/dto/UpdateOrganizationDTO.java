package com.seuportfolio.registryapi.modules.organizations.presentation.dto;

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
public class UpdateOrganizationDTO {

	private OrganizationChangesDTO organizationChanges;

	private List<
		@Size(
			min = 2,
			max = 60,
			message = "O nome da tag deve ter entre 2 e 60 caracteres"
		) String
	> deleteTags;

	private List<
		@Size(
			min = 2,
			max = 60,
			message = "O nome da tag deve ter entre 2 e 60 caracteres"
		) String
	> insertTags;
}
