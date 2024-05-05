package com.seuportfolio.registryapi.modules.user.useCases;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.modules.user.repositories.UserRepo;
import jakarta.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Service
public class RefreshTokenUseCase {

	@Value("${api.security.refreshtoken.secret}")
	private String secret;

	@Value("${api.security.refreshtoken.expiration.hour}")
	private int hour;

	@Value("${spring.application.name}")
	private String apiName;

	@Value("${api.security.refreshtoken.secure}")
	private boolean secure;

	@Value("${api.security.refreshtoken.domain}")
	private String domain;

	@Value("${api.security.refreshtoken.httpOnly}")
	private boolean httpOnly;

	@Autowired
	private UserRepo userRepo;

	public Cookie gen(UserEntity user)
		throws JWTCreationException, AssertionError {
		Instant now = LocalDateTime.now(ZoneOffset.UTC).toInstant(
			ZoneOffset.of("-03:00")
		);

		Instant exp = LocalDateTime.now(ZoneOffset.UTC)
			.plusHours(this.hour)
			.toInstant(ZoneOffset.of("-03:00"));

		var token = this.genToken(user, exp);
		var formattedToken = "s:" + token;

		try {
			var cookie = new Cookie(
				"refresh-token",
				URLEncoder.encode(
					formattedToken,
					StandardCharsets.UTF_8.toString()
				)
			);
			cookie.setSecure(this.secure);
			cookie.setDomain(this.domain);
			cookie.setHttpOnly(this.httpOnly);
			cookie.setPath("/");
			cookie.setAttribute("SameSite", "Strict");

			long maxAge = (exp.toEpochMilli() - now.toEpochMilli()) / 1000;
			cookie.setMaxAge((int) maxAge);

			return cookie;
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("Unsupported charset: UTF-8");
		}
	}

	public UserEntity validate(String value)
		throws JWTVerificationException, UsernameNotFoundException {
		try {
			var decodedValue = URLDecoder.decode(
				value,
				StandardCharsets.UTF_8.toString()
			);

			var token = decodedValue.replace("s:", "");

			Algorithm algorithm = Algorithm.HMAC256(this.secret);
			var sub = JWT.require(algorithm)
				.withIssuer(this.apiName)
				.build()
				.verify(token)
				.getSubject();

			Optional<UserEntity> user = this.userRepo.findByEmail(sub);
			if (user.isEmpty()) throw new UsernameNotFoundException(
				"User not found"
			);

			return user.get();
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("Unsupported charset: UTF-8");
		}
	}

	private String genToken(UserEntity user, Instant exp) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(this.secret);
			String token = JWT.create()
				.withIssuer(this.apiName)
				.withSubject(user.getEmail())
				.withExpiresAt(exp)
				.sign(algorithm);

			return token;
		} catch (JWTCreationException e) {
			throw new RuntimeException("Error while generating token", e);
		}
	}
}
