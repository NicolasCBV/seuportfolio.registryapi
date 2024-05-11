package com.seuportfolio.registryapi.modules.user.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CreateUserUseCaseTests {

	@Mock
	@Autowired
	private UserRepo unusedUserRepo;

	@Mock
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	@InjectMocks
	private CreateUserUseCase createUserUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to create user")
	void createUserCase() {
		var dto = CreateUserDTO.builder()
			.email("johndoe@email.com")
			.fullName("John Doe")
			.password("123456")
			.build();

		when(this.bCryptPasswordEncoder.encode(dto.getPassword())).thenReturn(
			dto.getPassword()
		);

		assertDoesNotThrow(() -> this.createUserUseCase.exec(dto));
	}
}
