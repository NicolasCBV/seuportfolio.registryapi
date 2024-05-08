package com.seuportfolio.registryapi.modules.organizations.repositories;

import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationTagEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganizationTagRepo
	extends JpaRepository<OrganizationTagEntity, UUID> {
	@Modifying(clearAutomatically = true)
	@Query(
		"DELETE FROM organization_tags o " +
		"WHERE o.organizationEntity.id = :id AND o.name = :name"
	)
	void deleteByName(
		@Param("id") UUID organizationId,
		@Param("name") String name
	);
}
