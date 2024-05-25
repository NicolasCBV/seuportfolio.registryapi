package com.seuportfolio.registryapi.modules.projects.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.projects.modals.ProjectStateEnum;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.ProjectChangesDTO;
import com.seuportfolio.registryapi.modules.projects.presentation.dto.UpdateProjectDTO;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import com.seuportfolio.registryapi.tests.commonFactories.CreateProjectFactory;
import com.seuportfolio.registryapi.tests.httpFactories.CreateUserFactory;
import com.seuportfolio.registryapi.utils.jwt.JWTDecoder;
import com.seuportfolio.registryapi.utils.project.ProjectStateMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
public class UpdateProjectTests {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private MockMvc mockMvc;

	private String newTagName = "new tag name";

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to update a project")
	@Transactional
	void updateProjectSuccessCase() throws Exception {
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

		var insertTags = new ArrayList<String>(1);
		insertTags.add(this.newTagName);

		var oldTagName = baseContentProject.getTagEntity().get(0).getName();
		var deleteTags = new ArrayList<String>(1);
		deleteTags.add(oldTagName);

		String state = ProjectStateMapper.fromShortToString(
			ProjectStateEnum.IN_PROGRESS.getValue()
		);
		var projectChanges = ProjectChangesDTO.builder()
			.name("new project name")
			.description("new project description")
			.state(state)
			.build();
		var dto = UpdateProjectDTO.builder()
			.projectChanges(projectChanges)
			.deleteTags(deleteTags)
			.insertTags(insertTags)
			.build();

		var requestBodyJson = mapper.writeValueAsString(dto);
		ResultActions finalResult =
			this.mockMvc.perform(
					patch("/project/" + baseContentProject.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.header(
							"Authorization",
							"Bearer " + parsedCreateUserBody.getAccessToken()
						)
						.content(requestBodyJson)
				);
		finalResult.andExpect(status().isOk());

		Optional<BaseContentEntity> optSearchedBaseContent =
			this.baseContentRepo.findById(baseContentProject.getId());
		assertThat(optSearchedBaseContent.isEmpty()).isFalse();

		var searchedBaseContent = optSearchedBaseContent.get();
		assertThat(searchedBaseContent.getName()).isEqualTo(
			projectChanges.getName()
		);
		assertThat(searchedBaseContent.getDescription()).isEqualTo(
			projectChanges.getDescription()
		);

		var searchedProject = searchedBaseContent.getProjectEntity();
		assertThat(searchedProject.getState()).isEqualTo(
			ProjectStateMapper.fromStringToShort(projectChanges.getState())
		);

		var tags = searchedBaseContent.getTagEntity();

		var searchedTag = tags.get(0);
		assertThat(searchedTag.getName()).isNotEqualTo(oldTagName);
		assertThat(searchedTag.getName()).isEqualTo(this.newTagName);
	}

	@Test
	@DisplayName("it should throw bad request because base content is wrong")
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
					patch("/project/wrong-base-content-id")
						.contentType(MediaType.APPLICATION_JSON)
						.header(
							"Authorization",
							"Bearer " + parsedCreateUserBody.getAccessToken()
						)
						.content("{}")
				);

		finalResult.andExpect(status().isBadRequest());
	}
}
