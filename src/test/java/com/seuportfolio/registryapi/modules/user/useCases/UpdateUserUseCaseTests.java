package com.seuportfolio.registryapi.modules.user.useCases;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.UpdateUserDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;

public class UpdateUserUseCaseTests {
	@Mock
	private UserRepo userRepo;

	@Autowired
	@InjectMocks
	private UpdateUserUseCase updateUserUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to update user")
	void updateUserCase() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		var dto = UpdateUserDTO.builder()
			.fullName("New John Doe")
			.description("New description")
			.build();

		this.updateUserUseCase.exec(user, dto);
		verify(this.userRepo, times(1)).save(user);
	}
}
