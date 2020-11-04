/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: // Detail description about the function of this module,		
 *             // interfaces with the other modules, and dependencies. 		
 * Revision History:		
 * Date	                 Author	                Action
 * 20170112	             Jaden           	    Modify
 * 20170209              Aaron                  Modify
 * ============================================================================		
 */
package com.pax.tms.res.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.deploy.dao.DeployDao;
import com.pax.tms.report.dao.TerminalDownloadDao;
import com.pax.tms.res.dao.PkgDao;
import com.pax.tms.res.dao.PkgGroupDao;
import com.pax.tms.res.dao.PkgOptLogDao;
import com.pax.tms.res.dao.PkgUsageInfoDao;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgUsageInfo;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.res.service.PkgUsageInfoService;
import com.pax.tms.user.service.AuditLogService;

@Service("pkgUsageInfoServiceImpl")
public class PkgUsageInfoServiceImpl extends BaseService<PkgUsageInfo, Long> implements PkgUsageInfoService {

	@Autowired
	private PkgUsageInfoDao pkgUsageInfoDao;

	@Autowired
	private PkgDao pkgDao;

	@Autowired
	private DeployDao deployDao;

	@Autowired
	private PkgGroupDao pkgGroupDao;

	@Autowired
	private PkgOptLogDao pkgOptLogDao;

	@Autowired
	private TerminalDownloadDao terminalDownloadDao;

	@Autowired
	private PkgService pkgService;

	@Autowired
	private AuditLogService auditLogService;

