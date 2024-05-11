package com.seuportfolio.registryapi.modules.organizations.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationAditionalInfoEntity;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
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
public class DeleteOrganizationTests {

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
	@DisplayName("it should be able to delete organization")
	@Transactional
	void deleteOrganizationSuccessCase() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		LoginResponseDTO createUserBody = this.createUser(mapper);
		TokenPayloadEntity decodedToken =
			this.decodeToken(createUserBody.accessToken(), mapper);
		Optional<UserEntity> optUser =
			this.userRepo.findByEmail(decodedToken.getSub());
		assertThat(optUser.isEmpty()).isFalse();

		BaseContentEntity org = this.createOrg(optUser.get());

		ResultActions result =
			this.mockMvc.perform(
					delete("/organization/" + org.getId().toString()).header(
						"Authorization",
						"Bearer " + createUserBody.accessToken()
					)
				);
		result.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("it should throw bad request")
	@Transactional
	void badRequestCase() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		LoginResponseDTO createUserBody = this.createUser(mapper);
		ResultActions result =
			this.mockMvc.perform(
					delete("/organization/wrong-id").header(
						"Authorization",
						"Bearer " + createUserBody.accessToken()
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
			StandardCharsets.UTF_8.toString()
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

	private BaseContentEntity createOrg(UserEntity user) throws Exception {
		var org = BaseContentEntity.builder()
			.name("org name")
			.description("description")
			.userEntity(user)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();
		var orgAditionalInfos = OrganizationAditionalInfoEntity.builder()
			.siteUrl("http://localhost:8080")
			.baseContentEntity(org)
			.build();
		org.setOrganizationEntity(orgAditionalInfos);

		this.entityManager.persist(org);

		Optional<BaseContentEntity> optSearchedOrg =
			this.baseContentRepo.findByName(org.getName());
		assertThat(optSearchedOrg.isEmpty()).isFalse();

		return optSearchedOrg.get();
	}
}
