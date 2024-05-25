package com.seuportfolio.registryapi.modules.projects.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.projects.modals.ProjectEntity;
import com.seuportfolio.registryapi.modules.projects.modals.ProjectStateEnum;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class GetProjectsUseCaseTests {

	@Mock
	private BaseContentRepo baseContentRepo;

	@Autowired
	@InjectMocks
	private GetProjectsUseCase getProjectsUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to get projects list")
	void getProjectsSuccessCase() {
		var user = UserEntity.builder()
			.id(UUID.randomUUID())
			.fullName("John Doe")
			.email("johndoe@email.com")
			.build();

		short category = BaseContentCategoryEnum.PROJECT.getValue();
		short state = ProjectStateEnum.FINISHED.getValue();
		var baseContentProject = BaseContentEntity.builder()
			.id(UUID.randomUUID())
			.name("project")
			.description("description")
			.category(category)
			.build();
		var project = ProjectEntity.builder()
			.id(UUID.randomUUID())
			.imageUrl("http://localhost:3333")
			.state(state)
			.baseContentEntity(baseContentProject)
			.build();
		baseContentProject.setProjectEntity(project);

		var projectList = new ArrayList<BaseContentEntity>();
		projectList.add(baseContentProject);

		int offset = 10;
		when(
			this.baseContentRepo.getBaseContentCollection(
					user.getId(),
					10,
					offset,
					category
				)
		).thenReturn(projectList);

		assertDoesNotThrow(() -> this.getProjectsUseCase.exec(offset, user));
	}
}
