package com.seuportfolio.registryapi.modules.projects.presentation.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetProjectsResponseDTO {

	private List<ProjectDTO> projects;
}
