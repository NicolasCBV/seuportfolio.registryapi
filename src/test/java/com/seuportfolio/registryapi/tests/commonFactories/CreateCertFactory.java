package com.seuportfolio.registryapi.tests.commonFactories;

import static org.assertj.core.api.Assertions.assertThat;

import com.seuportfolio.registryapi.modules.certifications.modals.CertificationEntity;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEnum;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Optional;

public class CreateCertFactory {

	@Transactional
	public static BaseContentEntity create(
		EntityManager entityManager,
		UserRepo userRepo,
		String email
	) {
		Optional<UserEntity> optUser = userRepo.findByEmail(email);
		assertThat(optUser.isEmpty()).isFalse();

		var user = optUser.get();

		var pack = PackageEntity.builder()
			.type(PackageEnum.ORGANIZATION.getValue())
			.build();
		var org = BaseContentEntity.builder()
			.name("org")
			.description("description")
			.userEntity(user)
			.ownerOf(pack)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();
		pack.setRoot(org);

		var certBaseInfos = BaseContentEntity.builder()
			.name("certification")
			.description("description")
			.userEntity(user)
			.category(BaseContentCategoryEnum.CERTIFICATION.getValue())
			.linkedOn(pack)
			.build();
		var cert = CertificationEntity.builder()
			.code("A12")
			.issuedAt(LocalDateTime.now(ZoneOffset.UTC))
			.link("http://localhost:3000")
			.imageUrl("http://localhost:3333")
			.baseContentEntity(certBaseInfos)
			.build();
		certBaseInfos.setCertificationEntity(cert);

		var baseContentList = new ArrayList<BaseContentEntity>(2);
		baseContentList.add(certBaseInfos);
		baseContentList.add(org);
		pack.setBaseContentEntities(baseContentList);

		var tag = TagEntity.builder()
			.name("old name")
			.baseContentEntity(certBaseInfos)
			.build();
		var tagList = new ArrayList<TagEntity>();
		tagList.add(tag);
		certBaseInfos.setTagEntity(tagList);

		entityManager.persist(pack);

		return certBaseInfos;
	}
}
