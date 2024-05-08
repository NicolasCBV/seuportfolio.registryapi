package com.seuportfolio.registryapi.modules.organizations.useCases;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationEntity;
import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationTagEntity;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.OrganizationChangesDTO;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.UpdateOrganizationDTO;
import com.seuportfolio.registryapi.modules.organizations.repositories.OrganizationRepo;
import com.seuportfolio.registryapi.modules.organizations.repositories.OrganizationTagRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import java.util.ArrayList;
import java.util.List;
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
public class UpdateOrganizationUseCaseTests {

	@Mock
	private OrganizationRepo organizationRepo;

	@Mock
	private OrganizationTagRepo organizationTagRepo;

	@Autowired
	@InjectMocks
	private UpdateOrganizationUseCase updateOrganizationUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to update a organization")
	void updateOrganizationSuccessCase() throws UseCaseException {
		UUID organizationId = UUID.randomUUID();
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();
		var org = Optional.of(
			OrganizationEntity.builder()
				.id(organizationId)
				.name("org name")
				.description("org description")
				.userEntity(user)
				.build()
		);

		when(this.organizationRepo.findById(organizationId)).thenReturn(org);

		var organizationChanges = OrganizationChangesDTO.builder()
			.name("new org name")
			.description("new description")
			.build();

		List<String> tagsToRemove = new ArrayList<String>(1);
		tagsToRemove.add("old tag");

		List<String> tagsToUpdate = new ArrayList<String>(1);
		tagsToUpdate.add("new tag");

		var dto = UpdateOrganizationDTO.builder()
			.organizationId(organizationId.toString())
			.organizationChanges(organizationChanges)
			.deleteTags(tagsToRemove)
			.insertTags(tagsToUpdate)
			.build();

		this.updateOrganizationUseCase.exec(dto);
		verify(this.organizationRepo, times(1)).updateOrganization(
			organizationChanges.getName(),
			organizationChanges.getDescription(),
			organizationId
		);

		var tag = OrganizationTagEntity.builder()
			.name("new tag")
			.organizationEntity(org.get())
			.build();
		verify(this.organizationTagRepo, times(1)).save(tag);

		verify(this.organizationTagRepo, times(1)).deleteByName(
			organizationId,
			tagsToRemove.get(0)
		);
	}

	@Test
	@DisplayName("it should throw use case exception")
	void useCaseExceptionCase() {
		var dto = UpdateOrganizationDTO.builder()
			.organizationId(UUID.randomUUID().toString())
			.build();

		assertThrows(
			UseCaseException.class,
			() -> this.updateOrganizationUseCase.exec(dto)
		);
	}
}
