package com.seuportfolio.registryapi.modules.globals.repositories.baseContent;

import static org.assertj.core.api.Assertions.assertThat;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class GetCollectionTests {

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private EntityManager entityManager;

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to find base content")
	void findBaseContentSuccessCase() {
		UserEntity user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();
		BaseContentEntity baseContent = this.createBaseContent(user);

		List<BaseContentEntity> searchedData =
			this.baseContentRepo.getBaseContentCollection(
					user.getId(),
					5,
					0,
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				);
		assertThat(searchedData.isEmpty()).isFalse();
		assertThat(baseContent.equals(searchedData.get(0))).isTrue();
	}

	private BaseContentEntity createBaseContent(UserEntity user) {
		BaseContentEntity org = BaseContentEntity.builder()
			.userEntity(user)
			.name("org name")
			.description("description")
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();

		this.entityManager.persist(user);
		this.entityManager.persist(org);

		return org;
	}
}
