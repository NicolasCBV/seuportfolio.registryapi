package com.seuportfolio.registryapi.modules.globals.modals;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
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

	@CreationTimestamp
	@Column(name = "created_at", nullable = false)
	@JsonProperty("created_at")
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at", nullable = false)
	@JsonProperty("updated_at")
	private LocalDateTime updatedAt;

	@ManyToOne(targetEntity = UserEntity.class, cascade = { CascadeType.MERGE })
	@JoinColumn(name = "user_id", nullable = false)
	@JsonBackReference
	private UserEntity userEntity;

	@Column(nullable = false, columnDefinition = "SMALLINT")
	private short category;

	@OneToMany(
		mappedBy = "baseContentEntity",
		cascade = { CascadeType.REMOVE, CascadeType.REFRESH }
	)
	@JsonProperty("tags")
	@JsonManagedReference
	private List<TagEntity> tagEntity;
}
