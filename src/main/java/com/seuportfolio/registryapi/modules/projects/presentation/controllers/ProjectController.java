package com.seuportfolio.registryapi.modules.projects.presentation.controllers;

import com.seuportfolio.registryapi.modules.projects.presentation.dto.CreateProjectDTO;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.GetProjectResponseDTO;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.GetProjectsResponseDTO;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.ProjectDTO;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.UpdateProjectDTO;
import com.seuportfolio.registryapi.modules.projects.useCases.CreateProjectUseCase;
import com.seuportfolio.registryapi.modules.projects.useCases.DeleteProjectUseCase;
import com.seuportfolio.registryapi.modules.projects.useCases.GetProjectUseCase;
import com.seuportfolio.registryapi.modules.projects.useCases.GetProjectsUseCase;
import com.seuportfolio.registryapi.modules.projects.useCases.UpdateProjectUseCase;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.jakarta.validation.UUIDParameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
@RequestMapping("/project")
public class ProjectController {

	@Autowired
	private CreateProjectUseCase createProjectUseCase;

	@Autowired
	private GetProjectsUseCase getProjectsUseCase;

	@Autowired
	private GetProjectUseCase getProjectUseCase;

	@Autowired
	private DeleteProjectUseCase deleteProjectUseCase;

	@Autowired
	private UpdateProjectUseCase updateProjectUseCase;

	@GetMapping
	public ResponseEntity<GetProjectsResponseDTO> getProjects(
		@PositiveOrZero @RequestParam("offset") int offset
	) {
		var user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		List<ProjectDTO> projectList =
			this.getProjectsUseCase.exec(offset, user);
		return ResponseEntity.ok()
			.body(new GetProjectsResponseDTO(projectList));
	}

	@GetMapping("{base_content_id}")
	public ResponseEntity<GetProjectResponseDTO> getProject(
		@Valid @PathVariable(
			"base_content_id"
		) @UUIDParameter String baseContentId
	) {
		var user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		Optional<ProjectDTO> optProject =
			this.getProjectUseCase.exec(baseContentId, user);
		var resBody = GetProjectResponseDTO.builder()
			.project(optProject)
			.build();
		return ResponseEntity.ok().body(resBody);
	}

	@PostMapping
	public ResponseEntity<Object> create(
		@Valid @RequestBody CreateProjectDTO dto
	) {
		var user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		this.createProjectUseCase.exec(dto, user);

		URI location = URI.create("http://localhost/project/id");
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
		this.deleteProjectUseCase.exec(user.getId().toString(), baseContentId);

		return ResponseEntity.noContent().build();
	}

	@PatchMapping("{base_content_id}")
	public ResponseEntity<Object> update(
		@Valid @UUIDParameter @PathVariable(
			"base_content_id"
		) String baseContentId,
		@Valid @RequestBody UpdateProjectDTO dto
	) throws UseCaseException {
		var user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		this.updateProjectUseCase.exec(
				UUID.fromString(baseContentId),
				user,
				dto
			);

		return ResponseEntity.ok().build();
	}
}
