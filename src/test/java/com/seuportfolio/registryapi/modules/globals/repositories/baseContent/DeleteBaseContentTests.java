package com.seuportfolio.registryapi.modules.globals.repositories.baseContent;

import static org.assertj.core.api.Assertions.assertThat;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
public class DeleteBaseContentTests {

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
	@DisplayName("it should be able to delete a base content")
	@Transactional
	void deleteBaseContentSuccessCase() {
		var map = this.createBaseContent();
		var user = (UserEntity) map.get("user");
		var org = (BaseContentEntity) map.get("org");

		this.baseContentRepo.safeDelete(
				org.getId(),
				user.getId(),
				BaseContentCategoryEnum.ORGANIZATION.getValue()
			);

		Optional<BaseContentEntity> optSearchedOrg =
			this.baseContentRepo.findById(org.getId());
		assertThat(optSearchedOrg.isEmpty()).isTrue();
	}

	private Map<String, Object> createBaseContent() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456789")
			.build();

		var org = BaseContentEntity.builder()
			.name("org")
			.description("description")
			.userEntity(user)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();

		this.entityManager.persist(user);
		this.entityManager.persist(org);

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("org", org);
		data.put("user", user);

		return data;
	}
}
