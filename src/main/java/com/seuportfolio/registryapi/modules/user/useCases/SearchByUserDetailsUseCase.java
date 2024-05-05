package com.seuportfolio.registryapi.modules.user.useCases;

import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SearchByUserDetailsUseCase implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;

	@Override
	public UserEntity loadUserByUsername(String email)
		throws UsernameNotFoundException {
		Optional<UserEntity> user = this.userRepo.findByEmail(email);
		if (user.isEmpty()) throw new UsernameNotFoundException(
			"User not found"
		);

		return user.get();
	}
}
