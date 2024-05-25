package com.seuportfolio.registryapi.modules.organizations.presentation.dto;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GetOrganizationResponseDTO {

	public Optional<OrganizationDTO> organization;
}
