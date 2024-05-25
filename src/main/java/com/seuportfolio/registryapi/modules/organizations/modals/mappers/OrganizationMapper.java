package com.seuportfolio.registryapi.modules.organizations.modals.mappers;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.OrganizationDTO;

public class OrganizationMapper {

	public static OrganizationDTO prettify(BaseContentEntity input) {
		return OrganizationDTO.builder()
			.id(input.getId().toString())
			.name(input.getName())
			.description(input.getDescription())
			.updatedAt(input.getUpdatedAt())
			.createdAt(input.getCreatedAt())
			.tags(input.getTagEntity())
			.build();
	}
}
