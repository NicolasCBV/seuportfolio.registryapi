package com.seuportfolio.registryapi.modules.certifications.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.seuportfolio.registryapi.modules.certifications.modals.CertificationEntity;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CertificationChangesDTO;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.UpdateCertificationDTO;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEnum;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.repositories.TagRepo;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
public class UpdateCertificationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private TagRepo tagRepo;

	@Autowired
	private BaseContentRepo baseContentRepo;

	private String oldTagName = "old tag";
	private String newTagName = "new tag";
	private String newCertName = "new cert name";
	private String newCertDescription = "new description";
	private String newCertCode = "new code";

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to update certification")
	@Transactional
	void updateCertificationSuccessCase() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		ResultActions createUserRes = this.createUser(mapper);
		String createUserBodyJson = createUserRes
			.andReturn()
			.getResponse()
			.getContentAsString();
		var createUserBody = mapper.readValue(
			createUserBodyJson,
			LoginResponseDTO.class
		);
		TokenPayloadEntity decodedToken =
			this.decodeToken(createUserBody, mapper);

		BaseContentEntity certBase = this.createCert(decodedToken.getSub());

		List<String> deleteTags = new ArrayList<String>(1);
		deleteTags.add(oldTagName);

		List<String> insertTags = new ArrayList<String>(1);
		insertTags.add(newTagName);

		var changes = CertificationChangesDTO.builder()
			.name(newCertName)
			.description(newCertDescription)
			.code(newCertCode)
			.build();

		var request = UpdateCertificationDTO.builder()
			.certificationChangesDTO(changes)
			.insertTags(insertTags)
			.deleteTags(deleteTags)
			.build();
		String requestJson = mapper.writeValueAsString(request);

		ResultActions finalRes =
			this.mockMvc.perform(
					patch("/certification/" + certBase.getId().toString())
						.contentType(MediaType.APPLICATION_JSON)
						.header(
							"Authorization",
							"Bearer " + createUserBody.getAccessToken()
						)
						.content(requestJson)
				);

		finalRes.andExpect(status().isOk());

		var optSearchedBaseContent =
			this.baseContentRepo.findById(certBase.getId());
		assertThat(optSearchedBaseContent.isEmpty()).isFalse();

		var searchedBaseContent = optSearchedBaseContent.get();
		assertThat(searchedBaseContent.getName()).isEqualTo(newCertName);
		assertThat(searchedBaseContent.getDescription()).isEqualTo(
			newCertDescription
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
		ResultActions createUserRes = this.createUser(mapper);
		String createUserResJson = createUserRes
			.andReturn()
			.getResponse()
			.getContentAsString();
		var parsedCreateUserRes = mapper.readValue(
			createUserResJson,
			LoginResponseDTO.class
		);

		ResultActions finalResult =
			this.mockMvc.perform(
					patch("/certification/" + UUID.randomUUID())
						.contentType(MediaType.APPLICATION_JSON)
						.header(
							"Authorization",
							"Bearer " + parsedCreateUserRes.getAccessToken()
						)
				);
		finalResult.andExpect(status().isBadRequest());
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

	private BaseContentEntity createCert(String email) {
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
			.ownerOf(pack)
			.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
			.build();
		pack.setRoot(org);

		var certBaseInfos = BaseContentEntity.builder()
			.name("certification")
			.description("description")
			.userEntity(user)
			.category(BaseContentCategoryEnum.CERTIFICATION.getValue())
			.linkedOn(pack)
			.build();
		var cert = CertificationEntity.builder()
			.code("A12")
			.issuedAt(LocalDateTime.now(ZoneOffset.UTC))
			.link("http://localhost:3000")
			.baseContentEntity(certBaseInfos)
			.build();
		certBaseInfos.setCertificationEntity(cert);

		var tag = TagEntity.builder()
			.name(oldTagName)
			.baseContentEntity(certBaseInfos)
			.build();
		var tagList = new ArrayList<TagEntity>();
		tagList.add(tag);
		certBaseInfos.setTagEntity(tagList);

		var baseContentList = new ArrayList<BaseContentEntity>(2);
		baseContentList.add(certBaseInfos);
		baseContentList.add(org);
		pack.setBaseContentEntities(baseContentList);

		this.entityManager.persist(pack);

		return certBaseInfos;
	}
}
