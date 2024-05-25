package com.seuportfolio.registryapi.modules.projects.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.CreateProjectDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import com.seuportfolio.registryapi.tests.httpFactories.CreateUserFactory;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
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
public class CreateProjectTests {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to create a project")
	void createProjectSuccessCase() throws Exception {
		var mapper = new ObjectMapper();
		ResultActions createUserRes = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
		String strContent = createUserRes
			.andReturn()
			.getResponse()
			.getContentAsString();

		LoginResponseDTO parsedCreateUserBody = mapper.readValue(
			strContent,
			LoginResponseDTO.class
		);

		List<String> tagList = new ArrayList<String>(1);
		tagList.add("simple tag");
		var requestBody = CreateProjectDTO.builder()
			.name("project")
			.description("description")
			.state("finished")
			.tags(tagList)
			.build();
		String requestBodyJson = mapper.writeValueAsString(requestBody);

		ResultActions finalResult =
			this.mockMvc.perform(
					post("/project")
						.contentType(MediaType.APPLICATION_JSON)
						.header(
							"Authorization",
							"Bearer " + parsedCreateUserBody.getAccessToken()
						)
						.content(requestBodyJson)
				);

		finalResult.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("it should throw bad request because body is a empty json")
	void createUserBadRequestCase() throws Exception {
		var mapper = new ObjectMapper();
		ResultActions createProjectRes = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
		String strContent = createProjectRes
			.andReturn()
			.getResponse()
			.getContentAsString();

		LoginResponseDTO parsedCreateUserBody = mapper.readValue(
			strContent,
			LoginResponseDTO.class
		);

		ResultActions result =
			this.mockMvc.perform(
					post("/project")
						.contentType(MediaType.APPLICATION_JSON)
						.header(
							"Authorization",
							"Bearer " + parsedCreateUserBody.getAccessToken()
						)
						.content("{}")
				);
		result.andExpect(status().isBadRequest());
	}
}
