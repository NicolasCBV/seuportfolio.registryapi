package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.CreateOrganizationDTO;
import com.seuportfolio.registryapi.modules.organizations.repositories.OrganizationRepo;
import com.seuportfolio.registryapi.modules.organizations.repositories.OrganizationTagRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateOrganizationUseCase {

	@Autowired
	private OrganizationRepo organizationRepo;

	@Autowired
	private OrganizationTagRepo organizationTagRepo;

	@Transactional
	public BaseContentEntity exec(CreateOrganizationDTO dto, UserEntity user) {
		var org = BaseContentEntity.builder()
			.name(dto.getName())
			.description(dto.getDescription())
			.userEntity(user)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();

		this.organizationRepo.save(org);

		for (String tag : dto.getTags()) {
			var tagEntity = TagEntity.builder()
				.name(tag)
				.baseContentEntity(org)
				.build();
			this.organizationTagRepo.save(tagEntity);
		}
		return org;
	}
}
