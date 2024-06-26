package com.seuportfolio.registryapi.modules.user.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import com.seuportfolio.registryapi.tests.httpFactories.CreateUserFactory;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RefreshTokenTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should refresh tokens")
	void refreshTokenSuccessCase() throws Exception {
		var mapper = new ObjectMapper();
		ResultActions createUserResult = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);

		Cookie cookie = createUserResult
			.andReturn()
			.getResponse()
			.getCookie("refresh-token");
		ResultActions refreshTokensResult =
			this.mockMvc.perform(post("/auth/refresh-tokens").cookie(cookie));

		refreshTokensResult
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.access_token").isString())
			.andExpect(header().exists("set-cookie"));
	}

	@Test
	@DisplayName("it should throw unauthorized")
	void unauthorizedCase() throws Exception {
		ResultActions refreshTokens =
			this.mockMvc.perform(post("/auth/refresh-tokens"));
		refreshTokens.andExpect(status().isForbidden());
	}
}
