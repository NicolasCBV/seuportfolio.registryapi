package com.seuportfolio.registryapi.modules.organizations.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOrganizationsResponseDTO {

	@JsonProperty("organizations")
	private List<BaseContentEntity> organizations;
}
