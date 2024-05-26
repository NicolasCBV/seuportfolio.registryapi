package com.seuportfolio.registryapi.modules.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoggerInterceptor implements HandlerInterceptor {

	private static Logger log = LoggerFactory.getLogger(
		LoggerInterceptor.class
	);

	@Override
	public boolean preHandle(
		HttpServletRequest req,
		HttpServletResponse res,
		Object handler
	) {
		UUID reqId = UUID.randomUUID();
		LoggerInterceptor.log.info(
			"The url \"" +
			req.getRequestURI() +
			"\" is being accesed with method \"" +
			req.getMethod() +
			"\" with id \"" +
			reqId +
			"\""
		);
		System.out.println(handler.toString());
		req.setAttribute("req_id", reqId);

		return true;
	}

	@Override
	public void afterCompletion(
		HttpServletRequest req,
		HttpServletResponse res,
		Object handler,
		Exception ex
	) {
		LoggerInterceptor.log.info(
			"Request with id \"" +
			req.getAttribute("req_id") +
			"\" ended with status " +
			res.getStatus()
		);
	}
}
