package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteOrganizationUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Transactional
	public void exec(UUID baseContentId, UserEntity user) {
		this.baseContentRepo.safeDelete(
				baseContentId,
				user.getId(),
				BaseContentCategoryEnum.ORGANIZATION.getValue()
			);
	}
}
