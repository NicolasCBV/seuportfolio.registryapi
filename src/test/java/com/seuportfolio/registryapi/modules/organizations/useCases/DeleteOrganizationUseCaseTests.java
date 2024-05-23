package com.seuportfolio.registryapi.modules.organizations.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.repositories.PackageRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public class DeleteOrganizationUseCaseTests {

	@Mock
	private PackageRepo unusedPackageRepo;

	@Mock
	private BaseContentRepo unusedBaseContentRepo;

	@Autowired
	@InjectMocks
	private DeleteOrganizationUseCase deleteOrganizationUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to delete organization")
	void deleteOrganizationSuccessCase() {
		UUID organizationId = UUID.randomUUID();
		var user = UserEntity.builder()
			.email("johndoe@email.com")
			.fullName("John Doe")
			.password("123456")
			.build();

		assertDoesNotThrow(
			() -> this.deleteOrganizationUseCase.exec(organizationId, user)
		);
	}
}
