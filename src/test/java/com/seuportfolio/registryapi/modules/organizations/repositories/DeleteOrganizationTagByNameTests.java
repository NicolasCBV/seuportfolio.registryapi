package com.seuportfolio.registryapi.modules.organizations.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationEntity;
import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationTagEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class DeleteOrganizationTagByNameTests {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private OrganizationTagRepo organizationTagRepo;

	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	void flushAll() {
		this.userRepo.deleteAll();
	}

	@Test
	@DisplayName("it should be able to delete tag")
	void deleteTagSuccessCase() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		var map = this.createOrganization(user);
		var org = (OrganizationEntity) map.get("org");
		var tag = (OrganizationTagEntity) map.get("tagList");

		this.organizationTagRepo.deleteByName(org.getId(), "tag name");

		Optional<OrganizationTagEntity> optSearchedTag =
			this.organizationTagRepo.findById(tag.getId());

		assertThat(optSearchedTag.isEmpty()).isTrue();
	}

	private Map<String, Object> createOrganization(UserEntity user) {
		this.entityManager.persist(user);

		Map<String, Object> returnableItem = new HashMap<String, Object>();

		var tag = OrganizationTagEntity.builder()
			.id(UUID.randomUUID())
			.name("tag name")
			.build();
		List<OrganizationTagEntity> tagList = new ArrayList<
			OrganizationTagEntity
		>(1);
		tagList.add(tag);
		returnableItem.put("tagList", tagList.get(0));

		var organization = OrganizationEntity.builder()
			.name("org name")
			.description("description")
			.userEntity(user)
			.organizationTagEntity(tagList)
			.build();
		returnableItem.put("org", organization);

		this.entityManager.persist(organization);
		return returnableItem;
	}
}
