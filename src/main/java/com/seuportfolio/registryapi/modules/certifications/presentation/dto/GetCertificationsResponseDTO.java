package com.seuportfolio.registryapi.modules.certifications.presentation.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCertificationsResponseDTO {

	private List<CertificationDTO> certifications;
}
