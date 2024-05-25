package com.seuportfolio.registryapi.modules.organizations.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import com.seuportfolio.registryapi.tests.commonFactories.CreateOrgFactory;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GetOrganizationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	void flushAll() {
		this.userRepo.deleteAll();
		this.baseContentRepo.deleteAll();
	}

	@Test
	@DisplayName("it should be able to get one organization")
	@Transactional
	void getOrgs() throws Exception {
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

		String token = parsedCreateUserBody.getAccessToken();
		TokenPayloadEntity decodedToken = JWTDecoder.decode(token, mapper);

		Optional<UserEntity> optUser =
			this.userRepo.findByEmail(decodedToken.getSub());
		assertThat(optUser.isEmpty()).isFalse();

		var baseContentOrg = CreateOrgFactory.create(
			this.entityManager,
			this.baseContentRepo,
			optUser.get()
		);

		ResultActions result =
			this.mockMvc.perform(
					get("/organization/" + baseContentOrg.getId()).header(
						"Authorization",
						"Bearer " + token
					)
				);

		result
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.organization.id").isString())
			.andExpect(jsonPath("$.organization.name").isString())
			.andExpect(jsonPath("$.organization.description").isString())
			.andExpect(jsonPath("$.organization.created_at").isString())
			.andExpect(jsonPath("$.organization.updated_at").isString())
			.andExpect(jsonPath("$.organization.tags").isArray())
			.andExpect(jsonPath("$.organization.tags[0].id").isString())
			.andExpect(jsonPath("$.organization.tags[0].name").isString())
			.andExpect(
				jsonPath("$.organization.tags[0].created_at").isString()
			);
	}

	@Test
	@DisplayName("it should throw bad request because base content id is wrong")
	@Transactional
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
		String token = parsedCreateUserBody.getAccessToken();

		ResultActions result =
			this.mockMvc.perform(
					get("/organization/wrong-id").header(
						"Authorization",
						"Bearer " + token
					)
				);

		result.andExpect(status().isBadRequest());
	}
}
