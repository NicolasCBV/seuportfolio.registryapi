package com.seuportfolio.registryapi.modules.user.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import com.seuportfolio.registryapi.tests.httpFactories.CreateUserFactory;
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
public class LoginTests {

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
	@DisplayName("it should be able to login")
	void loginSuccessCase() throws Exception {
		var mapper = new ObjectMapper();
		CreateUserFactory.create(mapper, this.mockMvc);

		var loginRequestBody = LoginDTO.builder()
			.email("johndoe@email.com")
			.password("123456")
			.build();
		String strReqBody = mapper.writeValueAsString(loginRequestBody);

		ResultActions loginResult =
			this.mockMvc.perform(
					post("/auth/login")
						.content(strReqBody)
						.contentType(MediaType.APPLICATION_JSON)
				);

		loginResult
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.access_token").isString())
			.andExpect(header().exists("set-cookie"));
	}

	@Test
	@DisplayName("it should throw unauthorized")
	void unauthorizedCase() throws Exception {
		var mapper = new ObjectMapper();
		var loginRequestBody = LoginDTO.builder()
			.email("johndoe@email.com")
			.password("123456")
			.build();

		String strReqBody = mapper.writeValueAsString(loginRequestBody);
		ResultActions loginResult =
			this.mockMvc.perform(
					post("/auth/login")
						.content(strReqBody)
						.contentType(MediaType.APPLICATION_JSON)
				);

		loginResult.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("it should throw bad request")
	void badRequestCase() throws Exception {
		ResultActions result =
			this.mockMvc.perform(
					post("/auth/login").contentType(MediaType.APPLICATION_JSON)
				);
		result.andExpect(status().isBadRequest());
	}
}
