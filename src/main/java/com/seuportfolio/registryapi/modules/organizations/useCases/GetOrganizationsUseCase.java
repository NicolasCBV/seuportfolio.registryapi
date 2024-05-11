package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetOrganizationsUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	public List<BaseContentEntity> exec(int offset, UserEntity user) {
		List<BaseContentEntity> orgs =
			this.baseContentRepo.getBaseContentCollection(
					user.getId(),
					10,
					offset,
					BaseContentCategoryEnum.ORGANIZATION.getValue()
				);
		return orgs;
	}
}
