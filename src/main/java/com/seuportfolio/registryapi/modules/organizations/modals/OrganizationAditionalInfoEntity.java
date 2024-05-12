package com.seuportfolio.registryapi.modules.organizations.modals;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "organization_aditional_infos")
public class OrganizationAditionalInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "site_url")
	@JsonProperty("site_url")
	private String siteUrl;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToOne(
		fetch = FetchType.LAZY,
		optional = false,
		cascade = CascadeType.ALL
	)
	@JoinColumn(name = "base_content_id")
	@JsonBackReference
	@JsonProperty("base_content")
	private BaseContentEntity baseContentEntity;
}
