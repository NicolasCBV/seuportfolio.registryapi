package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.repositories.PackageRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteOrganizationUseCase {

	@Autowired
	private PackageRepo packageRepo;

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Transactional
	public void exec(UUID baseContentId, UserEntity user) {
		var optOrg =
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					baseContentId,
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				);
		if (optOrg.isEmpty()) return;

		Optional<PackageEntity> optPack = optOrg.get().getOwnerOf();
		if (optPack.isEmpty()) return;

		this.packageRepo.delete(optPack.get());
	}
}
