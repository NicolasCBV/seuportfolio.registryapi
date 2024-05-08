package com.seuportfolio.registryapi.modules.organizations.repositories;

import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganizationRepo
	extends JpaRepository<OrganizationEntity, UUID> {
	@Query(
		"SELECT o " +
		"FROM organizations o " +
		"WHERE o.userEntity.id = :user_id " +
		"ORDER BY o.name " +
		"LIMIT :limit OFFSET :offset"
	)
	List<OrganizationEntity> getOrganizations(
		@Param("user_id") UUID userId,
		@Param("limit") int limit,
		@Param("offset") int offset
	);

	@Modifying(clearAutomatically = true)
	@Query(
		"UPDATE organizations o " +
		"SET name = :name, description = :description " +
		"WHERE o.id = :organization_id"
	)
	void updateOrganization(
		@Param("name") String name,
		@Param("description") String description,
		@Param("organization_id") UUID organizationId
	);

	Optional<OrganizationEntity> findByName(String name);
}
