package com.seuportfolio.registryapi.modules.globals.repositories.tag;

import static org.assertj.core.api.Assertions.assertThat;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.TagRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class DeleteTagByNameTests {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private TagRepo tagRepo;

	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to delete tag")
	void deleteTagSuccessCase() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		var org = this.createOrganization(user);
		var tag = org.getTagEntity().get(0);
		Optional<TagEntity> optSearchedTag = this.tagRepo.findById(tag.getId());

		assertThat(optSearchedTag.isEmpty()).isFalse();

		this.tagRepo.deleteByName(tag.getName());
		Optional<TagEntity> deletedOptSearchedTag =
			this.tagRepo.findById(tag.getId());
		assertThat(deletedOptSearchedTag.isEmpty()).isTrue();
	}

	private BaseContentEntity createOrganization(UserEntity user) {
		var tag = TagEntity.builder().name("tag name").build();

		List<TagEntity> tagList = new ArrayList<TagEntity>(1);
		tagList.add(tag);

		var organization = BaseContentEntity.builder()
			.name("org name")
			.description("description")
			.userEntity(user)
			.tagEntity(tagList)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();

		tag.setBaseContentEntity(organization);

		this.entityManager.persist(user);
		this.entityManager.persist(organization);
		return organization;
	}
}
