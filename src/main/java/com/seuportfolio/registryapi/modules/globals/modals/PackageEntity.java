package com.seuportfolio.registryapi.modules.globals.modals;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "packages")
public class PackageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private short type;

	@CreationTimestamp(source = SourceType.DB)
	@JsonProperty("created_at")
	private LocalDateTime createdAt;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "root_id", nullable = false, unique = true)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private BaseContentEntity root;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToMany(mappedBy = "linkedOn", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<BaseContentEntity> baseContentEntities;
}
