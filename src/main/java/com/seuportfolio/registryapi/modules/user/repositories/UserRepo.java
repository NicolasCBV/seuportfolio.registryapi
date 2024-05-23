package com.seuportfolio.registryapi.modules.user.repositories;

import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepo extends JpaRepository<UserEntity, UUID> {
	List<UserEntity> findByFullName(String fullName);
	Optional<UserEntity> findByEmail(String email);

	@Modifying(clearAutomatically = true)
	@Query(
		"UPDATE users u SET u.fullName = :fullName, u.description = :description WHERE u.email = :email"
	)
	int updateByEmail(
		@Param("email") String email,
		@Param("fullName") String fullName,
		@Param("description") String description
	);
}
