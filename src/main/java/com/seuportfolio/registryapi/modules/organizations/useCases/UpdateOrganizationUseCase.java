package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.repositories.TagRepo;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.OrganizationChangesDTO;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.UpdateOrganizationDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.errors.enums.UseCaseTagEnum;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateOrganizationUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private TagRepo tagRepo;

	@Transactional
	public BaseContentEntity exec(
		String orgId,
		UpdateOrganizationDTO dto,
		UserEntity user
	) throws UseCaseException {
		UUID baseContentId = UUID.fromString(orgId);
		Optional<BaseContentEntity> optOrg =
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					baseContentId,
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				);
		if (optOrg.isEmpty()) throw new UseCaseException(
			"Organization not found",
			UseCaseTagEnum.CONTENT_NOT_FOUND
		);

		var org = optOrg.get();

		this.tryDeleteTags(org, dto.getDeleteTags());
		this.tryUpdateTags(org, dto.getInsertTags());

		OrganizationChangesDTO orgChanges = dto.getOrganizationChanges();
		if (
			orgChanges != null &&
			orgChanges.getClass() == OrganizationChangesDTO.class
		) {
			this.tryUpdateOrg(org, dto.getOrganizationChanges());
			this.tryUpdateOrgAditionalInfos(org, dto.getOrganizationChanges());
		}

		return this.baseContentRepo.save(org);
	}

	private void tryUpdateOrg(
		BaseContentEntity org,
		OrganizationChangesDTO dto
	) {
		String description = dto.getDescription() != null
			? dto.getDescription()
			: org.getDescription();
		String name = dto.getName() != null ? dto.getName() : org.getName();

		org.setName(name);
		org.setDescription(description);
	}

	private void tryDeleteTags(
		BaseContentEntity org,
		List<String> tagsToDelete
	) {
		List<TagEntity> tagList = org.getTagEntity();

		if (tagsToDelete != null && tagList != null) {
			var removedTags = new ArrayList<TagEntity>();
			for (String tagToDelete : tagsToDelete) tagList.removeIf(tag -> {
				if (tag.getName().equals(tagToDelete)) {
					removedTags.add(tag);
					return true;
				}

				return false;
			});

			this.tagRepo.deleteAll(removedTags);
		}
	}

	private void tryUpdateTags(
		BaseContentEntity org,
		List<String> tagsToUpdate
	) {
		var tagList = new ArrayList<TagEntity>();
		if (tagsToUpdate != null) for (String tagsToAdd : tagsToUpdate) {
			var tag = TagEntity.builder()
				.name(tagsToAdd)
				.baseContentEntity(org)
				.build();
			tagList.add(tag);
		}

		this.tagRepo.saveAll(tagList);
	}

	private void tryUpdateOrgAditionalInfos(
		BaseContentEntity org,
		OrganizationChangesDTO dto
	) {
		var organizationAditionalInfos = org.getOrganizationEntity();
		String siteUrl = dto.getSiteUrl() != null
			? dto.getSiteUrl()
			: organizationAditionalInfos.getSiteUrl();

		organizationAditionalInfos.setSiteUrl(siteUrl);
		this.entityManager.merge(organizationAditionalInfos);
	}
}
