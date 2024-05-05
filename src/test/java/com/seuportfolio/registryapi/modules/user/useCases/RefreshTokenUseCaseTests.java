package com.seuportfolio.registryapi.modules.user.useCases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;

import jakarta.servlet.http.Cookie;

public class RefreshTokenUseCaseTests {
	@Mock
	private UserRepo userRepo;
	
    private RefreshTokenUseCase refreshTokenUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		this.refreshTokenUseCase = RefreshTokenUseCase.builder()
			.secret("SECRET")
			.hour(5)
			.apiName("registryapi")
			.domain("localhost")
			.secure(true)
			.httpOnly(true)
			.userRepo(this.userRepo)
			.build();
	}

	@Test
	@DisplayName("it should be able to gen refresh tokens")
	void genTokenSuccessCase() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();
		var optUser = Optional.of(user);

		Cookie cookie = this.refreshTokenUseCase.gen(user);

		assertThat(cookie.getName()).isEqualTo("refresh-token");
		
		String token = cookie.getValue();
		when(this.userRepo.findByEmail(user.getEmail())).thenReturn(optUser);
		assertThat(this.refreshTokenUseCase.validate(token))
			.isInstanceOf(UserEntity.class);
	}

	@Test
	@DisplayName("it should throw one error because token is invalid")
	void invalidTokenFailCase() {
		assertThrows(
			JWTVerificationException.class, 
			() -> this.refreshTokenUseCase.validate("wrong token")
		);
	}

	@Test
	@DisplayName("it should throw one error because user doesn't exist")
	void userNotFoundCase() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		Cookie cookie = this.refreshTokenUseCase.gen(user);
		String token = cookie.getValue();

		when(this.userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());

		assertThrows(
			UsernameNotFoundException.class,
			() -> this.refreshTokenUseCase.validate(token)
		);
	}
}
