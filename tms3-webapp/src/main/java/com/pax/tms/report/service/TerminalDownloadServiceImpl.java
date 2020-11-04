/*
 * ============================================================================
 * = COPYRIGHT				
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.		
 * ============================================================================		
 */
package com.pax.tms.report.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.deploy.model.Deploy;
import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.service.AlertProcessService;
import com.pax.tms.report.dao.TerminalDownloadDao;
import com.pax.tms.report.web.form.QueryTerminalDownloadForm;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.model.TerminalDownload;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.security.UTCTime;

@Service("TerminalDownloadServiceImpl")
public class TerminalDownloadServiceImpl extends BaseService<TerminalDownload, Long>
		implements TerminalDownloadService {

	@Autowired
	private TerminalDownloadDao tdownloadDao;

	@Autowired
	private GroupService groupService;

	@Override
	public IBaseDao<TerminalDownload, Long> getBaseDao() {
		return tdownloadDao;
	}

	@Autowired
	TerminalService terminalService;
	
	@Autowired
	AlertProcessService alertProcessService;
	
	@Autowired
	TerminalDownloadService terminalDownloadService;
	
	@SuppressWarnings("unchecked")
	@Override
	public <E, S extends IPageForm> Page<E> page(S command) {
		QueryTerminalDownloadForm form = (QueryTerminalDownloadForm) command;
		// check group permission
		groupService.checkPermissionOfGroup(form, form.getGroupId());

		long totalCount = getBaseDao().count(command);
		int index = command.getPageIndex();
		int size = command.getPageSize();
		int firstResult = Page.getPageStart(totalCount, index, size);

		List<Map<String, Object>> list = getBaseDao().page(command, firstResult, size);

		list.forEach(va -> {
			if ("NOACTIVITION".equals(va.get("actvStatus").toString())) {
				va.put("actvStatus", "-");
			}else if("SUCCESS".equals(va.get("actvStatus").toString())){
				va.put("actvStatus", "SUCCESSFUL");
			}
			Date now = new Date();
			Date expireDate = (Date) va.get("expireDate");
			if (expireDate != null) {
				if (expireDate.getTime() <= now.getTime()) {
					if (va.get("downStatus") != null && 
							("PENDING".equals(va.get("downStatus").toString()) || "DOWNLOADING".equals(va.get("downStatus").toString()))) {
						va.put("downStatus", "EXPIRED");
						va.put("actvStatus", "-");
					}
				}
			}
			if ("DOWNLOADING".equals(va.get("downStatus").toString())) {
				va.put("downStatus", "PENDING");
			}else if("SUCCESS".equals(va.get("downStatus").toString())){
				va.put("downStatus", "SUCCESSFUL");
			}
		});
		return Page.getPage(index, size, totalCount, (List<E>) list);
	}
	/**
	 * 把符合条件的数据都拿出来，然后把过期的downloading状态去除再进行逻辑分页
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <E, S extends IPageForm> Page<E> getPendingPage(S command) {
		QueryTerminalDownloadForm form = (QueryTerminalDownloadForm) command;
		// check group permission
		groupService.checkPermissionOfGroup(form, form.getGroupId());
		String itemStatus = "";
		if(null != form.getDownStatusType() && !"".equals(form.getDownStatusType())){
			itemStatus = form.getDownStatusType();
		} else {
			itemStatus = form.getActiStatusType();
		}
		Long totalCount = terminalDownloadService.getStatusCount(form.getGroupId(), itemStatus);
		int index = command.getPageIndex();
		int size = command.getPageSize();

		List<Map<String, Object>> list = getBaseDao().list(command);
		list.forEach(va -> {
			if ("NOACTIVITION".equals(va.get("actvStatus").toString())) {
				va.put("actvStatus", "-");
			}else if("SUCCESS".equals(va.get("actvStatus").toString())){
				va.put("actvStatus", "SUCCESSFUL");
			}
			Date now = new Date();
			Date expireDate = (Date) va.get("expireDate");
			if (expireDate != null) {
				if (expireDate.getTime() <= now.getTime()) {
					if (va.get("downStatus") != null && 
							("PENDING".equals(va.get("downStatus").toString()) || "DOWNLOADING".equals(va.get("downStatus").toString()))) {
						va.put("downStatus", "EXPIRED");
						va.put("actvStatus", "-");
					}
				}
			}
			if ("DOWNLOADING".equals(va.get("downStatus").toString())) {
				va.put("downStatus", "PENDING");
			}else if("SUCCESS".equals(va.get("downStatus").toString())){
				va.put("downStatus", "SUCCESSFUL");
			}
		});
		list = checkPendingPage(list);
		list = listToPage(list, index, command.getPageSize().intValue());
		return Page.getPage(index, size, totalCount, (List<E>) list);
	}

	@Override
	public void save(Terminal terminal, Deploy deploy) {
		TerminalDownload terminalDownload = new TerminalDownload();
		terminalDownload.setDeploy(deploy);
		terminalDownload.setTid(terminal);
		terminalDownload.setTsn(terminal.getTid());
		terminalDownload.setModel(deploy.getModel());
		terminalDownload.setPkgName(deploy.getPkg().getName());
		terminalDownload.setPkgVersion(deploy.getPkg().getVersion());
		if (StringUtils.isEmpty(deploy.getPkg().getPgmType())) {
			terminalDownload.setPkgType(deploy.getPkg().getType());
		} else {
			terminalDownload.setPkgType(deploy.getPkg().getPgmType());
		}
		terminalDownload.setDownSchedule(deploy.getDwnlStartTime());
		terminalDownload.setActvSchedule(deploy.getActvStartTime());
		terminalDownload.setCreateDate(deploy.getCreateDate());
		terminalDownload.setModifyDate(deploy.getModifyDate());
		terminalDownload.setExpireDate(deploy.getDwnlEndTime());
		tdownloadDao.save(terminalDownload);
	}

	@Override
	public Long getTerminalReportbyTSN(String tsn) {
		return tdownloadDao.getTerminalReportbyTSN(tsn);
	}

	@Override
	public List<Map<String, Object>> getExportList(QueryTerminalDownloadForm command) {
		List<Map<String, Object>> list = getBaseDao().list(command);
		list.forEach(va -> {
			if ("NOACTIVITION".equals(va.get("actvStatus").toString())) {
				va.put("actvStatus", "-");
			}else if("SUCCESS".equals(va.get("actvStatus").toString())){
				va.put("actvStatus", "SUCCESSFUL");
			}
			Date now = new Date();
			Date expireDate = (Date) va.get("expireDate");
			if (expireDate != null) {
				if (expireDate.getTime() <= now.getTime()) {
					if (va.get("downStatus") != null && "PENDING".equals(va.get("downStatus").toString())) {
						va.put("downStatus", "EXPIRED");
						va.put("actvStatus", "-");
					}
				}
			}
			if ("DOWNLOADING".equals(va.get("downStatus").toString())) {
				va.put("downStatus", "PENDING");
			}else if("SUCCESS".equals(va.get("downStatus").toString())){
				va.put("downStatus", "SUCCESSFUL");
			}
		});
		return list;
	}
	
	@Override
	public Long getStatusCount(Long groupId, String itemStatus) {
		Date dayStart = UTCTime.getLastNHours(alertProcessService.getLastNhours());
		return tdownloadDao.getStatusCount(groupId, dayStart, itemStatus);
	}
	
	/**
	 * 检查Page，并去掉downStatus为EXPIRED的数据
	 * @param page
	 * @return
	 */
	private List<Map<String, Object>> checkPendingPage(List<Map<String, Object>> lists){
		List<Map<String, Object>> result = new ArrayList<>();
		if(null == lists){
			return result;
		}
		for(int i=0;i<lists.size();i++){
			String downStatus = lists.get(i).get("downStatus").toString();
			if(!DownOrActvStatus.EXPIRED.getName().toLowerCase().equals(downStatus.toLowerCase())){
				result.add(lists.get(i));
			}
		}
		return result;
	}
	
	/**
	 * 将lists分页返回
	 * @param lists
	 * @param pageIndex 页码,默认1
	 * @param pageSize 页数,默认10
	 * @return
	 */
	private List<Map<String, Object>> listToPage(List<Map<String, Object>> lists, int pageIndex, int pageSize){
		List<Map<String, Object>> result = new ArrayList<>();
		int pageStart = Page.getPageStart(pageIndex, pageSize);//游标起始位置
		if(null == lists || lists.isEmpty()){
			return result;
		}
		if(pageIndex <= 0){
			pageIndex = 1;
		}
		if(pageSize <= 0){
			pageSize = 10;
		}
		int listSize = lists.size();
		if(listSize > pageStart){
			for(int i=pageStart;i<listSize;i++){
				result.add(lists.get(i));
				if(result.size() >= pageSize){
					break;
				}
			}
		}
		else {
			return lists;
		}
		return result;
	}

}
