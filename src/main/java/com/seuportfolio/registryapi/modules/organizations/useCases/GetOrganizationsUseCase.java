package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.OrganizationDTO;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetOrganizationsUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	public List<OrganizationDTO> exec(int offset, UserEntity user) {
		List<BaseContentEntity> orgs =
			this.baseContentRepo.getBaseContentCollection(
					user.getId(),
					10,
					offset,
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				);
		List<OrganizationDTO> data = orgs
			.stream()
			.map(
				item ->
					OrganizationDTO.builder()
						.id(item.getId().toString())
						.name(item.getName())
						.description(item.getDescription())
						.updatedAt(item.getUpdatedAt())
						.createdAt(item.getCreatedAt())
						.tags(item.getTagEntity())
						.build()
			)
			.collect(Collectors.toList());
		return data;
	}
}
