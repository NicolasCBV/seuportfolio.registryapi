package com.seuportfolio.registryapi.modules.certifications.useCases;

import com.seuportfolio.registryapi.modules.certifications.modals.CertificationEntity;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CreateCertificationDTO;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.errors.enums.UseCaseTagEnum;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateCertificationUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Transactional
	public void exec(CreateCertificationDTO dto, UserEntity user)
		throws UseCaseException {
		PackageEntity pack = this.searchForOrgOwnerPackage(dto, user);

		var certification = CertificationEntity.builder()
			.code(dto.getCode())
			.link(dto.getLink())
			.issuedAt(dto.getIssuedAt())
			.build();
		var baseContent = BaseContentEntity.builder()
			.userEntity(user)
			.name(dto.getName())
			.description(dto.getDescription())
			.certificationEntity(certification)
			.linkedOn(pack)
			.category(BaseContentCategoryEnum.CERTIFICATION.getValue())
			.build();

		List<String> tags = dto.getTags();
		var parsedTagList = new ArrayList<TagEntity>();

		if (tags != null) for (String tag : tags) {
			var parsedTag = TagEntity.builder()
				.name(tag)
				.baseContentEntity(baseContent)
				.build();
			parsedTagList.add(parsedTag);
		}

		baseContent.setTagEntity(parsedTagList);
		certification.setBaseContentEntity(baseContent);

		this.baseContentRepo.save(baseContent);
	}

	private PackageEntity searchForOrgOwnerPackage(
		CreateCertificationDTO dto,
		UserEntity user
	) throws UseCaseException {
		Optional<BaseContentEntity> optBaseContent =
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					UUID.fromString(dto.getOrganizationId()),
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				);
		if (
			optBaseContent.isEmpty() ||
			optBaseContent.get().getOwnerOf().isEmpty()
		) throw new UseCaseException(
			"Organization does not exist",
			UseCaseTagEnum.CONTENT_NOT_FOUND
		);

		return optBaseContent.get().getOwnerOf().get();
	}
}
