package com.seuportfolio.registryapi.modules.user.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class UserRepoTests {

	@Autowired
	EntityManager entityManager;

	@Autowired
	UserRepo userRepo;

	@BeforeEach
	void flushAll() {
		this.userRepo.deleteAll();
	}

	@Test
	@DisplayName("it should be able to update user entities")
	void updateByEmail() {
		var user = this.createUser();
		String newFullName = "New Full Name";
		String newDescription = "New description";

		this.userRepo.updateByEmail(
				user.getEmail(),
				newFullName,
				newDescription
			);

		Optional<UserEntity> searchedUser =
			this.userRepo.findById(user.getId());
		assertThat(searchedUser.isPresent()).isTrue();

		String actualDescription = searchedUser.get().getDescription();
		String actualFullName = searchedUser.get().getFullName();

		assertThat(newFullName.equals(actualFullName)).isTrue();
		assertThat(newDescription.equals(actualDescription)).isTrue();
	}

	private UserEntity createUser() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		this.entityManager.persist(user);
		return user;
	}
}
