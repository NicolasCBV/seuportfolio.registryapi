package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.organizations.modals.mappers.OrganizationMapper;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.OrganizationDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetOrganizationUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	public Optional<OrganizationDTO> exec(
		String baseContentId,
		UserEntity user
	) {
		Optional<BaseContentEntity> optBaseContentOrg =
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					UUID.fromString(baseContentId),
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				);
		if (optBaseContentOrg.isEmpty()) return Optional.empty();
		return Optional.of(
			OrganizationMapper.prettify(optBaseContentOrg.get())
		);
	}
}
