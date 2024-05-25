package com.seuportfolio.registryapi.modules.globals.useCases;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.repositories.TagRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import com.seuportfolio.registryapi.utils.errors.UseCaseException;
import com.seuportfolio.registryapi.utils.errors.enums.UseCaseTagEnum;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateTagsUseCase {

	@Autowired
	private BaseContentRepo baseContentRepo;

	@Autowired
	private TagRepo tagRepo;

	@Transactional
	public BaseContentEntity exec(
		UUID baseContentId,
		BaseContentCategoryEnum category,
		UserEntity user,
		List<String> insertTags,
		List<String> deleteTags
	) throws UseCaseException {
		Optional<BaseContentEntity> optOrg =
			this.baseContentRepo.findByUserIdAndIdAndCategory(
					user.getId(),
					baseContentId,
					category.getValue()
				);
		if (optOrg.isEmpty()) throw new UseCaseException(
			"Base content not found. Category: " + category.getValue(),
			UseCaseTagEnum.CONTENT_NOT_FOUND
		);

		var org = optOrg.get();

		this.tryDeleteTags(org, deleteTags);
		this.tryUpdateTags(org, insertTags);

		return this.baseContentRepo.save(org);
	}

	private void tryDeleteTags(
		BaseContentEntity baseContent,
		List<String> tagsToDelete
	) {
		List<TagEntity> tagList = baseContent.getTagEntity();

		if (tagsToDelete != null && tagList != null) {
			var removedTags = new ArrayList<TagEntity>();
			for (String tagToDelete : tagsToDelete) tagList.removeIf(tag -> {
				if (tag.getName().equals(tagToDelete)) {
					removedTags.add(tag);
					return true;
				}

				return false;
			});

			this.tagRepo.deleteAll(removedTags);
		}
	}

	private void tryUpdateTags(
		BaseContentEntity org,
		List<String> tagsToUpdate
	) {
		var tagList = org.getTagEntity();
		if (tagsToUpdate != null) for (String tagsToAdd : tagsToUpdate) {
			var tag = TagEntity.builder()
				.name(tagsToAdd)
				.baseContentEntity(org)
				.build();
			tagList.add(tag);
		}

		this.tagRepo.saveAll(tagList);
	}
}
