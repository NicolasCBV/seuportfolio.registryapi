package com.seuportfolio.registryapi.modules.projects.modals;

public enum ProjectStateEnum {
	IN_PROGRESS((short) 0),
	FINISHED((short) 1);

	private final short value;

	ProjectStateEnum(final short value) {
		this.value = value;
	}

	public short getValue() {
		return this.value;
	}
}
