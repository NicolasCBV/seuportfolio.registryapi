package com.seuportfolio.registryapi.modules.globals.repositories;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseContentRepo
	extends JpaRepository<BaseContentEntity, UUID> {
	Optional<BaseContentEntity> findByName(String name);
}
