package com.seuportfolio.registryapi.modules.auth;

import com.seuportfolio.registryapi.modules.user.useCases.SearchByUserDetailsUseCase;
import com.seuportfolio.registryapi.modules.user.useCases.TokenUseCase;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SecurityFilter extends OncePerRequestFilter {

	@Autowired
	private TokenUseCase tokenUseCase;

	@Autowired
	private SearchByUserDetailsUseCase searchByUserDetailsUseCase;

	@Override
	protected void doFilterInternal(
		HttpServletRequest req,
		HttpServletResponse res,
		FilterChain filterChain
	) throws ServletException, IOException, UsernameNotFoundException {
		var token = this.recoverToken(req);
		if (token != null) {
			var login = this.tokenUseCase.validate(token);
			UserDetails user =
				this.searchByUserDetailsUseCase.loadUserByUsername(login);

			var authentication = new UsernamePasswordAuthenticationToken(
				user,
				null,
				user.getAuthorities()
			);
			SecurityContextHolder.getContext()
				.setAuthentication(authentication);
		}
		filterChain.doFilter(req, res);
	}

	private String recoverToken(HttpServletRequest req) {
		var authHeader = req.getHeader("Authorization");
		if (authHeader == null) return null;
		return authHeader.replace("Bearer ", "");
	}
}
