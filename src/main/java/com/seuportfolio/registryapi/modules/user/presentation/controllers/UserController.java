package com.seuportfolio.registryapi.modules.user.presentation.controllers;

import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTOResponse;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.UpdateUserDTO;
import com.seuportfolio.registryapi.modules.user.useCases.CreateUserUseCase;
import com.seuportfolio.registryapi.modules.user.useCases.DeleteUserUseCase;
import com.seuportfolio.registryapi.modules.user.useCases.RefreshTokenUseCase;
import com.seuportfolio.registryapi.modules.user.useCases.TokenUseCase;
import com.seuportfolio.registryapi.modules.user.useCases.UpdateUserUseCase;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private CreateUserUseCase createUserUseCase;

	@Autowired
	private UpdateUserUseCase updateUserUseCase;

	@Autowired
	private DeleteUserUseCase deleteUserUseCase;

	@Autowired
	private TokenUseCase tokenUseCase;

	@Autowired
	private RefreshTokenUseCase refreshTokenUseCase;

	@PostMapping
	public CreateUserDTOResponse create(
		@Valid @RequestBody CreateUserDTO body,
		HttpServletResponse res
	) {
		UserEntity user = this.createUserUseCase.exec(body);

		var accessToken = this.tokenUseCase.gen(user);
		var refreshTokenCookie = this.refreshTokenUseCase.gen(user);
		res.addCookie(refreshTokenCookie);
		res.addHeader("location", "http://localhost/user/user_id");
		res.setStatus(201);

		return new CreateUserDTOResponse(accessToken);
	}

	@PatchMapping
	public LoginResponseDTO update(
		@Valid @RequestBody UpdateUserDTO body,
		HttpServletResponse res
	) {
		var user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		this.updateUserUseCase.exec(user, body);

		var accessToken = this.tokenUseCase.gen(user);
		var refreshTokenCookie = this.refreshTokenUseCase.gen(user);

		res.addCookie(refreshTokenCookie);

		return new LoginResponseDTO(accessToken);
	}

	@DeleteMapping
	public ResponseEntity<Object> delete() {
		var user = (UserEntity) SecurityContextHolder.getContext()
			.getAuthentication()
			.getPrincipal();
		this.deleteUserUseCase.exec(user);
		return ResponseEntity.noContent().build();
	}
}
