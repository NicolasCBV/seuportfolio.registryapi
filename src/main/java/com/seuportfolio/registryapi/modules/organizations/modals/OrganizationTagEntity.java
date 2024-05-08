package com.seuportfolio.registryapi.modules.organizations.modals;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "organization_tags")
@Table(
	uniqueConstraints = @UniqueConstraint(
		columnNames = { "name", "organization_id" },
		name = "UQ_organization_tags_organization_id_name"
	)
)
public class OrganizationTagEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(length = 60, nullable = false)
	private String name;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@ManyToOne(
		cascade = { CascadeType.MERGE },
		targetEntity = OrganizationEntity.class
	)
	@JoinColumn(name = "organization_id", nullable = false)
	@JsonBackReference
	private OrganizationEntity organizationEntity;
}
