package com.seuportfolio.registryapi.modules.certifications.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UpdateCertificationDTO {

	@JsonProperty("changes")
	private CertificationChangesDTO certificationChangesDTO;

	@JsonProperty("tags_to_delete")
	private List<
		@Size(
			min = 2,
			max = 60,
			message = "O nome da tag deve ter entre 2 e 60 caracteres"
		) String
	> deleteTags;

	@JsonProperty("tags_to_add")
	private List<
		@Size(
			min = 2,
			max = 60,
			message = "O nome da tag deve ter entre 2 e 60 caracteres"
		) String
	> insertTags;
}
