package com.seuportfolio.registryapi.modules.user.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;

import jakarta.servlet.http.Cookie;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RefreshTokenTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	void flushAll() {
		this.userRepo.deleteAll();
	}

	@Test
	@DisplayName("it should refresh tokens")
	void refreshTokenSuccessCase() throws Exception {
		var mapper = new ObjectMapper();
		ResultActions createUserResult = this.createUser(mapper);

		Cookie cookie = createUserResult.andReturn().getResponse().getCookie("refresh-token");
		ResultActions refreshTokensResult = this.mockMvc.perform(post("/auth/refresh-tokens")
				.cookie(cookie));

		refreshTokensResult.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accessToken").isString())
			.andExpect(header().exists("set-cookie"));
	}

	@Test
	@DisplayName("it should throw unauthorized")
	void unauthorizedCase() throws Exception {
		ResultActions refreshTokens = this.mockMvc.perform(post("/auth/refresh-tokens"));
		refreshTokens.andExpect(status().isForbidden());
	}

	private ResultActions createUser(ObjectMapper mapper) throws Exception {
		var body = CreateUserDTO.builder()
			.email("johndoe@email.com")
			.password("123456")
			.fullName("John Doe")
			.build();

		var json = mapper.writeValueAsString(body);

		ResultActions result = this.mockMvc.perform(post("/user")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON));

		result.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accessToken").isString())
			.andExpect(header().exists("set-cookie"));

		return result;
	}
}
