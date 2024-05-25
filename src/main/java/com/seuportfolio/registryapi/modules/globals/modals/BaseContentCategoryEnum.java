package com.seuportfolio.registryapi.modules.globals.modals;

public enum BaseContentCategoryEnum {
	ORGANIZATION((short) 0),
	CERTIFICATION((short) 1),
	PROJECT((short) 2);

	private final Short value;

	BaseContentCategoryEnum(final short value) {
		this.value = value;
	}

	public short getValue() {
		return this.value;
	}
}
