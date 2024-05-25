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
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class GetCertificationUseCaseTests {

	@Mock
	private BaseContentRepo baseContentRepo;

	@Autowired
	@InjectMocks
	private GetCertificationUseCase getCertificationUseCase;

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

		var user = UserEntity.builder()
			.id(UUID.randomUUID())
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		when(
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					certBaseContent.getId(),
					BaseContentCategoryEnum.CERTIFICATION.getValue()
				)
		).thenReturn(Optional.of(certBaseContent));

		assertDoesNotThrow(
			() ->
				this.getCertificationUseCase.exec(
						certBaseContent.getId().toString(),
						user
					)
		);
	}
}
