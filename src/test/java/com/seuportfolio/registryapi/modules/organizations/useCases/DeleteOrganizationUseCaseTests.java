package com.seuportfolio.registryapi.modules.organizations.useCases;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.seuportfolio.registryapi.modules.organizations.repositories.OrganizationRepo;
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
	private OrganizationRepo organizationRepo;

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
		this.deleteOrganizationUseCase.exec(organizationId);

		verify(this.organizationRepo, times(1)).deleteById(organizationId);
	}
}
