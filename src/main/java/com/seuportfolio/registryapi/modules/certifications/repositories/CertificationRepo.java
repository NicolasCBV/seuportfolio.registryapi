package com.seuportfolio.registryapi.modules.certifications.repositories;

import com.seuportfolio.registryapi.modules.certifications.modals.CertificationEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepo
	extends JpaRepository<CertificationEntity, UUID> {}
