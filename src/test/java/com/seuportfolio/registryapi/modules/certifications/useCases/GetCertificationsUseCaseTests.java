package com.seuportfolio.registryapi.modules.certifications.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.certifications.modals.CertificationEntity;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEnum;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class GetCertificationsUseCaseTests {

	@Mock
	private BaseContentRepo baseContentRepo;

	@Autowired
	@InjectMocks
	private GetCertificationsUseCase getCertificationUseCase;

	@BeforeEach
	private void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to get certifications")
	void getCertificationSuccessCase() {
		var org = BaseContentEntity.builder()
			.id(UUID.randomUUID())
			.name("org")
			.description("description")
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();
		var pack = PackageEntity.builder()
			.root(org)
			.type(PackageEnum.ORGANIZATION.getValue())
			.build();
		org.setLinkedOn(pack);

		var certBaseContent = BaseContentEntity.builder()
			.id(UUID.randomUUID())
			.name("certification")
			.description("description")
			.category(BaseContentCategoryEnum.CERTIFICATION.getValue())
			.linkedOn(pack)
			.build();
		var certification = CertificationEntity.builder()
			.id(UUID.randomUUID())
			.code("123456")
			.link("http://localhost")
			.issuedAt(LocalDateTime.now(ZoneOffset.UTC))
			.imageUrl("http://localhost")
			.baseContentEntity(certBaseContent)
			.build();
		certBaseContent.setCertificationEntity(certification);

		var certList = new ArrayList<BaseContentEntity>(1);
		certList.add(certBaseContent);

		var user = UserEntity.builder()
			.id(UUID.randomUUID())
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		int offset = 0;

		when(
			this.baseContentRepo.getBaseContentCollection(
					user.getId(),
					10,
					offset,
					BaseContentCategoryEnum.CERTIFICATION.getValue()
				)
		).thenReturn(certList);

		assertDoesNotThrow(
			() -> this.getCertificationUseCase.exec(offset, user)
		);
	}
}
