package com.seuportfolio.registryapi.modules.projects.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEnum;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.PackageRepo;
import com.seuportfolio.registryapi.modules.projects.modals.ProjectEntity;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.CreateProjectDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.project.ProjectStateMapper;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateProjectUseCase {

	@Autowired
	private PackageRepo packageRepo;

	@Transactional
	public void exec(CreateProjectDTO dto, UserEntity user) {
		var baseContentProject = BaseContentEntity.builder()
			.name(dto.getName())
			.description(dto.getDescription())
			.category(BaseContentCategoryEnum.PROJECT.getValue())
			.userEntity(user)
			.build();
		var pack = PackageEntity.builder()
			.root(baseContentProject)
			.type(PackageEnum.PROJECT.getValue())
			.build();
		baseContentProject.setOwnerOf(pack);

		var tagList = new ArrayList<TagEntity>();
		for (String tag : dto.getTags()) {
			var tagEntity = TagEntity.builder()
				.name(tag)
				.baseContentEntity(baseContentProject)
				.build();
			tagList.add(tagEntity);
		}
		baseContentProject.setTagEntity(tagList);

		short state = ProjectStateMapper.fromStringToShort(dto.getState());
		var project = ProjectEntity.builder()
			.state(state)
			.baseContentEntity(baseContentProject)
			.build();
		baseContentProject.setProjectEntity(project);

		this.packageRepo.save(pack);
	}
}
