package com.seuportfolio.registryapi.modules.certifications.presentation.controllers;

import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CertificationDTO;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CreateCertificationDTO;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.GetCertificationResponseDTO;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.GetCertificationsResponseDTO;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.UpdateCertificationDTO;
import com.seuportfolio.registryapi.modules.certifications.useCases.CreateCertificationUseCase;
import com.seuportfolio.registryapi.modules.certifications.useCases.DeleteCertificationUseCase;
import com.seuportfolio.registryapi.modules.certifications.useCases.GetCertificationUseCase;
import com.seuportfolio.registryapi.modules.certifications.useCases.GetCertificationsUseCase;
import com.seuportfolio.registryapi.modules.certifications.useCases.UpdateCertificationUseCase;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.jakarta.validation.UUIDParameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("certification")
public class CertificationController {

	@Autowired
	private CreateCertificationUseCase createCertificationUseCase;

	@Autowired
	private GetCertificationsUseCase getCertificationsUseCase;

	@Autowired
	private GetCertificationUseCase getCertificationUseCase;

	@Autowired
	private DeleteCertificationUseCase deleteCertificationUseCase;

	@Autowired
	private UpdateCertificationUseCase updateCertificationUseCase;

	@GetMapping
	public ResponseEntity<GetCertificationsResponseDTO> get(
		@RequestParam("offset") @PositiveOrZero int offset
	) {
		var user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();

		List<CertificationDTO> certList =
			this.getCertificationsUseCase.exec(offset, user);
		var certs = GetCertificationsResponseDTO.builder()
			.certifications(certList)
			.build();

		return ResponseEntity.ok().body(certs);
	}

	@PostMapping
	public ResponseEntity<Object> create(
		@Valid @RequestBody CreateCertificationDTO dto
	) throws UseCaseException {
		var user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		this.createCertificationUseCase.exec(dto, user);
		URI location = URI.create("http://localhost:8080/certification/id");
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("{base_content_id}")
	public ResponseEntity<Object> delete(
		@Valid @UUIDParameter @PathVariable(
			"base_content_id"
		) String baseContentId
	) {
		var user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		this.deleteCertificationUseCase.exec(
				user.getId().toString(),
				baseContentId
			);

		return ResponseEntity.noContent().build();
	}

	@PatchMapping("{base_content_id}")
	public ResponseEntity<Object> update(
		@Valid @UUIDParameter @PathVariable(
			"base_content_id"
		) String baseContentId,
		@Valid @RequestBody UpdateCertificationDTO dto
	) throws UseCaseException {
		var user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		this.updateCertificationUseCase.exec(baseContentId, dto, user);

		return ResponseEntity.ok().build();
	}

	@GetMapping("{base_content_id}")
	public ResponseEntity<GetCertificationResponseDTO> get(
		@Valid @UUIDParameter @PathVariable(
			"base_content_id"
		) String baseContentId
	) {
		var user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		Optional<CertificationDTO> resBody =
			this.getCertificationUseCase.exec(baseContentId, user);
		var parsedResBody = GetCertificationResponseDTO.builder()
			.certification(resBody) // dispara um erro caso seja nulo
			.build();

		return ResponseEntity.ok().body(parsedResBody);
	}
}
