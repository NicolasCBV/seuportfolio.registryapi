package com.seuportfolio.registryapi.modules.organizations.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class CreateOrganizationTests {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private OrganizationRepo organizationRepo;

	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	void flushAll() {
		this.userRepo.deleteAll();
	}

	@Test
	@DisplayName("it should be able to get organizations entities")
	void getOrganizationsSuccessCase() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.description("description")
			.build();

		this.createOrg(user);
		var orgs = this.organizationRepo.getOrganizations(user.getId(), 2, 2);
		assertThat(orgs.get(0).getName()).isEqualTo("org:2");
		assertThat(orgs.get(1).getName()).isEqualTo("org:3");
	}

	private List<OrganizationEntity> createOrg(UserEntity user) {
		List<OrganizationEntity> orgs = new ArrayList<OrganizationEntity>(100);

		this.entityManager.persist(user);
		for (int i = 0; i < 10; i++) {
			var org = OrganizationEntity.builder()
				.name("org:" + i)
				.description("Simple description")
				.userEntity(user)
				.build();
			this.entityManager.persist(org);
			orgs.add(org);
		}

		return orgs;
	}
}
