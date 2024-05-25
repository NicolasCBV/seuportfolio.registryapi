package com.seuportfolio.registryapi.modules.projects.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetProjectResponseDTO {

	@JsonProperty("projects")
	private List<ProjectDTO> projectDTO;
}
