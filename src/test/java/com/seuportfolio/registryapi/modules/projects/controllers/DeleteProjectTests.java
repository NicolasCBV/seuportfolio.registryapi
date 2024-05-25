package com.seuportfolio.registryapi.modules.projects.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
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
public class DeleteProjectTests {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private MockMvc mockMvc;

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to delete a project")
	@Transactional
	void deleteProjectSuccessCase() throws Exception {
		var mapper = new ObjectMapper();
		ResultActions createUserRes = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
		String strContent = createUserRes
			.andReturn()
			.getResponse()
			.getContentAsString();

		var parsedCreateUserBody = mapper.readValue(
			strContent,
			LoginResponseDTO.class
		);
		TokenPayloadEntity token = JWTDecoder.decode(
			parsedCreateUserBody.getAccessToken(),
			mapper
		);

		Optional<UserEntity> optUser =
			this.userRepo.findByEmail(token.getSub());
		assertThat(optUser.isEmpty()).isFalse();

		BaseContentEntity baseContentProject = CreateProjectFactory.create(
			this.entityManager,
			this.baseContentRepo,
			optUser.get()
		);

		ResultActions finalResult =
			this.mockMvc.perform(
					delete("/project/" + baseContentProject.getId()).header(
						"Authorization",
						"Bearer " + parsedCreateUserBody.getAccessToken()
					)
				);
		finalResult.andExpect(status().isNoContent());

		Optional<BaseContentEntity> optSearchedBaseContent =
			this.baseContentRepo.findById(baseContentProject.getId());

		assertThat(optSearchedBaseContent.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("it should throw bad request becase base content id is wrong")
	void badRequestCase() throws Exception {
		var mapper = new ObjectMapper();
		ResultActions createUserRes = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
		String strContent = createUserRes
			.andReturn()
			.getResponse()
			.getContentAsString();

		var parsedCreateUserBody = mapper.readValue(
			strContent,
			LoginResponseDTO.class
		);

		ResultActions finalResult =
			this.mockMvc.perform(
					delete("/project/wrong-uuid-format").header(
						"Authorization",
						"Bearer " + parsedCreateUserBody.getAccessToken()
					)
				);

		finalResult.andExpect(status().isBadRequest());
	}
}
