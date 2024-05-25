package com.seuportfolio.registryapi.modules.certifications.useCases;

import com.seuportfolio.registryapi.modules.certifications.modals.mappers.CertificationMapper;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CertificationDTO;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetCertificationUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	public Optional<CertificationDTO> exec(
		String baseContentId,
		UserEntity user
	) {
		Optional<BaseContentEntity> baseContentCertification =
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					UUID.fromString(baseContentId),
					BaseContentCategoryEnum.CERTIFICATION.getValue()
				);
		if (baseContentCertification.isEmpty()) return Optional.empty();
		return Optional.of(
			CertificationMapper.prettify(baseContentCertification.get())
		);
	}
}
