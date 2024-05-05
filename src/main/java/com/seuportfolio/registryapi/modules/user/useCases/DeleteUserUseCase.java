package com.seuportfolio.registryapi.modules.user.useCases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;

@Service
public class DeleteUserUseCase {
	@Autowired
	private UserRepo userRepo;

	public void exec(UserEntity user) {
		this.userRepo.delete(user);
	}
}
