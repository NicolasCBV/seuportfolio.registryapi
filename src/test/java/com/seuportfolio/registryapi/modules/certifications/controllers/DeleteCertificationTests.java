package com.seuportfolio.registryapi.modules.certifications.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.certifications.modals.CertificationEntity;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEnum;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
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
public class DeleteCertificationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to delete a certificate")
	@Transactional
	void deleteCertificateSuccessCase() throws Exception {
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
		BaseContentEntity certBaseContent = this.createCert(token.getSub());

		ResultActions result =
			this.mockMvc.perform(
					delete("/certification/" + certBaseContent.getId()).header(
						"Authorization",
						"Bearer " + parsedCreateUserResJson.getAccessToken()
					)
				);

		result.andExpect(status().isNoContent());

		Optional<BaseContentEntity> optSearchedCert =
			this.baseContentRepo.findById(certBaseContent.getId());
		assertThat(optSearchedCert.isEmpty()).isTrue();
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

		var baseContentList = new ArrayList<BaseContentEntity>(2);
		baseContentList.add(certBaseInfos);
		baseContentList.add(org);
		pack.setBaseContentEntities(baseContentList);

		this.entityManager.persist(pack);

		return certBaseInfos;
	}
}
