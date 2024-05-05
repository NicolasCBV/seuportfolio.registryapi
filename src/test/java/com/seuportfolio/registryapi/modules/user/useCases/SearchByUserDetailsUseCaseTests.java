package com.seuportfolio.registryapi.modules.user.useCases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;

public class SearchByUserDetailsUseCaseTests {
	@Mock
	private UserRepo userRepo;

	@Autowired
	@InjectMocks
	private SearchByUserDetailsUseCase searchByUserDetailsUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to search for user details with sucess")
	void loadUserSuccessCase() {
		UserEntity user = UserEntity.builder()
				.email("johndoe@email.com")
				.fullName("John Doe")
				.password("123456")
				.build();
		var optUser = Optional.of(user);

		when(this.userRepo.findByEmail(user.getEmail())).thenReturn(optUser);

		var searchedUser = this.searchByUserDetailsUseCase.loadUserByUsername(user.getEmail());
		
		verify(this.userRepo, times(1)).findByEmail(user.getEmail());
		assertThat(user.equals(searchedUser)).isTrue();
	}

	@Test()
	@DisplayName("it should not find user details")
	void loadUserFailCase() {
		String email = "johndoe@email.com";

		when(this.userRepo.findByEmail(email)).thenReturn(Optional.empty());
		assertThrows(
			UsernameNotFoundException.class, 
			() -> this.searchByUserDetailsUseCase.loadUserByUsername(email));
	}
}
