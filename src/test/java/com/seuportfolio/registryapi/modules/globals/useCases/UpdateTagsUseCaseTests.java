package com.seuportfolio.registryapi.modules.globals.useCases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

import com.seuportfolio.registryapi.modules.globals.modals.BaseContentCategoryEnum;
import com.seuportfolio.registryapi.modules.globals.modals.BaseContentEntity;
import com.seuportfolio.registryapi.modules.globals.modals.TagEntity;
import com.seuportfolio.registryapi.modules.globals.repositories.BaseContentRepo;
import com.seuportfolio.registryapi.modules.globals.repositories.TagRepo;
import com.seuportfolio.registryapi.modules.user.modals.UserEntity;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

public class UpdateTagsUseCaseTests {

	@Mock
	private BaseContentRepo baseContentRepo;

	@Mock
	private TagRepo unusedTagRepo;

	@Autowired
	@InjectMocks
	private UpdateTagsUseCase updateTagsUseCase;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("it should be able to update tags")
	void updateTagsSuccessCase() {
		var user = UserEntity.builder()
			.fullName("John Doe")
			.email("johndoe@email.com")
			.password("123456")
			.build();

		var tag = TagEntity.builder().name("old tag name").build();
		var tagList = new ArrayList<TagEntity>(1);
		tagList.add(tag);

		var org = BaseContentEntity.builder()
			.name("org")
			.description("description")
			.tagEntity(tagList)
			.build();
		tag.setBaseContentEntity(org);

		when(
			baseContentRepo.findByUserIdAndIdAndCategory(
				user.getId(),
				org.getId(),
				BaseContentCategoryEnum.ORGANIZATION.getValue()
			)
		).thenReturn(Optional.of(org));

		var insertTags = new ArrayList<String>(1);
		insertTags.add("new tag");

		var removeTags = new ArrayList<String>(1);
		removeTags.add(tag.getName());
		assertDoesNotThrow(
			() ->
				this.updateTagsUseCase.exec(
						org.getId(),
						BaseContentCategoryEnum.ORGANIZATION,
						user,
						insertTags,
						removeTags
					)
		);
	}
}
