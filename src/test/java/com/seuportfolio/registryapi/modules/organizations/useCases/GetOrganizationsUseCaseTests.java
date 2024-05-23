package com.seuportfolio.registryapi.modules.organizations.useCases;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
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

public class GetOrganizationsUseCaseTests {

	@Mock
	private BaseContentRepo baseContentRepo;

	@Autowired
	@InjectMocks
	private GetOrganizationsUseCase getOrganizationsUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to get organizations")
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
		var orgList = new ArrayList<BaseContentEntity>(1);
		orgList.add(org);

		int offset = 0;
		int limit = 10;
		when(
			this.baseContentRepo.getBaseContentCollection(
					user.getId(),
					limit,
					offset,
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				)
		).thenReturn(orgList);

		this.getOrganizationsUseCase.exec(offset, user);
		verify(this.baseContentRepo, times(1)).getBaseContentCollection(
			user.getId(),
			limit,
			offset,
			BaseContentCategoryEnum.ORGANIZATION.getValue()
		);
	}
}
