package com.seuportfolio.registryapi.modules.certifications.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CreateCertificationDTO;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEnum;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
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

public class CreateCertificationUseCaseTests {

	@Mock
	private BaseContentRepo baseContentRepo;

	@Autowired
	@InjectMocks
	private CreateCertificationUseCase createCertificationUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to create certifications")
	void createCertificationSuccessCase() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		var pack = PackageEntity.builder()
			.type(PackageEnum.ORGANIZATION.getValue())
			.build();
		var org = BaseContentEntity.builder()
			.id(UUID.randomUUID())
			.name("org")
			.description("description")
			.userEntity(user)
			.ownerOf(pack)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();
		pack.setRoot(org);

		var dto = CreateCertificationDTO.builder()
			.organizationId(org.getId().toString())
			.name("certification")
			.issuedAt(LocalDateTime.now(ZoneOffset.UTC))
			.description("description")
			.code("123456")
			.link("http://localhost")
			.build();

		when(
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					UUID.fromString(dto.getOrganizationId()),
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				)
		).thenReturn(Optional.of(org));

		assertDoesNotThrow(
			() -> this.createCertificationUseCase.exec(dto, user)
		);
	}

	@Test
	@DisplayName("it should be able to throw error - UseCaseException")
	void createCertificationException() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();
		var dto = CreateCertificationDTO.builder()
			.organizationId(UUID.randomUUID().toString())
			.name("certification")
			.issuedAt(LocalDateTime.now(ZoneOffset.UTC))
			.description("description")
			.code("123456")
			.link("http://localhost")
			.build();

		assertThrows(
			UseCaseException.class,
			() -> this.createCertificationUseCase.exec(dto, user)
		);
	}
}