	@Override
	public IBaseDao<PkgUsageInfo, Long> getBaseDao() {
		return pkgUsageInfoDao;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateUsageInfo() {

		List<Map<String, Object>> pkgInfoList = pkgDao.getPkgIdsAndTime();
		if (CollectionUtils.isEmpty(pkgInfoList)) {
			return;
		}

		List<Long> deployPkgIds = deployDao.getDeployPkgIds();
		List<Map<String, Object>> deployPkgInfoList = new ArrayList<>();
		List<Map<String, Object>> notDeployPkgInfoList = new ArrayList<>();
		// update deployPkg usageTime
		for (Map<String, Object> map : pkgInfoList) {
			if (CollectionUtils.isEmpty(deployPkgIds)) {
				break;
			}
			Long pkgId = (Long) map.get("pkgId");
			if (deployPkgIds.contains(pkgId)) {
				Map<String, Object> pkgInfoMap = new HashMap<String, Object>();
				pkgInfoMap.put("pkgId", pkgId);
				pkgInfoMap.put("usageTime", new Date());
				deployPkgInfoList.add(pkgInfoMap);
			} else {
				notDeployPkgInfoList.add(map);
			}
		}

		List<Map<String, Object>> downloadPkgInfoList = terminalDownloadDao.getDownloadPkgUsageInfo();
		// update notDeployPkgTime
		for (Map<String, Object> pkgMap : notDeployPkgInfoList) {
			if (CollectionUtils.isEmpty(downloadPkgInfoList)) {
				break;
			}
			Long pkgId = (Long) pkgMap.get("pkgId");
			Date usageTime = (Date) pkgMap.get("usageTime");
			for (Map<String, Object> downloadMap : downloadPkgInfoList) {
				Long downloadPkgId = (Long) downloadMap.get("pkgId");
				Date downloadUsageTime = (Date) downloadMap.get("usageTime");
				if (pkgId == downloadPkgId && usageTime.before(downloadUsageTime)) {
					pkgMap.put("usageTime", downloadUsageTime);
				}
			}
		}

		List<Long> existPkgIds = pkgUsageInfoDao.getPkgIds();
		deployPkgInfoList.addAll(notDeployPkgInfoList);
		Object[] needUpdateAndSaveUsageInfo = getNeedUpdateAndSaveUsageInfo(deployPkgInfoList, existPkgIds);
		List<Map<String, Object>> needToUpdateInfo = (List<Map<String, Object>>) needUpdateAndSaveUsageInfo[0];
		List<Map<String, Object>> needToSaveInfo = (List<Map<String, Object>>) needUpdateAndSaveUsageInfo[1];

		updatePkgUsageInfo(needToUpdateInfo);
		savePkgUsageInfo(needToSaveInfo);

	}

	public Object[] getNeedUpdateAndSaveUsageInfo(List<Map<String, Object>> pkgInfoList, List<Long> existPkgIds) {
		Object[] object = new Object[2];
		if (CollectionUtils.isEmpty(existPkgIds)) {
			object[0] = Collections.emptyList();
			object[1] = pkgInfoList;
			return object;
		}
		List<Map<String, Object>> needToUpdateInfo = new ArrayList<>();
		List<Map<String, Object>> needToSaveInfo = new ArrayList<>();
		for (Map<String, Object> map : pkgInfoList) {
			Long pkgId = (Long) map.get("pkgId");
			if (existPkgIds.contains(pkgId)) {
				needToUpdateInfo.add(map);
			} else {
				needToSaveInfo.add(map);
			}
		}
		object[0] = needToUpdateInfo;
		object[1] = needToSaveInfo;
		return object;
	}

	public void savePkgUsageInfo(List<Map<String, Object>> needToSaveInfo) {
		if (CollectionUtils.isEmpty(needToSaveInfo)) {
			return;
		}
		for (Map<String, Object> map : needToSaveInfo) {
			PkgUsageInfo pkgUsageInfo = new PkgUsageInfo();
			Long pkgId = (Long) map.get("pkgId");
			Pkg pkg = pkgService.get(pkgId);
			Date usageTime = (Date) map.get("usageTime");
			pkgUsageInfo.setPkg(pkg);
			pkgUsageInfo.setLastOptTime(usageTime);
			pkgUsageInfoDao.save(pkgUsageInfo);
		}
	}

	public void updatePkgUsageInfo(List<Map<String, Object>> needToUpdateInfo) {
		if (CollectionUtils.isEmpty(needToUpdateInfo)) {
			return;
		}
		for (Map<String, Object> map : needToUpdateInfo) {
			Long pkgId = (Long) map.get("pkgId");
			PkgUsageInfo pkgUsageInfo = pkgUsageInfoDao.getPkgUsageInfoByPkgId(pkgId);
			Date usageTime = (Date) map.get("usageTime");
			pkgUsageInfo.setLastOptTime(usageTime);
			pkgUsageInfoDao.update(pkgUsageInfo);
		}
	}

	@Override
	public void clearPkgAndUsageInfo(Date when) {

		List<Long> getNotUsedPkgIds = pkgUsageInfoDao.getNotUsedPkgIds(when);
		if (CollectionUtils.isEmpty(getNotUsedPkgIds)) {
			return;
		}

		List<Pkg> pkgInfo = clearPkg(getNotUsedPkgIds);
		for (Pkg pkg : pkgInfo) {
			auditLogService
					.addAuditLogs("system clean package Name " + pkg.getName() + ", Version " + pkg.getVersion());
		}

		pkgUsageInfoDao.clearPkgAndUsageInfo(when);
	}

	private List<Pkg> clearPkg(List<Long> pkgList) {
		if (CollectionUtils.isEmpty(pkgList)) {
			return Collections.emptyList();
		}
		pkgGroupDao.deletePkgGroupByPkgId(pkgList);

		Collection<Long> unassignedPkgIds = pkgGroupDao.getPkgUnAssignToGroup(pkgList);
		if (CollectionUtils.isEmpty(unassignedPkgIds)) {

			return Collections.emptyList();
		}
		List<String> packageFilePaths = pkgDao.getPkgFilePaths(pkgList);
		List<String> programFilePaths = pkgDao.programFilePaths(pkgList);
		packageFilePaths.addAll(programFilePaths);
		pkgOptLogDao.insertOptLogs(packageFilePaths);
		List<Pkg> pkgInfo = pkgDao.getNamesById(pkgList);
		pkgDao.delete(unassignedPkgIds);
		return pkgInfo;
	}

}
