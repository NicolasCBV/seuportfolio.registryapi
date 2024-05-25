package com.seuportfolio.registryapi.modules.projects.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.seuportfolio.registryapi.modules.globals.repositories.PackageRepo;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.CreateProjectDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateProjectUseCaseTests {

	@Mock
	private PackageRepo unusedPackageRepo;

	@Autowired
	@InjectMocks
	private CreateProjectUseCase createProjectUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to create a project")
	void createProjectSuccessCase() throws UseCaseException {
		var user = UserEntity.builder()
			.id(UUID.randomUUID())
			.fullName("John Doe")
			.email("Johndoe@email.com")
			.password("123456789")
			.build();

		var tagList = new ArrayList<String>();
		tagList.add("tag");

		var dto = CreateProjectDTO.builder()
			.name("project")
			.description("description")
			.state("finished")
			.tags(tagList)
			.build();

		assertDoesNotThrow(() -> this.createProjectUseCase.exec(dto, user));
	}
}
