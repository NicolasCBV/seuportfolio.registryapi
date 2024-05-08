package com.seuportfolio.registryapi.modules.organizations.useCases;

import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationEntity;
import com.seuportfolio.registryapi.modules.organizations.modals.OrganizationTagEntity;
import com.seuportfolio.registryapi.modules.organizations.presentation.dto.CreateOrganizationDTO;
import com.seuportfolio.registryapi.modules.organizations.repositories.OrganizationRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateOrganizationUseCase {

	@Autowired
	private OrganizationRepo organizationRepo;

	@Transactional
	public OrganizationEntity exec(CreateOrganizationDTO dto, UserEntity user) {
		var org = OrganizationEntity.builder()
			.name(dto.getName())
			.description(dto.getDescription())
			.userEntity(user)
			.build();

		List<OrganizationTagEntity> tags = new ArrayList<
			OrganizationTagEntity
		>();
		for (String tag : dto.getTags()) tags.add(
			OrganizationTagEntity.builder()
				.name(tag)
				.organizationEntity(org)
				.build()
		);

		this.organizationRepo.save(org);
		return org;
	}
}
