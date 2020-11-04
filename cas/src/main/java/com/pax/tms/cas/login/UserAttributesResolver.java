package com.pax.tms.cas.login;

import java.util.List;
import java.util.Map;

public interface UserAttributesResolver {
	void resolve(Map<String, List<String>> appRolesMap, TmsUserAttributes userAttributes);
}
