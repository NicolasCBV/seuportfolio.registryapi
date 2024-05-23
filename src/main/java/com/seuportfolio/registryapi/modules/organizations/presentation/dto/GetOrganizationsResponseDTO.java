package com.seuportfolio.registryapi.modules.organizations.presentation.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetOrganizationsResponseDTO {

	private List<OrganizationDTO> organizations;
}
