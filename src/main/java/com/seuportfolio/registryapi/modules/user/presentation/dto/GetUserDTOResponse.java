package com.seuportfolio.registryapi.modules.user.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetUserDTOResponse(
	UUID id,
	String fullName,
	String email,
	String description,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {}
