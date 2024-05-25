package com.seuportfolio.registryapi.modules.globals.repositories.baseContent;

import static org.assertj.core.api.Assertions.assertThat;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UpdateBaseContentTests {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private BaseContentRepo baseContentRepo;

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to update organization")
	@Transactional
	void updateBaseContentSuccessCase() {
		var user = this.createUser();
		var org = this.createOrganization(user);

		String newName = "New org name";
		String newDescription = "New description";
		this.baseContentRepo.updateBaseContent(
				newName,
				newDescription,
				org.getId(),
				BaseContentCategoryEnum.ORGANIZATION.getValue()
			);

		this.baseContentRepo.flush();
		Optional<BaseContentEntity> optUpdatedOrg =
			this.baseContentRepo.findByName(newName);

		assertThat(optUpdatedOrg.isEmpty()).isFalse();

		var updatedOrg = optUpdatedOrg.get();
		assertThat(updatedOrg.getName()).isEqualTo(newName);
		assertThat(updatedOrg.getDescription()).isEqualTo(newDescription);
	}

	private UserEntity createUser() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();
		this.entityManager.persist(user);

		return user;
	}

	private BaseContentEntity createOrganization(UserEntity user) {
		var org = BaseContentEntity.builder()
			.name("org name")
			.description("description")
			.userEntity(user)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();
		var orgList = new ArrayList<BaseContentEntity>(1);
		orgList.add(org);

		user.setBaseContentEntity(orgList);
		this.entityManager.persist(org);

		return org;
	}
}
