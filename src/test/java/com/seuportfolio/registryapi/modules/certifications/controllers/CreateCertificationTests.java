package com.seuportfolio.registryapi.modules.certifications.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CreateCertificationDTO;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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
public class CreateCertificationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private BaseContentRepo baseContentRepo;

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to create a certificate")
	@Transactional
	void createCertificationSuccessCase() throws Exception {
		var mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		ResultActions createUserResult = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
		String content = createUserResult
			.andReturn()
			.getResponse()
			.getContentAsString();
		LoginResponseDTO loginResult = mapper.readValue(
			content,
			LoginResponseDTO.class
		);
		TokenPayloadEntity token = JWTDecoder.decode(
			loginResult.getAccessToken(),
			mapper
		);

		Optional<UserEntity> optUser =
			this.userRepo.findByEmail(token.getSub());
		assertThat(optUser.isEmpty()).isFalse();

		BaseContentEntity org = CreateOrgFactory.create(
			this.entityManager,
			this.baseContentRepo,
			optUser.get()
		);

		var tagList = new ArrayList<String>(1);
		tagList.add("simple tag");
		var createCertificationBody = CreateCertificationDTO.builder()
			.organizationId(org.getId().toString())
			.name("certification")
			.issuedAt(LocalDateTime.now(ZoneOffset.UTC))
			.description("description")
			.code("123456")
			.link("http://localhost")
			.tags(tagList)
			.build();

		ResultActions result =
			this.mockMvc.perform(
					post("/certification")
						.contentType(MediaType.APPLICATION_JSON)
						.header(
							"Authorization",
							"Bearer " + loginResult.getAccessToken()
						)
						.content(
							mapper.writeValueAsString(createCertificationBody)
						)
				);
		result.andExpect(status().isCreated());
	}

	@Test
	@DisplayName("it should throw bad request")
	void badRequestCase() throws Exception {
		var mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		ResultActions createUserResult = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
		String content = createUserResult
			.andReturn()
			.getResponse()
			.getContentAsString();
		LoginResponseDTO loginResult = mapper.readValue(
			content,
			LoginResponseDTO.class
		);

		ResultActions result =
			this.mockMvc.perform(
					post("/certification")
						.contentType(MediaType.APPLICATION_JSON)
						.header(
							"Authorization",
							"Bearer " + loginResult.getAccessToken()
						)
						.content("{}")
				);
		result.andExpect(status().isBadRequest());
	}
}
