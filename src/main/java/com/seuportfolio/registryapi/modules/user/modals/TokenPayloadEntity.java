package com.seuportfolio.registryapi.modules.user.modals;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenPayloadEntity {

	@JsonProperty("iss")
	String iss;

	@JsonProperty("sub")
	String sub;

	@JsonProperty("exp")
	Long exp;

	@JsonProperty("fullName")
	String fullName;

	@JsonProperty("description")
	String description;

	@JsonProperty("createdAt")
	String createdAt;

	@JsonProperty("updatedAt")
	String updatedAt;
}
