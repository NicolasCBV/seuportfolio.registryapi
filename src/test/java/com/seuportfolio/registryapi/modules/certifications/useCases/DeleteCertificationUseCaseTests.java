package com.seuportfolio.registryapi.modules.certifications.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteCertificationUseCaseTests {

	@Mock
	private BaseContentRepo unusedBaseContentRepo;

	@Autowired
	@InjectMocks
	private DeleteCertificationUseCase deleteCertificationUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to delete a certification")
	void deleteCertificationSuccessCase() {
		String userId = UUID.randomUUID().toString();
		String baseContentId = UUID.randomUUID().toString();

		assertDoesNotThrow(
			() -> this.deleteCertificationUseCase.exec(userId, baseContentId)
		);
	}
}
