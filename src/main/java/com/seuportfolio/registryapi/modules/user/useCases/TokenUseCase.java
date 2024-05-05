package com.seuportfolio.registryapi.modules.user.useCases;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Service
public class TokenUseCase {

	@Value("${api.security.token.secret}")
	private String secret;

	@Value("${api.security.token.expiration.hour}")
	private int hour;

	@Value("${spring.application.name}")
	private String apiName;

	public String gen(UserEntity user) {
		var tokenPayload = new LinkedHashMap<String, String>();
		tokenPayload.put("fullName", user.getFullName());
		tokenPayload.put("description", user.getDescription());
		tokenPayload.put("createdAt", user.getCreatedAt().toString());
		tokenPayload.put("updatedAt", user.getCreatedAt().toString());

		try {
			Algorithm algorithm = Algorithm.HMAC256(this.secret);
			String token = JWT.create()
				.withIssuer(this.apiName)
				.withSubject(user.getEmail())
				.withExpiresAt(this.generateExpirationDate())
				.withPayload(tokenPayload)
				.sign(algorithm);

			return token;
		} catch (JWTCreationException e) {
			throw new RuntimeException("Error while generating token", e);
		}
	}

	public String validate(String token) {
		Algorithm algorithm = Algorithm.HMAC256(this.secret);
		return JWT.require(algorithm)
			.withIssuer(this.apiName)
			.build()
			.verify(token)
			.getSubject();
	}

	private Instant generateExpirationDate() {
		return LocalDateTime.now(ZoneOffset.UTC)
			.plusHours(this.hour)
			.toInstant(ZoneOffset.of("-03:00"));
	}
}
