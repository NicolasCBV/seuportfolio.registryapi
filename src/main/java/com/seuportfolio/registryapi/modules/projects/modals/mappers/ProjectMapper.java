package com.seuportfolio.registryapi.modules.projects.modals.mappers;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.ProjectDTO;
import com.seuportfolio.registryapi.utils.project.ProjectStateMapper;

public class ProjectMapper {

	public static ProjectDTO prettify(BaseContentEntity input) {
		var project = input.getProjectEntity();
		String state = ProjectStateMapper.fromShortToString(project.getState());
		return new ProjectDTO(
			input.getId().toString(),
			input.getName(),
			input.getDescription(),
			project.getImageUrl(),
			input.getCreatedAt(),
			input.getUpdatedAt(),
			state,
			input.getTagEntity()
		);
	}
}
