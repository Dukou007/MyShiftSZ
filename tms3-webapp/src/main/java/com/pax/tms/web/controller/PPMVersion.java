package com.pax.tms.web.controller;

public final class PPMVersion {

	private static String version;

	private static String mainVersion;

	private PPMVersion() {
		// Utility
	}

	public static String getPPMVersion() {
		return version;
	}

	public static void setPPMVersion(String version) {
		PPMVersion.version = version;
	}

	public static String getPPMMainVersion() {
		return mainVersion;
	}

	public static void setPPMMainVersion(String mainVersion) {
		PPMVersion.mainVersion = mainVersion;
	}
}
