package com.seuportfolio.registryapi.modules.user.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;
import java.util.Base64.Decoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.auth0.jwt.JWT;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.UpdateUserDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateUserTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	void flushAll() {
		this.userRepo.deleteAll();
	}

	@Test
	@DisplayName("it should be able to update user")
	void updateUserSuccessCase() throws Exception {
		String accessToken = this.createUser();

		String newFullName = "new full name";
		String newDescription = "new description";

		var body = UpdateUserDTO.builder()
			.fullName(newFullName)
			.description(newDescription)
			.build();

		var mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(body);

		ResultActions res = this.mockMvc.perform(patch("/user")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken));

		res.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accessToken").isString())
			.andExpect(header().exists("set-cookie"));

		String jsonRes = res.andReturn().getResponse().getContentAsString();
		LoginResponseDTO resBody = mapper.readValue(jsonRes, LoginResponseDTO.class);

		TokenPayloadEntity payload = this.decodeToken(mapper, resBody.accessToken());

		assertThat(payload.getFullName()).isEqualTo(newFullName);
		assertThat(payload.getDescription()).isEqualTo(newDescription);
	}

	@Test
	@DisplayName("it should throw a bad request")
	void badRequestCase() throws Exception {
		String accessToken = this.createUser();

		ResultActions res = this.mockMvc.perform(patch("/user")
				.contentType(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + accessToken));

		res.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("it should throw a unauthorized")
	void unauthorizedCase() throws Exception {
		ResultActions res = this.mockMvc.perform(patch("/user")
				.contentType(MediaType.APPLICATION_JSON));

		res.andExpect(status().isForbidden());
	}

	private TokenPayloadEntity decodeToken(ObjectMapper mapper, String token) throws Exception {
		String payload = JWT.decode(token).getPayload();
		Decoder base64Decoder = Base64.getUrlDecoder();
		String decodedPayload = new String(base64Decoder.decode(payload));

		var decodedToken = mapper.readValue(decodedPayload, TokenPayloadEntity.class);
		return decodedToken;
	}

	private String createUser() throws Exception {
		var body = CreateUserDTO.builder()
			.email("johndoe@email.com")
			.fullName("John Doe")
			.password("123456")
			.build();

		var mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(body);

		ResultActions res = this.mockMvc
			.perform(post("/user")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON));

		res.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accessToken").isString())
			.andExpect(header().exists("set-cookie"));

		String resBody = res.andReturn().getResponse().getContentAsString();
		LoginResponseDTO resJson = mapper.readValue(resBody, LoginResponseDTO.class);
		return resJson.accessToken();
	}
}
