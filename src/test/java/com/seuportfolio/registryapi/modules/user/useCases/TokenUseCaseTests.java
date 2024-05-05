package com.seuportfolio.registryapi.modules.user.useCases;

import static org.assertj.core.api.Assertions.assertThat;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.time.LocalDateTime;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TokenUseCaseTests {

	private TokenUseCase tokenUseCase;

	@BeforeEach
	void setup() {
		this.tokenUseCase = TokenUseCase.builder()
			.apiName("registryapi")
			.secret("SECRET")
			.hour(5)
			.build();
	}

	@Test
	@DisplayName("it should be able to gen a access token")
	void genTokenSuccessCase()
		throws JsonMappingException, JsonProcessingException {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		String token = this.tokenUseCase.gen(user);

		this.tokenUseCase.validate(token);

		var decoder = Base64.getUrlDecoder();
		String payload = new String(
			decoder.decode(JWT.decode(token).getPayload())
		);

		var objectMapper = new ObjectMapper();
		var data = objectMapper.readValue(payload, TokenPayloadEntity.class);

		assertThat(data.getFullName().equals(user.getFullName())).isTrue();
		assertThat(data.getDescription() == user.getDescription()).isTrue();
	}
}
