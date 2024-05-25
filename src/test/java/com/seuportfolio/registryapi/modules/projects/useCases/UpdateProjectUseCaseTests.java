package com.seuportfolio.registryapi.modules.projects.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.useCases.UpdateTagsUseCase;
import com.seuportfolio.registryapi.modules.projects.modals.ProjectEntity;
import com.seuportfolio.registryapi.modules.projects.modals.ProjectStateEnum;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.ProjectChangesDTO;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.UpdateProjectDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.errors.enums.UseCaseTagEnum;
import com.seuportfolio.registryapi.utils.project.ProjectStateMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class UpdateProjectUseCaseTests {

	@Mock
	private BaseContentRepo baseContentRepo;

	@Mock
	private UpdateTagsUseCase updateTagsUseCase;

	@Autowired
	@InjectMocks
	private UpdateProjectUseCase updateProjectUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to update a project")
	@SuppressWarnings("unchecked")
	void updateProjectSuccessCase() throws UseCaseException {
		Map<String, Object> map = this.buildPayload();
		var user = (UserEntity) map.get("user");
		var baseContentProject = (BaseContentEntity) map.get(
			"baseContentProject"
		);
		var insertTags = (ArrayList<String>) map.get("insertTags");
		var deleteTags = (ArrayList<String>) map.get("deleteTags");

		String state = ProjectStateMapper.fromShortToString(
			ProjectStateEnum.IN_PROGRESS.getValue()
		);
		var projectChanges = ProjectChangesDTO.builder()
			.name("new project name")
			.description("new project description")
			.state(state)
			.build();
		var dto = UpdateProjectDTO.builder()
			.projectChanges(projectChanges)
			.deleteTags(deleteTags)
			.insertTags(insertTags)
			.build();

		when(
			this.updateTagsUseCase.exec(
					baseContentProject.getId(),
					BaseContentCategoryEnum.PROJECT,
					user,
					insertTags,
					deleteTags
				)
		).thenReturn(baseContentProject);

		assertDoesNotThrow(
			() ->
				this.updateProjectUseCase.exec(
						baseContentProject.getId(),
						user,
						dto
					)
		);
	}

	@Test
	@DisplayName("it should throw a UseCaseException")
	@SuppressWarnings("unchecked")
	void useCaseExceptionCase() throws UseCaseException {
		Map<String, Object> map = this.buildPayload();
		var user = (UserEntity) map.get("user");
		var baseContentProject = (BaseContentEntity) map.get(
			"baseContentProject"
		);
		var insertTags = (ArrayList<String>) map.get("insertTags");
		var deleteTags = (ArrayList<String>) map.get("deleteTags");

		String state = ProjectStateMapper.fromShortToString(
			ProjectStateEnum.IN_PROGRESS.getValue()
		);
		var projectChanges = ProjectChangesDTO.builder()
			.name("new project name")
			.description("new project description")
			.state(state)
			.build();
		var dto = UpdateProjectDTO.builder()
			.projectChanges(projectChanges)
			.deleteTags(deleteTags)
			.insertTags(insertTags)
			.build();

		var category = BaseContentCategoryEnum.PROJECT;
		when(
			this.updateTagsUseCase.exec(
					baseContentProject.getId(),
					category,
					user,
					insertTags,
					deleteTags
				)
		).thenThrow(
			new UseCaseException(
				"Base content not found. Category: " + category.getValue(),
				UseCaseTagEnum.CONTENT_NOT_FOUND
			)
		);

		assertThrows(
			UseCaseException.class,
			() ->
				this.updateProjectUseCase.exec(
						baseContentProject.getId(),
						user,
						dto
					)
		);
	}

	private Map<String, Object> buildPayload() {
		var user = UserEntity.builder()
			.id(UUID.randomUUID())
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		var project = ProjectEntity.builder()
			.id(UUID.randomUUID())
			.state(ProjectStateEnum.FINISHED.getValue())
			.build();
		var baseContentProject = BaseContentEntity.builder()
			.id(UUID.randomUUID())
			.name("project")
			.description("description")
			.category(BaseContentCategoryEnum.PROJECT.getValue())
			.userEntity(user)
			.projectEntity(project)
			.build();
		var pack = PackageEntity.builder()
			.id(UUID.randomUUID())
			.root(baseContentProject)
			.build();
		baseContentProject.setOwnerOf(pack);

		var insertTags = new ArrayList<String>(1);
		insertTags.add("new tag");

		var deleteTags = new ArrayList<String>(1);
		deleteTags.add("old tags");

		var map = new HashMap<String, Object>();
		map.put("user", user);
		map.put("project", project);
		map.put("baseContentProject", baseContentProject);
		map.put("pack", pack);
		map.put("insertTags", insertTags);
		map.put("deleteTags", deleteTags);

		return map;
	}
}
