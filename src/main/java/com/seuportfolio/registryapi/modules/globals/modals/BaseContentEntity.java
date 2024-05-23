package com.seuportfolio.registryapi.modules.globals.modals;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.seuportfolio.registryapi.modules.certifications.modals.CertificationEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
	uniqueConstraints = @UniqueConstraint(
		columnNames = { "name", "user_id" },
		name = "UQ_base_content_name_user_id"
	)
)
@Entity(name = "base_contents")
public class BaseContentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(length = 64, nullable = false)
	private String name;

	@Column(length = 120, nullable = false)
	private String description;

	@CreationTimestamp(source = SourceType.DB)
	@Column(name = "created_at", nullable = false, updatable = false)
	@JsonProperty("created_at")
	private LocalDateTime createdAt;

	@UpdateTimestamp(source = SourceType.DB)
	@Column(name = "updated_at", nullable = false)
	@JsonProperty("updated_at")
	private LocalDateTime updatedAt;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonBackReference
	private UserEntity userEntity;

	@Column(nullable = false, columnDefinition = "SMALLINT")
	private short category;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "baseContentEntity", cascade = CascadeType.ALL)
	@JsonProperty("tags")
	@JsonManagedReference
	private List<TagEntity> tagEntity;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToOne(mappedBy = "baseContentEntity", cascade = CascadeType.ALL)
	@JsonProperty("certification_infos")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private CertificationEntity certificationEntity;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToOne(mappedBy = "root", cascade = CascadeType.ALL, optional = true)
	@JsonProperty("package_owner")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private PackageEntity ownerOf;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@ManyToOne(cascade = CascadeType.ALL, optional = true)
	@JoinColumn(name = "package_id", nullable = true)
	@JsonProperty("linked_on")
	@JsonBackReference
	@OnDelete(action = OnDeleteAction.CASCADE)
	private PackageEntity linkedOn;

	public Optional<PackageEntity> getOwnerOf() {
		return Optional.ofNullable(this.ownerOf);
	}

	public Optional<PackageEntity> getLinkedOn() {
		return Optional.ofNullable(this.linkedOn);
	}
}
