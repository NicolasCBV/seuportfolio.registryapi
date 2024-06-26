package com.seuportfolio.registryapi.modules.projects.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.projects.modals.mappers.ProjectMapper;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.ProjectDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetProjectsUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	public List<ProjectDTO> exec(int offset, UserEntity user) {
		List<BaseContentEntity> baseContentProjectList =
			this.baseContentRepo.getBaseContentCollection(
					user.getId(),
					10,
					offset,
					BaseContentCategoryEnum.PROJECT.getValue()
				);

		List<ProjectDTO> data = baseContentProjectList
			.stream()
			.map(ProjectMapper::prettify)
			.collect(Collectors.toList());

		return data;
	}
}
