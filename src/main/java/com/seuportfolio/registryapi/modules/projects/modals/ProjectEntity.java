package com.seuportfolio.registryapi.modules.projects.modals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "projects")
public class ProjectEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "image_url", nullable = true, length = 255)
	@JsonProperty("image_url")
	private String imageUrl;

	@Column(nullable = false)
	private short state;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToOne(optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "base_content_id", unique = true, nullable = false)
	@JsonProperty("base_content_id")
	private BaseContentEntity baseContentEntity;
}
