package com.pax.tms.res.dao;

import java.util.List;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.res.model.PkgOptLog;

public interface PkgOptLogDao extends IBaseDao<PkgOptLog, Long> {

	void insertOptLogs(List<String> optLogFilePaths);

	List<String> getOptLogFilePaths();

	void deletePkgOptLog(String filePath);

}
