package com.seuportfolio.registryapi.modules.projects.presentation.dto;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetProjectResponseDTO {

	private Optional<ProjectDTO> project;
}
