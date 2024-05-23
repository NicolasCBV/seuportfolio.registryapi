package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.PackageRepo;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.CreateOrganizationDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateOrganizationUseCaseTests {

	@Mock
	private PackageRepo unusedPackageRepo;

	@Autowired
	@InjectMocks
	private CreateOrganizationUseCase createOrganizationUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to create an organization")
	void createOrganizationSuccessCase() {
		List<String> tags = new ArrayList<String>(1);
		tags.add("good org");

		var user = UserEntity.builder()
			.email("johndoe@email.com")
			.fullName("John Doe")
			.password("123456")
			.build();

		var org = BaseContentEntity.builder()
			.name("my org")
			.description("description")
			.userEntity(user)
			.build();
		var dto = CreateOrganizationDTO.builder()
			.name(org.getName())
			.description(org.getDescription())
			.tags(tags)
			.build();

		this.createOrganizationUseCase.exec(dto, user);
	}
}
