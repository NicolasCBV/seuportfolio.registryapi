package com.seuportfolio.registryapi.modules.user.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

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
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DeleteUserTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	void flushAll() {
		this.userRepo.deleteAll();
	}

	@Test
	@DisplayName("it should be able to delete user")
	void deleteUserSuccessCase() throws Exception {
		var mapper = new ObjectMapper();

		ResultActions createUserResult = this.createUser(mapper);
		String strContent = createUserResult
			.andReturn().getResponse().getContentAsString();

		var body = mapper.readValue(strContent, LoginResponseDTO.class);
		ResultActions deleteUserResult = this.mockMvc
			.perform(delete("/user")
				.header("Authorization", "Bearer " + body.accessToken()));

		deleteUserResult.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("it should throw unauthorized")
	void unauthorizedCase() throws Exception {
		ResultActions result = this.mockMvc.perform(delete("/user"));
		result.andExpect(status().isForbidden());
	}

	private ResultActions createUser(ObjectMapper mapper) throws Exception {
		var body = CreateUserDTO.builder()
			.email("johndoe@email.com")
			.fullName("John Doe")
			.password("123456")
			.build();

		String json = mapper.writeValueAsString(body);

		ResultActions result = this.mockMvc
			.perform(post("/user")
				.content(json)
				.contentType(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accessToken").isString())
			.andExpect(header().exists("set-cookie"));

		return result;
	}
}
