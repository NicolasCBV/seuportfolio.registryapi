package com.seuportfolio.registryapi.modules.organizations.repositories;

import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationAditionalInfoEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationAditionalInfoRepo
	extends JpaRepository<OrganizationAditionalInfoEntity, UUID> {}
