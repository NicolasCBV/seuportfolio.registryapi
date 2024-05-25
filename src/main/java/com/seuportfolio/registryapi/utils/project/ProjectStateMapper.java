package com.seuportfolio.registryapi.utils.project;

import com.seuportfolio.registryapi.modules.projects.modals.ProjectStateEnum;

public class ProjectStateMapper {

	public static ProjectStateEnum fromStringToEnum(String state) {
		return state.equals("finished")
			? ProjectStateEnum.FINISHED
			: ProjectStateEnum.IN_PROGRESS;
	}

	public static short fromStringToShort(String state) {
		return state.equals("finished") ? (short) 1 : (short) 0;
	}

	public static String fromShortToString(short state) {
		return state == 1 ? "finished" : "in_progress";
	}
}
