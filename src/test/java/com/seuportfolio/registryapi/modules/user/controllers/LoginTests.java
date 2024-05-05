package com.seuportfolio.registryapi.modules.user.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTests {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	void flushAll() {
		this.userRepo.deleteAll();
	}

	@Test
	@DisplayName("it should be able to login")
	void loginSuccessCase() throws Exception {
		var mapper = new ObjectMapper();
		this.createUser(mapper);

		var loginRequestBody = LoginDTO.builder()
			.email("johndoe@email.com")
			.password("123456")
			.build();
		String strReqBody = mapper.writeValueAsString(loginRequestBody);

		ResultActions loginResult = this.mockMvc.perform(post("/auth/login")
				.content(strReqBody)
				.contentType(MediaType.APPLICATION_JSON));

		loginResult.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accessToken").isString())
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
		ResultActions loginResult = this.mockMvc.perform(post("/auth/login")
				.content(strReqBody)
				.contentType(MediaType.APPLICATION_JSON));

		loginResult.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("it should throw bad request")
	void badRequestCase() throws Exception {
		ResultActions result = this.mockMvc.perform(post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON));
		result.andExpect(status().isBadRequest());
	}

	private ResultActions createUser(ObjectMapper mapper) throws Exception {
		var body = CreateUserDTO.builder()
			.email("johndoe@email.com")
			.password("123456")
			.fullName("John Doe")
			.build();
		String json = mapper.writeValueAsString(body);

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