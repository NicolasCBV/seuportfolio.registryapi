package com.seuportfolio.registryapi.tests.commonFactories;

import static org.assertj.core.api.Assertions.assertThat;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEnum;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.projects.modals.ProjectEntity;
import com.seuportfolio.registryapi.modules.projects.modals.ProjectStateEnum;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

public class CreateProjectFactory {

	@Transactional
	public static BaseContentEntity create(
		EntityManager entityManager,
		BaseContentRepo baseContentRepo,
		UserEntity user
	) {
		var pack = PackageEntity.builder()
			.type(PackageEnum.PROJECT.getValue())
			.build();

		var project = ProjectEntity.builder()
			.imageUrl("http://localhost:3333")
			.state(ProjectStateEnum.FINISHED.getValue())
			.build();
		var baseContentProject = BaseContentEntity.builder()
			.name("project name")
			.description("description")
			.userEntity(user)
			.category(BaseContentCategoryEnum.PROJECT.getValue())
			.ownerOf(pack)
			.projectEntity(project)
			.build();

		project.setBaseContentEntity(baseContentProject);
		pack.setRoot(baseContentProject);

		var tag = TagEntity.builder()
			.name("old tag")
			.baseContentEntity(baseContentProject)
			.build();
		var tagList = new ArrayList<TagEntity>(2);
		tagList.add(tag);
		baseContentProject.setTagEntity(tagList);

		entityManager.persist(baseContentProject);

		Optional<BaseContentEntity> optSearchedProject =
			baseContentRepo.findByName(baseContentProject.getName());
		assertThat(optSearchedProject.isEmpty()).isFalse();

		return optSearchedProject.get();
	}
}
