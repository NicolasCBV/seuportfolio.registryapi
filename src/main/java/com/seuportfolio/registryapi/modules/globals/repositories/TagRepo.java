package com.seuportfolio.registryapi.modules.globals.repositories;

import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepo extends JpaRepository<TagEntity, UUID> {
	int deleteByName(String name);
	Optional<TagEntity> findByName(String name);
}
