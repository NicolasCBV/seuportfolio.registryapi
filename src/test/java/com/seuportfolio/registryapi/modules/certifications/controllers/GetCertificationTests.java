package com.seuportfolio.registryapi.modules.certifications.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import com.seuportfolio.registryapi.tests.commonFactories.CreateCertFactory;
import com.seuportfolio.registryapi.tests.httpFactories.CreateUserFactory;
import com.seuportfolio.registryapi.utils.jwt.JWTDecoder;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
public class GetCertificationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to get one certification")
	@Transactional
	void getCertificationSuccessCase() throws Exception {
		var mapper = new ObjectMapper();

		ResultActions createUserRes = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
		String createUserResJson = createUserRes
			.andReturn()
			.getResponse()
			.getContentAsString();
		LoginResponseDTO parsedCreateUserResJson = mapper.readValue(
			createUserResJson,
			LoginResponseDTO.class
		);

		var token = JWTDecoder.decode(
			parsedCreateUserResJson.getAccessToken(),
			mapper
		);
		BaseContentEntity certBaseContent = CreateCertFactory.create(
			this.entityManager,
			this.userRepo,
			token.getSub()
		);

		ResultActions result =
			this.mockMvc.perform(
					get("/certification/" + certBaseContent.getId()).header(
						"Authorization",
						"Bearer " + parsedCreateUserResJson.getAccessToken()
					)
				);

		result
			.andExpect(jsonPath("$.certification.id").isString())
			.andExpect(jsonPath("$.certification.organization_id").isString())
			.andExpect(jsonPath("$.certification.name").isString())
			.andExpect(jsonPath("$.certification.image_url").isString())
			.andExpect(jsonPath("$.certification.description").isString())
			.andExpect(jsonPath("$.certification.code").isString())
			.andExpect(jsonPath("$.certification.link").isString())
			.andExpect(jsonPath("$.certification.issued_at").isString())
			.andExpect(jsonPath("$.certification.created_at").isString())
			.andExpect(jsonPath("$.certification.updated_at").isString())
			.andExpect(jsonPath("$.certification.tags").isArray())
			.andExpect(jsonPath("$.certification.tags[0].id").isString())
			.andExpect(jsonPath("$.certification.tags[0].name").isString())
			.andExpect(
				jsonPath("$.certification.tags[0].created_at").isString()
			);
	}

	@Test
	@DisplayName(
		"it should be throw bad request because base content id is wrong"
	)
	@Transactional
	void badRequestCase() throws Exception {
		var mapper = new ObjectMapper();

		ResultActions createUserRes = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
		String createUserResJson = createUserRes
			.andReturn()
			.getResponse()
			.getContentAsString();
		LoginResponseDTO parsedCreateUserResJson = mapper.readValue(
			createUserResJson,
			LoginResponseDTO.class
		);

		ResultActions result =
			this.mockMvc.perform(
					get("/certification/wrong-id").header(
						"Authorization",
						"Bearer " + parsedCreateUserResJson.getAccessToken()
					)
				);

		result.andExpect(status().isBadRequest());
	}
}
