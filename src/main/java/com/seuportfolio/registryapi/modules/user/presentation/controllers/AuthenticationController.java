package com.seuportfolio.registryapi.modules.user.presentation.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.useCases.RefreshTokenUseCase;
import com.seuportfolio.registryapi.modules.user.useCases.TokenUseCase;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenUseCase tokenUseCase;

	@Autowired
	private RefreshTokenUseCase refreshTokenUseCase;

	@PostMapping("/login")
	public LoginResponseDTO login(
		@RequestBody @Valid LoginDTO body,
		HttpServletResponse res
	) {
		var usernamePassword = new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());
		var auth = this.authenticationManager.authenticate(usernamePassword);
		UserEntity user = (UserEntity) auth.getPrincipal();

		var accessToken = this.tokenUseCase.gen(user);
		var refreshTokenCookie = this.refreshTokenUseCase.gen(user);

		var resBody = new LoginResponseDTO(accessToken);
		
		res.addCookie(refreshTokenCookie);
		res.setStatus(201);
		
		return resBody;
	}

	@PostMapping("/refresh-tokens")
	public LoginResponseDTO login(HttpServletResponse res, @CookieValue("refresh-token") Optional<String> cookieValue) {
		if(cookieValue.isEmpty())
			throw new BadCredentialsException("Refresh token was not found");

		var user = this.refreshTokenUseCase.validate(cookieValue.get());
		var accessToken = this.tokenUseCase.gen(user);
		var refreshTokenCookie = this.refreshTokenUseCase.gen(user);

		var resBody = new LoginResponseDTO(accessToken);

		res.addCookie(refreshTokenCookie);
		res.setStatus(201);

		return resBody;
	}
}
