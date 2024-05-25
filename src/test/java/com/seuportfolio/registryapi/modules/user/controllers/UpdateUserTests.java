package com.seuportfolio.registryapi.modules.user.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.UpdateUserDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import com.seuportfolio.registryapi.tests.httpFactories.CreateUserFactory;
import com.seuportfolio.registryapi.utils.jwt.JWTDecoder;
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
public class UpdateUserTests {

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
	@DisplayName("it should be able to update user")
	void updateUserSuccessCase() throws Exception {
		var mapper = new ObjectMapper();
		var createUserRes = CreateUserFactory.create(mapper, this.mockMvc);
		var createUserJson = createUserRes
			.andReturn()
			.getResponse()
			.getContentAsString();
		var loginBody = mapper.readValue(
			createUserJson,
			LoginResponseDTO.class
		);
		String accessToken = loginBody.getAccessToken();

		String newFullName = "new full name";
		String newDescription = "new description";

		var body = UpdateUserDTO.builder()
			.fullName(newFullName)
			.description(newDescription)
			.build();

		String json = mapper.writeValueAsString(body);

		ResultActions res =
			this.mockMvc.perform(
					patch("/user")
						.content(json)
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + accessToken)
				);

		res
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.access_token").isString())
			.andExpect(header().exists("set-cookie"));

		String jsonRes = res.andReturn().getResponse().getContentAsString();
		LoginResponseDTO resBody = mapper.readValue(
			jsonRes,
			LoginResponseDTO.class
		);

		TokenPayloadEntity payload = JWTDecoder.decode(
			resBody.getAccessToken(),
			mapper
		);

		assertThat(payload.getFullName()).isEqualTo(newFullName);
		assertThat(payload.getDescription()).isEqualTo(newDescription);
	}

	@Test
	@DisplayName("it should throw a bad request")
	void badRequestCase() throws Exception {
		var mapper = new ObjectMapper();
		var createUserRes = CreateUserFactory.create(mapper, this.mockMvc);
		var createUserJson = createUserRes
			.andReturn()
			.getResponse()
			.getContentAsString();
		var loginBody = mapper.readValue(
			createUserJson,
			LoginResponseDTO.class
		);
		String accessToken = loginBody.getAccessToken();

		ResultActions res =
			this.mockMvc.perform(
					patch("/user")
						.contentType(MediaType.APPLICATION_JSON)
						.header("Authorization", "Bearer " + accessToken)
				);

		res.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("it should throw a unauthorized")
	void unauthorizedCase() throws Exception {
		ResultActions res =
			this.mockMvc.perform(
					patch("/user").contentType(MediaType.APPLICATION_JSON)
				);

		res.andExpect(status().isForbidden());
	}
}
