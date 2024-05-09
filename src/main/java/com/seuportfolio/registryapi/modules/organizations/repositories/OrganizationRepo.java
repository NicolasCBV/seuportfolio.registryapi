package com.seuportfolio.registryapi.modules.organizations.repositories;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganizationRepo
	extends JpaRepository<BaseContentEntity, UUID> {
	@Query(
		"SELECT b " +
		"FROM base_contents b " +
		"WHERE b.userEntity.id = :user_id AND category = 0 " +
		"ORDER BY b.name " +
		"LIMIT :limit OFFSET :offset"
	)
	List<BaseContentEntity> getOrganizations(
		@Param("user_id") UUID userId,
		@Param("limit") int limit,
		@Param("offset") int offset
	);

	@Modifying(clearAutomatically = true)
	@Query(
		"UPDATE base_contents b " +
		"SET name = :name, description = :description " +
		"WHERE b.id = :organization_id AND b.category = 0"
	)
	void updateOrganization(
		@Param("name") String name,
		@Param("description") String description,
		@Param("organization_id") UUID organizationId
	);
}
