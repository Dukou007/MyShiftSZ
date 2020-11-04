package com.pax.tms.res.service;

import com.pax.common.service.IBaseService;
import com.pax.tms.res.model.PkgOptLog;

public interface PkgOptLogService extends IBaseService<PkgOptLog, Long> {

	void doProcessPkgOptLog();

}
