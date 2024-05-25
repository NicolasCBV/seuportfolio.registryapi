package com.seuportfolio.registryapi.modules.projects.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.projects.modals.mappers.ProjectMapper;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.ProjectDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetProjectUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	public Optional<ProjectDTO> exec(String baseContentId, UserEntity user) {
		Optional<BaseContentEntity> optBaseContentProject =
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					UUID.fromString(baseContentId),
					BaseContentCategoryEnum.PROJECT.getValue()
				);
		if (optBaseContentProject.isEmpty()) return Optional.empty();
		return Optional.of(ProjectMapper.prettify(optBaseContentProject.get()));
	}
}
