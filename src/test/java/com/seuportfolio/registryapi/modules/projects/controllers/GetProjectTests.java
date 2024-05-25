package com.seuportfolio.registryapi.modules.projects.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import com.seuportfolio.registryapi.tests.commonFactories.CreateProjectFactory;
import com.seuportfolio.registryapi.tests.httpFactories.CreateUserFactory;
import com.seuportfolio.registryapi.utils.jwt.JWTDecoder;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GetProjectTests {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should to get one project")
	@Transactional
	void getProjectsSuccessCase() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ResultActions createUserResult = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
		var createUserResultJson = createUserResult
			.andReturn()
			.getResponse()
			.getContentAsString();
		var parsedCreateUserBody = mapper.readValue(
			createUserResultJson,
			LoginResponseDTO.class
		);

		TokenPayloadEntity decodedToken = JWTDecoder.decode(
			parsedCreateUserBody.getAccessToken(),
			mapper
		);
		Optional<UserEntity> optUser =
			this.userRepo.findByEmail(decodedToken.getSub());
		assertThat(optUser.isEmpty()).isFalse();

		var baseContentProject = CreateProjectFactory.create(
			this.entityManager,
			this.baseContentRepo,
			optUser.get()
		);

		ResultActions finalResult =
			this.mockMvc.perform(
					get("/project/" + baseContentProject.getId()).header(
						"Authorization",
						"Bearer " + parsedCreateUserBody.getAccessToken()
					)
				);

		finalResult
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.project.id").isString())
			.andExpect(jsonPath("$.project.name").isString())
			.andExpect(jsonPath("$.project.description").isString())
			.andExpect(jsonPath("$.project.image_url").isString())
			.andExpect(jsonPath("$.project.created_at").isString())
			.andExpect(jsonPath("$.project.updated_at").isString())
			.andExpect(jsonPath("$.project.state").isString())
			.andExpect(jsonPath("$.project.tags").isArray())
			.andExpect(jsonPath("$.project.tags[0].id").isString())
			.andExpect(jsonPath("$.project.tags[0].name").isString())
			.andExpect(jsonPath("$.project.tags[0].created_at").isString());
	}

	@Test
	@DisplayName("it should throw bad request because base content is wrong")
	void badRequestCase() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		ResultActions createUserResult = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
		var createUserResultJson = createUserResult
			.andReturn()
			.getResponse()
			.getContentAsString();
		var parsedCreateUserBody = mapper.readValue(
			createUserResultJson,
			LoginResponseDTO.class
		);

		ResultActions resultActions =
			this.mockMvc.perform(
					get("/project/wrong-id").header(
						"Authorization",
						"Bearer " + parsedCreateUserBody.getAccessToken()
					)
				);

		resultActions.andExpect(status().isBadRequest());
	}
}
