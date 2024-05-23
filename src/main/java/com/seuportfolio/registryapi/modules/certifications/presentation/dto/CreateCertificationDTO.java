package com.seuportfolio.registryapi.modules.certifications.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seuportfolio.registryapi.utils.jakarta.validation.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCertificationDTO {

	@NotNull(message = "O id da organização não pode estar vázio")
	@JsonProperty("organization_id")
	@UUID
	private String organizationId;

	@NotNull(message = "O nome da organização não pode estar vázio")
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
	@NotNull(message = "A descrição do certificado não pode estar vázia")
	private String description;

	@Size(
		max = 64,
		message = "O código do certificado deve ser menor que 64 caracteres"
	)
	private String code;

	@Size(
		min = 12,
		message = "O link do certificado deve ter no mínimo 12 caracteres"
	)
	@Size(
		max = 255,
		message = "O link do certificado deve ter máximo 255 caracteres"
	)
	private String link;

	@NotNull(message = "A data de emissão não pode estar vázia")
	@DateTimeFormat(style = "dd-MM-yyyy")
	@JsonProperty("issued_at")
	private LocalDateTime issuedAt;

	@NotNull(message = "O certificado deve ter pelo menos 1 tag")
	private List<
		@Size(
			min = 2,
			max = 60,
			message = "As tags devem ter no mínimo 2 e no máximo 60 caracteres"
		) String
	> tags;
}
