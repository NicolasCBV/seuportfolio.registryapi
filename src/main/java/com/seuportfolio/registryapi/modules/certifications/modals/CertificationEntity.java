package com.seuportfolio.registryapi.modules.certifications.modals;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
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
@Entity(name = "certifications")
public class CertificationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(length = 64, nullable = true)
	private String code;

	@Column(length = 255, nullable = true)
	private String link;

	@Column(name = "image_url", length = 255, nullable = true)
	@JsonProperty("image_url")
	private String imageUrl;

	@Column(name = "issued_at", nullable = false)
	@JsonProperty("issued_at")
	private LocalDateTime issuedAt;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@OneToOne(optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "base_content_id", unique = true, nullable = false)
	@JsonProperty("base_content_id")
	private BaseContentEntity baseContentEntity;
}
