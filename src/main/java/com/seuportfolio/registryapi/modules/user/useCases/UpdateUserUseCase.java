package com.seuportfolio.registryapi.modules.user.useCases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.presentation.dto.UpdateUserDTO;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;

@Service
public class UpdateUserUseCase {
	@Autowired
	private UserRepo userRepo;

	public void exec(UserEntity userEntity, UpdateUserDTO dto) {
		if(dto.getFullName() != null)
			userEntity.setFullName(dto.getFullName());
		if(dto.getDescription() != null)
			userEntity.setDescription(dto.getDescription());

		this.userRepo.save(userEntity);
	}
}
