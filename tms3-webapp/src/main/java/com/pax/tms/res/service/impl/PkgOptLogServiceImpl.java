package com.pax.tms.res.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.fs.FileManagerUtils;
import com.pax.common.service.impl.BaseService;
import com.pax.fastdfs.exception.FdfsServerException;
import com.pax.fastdfs.proto.ErrorCodeConstants;
import com.pax.tms.res.dao.PkgOptLogDao;
import com.pax.tms.res.model.PkgOptLog;
import com.pax.tms.res.service.PkgOptLogService;

@Service("pkgOptLogServiceImpl")
public class PkgOptLogServiceImpl extends BaseService<PkgOptLog, Long> implements PkgOptLogService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PkgOptLogServiceImpl.class);

	@Autowired
	private PkgOptLogDao pkgOptLogDao;

	@Override
	public IBaseDao<PkgOptLog, Long> getBaseDao() {

		return pkgOptLogDao;
	}

	@Override
	public void doProcessPkgOptLog() {
		List<String> filePaths = pkgOptLogDao.getOptLogFilePaths();
		if (CollectionUtils.isEmpty(filePaths)) {
			return;
		}
		for (String filePath : filePaths) {
			try {
				FileManagerUtils.getFileManager().deleteFile(filePath);
			} catch (FdfsServerException e) {
				if (e.getErrorCode() == ErrorCodeConstants.ERR_NO_ENOENT) {
					LOGGER.debug("{} - Can't find the FastDFS server node or files", filePath);
				} else {
					throw e;
				}
			}
			pkgOptLogDao.deletePkgOptLog(filePath);
		}
	}

}
