package com.seuportfolio.registryapi.modules.organizations.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
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

public class GetOrganizationUseCaseTests {

	@Mock
	private BaseContentRepo baseContentRepo;

	@Autowired
	@InjectMocks
	private GetOrganizationUseCase getOrganizationUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to get one organization")
	void getOrganizationsSuccessCase() {
		var user = UserEntity.builder()
			.id(UUID.randomUUID())
			.fullName("john doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		var org = BaseContentEntity.builder()
			.id(UUID.randomUUID())
			.name("org")
			.description("description")
			.build();

		when(
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					org.getId(),
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				)
		).thenReturn(Optional.of(org));

		assertDoesNotThrow(
			() -> this.getOrganizationUseCase.exec(org.getId().toString(), user)
		);
	}
}
