package com.seuportfolio.registryapi.modules.organizations.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEntity;
import com.seuportfolio.registryapi.modules.globals.modals.PackageEnum;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.repositories.PackageRepo;
import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
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
public class GetOrganizationsTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private PackageRepo packageRepo;

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private UserRepo userRepo;

	@BeforeEach
	void flushAll() {
		this.userRepo.deleteAll();
		this.baseContentRepo.deleteAll();
	}

	@Test
	@DisplayName("it should be able to get organizations")
	@Transactional
	void getOrgs() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		LoginResponseDTO createUserBody = this.createUser(mapper);

		String token = createUserBody.getAccessToken();
		TokenPayloadEntity decodedToken = this.decodeToken(token, mapper);

		this.createOrg(decodedToken.getSub());

		ResultActions result =
			this.mockMvc.perform(
					get("/organization?offset=0").header(
						"Authorization",
						"Bearer " + token
					)
				);

		result
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.organizations").isArray())
			.andExpect(jsonPath("$.organizations[0].id").isString())
			.andExpect(jsonPath("$.organizations[0].name").isString())
			.andExpect(jsonPath("$.organizations[0].description").isString())
			.andExpect(jsonPath("$.organizations[0].created_at").isString())
			.andExpect(jsonPath("$.organizations[0].updated_at").isString())
			.andExpect(jsonPath("$.organizations[0].tags").isArray())
			.andExpect(jsonPath("$.organizations[0].tags[0].id").isString())
			.andExpect(jsonPath("$.organizations[0].tags[0].name").isString())
			.andExpect(
				jsonPath("$.organizations[0].tags[0].created_at").isString()
			);
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
			.andExpect(jsonPath("$.access_token").isString());

		String resJson = result.andReturn().getResponse().getContentAsString();
		LoginResponseDTO resBody = mapper.readValue(
			resJson,
			LoginResponseDTO.class
		);

		return resBody;
	}

	private List<BaseContentEntity> createOrg(String email) {
		Optional<UserEntity> user = this.userRepo.findByEmail(email);
		assertThat(user.isEmpty()).isFalse();

		List<BaseContentEntity> orgs = new ArrayList<BaseContentEntity>();

		for (int i = 0; i < 10; i++) {
			var tag = TagEntity.builder().name("simple tag").build();

			List<TagEntity> tagList = new ArrayList<TagEntity>(1);
			tagList.add(tag);

			var pack = PackageEntity.builder()
				.type(PackageEnum.ORGANIZATION.getValue())
				.build();
			var org = BaseContentEntity.builder()
				.name("org:" + i)
				.description("Simple description")
				.userEntity(user.get())
				.category(BaseContentCategoryEnum.ORGANIZATION.getValue())
				.tagEntity(tagList)
				.ownerOf(pack)
				.build();

			pack.setRoot(org);
			tag.setBaseContentEntity(org);

			this.packageRepo.save(pack);
		}

		return orgs;
	}
}
