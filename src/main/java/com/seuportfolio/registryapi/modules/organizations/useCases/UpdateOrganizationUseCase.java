package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.OrganizationChangesDTO;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.UpdateOrganizationDTO;
import com.seuportfolio.registryapi.modules.organizations.repositories.OrganizationRepo;
import com.seuportfolio.registryapi.modules.organizations.repositories.OrganizationTagRepo;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.errors.enums.UseCaseTagEnum;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateOrganizationUseCase {

	@Autowired
	private OrganizationRepo organizationRepo;

	@Autowired
	private OrganizationTagRepo organizationTagRepo;

	@Transactional
	public void exec(UpdateOrganizationDTO dto) throws UseCaseException {
		UUID organizationId = UUID.fromString(dto.getOrganizationId());
		Optional<BaseContentEntity> optOrg =
			this.organizationRepo.findById(organizationId);
		if (optOrg.isEmpty()) throw new UseCaseException(
			"Organization not found",
			UseCaseTagEnum.CONTENT_NOT_FOUND
		);

		var org = optOrg.get();

		this.updateOrganization(org, dto.getOrganizationChanges());
		this.tryDeleteTags(organizationId, dto.getDeleteTags());
		this.tryUpdateTags(org, dto.getInsertTags());
	}

	private void updateOrganization(
		BaseContentEntity org,
		OrganizationChangesDTO dto
	) {
		if (dto != null) this.organizationRepo.updateOrganization(
				dto.getName(),
				dto.getDescription(),
				org.getId()
			);
	}

	private void tryDeleteTags(UUID organizationId, List<String> tagsToDelete) {
		if (
			tagsToDelete != null
		) for (String tagToDelete : tagsToDelete) this.organizationTagRepo.deleteByName(
				organizationId,
				tagToDelete
			);
	}

	private void tryUpdateTags(
		BaseContentEntity org,
		List<String> tagsToUpdate
	) {
		if (tagsToUpdate != null) for (String tagsToAdd : tagsToUpdate) {
			var tag = TagEntity.builder()
				.name(tagsToAdd)
				.baseContentEntity(org)
				.build();
			this.organizationTagRepo.save(tag);
		}
	}
}
