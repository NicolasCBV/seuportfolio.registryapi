package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteOrganizationUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	public void exec(UUID baseContentId, UserEntity user) {
		Optional<BaseContentEntity> baseContent =
			this.baseContentRepo.findById(baseContentId);
		if (!baseContent.isEmpty()) this.baseContentRepo.delete(
				baseContent.get()
			);
	}
}
