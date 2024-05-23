package com.seuportfolio.registryapi.modules.certifications.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.certifications.modals.CertificationEntity;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEnum;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
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

		ResultActions createUserRes = this.createUser(mapper);
		String createUserResJson = createUserRes
			.andReturn()
			.getResponse()
			.getContentAsString();
		LoginResponseDTO parsedCreateUserResJson = mapper.readValue(
			createUserResJson,
			LoginResponseDTO.class
		);

		var token = this.decodeToken(parsedCreateUserResJson, mapper);
		this.createCert(token.getSub());

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

	private void createCert(String email) {
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

		var certificationTag = TagEntity.builder().name("simple tag").build();

		var tagList = new ArrayList<TagEntity>(1);
		tagList.add(certificationTag);

		var certificationBasicInfos = BaseContentEntity.builder()
			.name("certification")
			.description("description")
			.userEntity(user)
			.category(BaseContentCategoryEnum.CERTIFICATION.getValue())
			.linkedOn(pack)
			.tagEntity(tagList)
			.build();
		var certification = CertificationEntity.builder()
			.code("A12")
			.issuedAt(LocalDateTime.now(ZoneOffset.UTC))
			.link("http://localhost:3000")
			.imageUrl("http://localhost:3000/some-image")
			.baseContentEntity(certificationBasicInfos)
			.build();

		certificationTag.setBaseContentEntity(certificationBasicInfos);
		certificationBasicInfos.setCertificationEntity(certification);

		var baseContentList = new ArrayList<BaseContentEntity>();
		baseContentList.add(org);
		baseContentList.add(certificationBasicInfos);
		pack.setBaseContentEntities(baseContentList);

		this.entityManager.persist(pack);
	}
}
