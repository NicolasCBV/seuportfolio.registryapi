package com.seuportfolio.registryapi.modules.user.useCases;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
			.description("simple description")
			.password("123456")
			.createdAt(LocalDateTime.now(ZoneOffset.UTC))
			.updatedAt(LocalDateTime.now(ZoneOffset.UTC))
			.build();

		String token = this.tokenUseCase.gen(user);

		this.tokenUseCase.validate(token);

		String payload = token.split("\\.", 4)[1];
		var decoder = Base64.getUrlDecoder();
		String decodedPayload = new String(
			decoder.decode(payload),
			StandardCharsets.UTF_8
		);

		var objectMapper = new ObjectMapper();
		var data = objectMapper.readValue(
			decodedPayload,
			TokenPayloadEntity.class
		);

		assertThat(data.getFullName().equals(user.getFullName())).isTrue();
		assertThat(
			data.getDescription().equals(user.getDescription())
		).isTrue();
	}
}
