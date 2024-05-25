package com.seuportfolio.registryapi.modules.globals.modals;

public enum PackageEnum {
	ORGANIZATION((short) 0),
	PROJECT((short) 1);

	private final short value;

	PackageEnum(final short value) {
		this.value = value;
	}

	public short getValue() {
		return this.value;
	}
}
