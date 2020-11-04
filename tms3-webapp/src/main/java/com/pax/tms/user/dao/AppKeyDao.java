package com.pax.tms.user.dao;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.open.api.model.AppClient;

public interface AppKeyDao extends IBaseDao<AppClient, Long> {

	AppClient getAppkey(String userName);

	void removeAppClient(String userName);

	AppClient getAppClient(String appKey);

	void deleteAppClient(Long userId);
}