package com.seuportfolio.registryapi.modules.globals.repositories;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BaseContentRepo
	extends JpaRepository<BaseContentEntity, UUID> {
	Optional<BaseContentEntity> findByName(String name);

	@Query(
		"SELECT b " +
		"FROM base_contents b " +
		"WHERE b.userEntity.id = :user_id AND b.id = :id AND b.category = :category"
	)
	Optional<BaseContentEntity> findByUserIdAndIdAndCategory(
		@Param("user_id") UUID userId,
		@Param("id") UUID id,
		@Param("category") short category
	);

	@Query(
		"SELECT b " +
		"FROM base_contents b " +
		"WHERE b.userEntity.id = :user_id AND category = :category " +
		"ORDER BY b.name " +
		"LIMIT :limit OFFSET :offset"
	)
	List<BaseContentEntity> getBaseContentCollection(
		@Param("user_id") UUID userId,
		@Param("limit") int limit,
		@Param("offset") int offset,
		@Param("category") short category
	);

	@Modifying(clearAutomatically = true)
	@Query(
		"UPDATE base_contents b " +
		"SET name = :name, description = :description " +
		"WHERE b.id = :base_content_id AND b.category = :category"
	)
	@Transactional
	int updateBaseContent(
		@Param("name") String name,
		@Param("description") String description,
		@Param("base_content_id") UUID organizationId,
		@Param("category") short category
	);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(
		"DELETE FROM base_contents b " +
		"WHERE id = :base_content_id AND userEntity.id = :user_id AND category = :category"
	)
	int safeDelete(
		@Param("base_content_id") UUID baseContentId,
		@Param("user_id") UUID userId,
		@Param("category") short category
	);
}
