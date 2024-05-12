package com.seuportfolio.registryapi.modules.organizations.useCases;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationAditionalInfoEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.Optional;
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
	private BaseContentRepo baseContentRepo;

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
		var orgAditionalInfos = OrganizationAditionalInfoEntity.builder()
			.siteUrl("http://localhost:8080")
			.build();

		UUID organizationId = UUID.randomUUID();
		var org = BaseContentEntity.builder()
			.id(organizationId)
			.name("org name")
			.description("description")
			.organizationEntity(orgAditionalInfos)
			.build();

		var user = UserEntity.builder()
			.email("johndoe@email.com")
			.fullName("John Doe")
			.password("123456")
			.build();
		org.setUserEntity(user);

		when(this.baseContentRepo.findById(organizationId)).thenReturn(
			Optional.of(org)
		);

		this.deleteOrganizationUseCase.exec(organizationId, user);

		verify(this.baseContentRepo, times(1)).delete(org);
	}
}
