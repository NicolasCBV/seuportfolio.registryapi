package com.seuportfolio.registryapi.modules.organizations.presentation.dto;

import com.seuportfolio.registryapi.utils.jakarta.validation.UUID;
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
public class UpdateOrganizationDTO {

	@NotNull(
		message = "O número de identificação da organização não pode ser nulo"
	)
	@UUID(
		message = "O número de identificação da organização deve ser um UUID de versão 4"
	)
	private String organizationId;

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
