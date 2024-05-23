package com.seuportfolio.registryapi.modules.certifications.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.certifications.modals.CertificationEntity;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CertificationChangesDTO;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.UpdateCertificationDTO;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.useCases.UpdateTagsUseCase;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.errors.enums.UseCaseTagEnum;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class UpdateCertificationUseCaseTests {

	@Mock
	private BaseContentRepo baseContentRepo;

	@Mock
	private UpdateTagsUseCase updateTagsUseCase;

	@Autowired
	@InjectMocks
	private UpdateCertificationUseCase updateCertificationUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to update certification")
	void updateCertificationSuccessCase() throws UseCaseException {
		var map = this.buildPayload();
		var user = (UserEntity) map.get("user");
		var certBase = (BaseContentEntity) map.get("certBase");
		var org = (BaseContentEntity) map.get("org");
		var changes = (CertificationChangesDTO) map.get("changes");

		var insertTags = new ArrayList<String>();
		insertTags.add("new tag");

		var removeTags = new ArrayList<String>();
		removeTags.add("new tag");

		when(
			this.updateTagsUseCase.exec(
					certBase.getId(),
					BaseContentCategoryEnum.CERTIFICATION,
					user,
					insertTags,
					removeTags
				)
		).thenReturn(certBase);

		when(
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					org.getId(),
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				)
		).thenReturn(Optional.of(org));

		var dto = UpdateCertificationDTO.builder()
			.certificationChangesDTO(changes)
			.insertTags(insertTags)
			.deleteTags(removeTags)
			.build();
		assertDoesNotThrow(
			() ->
				this.updateCertificationUseCase.exec(
						certBase.getId().toString(),
						dto,
						user
					)
		);
	}

	@Test
	@DisplayName("it should throw UseCaseException")
	void useCaseExceptionCase() throws UseCaseException {
		var map = this.buildPayload();
		var user = (UserEntity) map.get("user");
		var certBase = (BaseContentEntity) map.get("certBase");

		var insertTags = new ArrayList<String>();
		insertTags.add("new tag");

		var removeTags = new ArrayList<String>();
		removeTags.add("new tag");

		var category = BaseContentCategoryEnum.CERTIFICATION;
		when(
			this.updateTagsUseCase.exec(
					certBase.getId(),
					category,
					user,
					insertTags,
					removeTags
				)
		).thenThrow(
			new UseCaseException(
				"Base content not found. Category: " + category.getValue(),
				UseCaseTagEnum.CONTENT_NOT_FOUND
			)
		);
	}

	private Map<String, Object> buildPayload() {
		var user = UserEntity.builder()
			.id(UUID.randomUUID())
			.fullName("John Doe")
			.description("description")
			.build();
		var certBase = BaseContentEntity.builder()
			.id(UUID.randomUUID())
			.name("cert")
			.description("description")
			.category(BaseContentCategoryEnum.CERTIFICATION.getValue())
			.build();
		var cert = CertificationEntity.builder()
			.id(UUID.randomUUID())
			.link("http://localhost")
			.issuedAt(LocalDateTime.now(ZoneOffset.UTC))
			.baseContentEntity(certBase)
			.build();
		certBase.setCertificationEntity(cert);

		var org = BaseContentEntity.builder()
			.id(UUID.randomUUID())
			.name("org")
			.description("description")
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();

		var changes = CertificationChangesDTO.builder()
			.organizationId(org.getId().toString())
			.name("new cert name")
			.description("new description")
			.code("new code")
			.build();

		var map = new HashMap<String, Object>();
		map.put("user", user);
		map.put("certBase", certBase);
		map.put("cert", cert);
		map.put("org", org);
		map.put("changes", changes);

		return map;
	}
}
