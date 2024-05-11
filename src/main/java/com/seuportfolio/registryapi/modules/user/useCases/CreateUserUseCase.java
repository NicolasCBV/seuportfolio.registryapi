package com.seuportfolio.registryapi.modules.user.useCases;

import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.CreateUserDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CreateUserUseCase {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserEntity exec(CreateUserDTO dto) {
		String hashedPassword =
			this.bCryptPasswordEncoder.encode(dto.getPassword());

		UserEntity user = UserEntity.builder()
			.fullName(dto.getFullName())
			.email(dto.getEmail())
			.password(hashedPassword)
			.createdAt(LocalDateTime.now(ZoneOffset.UTC))
			.updatedAt(LocalDateTime.now(ZoneOffset.UTC))
			.build();

		return this.userRepo.save(user);
	}
}
