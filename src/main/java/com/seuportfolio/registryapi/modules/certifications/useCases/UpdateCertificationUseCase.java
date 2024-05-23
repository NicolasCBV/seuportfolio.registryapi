package com.seuportfolio.registryapi.modules.certifications.useCases;

import com.seuportfolio.registryapi.modules.certifications.modals.CertificationEntity;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CertificationChangesDTO;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.UpdateCertificationDTO;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.useCases.UpdateTagsUseCase;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.errors.enums.UseCaseTagEnum;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateCertificationUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private UpdateTagsUseCase updateTagsUseCase;

	@Transactional
	public BaseContentEntity exec(
		String rawBaseContentId,
		UpdateCertificationDTO dto,
		UserEntity user
	) throws UseCaseException {
		UUID certBaseId = UUID.fromString(rawBaseContentId);
		BaseContentEntity certBase =
			this.updateTagsUseCase.exec(
					certBaseId,
					BaseContentCategoryEnum.CERTIFICATION,
					user,
					dto.getInsertTags(),
					dto.getDeleteTags()
				);

		var changes = dto.getCertificationChangesDTO();
		var cert = certBase.getCertificationEntity();
		this.updateCert(certBase, cert, changes);

		if (changes.getOrganizationId() != null) this.changeOrgReferencial(
				changes.getOrganizationId(),
				cert,
				user
			);

		this.baseContentRepo.save(certBase);
		return certBase;
	}

	private void updateCert(
		BaseContentEntity base,
		CertificationEntity cert,
		CertificationChangesDTO changes
	) {
		base.setName(
			changes.getName() != null ? changes.getName() : base.getName()
		);
		base.setDescription(
			changes.getDescription() != null
				? changes.getDescription()
				: base.getDescription()
		);

		cert.setCode(
			changes.getCode() != null ? changes.getCode() : cert.getCode()
		);
		cert.setLink(
			changes.getLink() != null ? changes.getLink() : cert.getLink()
		);
		cert.setIssuedAt(
			changes.getIssuedAt() != null
				? changes.getIssuedAt()
				: cert.getIssuedAt()
		);
	}

	private void changeOrgReferencial(
		String orgId,
		CertificationEntity cert,
		UserEntity user
	) throws UseCaseException {
		Optional<BaseContentEntity> optOrg =
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					UUID.fromString(orgId),
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				);

		if (optOrg.isEmpty()) throw new UseCaseException(
			"Base content not found. Category: " +
			BaseContentCategoryEnum.CERTIFICATION,
			UseCaseTagEnum.CONTENT_NOT_FOUND
		);

		cert.setBaseContentEntity(optOrg.get());
	}
}
