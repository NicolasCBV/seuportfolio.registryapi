package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEnum;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.PackageRepo;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.CreateOrganizationDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateOrganizationUseCase {

	@Autowired
	private PackageRepo packageRepo;

	@Transactional
	public BaseContentEntity exec(CreateOrganizationDTO dto, UserEntity user) {
		var org = BaseContentEntity.builder()
			.id(UUID.randomUUID())
			.name(dto.getName())
			.description(dto.getDescription())
			.userEntity(user)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();
		var pack = PackageEntity.builder()
			.id(UUID.randomUUID())
			.type(PackageEnum.ORGANIZATION.getValue())
			.build();

		List<TagEntity> tagList = new ArrayList<TagEntity>();
		for (String tag : dto.getTags()) {
			var tagEntity = TagEntity.builder()
				.name(tag)
				.baseContentEntity(org)
				.build();
			tagList.add(tagEntity);
		}
		org.setTagEntity(tagList);

		org.setOwnerOf(pack);
		pack.setRoot(org);
		this.packageRepo.save(pack);
		return org;
	}
}
