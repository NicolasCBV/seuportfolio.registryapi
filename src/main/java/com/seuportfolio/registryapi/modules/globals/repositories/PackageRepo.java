package com.seuportfolio.registryapi.modules.globals.repositories;

import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepo extends JpaRepository<PackageEntity, UUID> {}
