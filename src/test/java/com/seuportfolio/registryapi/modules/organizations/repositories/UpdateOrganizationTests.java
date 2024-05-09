package com.seuportfolio.registryapi.modules.organizations.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UpdateOrganizationTests {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private OrganizationRepo organizationRepo;

	@BeforeEach
	void flushAll() {
		this.userRepo.deleteAll();
	}

	@Test
	@DisplayName("it should be able to update organizatio")
	void updateOrganizationSuccessCase() {
		UserEntity user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		var org = this.createOrganization(user);

		String newName = "New org name";
		String newDescription = "New description";
		this.organizationRepo.updateOrganization(
				newName,
				newDescription,
				org.getId()
			);

		Optional<BaseContentEntity> optUpdatedOrg =
			this.organizationRepo.findById(org.getId());

		assertThat(optUpdatedOrg.isEmpty()).isFalse();

		var updatedOrg = optUpdatedOrg.get();
		assertThat(updatedOrg.getName()).isEqualTo(newName);
		assertThat(updatedOrg.getDescription()).isEqualTo(newDescription);
	}

	private BaseContentEntity createOrganization(UserEntity user) {
		var org = BaseContentEntity.builder()
			.name("org name")
			.description("description")
			.userEntity(user)
			.build();

		this.entityManager.persist(user);
		this.entityManager.persist(org);

		return org;
	}
}
