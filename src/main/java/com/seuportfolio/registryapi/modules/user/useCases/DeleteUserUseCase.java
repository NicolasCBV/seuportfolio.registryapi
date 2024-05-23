package com.seuportfolio.registryapi.modules.user.useCases;

import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserUseCase {

	@Autowired
	private UserRepo userRepo;

	@Transactional
	public void exec(UserEntity user) {
		this.userRepo.delete(user);
	}
}
