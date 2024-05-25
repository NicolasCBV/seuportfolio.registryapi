package com.seuportfolio.registryapi.modules.organizations.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.repositories.TagRepo;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.OrganizationChangesDTO;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.UpdateOrganizationDTO;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import com.seuportfolio.registryapi.tests.commonFactories.CreateOrgFactory;
import com.seuportfolio.registryapi.tests.httpFactories.CreateUserFactory;
import com.seuportfolio.registryapi.utils.jwt.JWTDecoder;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
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
public class UpdateOrganizationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private TagRepo tagRepo;

	@Autowired
	private BaseContentRepo baseContentRepo;

	private String newTagName = "new tag name";

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to update organization infos")
	@Transactional
	void updateOrganizationSuccessCase() throws Exception {
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

		BaseContentEntity org = CreateOrgFactory.create(
			this.entityManager,
			this.baseContentRepo,
			optUser.get()
		);

		var organizationChanges = OrganizationChangesDTO.builder()
			.name("new org name")
			.description("new org description")
			.build();

		String oldTagName = org.getTagEntity().get(0).getName();
		List<String> deleteTags = new ArrayList<String>(1);
		deleteTags.add(oldTagName);

		List<String> insertTags = new ArrayList<String>(1);
		insertTags.add(this.newTagName);

		var requestBody = UpdateOrganizationDTO.builder()
			.organizationChanges(organizationChanges)
			.deleteTags(deleteTags)
			.insertTags(insertTags)
			.build();

		var requestBodyJson = mapper.writeValueAsString(requestBody);

		ResultActions result =
			this.mockMvc.perform(
					patch("/organization/" + org.getId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBodyJson)
						.header(
							"Authorization",
							"Bearer " + parsedCreateUserBody.getAccessToken()
						)
				);
		result.andExpect(status().isOk());

		var optSearchedBaseContent = this.baseContentRepo.findById(org.getId());
		assertThat(optSearchedBaseContent.isEmpty()).isFalse();

		var searchedBaseContent = optSearchedBaseContent.get();

		assertThat(searchedBaseContent.getName()).isEqualTo("new org name");
		assertThat(searchedBaseContent.getDescription()).isEqualTo(
			"new org description"
		);

		Optional<TagEntity> optSearchedTag =
			this.tagRepo.findByName(this.newTagName);
		assertThat(optSearchedTag.isEmpty()).isFalse();

		Optional<TagEntity> optSearchedDeletedTag =
			this.tagRepo.findByName(oldTagName);
		assertThat(optSearchedDeletedTag.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("it should throw bad request")
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

		ResultActions result =
			this.mockMvc.perform(
					patch("/organization/wrong-id")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}")
						.header(
							"Authorization",
							"Bearer " + parsedCreateUserBody.getAccessToken()
						)
				);
		result.andExpect(status().isBadRequest());
	}
}
