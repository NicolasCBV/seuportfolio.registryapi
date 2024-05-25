package com.seuportfolio.registryapi.modules.certifications.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
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
public class GetCertificationsTests {

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
	@DisplayName("it should be able to get certificates")
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
		CreateCertFactory.create(
			this.entityManager,
			this.userRepo,
			token.getSub()
		);

		ResultActions result =
			this.mockMvc.perform(
					get("/certification?offset=0").header(
						"Authorization",
						"Bearer " + parsedCreateUserResJson.getAccessToken()
					)
				);

		result
			.andExpect(jsonPath("$.certifications").isArray())
			.andExpect(jsonPath("$.certifications[0].id").isString())
			.andExpect(
				jsonPath("$.certifications[0].organization_id").isString()
			)
			.andExpect(jsonPath("$.certifications[0].name").isString())
			.andExpect(jsonPath("$.certifications[0].image_url").isString())
			.andExpect(jsonPath("$.certifications[0].description").isString())
			.andExpect(jsonPath("$.certifications[0].code").isString())
			.andExpect(jsonPath("$.certifications[0].link").isString())
			.andExpect(jsonPath("$.certifications[0].issued_at").isString())
			.andExpect(jsonPath("$.certifications[0].created_at").isString())
			.andExpect(jsonPath("$.certifications[0].updated_at").isString())
			.andExpect(jsonPath("$.certifications[0].tags").isArray())
			.andExpect(jsonPath("$.certifications[0].tags[0].id").isString())
			.andExpect(jsonPath("$.certifications[0].tags[0].name").isString())
			.andExpect(
				jsonPath("$.certifications[0].tags[0].created_at").isString()
			);
	}
}
