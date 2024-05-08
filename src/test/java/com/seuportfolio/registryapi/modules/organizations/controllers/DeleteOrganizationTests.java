// package com.seuportfolio.registryapi.modules.organizations.controllers;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//
// import java.util.Base64;
// import java.util.Optional;
// import java.util.Base64.Decoder;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.ResultActions;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationEntity;
// import com.seuportfolio.registryapi.modules.organizations.repositories.OrganizationRepo;
// import com.seuportfolio.registryapi.modules.user.modals.TokenPayloadEntity;
// import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
// import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
// import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
// import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
//
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
// import static org.assertj.core.api.Assertions.assertThat;
//
// @SpringBootTest
// @AutoConfigureMockMvc
// @ActiveProfiles("test")
// public class DeleteOrganizationTests {
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private OrganizationRepo organizationRepo;
//
// 	@Autowired
// 	private UserRepo userRepo;
//
// 	@BeforeEach
// 	void flushAll() {
// 		this.userRepo.deleteAll();
// 	}
//
// 	@Test
// 	@DisplayName("it should be able to delete organization")
// 	void deleteOrganizationSuccessCase() throws Exception {
// 		ObjectMapper mapper = new ObjectMapper();
//
// 		LoginResponseDTO createUserBody = this.createUser(mapper);
// 		TokenPayloadEntity decodedToken = this.decodeToken(
// 			createUserBody.accessToken(), mapper
// 		);
// 		Optional<UserEntity> optUser = this.userRepo.findByEmail(decodedToken.getSub());
// 		assertThat(optUser.isEmpty()).isFalse();
//
// 		OrganizationEntity org = this.createOrg(optUser.get());
//
// 		ResultActions result = this.mockMvc.perform(delete("/organization")
// 				.param("organization_id", org.getId().toString())
// 				.header("Authorization", "Bearer " + createUserBody.accessToken()));
// 		result.andExpect(status().isNoContent());
// 	}
//
// 	@Test
// 	@DisplayName("it should throw bad request")
// 	void badRequestCase() throws Exception {
// 		ObjectMapper mapper = new ObjectMapper();
//
// 		LoginResponseDTO createUserBody = this.createUser(mapper);
// 		ResultActions result = this.mockMvc.perform(delete("/organization")
// 				.param("organization_id", "wrong-id")
// 				.header("Authorization", "Bearer " + createUserBody.accessToken()));
// 		result.andExpect(status().isBadRequest());
// 	}
//
// 	private TokenPayloadEntity decodeToken(String token, ObjectMapper mapper) throws Exception {
// 		String payload = token.split("\\.", 4)[1];
//
// 		Decoder decoder = Base64.getUrlDecoder();
// 		String payloadAsJson = new String(decoder.decode(payload));
//
// 		TokenPayloadEntity parsedPayload = mapper.readValue(payloadAsJson, TokenPayloadEntity.class);
// 		return parsedPayload;
// 	}
//
// 	private LoginResponseDTO createUser(ObjectMapper mapper) throws Exception {
// 		var body = CreateUserDTO.builder()
// 			.email("johndoe@email.com")
// 			.fullName("John Doe")
// 			.password("123456")
// 			.build();
//
// 		String json = mapper.writeValueAsString(body);
//
// 		ResultActions result =
// 			this.mockMvc.perform(
// 					post("/user")
// 						.content(json)
// 						.contentType(MediaType.APPLICATION_JSON)
// 				);
// 		result.andExpect(status().isCreated())
// 			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
// 			.andExpect(jsonPath("$.accessToken").isString());
//
// 		String resJson = result.andReturn().getResponse().getContentAsString();
// 		LoginResponseDTO resBody = mapper.readValue(resJson, LoginResponseDTO.class);
//
// 		return resBody;
// 	}
//
// 	private OrganizationEntity createOrg(UserEntity user) throws Exception {
// 		var org = OrganizationEntity.builder()
// 			.name("org name")
// 			.description("description")
// 			.userEntity(user)
// 			.build();
// 		this.organizationRepo.save(org);
//
// 		Optional<OrganizationEntity> optSearchedOrg = this.organizationRepo.findByName(org.getName());
// 		assertThat(optSearchedOrg.isEmpty()).isFalse();
//
// 		return optSearchedOrg.get();
// 	}
// }
