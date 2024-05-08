package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationEntity;
import com.seuportfolio.registryapi.modules.organizations.repositories.OrganizationRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetOrganizationsUseCase {

	@Autowired
	private OrganizationRepo organizationRepo;

	public List<OrganizationEntity> exec(int offset, UserEntity user) {
		List<OrganizationEntity> orgs =
			this.organizationRepo.getOrganizations(user.getId(), 10, offset);
		return orgs;
	}
}
