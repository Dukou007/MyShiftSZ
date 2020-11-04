package com.pax.tms.user.dao;

import java.util.List;

import com.pax.common.dao.ICommonDao;

public interface AdminDao extends ICommonDao {

	/**
	 * @param sql
	 */
	void executeSql(String sql);

	List<String> selectSql(String sql);

}
