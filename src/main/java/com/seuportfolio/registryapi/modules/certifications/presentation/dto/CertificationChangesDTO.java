package com.seuportfolio.registryapi.modules.certifications.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seuportfolio.registryapi.utils.jakarta.validation.UUID;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationChangesDTO {

	@UUID
	@JsonProperty("organization_id")
	private String organizationId;

	@Size(
		min = 2,
		message = "O nome do certificado precisa ter no mínimo 2 caracteres"
	)
	@Size(
		max = 64,
		message = "O nome do certificado precisa ter menos que 65 caracteres"
	)
	private String name;

	@Size(
		min = 10,
		message = "A descrição do certificado precisa conter no mínimo 10 caracteres"
	)
	@Size(
		max = 120,
		message = "A descrição do certificado precisa ser menor que 121 caracteres"
	)
	private String description;

	@Size(max = 64, message = "O código precisa ter menos que 65 caracteres")
	private String code;

	@Size(max = 255, message = "O link precisa ter menos que 256 caracteres")
	private String link;

	@DateTimeFormat(style = "dd-MM-yyyy")
	@JsonProperty("issued_at")
	private LocalDateTime issuedAt;
}
