package com.seuportfolio.registryapi.modules.certifications.modals.mappers;

import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CertificationDTO;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;

public class CertificationMapper {

	public static CertificationDTO prettify(BaseContentEntity input) {
		return CertificationDTO.builder()
			.id(input.getId().toString())
			.organizationId(input.getCertificationEntity().getId().toString())
			.name(input.getName())
			.description(input.getDescription())
			.code(input.getCertificationEntity().getCode())
			.link(input.getCertificationEntity().getLink())
			.issuedAt(input.getCertificationEntity().getIssuedAt())
			.imageUrl(input.getCertificationEntity().getImageUrl())
			.createdAt(input.getCreatedAt())
			.updatedAt(input.getUpdatedAt())
			.tags(input.getTagEntity())
			.build();
	}
}
