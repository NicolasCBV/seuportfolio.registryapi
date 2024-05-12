package com.seuportfolio.registryapi.modules.organizations.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.repositories.TagRepo;
import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationAditionalInfoEntity;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.OrganizationChangesDTO;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.UpdateOrganizationDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import jakarta.persistence.EntityManager;
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
	private BaseContentRepo baseContentRepo;

	@Mock
	private EntityManager unusedEntityManager;

	@Mock
	private TagRepo unusedTagRepo;

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
			.id(UUID.randomUUID())
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();
		var aditionalInfos = OrganizationAditionalInfoEntity.builder()
			.siteUrl("http://new-site")
			.build();
		var org = BaseContentEntity.builder()
			.id(organizationId)
			.name("org name")
			.description("org description")
			.userEntity(user)
			.organizationEntity(aditionalInfos)
			.build();
		var optOrg = Optional.of(org);
		aditionalInfos.setBaseContentEntity(org);

		when(
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					org.getId(),
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				)
		).thenReturn(optOrg);
		when(this.baseContentRepo.save(org)).thenReturn(org);

		var organizationChanges = OrganizationChangesDTO.builder()
			.name("new org name")
			.description("new description")
			.siteUrl("http://new-site")
			.build();

		List<String> tagsToRemove = new ArrayList<String>(1);
		tagsToRemove.add("old tag");

		List<String> tagsToUpdate = new ArrayList<String>(1);
		tagsToUpdate.add("new tag");

		var dto = UpdateOrganizationDTO.builder()
			.organizationChanges(organizationChanges)
			.deleteTags(tagsToRemove)
			.insertTags(tagsToUpdate)
			.build();

		assertDoesNotThrow(
			() ->
				this.updateOrganizationUseCase.exec(
						organizationId.toString(),
						dto,
						user
					)
		);
	}

	@Test
	@DisplayName("it should throw use case exception")
	void useCaseExceptionCase() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();
		var dto = UpdateOrganizationDTO.builder().build();

		assertThrows(
			UseCaseException.class,
			() ->
				this.updateOrganizationUseCase.exec(
						UUID.randomUUID().toString(),
						dto,
						user
					)
		);
	}
}
