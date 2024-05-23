package com.seuportfolio.registryapi.modules.certifications.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CreateCertificationDTO;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEnum;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Base64;
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
		ResultActions createUserResult = this.createUser(mapper);
		String content = createUserResult
			.andReturn()
			.getResponse()
			.getContentAsString();
		LoginResponseDTO loginResult = mapper.readValue(
			content,
			LoginResponseDTO.class
		);
		TokenPayloadEntity token = this.decodeToken(loginResult, mapper);

		BaseContentEntity org = this.createOrg(token.getSub());

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
		ResultActions createUserResult = this.createUser(mapper);
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

	private TokenPayloadEntity decodeToken(
		LoginResponseDTO dto,
		ObjectMapper mapper
	) throws Exception {
		String token = dto.getAccessToken();
		String payload = token.split("\\.", 4)[1];

		var decoder = Base64.getUrlDecoder();
		String decodedPayload = new String(
			decoder.decode(payload),
			StandardCharsets.UTF_8.toString()
		);
		return mapper.readValue(decodedPayload, TokenPayloadEntity.class);
	}

	private ResultActions createUser(ObjectMapper mapper) throws Exception {
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

		return result;
	}

	private BaseContentEntity createOrg(String email) {
		Optional<UserEntity> optUser = this.userRepo.findByEmail(email);
		assertThat(optUser.isEmpty()).isFalse();

		var user = optUser.get();

		var pack = PackageEntity.builder()
			.type(PackageEnum.ORGANIZATION.getValue())
			.build();
		var org = BaseContentEntity.builder()
			.name("org")
			.description("description")
			.userEntity(user)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.ownerOf(pack)
			.build();
		pack.setRoot(org);

		var baseContentList = new ArrayList<BaseContentEntity>();
		baseContentList.add(org);
		user.setBaseContentEntity(baseContentList);

		this.entityManager.persist(org);
		return org;
	}
}
