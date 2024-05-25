package com.seuportfolio.registryapi.modules.certifications.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.CertificationChangesDTO;
import com.seuportfolio.registryapi.modules.certifications.presentation.dto.UpdateCertificationDTO;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.repositories.TagRepo;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import com.seuportfolio.registryapi.tests.commonFactories.CreateCertFactory;
import com.seuportfolio.registryapi.tests.httpFactories.CreateUserFactory;
import com.seuportfolio.registryapi.utils.jwt.JWTDecoder;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
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

		ResultActions createUserRes = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
		String createUserBodyJson = createUserRes
			.andReturn()
			.getResponse()
			.getContentAsString();
		var createUserBody = mapper.readValue(
			createUserBodyJson,
			LoginResponseDTO.class
		);
		TokenPayloadEntity decodedToken = JWTDecoder.decode(
			createUserBody.getAccessToken(),
			mapper
		);

		BaseContentEntity certBase = CreateCertFactory.create(
			this.entityManager,
			this.userRepo,
			decodedToken.getSub()
		);

		String oldTagName = certBase.getTagEntity().get(0).getName();
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
		ResultActions createUserRes = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
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
}
