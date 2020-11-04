/*		
 * ============================================================================		
 *  COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.	
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Alan Jiang           	Modify
 * ============================================================================		
 */
package com.pax.tms.report.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pax.common.dao.IBaseDao;
import com.pax.tms.terminal.model.TerminalDownload;

public interface TerminalDownloadDao extends IBaseDao<TerminalDownload, Long> {

	void deleteTerminalDownload(Long deployId);

	void deleteTerminalDownloads(List<Long> deployIds);
	
	void updateTerminalDownloadStatus(List<Long> overDueDeployIds);

	Long getTerminalReportbyTSN(String tsn);

	List<Map<String, Object>> getDownloadPkgUsageInfo();
	
	Long getStatusCount(Long groupId, Date dayStart, String itemStatus);
}
