package com.seuportfolio.registryapi.modules.projects.useCases;

import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteProjectUseCaseTests {

	@Mock
	private BaseContentRepo baseContentRepo;

	@Autowired
	@InjectMocks
	private DeleteProjectUseCase deleteProjectUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("It should be able to delete a project")
	void deleteProjectSuccessCase() {
		this.deleteProjectUseCase.exec(
				UUID.randomUUID().toString(),
				UUID.randomUUID().toString()
			);
	}
}
