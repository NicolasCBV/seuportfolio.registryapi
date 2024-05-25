package com.seuportfolio.registryapi.tests.commonFactories;

import static org.assertj.core.api.Assertions.assertThat;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEnum;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

public class CreateOrgFactory {

	@Transactional
	public static BaseContentEntity create(
		EntityManager entityManager,
		BaseContentRepo baseContentRepo,
		UserEntity user
	) {
		var pack = PackageEntity.builder()
			.type(PackageEnum.ORGANIZATION.getValue())
			.build();
		var org = BaseContentEntity.builder()
			.name("org name")
			.description("description")
			.userEntity(user)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.ownerOf(pack)
			.build();
		pack.setRoot(org);

		var tag = TagEntity.builder()
			.name("old tag")
			.baseContentEntity(org)
			.build();
		var tagList = new ArrayList<TagEntity>(2);
		tagList.add(tag);
		org.setTagEntity(tagList);

		entityManager.persist(org);

		Optional<BaseContentEntity> optSearchedOrg = baseContentRepo.findByName(
			org.getName()
		);
		assertThat(optSearchedOrg.isEmpty()).isFalse();

		return optSearchedOrg.get();
	}
}
