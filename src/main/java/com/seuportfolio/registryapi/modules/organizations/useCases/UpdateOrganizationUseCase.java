package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.useCases.UpdateTagsUseCase;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.OrganizationChangesDTO;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.UpdateOrganizationDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateOrganizationUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private UpdateTagsUseCase updateTagsUseCase;

	@Transactional
	public BaseContentEntity exec(
		String baseContentId,
		UpdateOrganizationDTO dto,
		UserEntity user
	) throws UseCaseException {
		BaseContentEntity org =
			this.updateTagsUseCase.exec(
					UUID.fromString(baseContentId),
					BaseContentCategoryEnum.ORGANIZATION,
					user,
					dto.getInsertTags(),
					dto.getDeleteTags()
				);

		OrganizationChangesDTO orgChanges = dto.getOrganizationChanges();
		if (orgChanges != null) this.tryUpdateOrg(
				org,
				dto.getOrganizationChanges()
			);

		return this.baseContentRepo.save(org);
	}

	private void tryUpdateOrg(
		BaseContentEntity org,
		OrganizationChangesDTO dto
	) {
		org.setDescription(
			dto.getDescription() != null
				? dto.getDescription()
				: org.getDescription()
		);
		org.setName(dto.getName() != null ? dto.getName() : org.getName());
	}
}
