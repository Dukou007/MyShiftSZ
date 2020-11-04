package com.pax.login;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;

public class TmsPac4jPrincipal implements Serializable {

	private static final long serialVersionUID = 4386562745409001326L;

	private final LinkedHashMap<String, CommonProfile> profiles;

	public TmsPac4jPrincipal() {
		// do nothing
		this.profiles = new LinkedHashMap<>();
	}

	public TmsPac4jPrincipal(LinkedHashMap<String, CommonProfile> profiles) {
		this.profiles = profiles;
	}

	/**
	 * Get the main profile of the authenticated user.
	 *
	 * @return the main profile
	 */
	public CommonProfile getProfile() {
		return ProfileHelper.flatIntoOneProfile(this.profiles).get();
	}

	/**
	 * Get all the profiles of the authenticated user.
	 *
	 * @return the list of profiles
	 */
	public List<CommonProfile> getProfiles() {
		return ProfileHelper.flatIntoAProfileList(this.profiles);
	}

}
