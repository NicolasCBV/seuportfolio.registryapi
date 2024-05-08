package com.seuportfolio.registryapi.modules.organizations.presentation.controllers;

import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationEntity;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.CreateOrganizationDTO;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.GetOrganizationsResponseDTO;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.UpdateOrganizationDTO;
import com.seuportfolio.registryapi.modules.organizations.useCases.CreateOrganizationUseCase;
import com.seuportfolio.registryapi.modules.organizations.useCases.DeleteOrganizationUseCase;
import com.seuportfolio.registryapi.modules.organizations.useCases.GetOrganizationsUseCase;
import com.seuportfolio.registryapi.modules.organizations.useCases.UpdateOrganizationUseCase;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.jakarta.validation.UUIDParameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.util.List;
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
@RequestMapping("/organization")
public class OrganizationController {

	@Autowired
	private CreateOrganizationUseCase createOrganizationUseCase;

	@Autowired
	private GetOrganizationsUseCase getOrganizationsUseCase;

	@Autowired
	private DeleteOrganizationUseCase deleteOrganizationUseCase;

	@Autowired
	private UpdateOrganizationUseCase updateOrganizationUseCase;

	@PatchMapping
	public ResponseEntity<Object> update(
		@Valid @RequestBody UpdateOrganizationDTO dto
	) throws UseCaseException {
		this.updateOrganizationUseCase.exec(dto);
		return ResponseEntity.ok().build();
	}

	@PostMapping
	public ResponseEntity<Object> create(
		@Valid @RequestBody CreateOrganizationDTO dto
	) {
		var user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		this.createOrganizationUseCase.exec(dto, user);

		URI location = URI.create("http://localhost/organizations?offset=0");
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/{organization_id}")
	public ResponseEntity<Object> delete(
		@Valid @UUIDParameter @PathVariable(
			"organization_id"
		) String organizationId
	) {
		this.deleteOrganizationUseCase.exec(
				java.util.UUID.fromString(organizationId)
			);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<GetOrganizationsResponseDTO> getOrgs(
		@RequestParam("offset") @PositiveOrZero int offset
	) {
		UserEntity user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		List<OrganizationEntity> orgs =
			this.getOrganizationsUseCase.exec(offset, user);
		GetOrganizationsResponseDTO body = GetOrganizationsResponseDTO.builder()
			.organizations(orgs)
			.build();

		return ResponseEntity.ok().body(body);
	}
}
