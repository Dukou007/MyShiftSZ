package com.pax.tms.user.service;

import java.util.List;

import com.pax.common.service.ICommonService;

public interface AdminService extends ICommonService {
	void executeSql(String sql);

	List<String> selectSql(String sql);
}
