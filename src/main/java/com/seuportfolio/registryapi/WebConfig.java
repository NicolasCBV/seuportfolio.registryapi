package com.seuportfolio.registryapi;

import com.seuportfolio.registryapi.modules.interceptors.LoggerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${clients.allow.main.url}")
	private String clientUrl;

	@Value("${client.allow.endpoints}")
	private String allowedEndpoints;

	private static Logger log = LoggerFactory.getLogger(
		LoggerInterceptor.class
	);

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoggerInterceptor());
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		WebConfig.log.info(
			"Enabling CORS based on the following pattern: " + this.clientUrl
		);
		registry
			.addMapping(this.allowedEndpoints)
			.allowedHeaders("*")
			.allowedMethods("GET", "POST")
			.allowedOrigins(this.clientUrl);
	}
}
