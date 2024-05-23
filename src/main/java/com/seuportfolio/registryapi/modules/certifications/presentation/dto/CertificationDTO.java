package com.seuportfolio.registryapi.modules.certifications.presentation.dto;

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
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder(
	{
		"id",
		"organization_id",
		"name",
		"image_url",
		"description",
		"code",
		"link",
		"issued_at",
		"created_at",
		"updated_at",
		"tags",
	}
)
public class CertificationDTO {

	private String id;

	@JsonProperty("organization_id")
	private String organizationId;

	private String name;

	@JsonProperty("image_url")
	private String imageUrl;

	private String description;
	private String code;
	private String link;

	@JsonProperty("issued_at")
	private LocalDateTime issuedAt;

	@JsonProperty("created_at")
	private LocalDateTime createdAt;

	@JsonProperty("updated_at")
	private LocalDateTime updatedAt;

	private List<TagEntity> tags;
}
