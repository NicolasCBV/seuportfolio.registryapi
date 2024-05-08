package com.seuportfolio.registryapi.modules.organizations.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationEntity;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.OrganizationChangesDTO;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.UpdateOrganizationDTO;
import com.seuportfolio.registryapi.modules.organizations.repositories.OrganizationRepo;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
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
	private OrganizationRepo organizationRepo;

	@BeforeEach
	void flushAll() {
		this.userRepo.deleteAll();
	}

	@Test
	@DisplayName("it should be able to update organization infos")
	void updateOrganizationSuccessCase() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		LoginResponseDTO createUserBody = this.createUser(mapper);
		TokenPayloadEntity decodedToken =
			this.decodeToken(createUserBody.accessToken(), mapper);
		Optional<UserEntity> optUser =
			this.userRepo.findByEmail(decodedToken.getSub());
		assertThat(optUser.isEmpty()).isFalse();

		OrganizationEntity org = this.createOrg(optUser.get());

		var organizationChanges = OrganizationChangesDTO.builder()
			.name("new org name")
			.description("new org description")
			.build();

		List<String> deleteTags = new ArrayList<String>(1);
		deleteTags.add("old tag");

		List<String> insertTags = new ArrayList<String>(1);
		insertTags.add("new tag");

		var requestBody = UpdateOrganizationDTO.builder()
			.organizationId(org.getId().toString())
			.organizationChanges(organizationChanges)
			.deleteTags(deleteTags)
			.insertTags(insertTags)
			.build();

		var requestBodyJson = mapper.writeValueAsString(requestBody);

		ResultActions result =
			this.mockMvc.perform(
					patch("/organization")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBodyJson)
						.header(
							"Authorization",
							"Bearer " + createUserBody.accessToken()
						)
				);

		result.andExpect(status().isOk());
	}

	@Test
	@DisplayName("it should throw bad request")
	void badRequestCase() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		LoginResponseDTO createUserResult = this.createUser(mapper);

		ResultActions result =
			this.mockMvc.perform(
					patch("/organization")
						.contentType(MediaType.APPLICATION_JSON)
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

	private OrganizationEntity createOrg(UserEntity user) throws Exception {
		var org = OrganizationEntity.builder()
			.name("org name")
			.description("description")
			.userEntity(user)
			.build();
		this.organizationRepo.save(org);

		Optional<OrganizationEntity> optSearchedOrg =
			this.organizationRepo.findByName(org.getName());
		assertThat(optSearchedOrg.isEmpty()).isFalse();

		return optSearchedOrg.get();
	}
}
