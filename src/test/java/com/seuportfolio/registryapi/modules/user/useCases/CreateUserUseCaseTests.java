package com.seuportfolio.registryapi.modules.user.useCases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateUserUseCaseTests {
	@Mock
	@Autowired
	private UserRepo userRepo;

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
		
		when(this.bCryptPasswordEncoder.encode(dto.getPassword()))
			.thenReturn(dto.getPassword());

		var user = this.createUserUseCase.exec(dto);
		assertThat(user.getFullName() == dto.getFullName()).isTrue();
		assertThat(user.getEmail() == dto.getEmail()).isTrue();
		assertThat(user.getPassword() == dto.getPassword()).isTrue();

		verify(this.userRepo, times(1)).save(user);
	}
}
