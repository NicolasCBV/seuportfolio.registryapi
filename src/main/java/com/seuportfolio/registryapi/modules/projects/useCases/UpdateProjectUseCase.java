package com.seuportfolio.registryapi.modules.projects.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.useCases.UpdateTagsUseCase;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.ProjectChangesDTO;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.UpdateProjectDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.project.ProjectStateMapper;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateProjectUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private UpdateTagsUseCase updateTagsUseCase;

	@Transactional
	public void exec(UUID baseContentId, UserEntity user, UpdateProjectDTO dto)
		throws UseCaseException {
		BaseContentEntity baseContentProject =
			this.updateTagsUseCase.exec(
					baseContentId,
					BaseContentCategoryEnum.PROJECT,
					user,
					dto.getInsertTags(),
					dto.getDeleteTags()
				);

		ProjectChangesDTO changes = dto.getProjectChanges();
		if (changes != null) this.updateProject(baseContentProject, changes);

		this.baseContentRepo.save(baseContentProject);
	}

	private void updateProject(
		BaseContentEntity baseContentProject,
		ProjectChangesDTO changes
	) {
		baseContentProject.setName(
			changes.getName() != null
				? changes.getName()
				: baseContentProject.getName()
		);
		baseContentProject.setDescription(
			changes.getDescription() != null
				? changes.getDescription()
				: baseContentProject.getDescription()
		);

		var project = baseContentProject.getProjectEntity();
		project.setState(
			changes.getState() != null
				? ProjectStateMapper.fromStringToShort(changes.getState())
				: project.getState()
		);
	}
}
