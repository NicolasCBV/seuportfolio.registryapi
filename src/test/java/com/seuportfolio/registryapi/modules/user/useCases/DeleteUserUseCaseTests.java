package com.seuportfolio.registryapi.modules.user.useCases;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteUserUseCaseTests {

	@Mock
	@Autowired
	private UserRepo userRepo;

	@Autowired
	@InjectMocks
	private DeleteUserUseCase deleteUserUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to delete user")
	void deleteCase() {
		var user = UserEntity.builder()
			.email("johndoe@email.com")
			.fullName("John Doe")
			.password("123456")
			.build();

		this.deleteUserUseCase.exec(user);

		verify(this.userRepo, times(1)).delete(user);
	}
}
