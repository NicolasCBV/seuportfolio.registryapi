package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.organizations.repositories.OrganizationRepo;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteOrganizationUseCase {

	@Autowired
	private OrganizationRepo organizationRepo;

	public void exec(UUID organizationId) {
		this.organizationRepo.deleteById(organizationId);
	}
}
