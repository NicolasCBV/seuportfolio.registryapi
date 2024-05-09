package com.seuportfolio.registryapi.modules.globals.repositories;

import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepo extends JpaRepository<TagEntity, UUID> {
	@Modifying(clearAutomatically = true)
	@Query(
		"DELETE FROM tags t " +
		"WHERE t.baseContentEntity.id = :id AND t.name = :name"
	)
	void deleteByName(
		@Param("id") UUID organizationId,
		@Param("name") String name
	);
}
