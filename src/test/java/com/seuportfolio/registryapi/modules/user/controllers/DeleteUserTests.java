package com.seuportfolio.registryapi.modules.user.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.LoginResponseDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import com.seuportfolio.registryapi.tests.httpFactories.CreateUserFactory;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DeleteUserTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepo userRepo;

	private String email = "johndoe@email.com";

	@BeforeEach
	@Transactional
	void flushAll() {
		this.userRepo.deleteAll();
		this.userRepo.flush();
	}

	@Test
	@DisplayName("it should be able to delete user")
	void deleteUserSuccessCase() throws Exception {
		var mapper = new ObjectMapper();

		ResultActions createUserResult = CreateUserFactory.create(
			mapper,
			this.mockMvc
		);
		String strContent = createUserResult
			.andReturn()
			.getResponse()
			.getContentAsString();

		var body = mapper.readValue(strContent, LoginResponseDTO.class);
		ResultActions deleteUserResult =
			this.mockMvc.perform(
					delete("/user").header(
						"Authorization",
						"Bearer " + body.getAccessToken()
					)
				);

		deleteUserResult.andExpect(status().isNoContent());

		Optional<UserEntity> optSearchedUser =
			this.userRepo.findByEmail(this.email);
		assertThat(optSearchedUser.isEmpty()).isEqualTo(true);
	}

	@Test
	@DisplayName("it should throw unauthorized")
	void unauthorizedCase() throws Exception {
		ResultActions result = this.mockMvc.perform(delete("/user"));
		result.andExpect(status().isForbidden());
	}
}
