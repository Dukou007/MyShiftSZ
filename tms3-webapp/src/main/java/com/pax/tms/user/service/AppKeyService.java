package com.pax.tms.user.service;

import com.pax.common.service.IBaseService;
import com.pax.tms.open.api.model.AppClient;

public interface AppKeyService extends IBaseService<AppClient, Long> {

	AppClient getAppClient(String appKey);

	String getAppKey(String userName);
	
	String getUserAppKey(String userName);

	String saveOrUpdateAppKey(String userName);

	void removeAppKey(String userName);

	void deleteAppClient(Long userId);
}
