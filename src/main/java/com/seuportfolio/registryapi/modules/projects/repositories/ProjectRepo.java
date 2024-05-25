package com.seuportfolio.registryapi.modules.projects.repositories;

import com.seuportfolio.registryapi.modules.projects.modals.ProjectEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepo extends JpaRepository<ProjectEntity, UUID> {}
