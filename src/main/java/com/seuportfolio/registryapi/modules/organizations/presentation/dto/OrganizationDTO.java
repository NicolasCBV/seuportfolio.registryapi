package com.seuportfolio.registryapi.modules.organizations.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder(
	{
		"id",
		"name",
		"description",
		"site_url",
		"created_at",
		"updated_at",
		"tags",
	}
)
public class OrganizationDTO {

	private String id;
	private String name;
	private String description;

	@JsonProperty("created_at")
	private LocalDateTime createdAt;

	@JsonProperty("updated_at")
	private LocalDateTime updatedAt;

	private List<TagEntity> tags;
}
