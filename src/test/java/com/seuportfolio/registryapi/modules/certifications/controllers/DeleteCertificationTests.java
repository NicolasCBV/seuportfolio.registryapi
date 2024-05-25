package com.seuportfolio.registryapi.modules.certifications.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import com.seuportfolio.registryapi.tests.commonFactories.CreateCertFactory;
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
public class DeleteCertificationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to delete a certificate")
	@Transactional
	void deleteCertificateSuccessCase() throws Exception {
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
					delete("/certification/" + certBaseContent.getId()).header(
						"Authorization",
						"Bearer " + parsedCreateUserResJson.getAccessToken()
					)
				);

		result.andExpect(status().isNoContent());

		Optional<BaseContentEntity> optSearchedCert =
			this.baseContentRepo.findById(certBaseContent.getId());
		assertThat(optSearchedCert.isEmpty()).isTrue();
	}
}
