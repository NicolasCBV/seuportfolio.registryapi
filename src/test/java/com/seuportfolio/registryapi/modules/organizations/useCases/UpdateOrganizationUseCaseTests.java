package com.seuportfolio.registryapi.modules.organizations.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.repositories.TagRepo;
import com.seuportfolio.registryapi.modules.globals.useCases.UpdateTagsUseCase;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.OrganizationChangesDTO;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.UpdateOrganizationDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.errors.enums.UseCaseTagEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private TagRepo unusedTagRepo;

	@Mock
	private UpdateTagsUseCase updateTagsUseCase;

	@Autowired
	@InjectMocks
	private UpdateOrganizationUseCase updateOrganizationUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to update a organization")
	@SuppressWarnings("unchecked")
	void updateOrganizationSuccessCase() throws UseCaseException {
		var map = this.buildPayload();
		var user = (UserEntity) map.get("user");
		var org = (BaseContentEntity) map.get("org");
		var organizationChanges = (OrganizationChangesDTO) map.get(
			"organizationChanges"
		);
		var tagsToRemove = (List<String>) map.get("tagsToRemove");
		var tagsToUpdate = (List<String>) map.get("tagsToUpdate");
		var organizationId = (UUID) map.get("organizationId");

		when(
			this.updateTagsUseCase.exec(
					org.getId(),
					BaseContentCategoryEnum.ORGANIZATION,
					user,
					tagsToUpdate,
					tagsToRemove
				)
		).thenReturn(org);
		when(this.baseContentRepo.save(org)).thenReturn(org);

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
	@SuppressWarnings("unchecked")
	void useCaseExceptionCase() throws UseCaseException {
		var map = this.buildPayload();
		var user = (UserEntity) map.get("user");
		var org = (BaseContentEntity) map.get("org");
		var organizationChanges = (OrganizationChangesDTO) map.get(
			"organizationChanges"
		);
		var tagsToRemove = (List<String>) map.get("tagsToRemove");
		var tagsToUpdate = (List<String>) map.get("tagsToUpdate");
		var organizationId = (UUID) map.get("organizationId");

		var dto = UpdateOrganizationDTO.builder()
			.organizationChanges(organizationChanges)
			.deleteTags(tagsToRemove)
			.insertTags(tagsToUpdate)
			.build();

		var category = BaseContentCategoryEnum.ORGANIZATION;
		when(
			this.updateTagsUseCase.exec(
					org.getId(),
					category,
					user,
					tagsToUpdate,
					tagsToRemove
				)
		).thenThrow(
			new UseCaseException(
				"Base content not found. Category: " + category.getValue(),
				UseCaseTagEnum.CONTENT_NOT_FOUND
			)
		);

		assertThrows(
			UseCaseException.class,
			() ->
				this.updateOrganizationUseCase.exec(
						organizationId.toString(),
						dto,
						user
					)
		);
	}

	private Map<String, Object> buildPayload() {
		UUID organizationId = UUID.randomUUID();
		var user = UserEntity.builder()
			.id(UUID.randomUUID())
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		var org = BaseContentEntity.builder()
			.id(organizationId)
			.name("org name")
			.description("org description")
			.userEntity(user)
			.build();
		var organizationChanges = OrganizationChangesDTO.builder()
			.name("new org name")
			.description("new description")
			.build();

		List<String> tagsToRemove = new ArrayList<String>(1);
		tagsToRemove.add("old tag");

		List<String> tagsToUpdate = new ArrayList<String>(1);
		tagsToUpdate.add("new tag");

		var map = new HashMap<String, Object>();
		map.put("organizationId", organizationId);
		map.put("user", user);
		map.put("org", org);
		map.put("organizationChanges", organizationChanges);
		map.put("tagsToRemove", tagsToRemove);
		map.put("tagsToUpdate", tagsToUpdate);

		return map;
	}
}
