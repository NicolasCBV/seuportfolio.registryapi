package com.seuportfolio.registryapi.modules.certifications.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteCertificationUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Transactional
	public void exec(String userId, String baseContentId) {
		this.baseContentRepo.safeDelete(
				UUID.fromString(baseContentId),
				UUID.fromString(userId),
				BaseContentCategoryEnum.CERTIFICATION.getValue()
			);
	}
}
