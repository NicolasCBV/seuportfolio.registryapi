package com.seuportfolio.registryapi.modules.certifications.useCases;

import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CertificationDTO;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetCertificationsUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	public List<CertificationDTO> exec(int offset, UserEntity user) {
		List<BaseContentEntity> baseContentList =
			this.baseContentRepo.getBaseContentCollection(
					user.getId(),
					10,
					offset,
					BaseContentCategoryEnum.CERTIFICATION.getValue()
				);

		List<CertificationDTO> data = baseContentList
			.stream()
			.map(
				item ->
					CertificationDTO.builder()
						.id(item.getId().toString())
						.organizationId(
							item.getCertificationEntity().getId().toString()
						)
						.name(item.getName())
						.description(item.getDescription())
						.code(item.getCertificationEntity().getCode())
						.link(item.getCertificationEntity().getLink())
						.issuedAt(item.getCertificationEntity().getIssuedAt())
						.imageUrl(item.getCertificationEntity().getImageUrl())
						.createdAt(item.getCreatedAt())
						.updatedAt(item.getUpdatedAt())
						.tags(item.getTagEntity())
						.build()
			)
			.collect(Collectors.toList());

		return data;
	}
}
