package com.seuportfolio.registryapi.modules.organizations.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.repositories.TagRepo;
import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationAditionalInfoEntity;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.OrganizationChangesDTO;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.UpdateOrganizationDTO;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
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

	private String oldTagName = "old tag";
	private String newTagName = "new tag";

	@BeforeEach
	void flushAll() {
		this.userRepo.deleteAll();
		this.baseContentRepo.deleteAll();
		this.tagRepo.deleteAll();
	}

	@Test
	@DisplayName("it should be able to update organization infos")
	@Transactional
	void updateOrganizationSuccessCase() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		LoginResponseDTO createUserBody = this.createUser(mapper);
		TokenPayloadEntity decodedToken =
			this.decodeToken(createUserBody.accessToken(), mapper);

		BaseContentEntity org = this.createOrg(decodedToken.getSub());

		var organizationChanges = OrganizationChangesDTO.builder()
			.name("new org name")
			.description("new org description")
			.siteUrl("http://new-site")
			.build();

		List<String> deleteTags = new ArrayList<String>(1);
		deleteTags.add(oldTagName);

		List<String> insertTags = new ArrayList<String>(1);
		insertTags.add(newTagName);

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
							"Bearer " + createUserBody.accessToken()
						)
				);
		result.andExpect(status().isOk());

		var optSearchedBaseContent = this.baseContentRepo.findById(org.getId());
		assertThat(optSearchedBaseContent.isEmpty()).isFalse();

		var searchedBaseContent = optSearchedBaseContent.get();

		assertThat(
			searchedBaseContent.getOrganizationEntity().getSiteUrl()
		).isEqualTo("http://new-site");
		assertThat(searchedBaseContent.getName()).isEqualTo("new org name");
		assertThat(searchedBaseContent.getDescription()).isEqualTo(
			"new org description"
		);

		Optional<TagEntity> optSearchedTag =
			this.tagRepo.findByName(newTagName);
		assertThat(optSearchedTag.isEmpty()).isFalse();

		Optional<TagEntity> optSearchedDeletedTag =
			this.tagRepo.findByName(oldTagName);
		assertThat(optSearchedDeletedTag.isEmpty()).isTrue();
	}

	@Test
	@DisplayName("it should throw bad request")
	void badRequestCase() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		LoginResponseDTO createUserResult = this.createUser(mapper);

		ResultActions result =
			this.mockMvc.perform(
					patch("/organization/wrong-id")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}")
						.header(
							"Authorization",
							"Bearer " + createUserResult.accessToken()
						)
				);
		result.andExpect(status().isBadRequest());
	}

	private TokenPayloadEntity decodeToken(String token, ObjectMapper mapper)
		throws Exception {
		String payload = token.split("\\.", 4)[1];

		Decoder decoder = Base64.getUrlDecoder();
		String payloadAsJson = new String(
			decoder.decode(payload),
			StandardCharsets.UTF_8
		);

		TokenPayloadEntity parsedPayload = mapper.readValue(
			payloadAsJson,
			TokenPayloadEntity.class
		);
		return parsedPayload;
	}

	private LoginResponseDTO createUser(ObjectMapper mapper) throws Exception {
		var body = CreateUserDTO.builder()
			.email("johndoe@email.com")
			.fullName("John Doe")
			.password("123456")
			.build();

		String json = mapper.writeValueAsString(body);

		ResultActions result =
			this.mockMvc.perform(
					post("/user")
						.content(json)
						.contentType(MediaType.APPLICATION_JSON)
				);
		result
			.andExpect(status().isCreated())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accessToken").isString());

		String resJson = result.andReturn().getResponse().getContentAsString();
		LoginResponseDTO resBody = mapper.readValue(
			resJson,
			LoginResponseDTO.class
		);

		return resBody;
	}

	private BaseContentEntity createOrg(String email) throws Exception {
		Optional<UserEntity> optUser = this.userRepo.findByEmail(email);
		assertThat(optUser.isEmpty()).isFalse();
		var user = optUser.get();

		var aditionalInfos = OrganizationAditionalInfoEntity.builder()
			.siteUrl("http://localhost:8080")
			.build();

		var org = BaseContentEntity.builder()
			.name("org name")
			.description("description")
			.userEntity(user)
			.organizationEntity(aditionalInfos)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();
		var tagList = new ArrayList<TagEntity>(2);
		tagList.add(
			TagEntity.builder().name(oldTagName).baseContentEntity(org).build()
		);

		org.setTagEntity(tagList);
		aditionalInfos.setBaseContentEntity(org);

		var orgList = new ArrayList<BaseContentEntity>(1);
		orgList.add(org);
		user.setBaseContentEntity(orgList);
		this.entityManager.persist(org);

		Optional<BaseContentEntity> optSearchedOrg =
			this.baseContentRepo.findByName(org.getName());
		assertThat(optSearchedOrg.isEmpty()).isFalse();

		return optSearchedOrg.get();
	}
}
