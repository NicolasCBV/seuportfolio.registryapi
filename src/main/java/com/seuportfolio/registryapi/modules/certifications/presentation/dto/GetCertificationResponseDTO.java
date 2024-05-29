package com.seuportfolio.registryapi.modules.certifications.presentation.dto;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GetCertificationResponseDTO {

	private Optional<CertificationDTO> certification;
}
