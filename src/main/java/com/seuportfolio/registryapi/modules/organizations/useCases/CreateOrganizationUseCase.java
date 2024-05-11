package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationAditionalInfoEntity;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.CreateOrganizationDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateOrganizationUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Transactional
	public BaseContentEntity exec(CreateOrganizationDTO dto, UserEntity user) {
		var baseContent = BaseContentEntity.builder()
			.name(dto.getName())
			.description(dto.getDescription())
			.userEntity(user)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();

		List<TagEntity> tagList = new ArrayList<TagEntity>();
		for (String tag : dto.getTags()) {
			var tagEntity = TagEntity.builder()
				.name(tag)
				.baseContentEntity(baseContent)
				.build();
			tagList.add(tagEntity);
		}

		var org = OrganizationAditionalInfoEntity.builder()
			.siteUrl(dto.getSiteUrl())
			.baseContentEntity(baseContent)
			.build();

		baseContent.setOrganizationEntity(org);
		baseContent.setTagEntity(tagList);
		return this.baseContentRepo.save(baseContent);
	}
}
