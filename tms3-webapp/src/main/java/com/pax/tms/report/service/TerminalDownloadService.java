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
package com.pax.tms.report.service;

import java.util.List;
import java.util.Map;

import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;
import com.pax.common.service.IBaseService;
import com.pax.tms.deploy.model.Deploy;
import com.pax.tms.report.web.form.QueryTerminalDownloadForm;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.model.TerminalDownload;

public interface TerminalDownloadService extends IBaseService<TerminalDownload, Long> {

	void save(Terminal terminal, Deploy deploy);

	Long getTerminalReportbyTSN(String tsn);

	List<Map<String, Object>> getExportList(QueryTerminalDownloadForm command);
	//获取real-status为downloads的pending和downloading或者success总数
	Long getStatusCount(Long groupId, String itemStatus);
	
	<E, S extends IPageForm> Page<E> getPendingPage(S command);
}
