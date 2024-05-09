package com.seuportfolio.registryapi.modules.organizations.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
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
		var org = (BaseContentEntity) map.get("org");
		var tag = (TagEntity) map.get("tagList");

		this.organizationTagRepo.deleteByName(org.getId(), "tag name");

		Optional<TagEntity> optSearchedTag =
			this.organizationTagRepo.findById(tag.getId());

		assertThat(optSearchedTag.isEmpty()).isTrue();
	}

	private Map<String, Object> createOrganization(UserEntity user) {
		this.entityManager.persist(user);

		Map<String, Object> returnableItem = new HashMap<String, Object>();

		var tag = TagEntity.builder()
			.id(UUID.randomUUID())
			.name("tag name")
			.build();
		List<TagEntity> tagList = new ArrayList<TagEntity>(1);
		tagList.add(tag);
		returnableItem.put("tagList", tagList.get(0));

		var organization = BaseContentEntity.builder()
			.name("org name")
			.description("description")
			.userEntity(user)
			.tagEntity(tagList)
			.build();
		returnableItem.put("org", organization);

		this.entityManager.persist(organization);
		return returnableItem;
	}
}
