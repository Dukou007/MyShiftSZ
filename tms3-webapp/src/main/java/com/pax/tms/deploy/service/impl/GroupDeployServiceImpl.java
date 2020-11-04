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
 * ============================================================================		
 */
package com.pax.tms.deploy.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.pax.common.dao.DbHelper;
import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.Page;
import com.pax.common.service.impl.BaseService;
import com.pax.common.util.DateTimeUtils;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.deploy.dao.DeployDao;
import com.pax.tms.deploy.dao.GroupDeployDao;
import com.pax.tms.deploy.dao.GroupDeployHistoryDao;
import com.pax.tms.deploy.dao.TerminalDeployDao;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.domain.TerminalDeployInfo;
import com.pax.tms.deploy.model.Deploy;
import com.pax.tms.deploy.model.GroupDeploy;
import com.pax.tms.deploy.model.GroupDeployHistory;
import com.pax.tms.deploy.service.DeployParaService;
import com.pax.tms.deploy.service.GroupDeployHistoryService;
import com.pax.tms.deploy.service.GroupDeployService;
import com.pax.tms.deploy.service.TerminalDeployService;
import com.pax.tms.deploy.web.form.AbstractGroupDeployForm;
import com.pax.tms.deploy.web.form.EditGroupDeployForm;
import com.pax.tms.deploy.web.form.GroupChangeParaForm;
import com.pax.tms.deploy.web.form.GroupDeployForm;
import com.pax.tms.deploy.web.form.GroupDeployOperatorForm;
import com.pax.tms.deploy.web.form.QueryCurrentGroupDeployForm;
import com.pax.tms.deploy.web.form.QueryHistoryGroupDeployForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.service.EventGrpService;
import com.pax.tms.monitor.web.form.OperatorEventForm;
import com.pax.tms.pub.web.form.RedisOperatorForm;
import com.pax.tms.report.dao.TerminalDownloadDao;
import com.pax.tms.report.service.TerminalDownloadService;
import com.pax.tms.res.model.Model;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgSchema;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.res.service.PkgSchemaService;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.terminal.dao.TerminalDao;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.web.form.OperatorLogForm;

@Service("groupDeployServiceImpl")
public class GroupDeployServiceImpl extends BaseService<GroupDeploy, Long> implements GroupDeployService {

	@Autowired
	private GroupDeployDao groupDeployDao;

	@Autowired
	private GroupService groupService;
	@Autowired
	private GroupDeployHistoryService groupDeployHistoryService;
	@Autowired
	private AuditLogService auditLogService;
	@Autowired
	private EventGrpService eventService;

	@Autowired
	private DeployDao deployDao;

	@Autowired
	private DeployParaService deployParaService;
	@Autowired
	private TerminalService terminalService;

	@Autowired
	private TerminalDeployDao terminalDeployDao;
	@Autowired
	private TerminalDeployService terminalDeployService;
	@Autowired
	private GroupDeployHistoryDao groupDeployHistoryDao;

	@Autowired
	private PkgService pkgService;
	@Autowired
	private PkgSchemaService pkgSchemaService;
	@Autowired
	private ModelService modelService;

	@Autowired
	TerminalDownloadService terminalDownloadService;
	@Autowired
	private TerminalDownloadDao terminalDownloadDao;

	@Autowired
	private TerminalDao terminalDao;

	@Override
	public IBaseDao<GroupDeploy, Long> getBaseDao() {

		return groupDeployDao;
	}

	@Override
	public void deploy(GroupDeployForm command) {
		validateInput(command);
		Group group = groupService.validateGroup(command.getGroupId());
		Pkg pkg = pkgService.validatePkg(command.getPkgId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		AclManager.checkPermissionOfPkg(command.getLoginUserId(), pkg);
		AclManager.checkPermissionOfPkgByGroup(command.getGroupId(), pkg);
		Model model = modelService.validateModel(command.getDestModel());
		command.setGroupName(group.getName());
		Deploy deploy = createDeploy(command, model, pkg);
		deploy.setDeployType(Deploy.TYPE_PKG);
		processDeployParams(pkg, deploy, command);

		deploy.save();

		GroupDeploy groupDeploy = new GroupDeploy();
		groupDeploy.setDeployTime(deploy.getDeployTime());
		groupDeploy.setGroup(group);
		groupDeploy.setDeploy(deploy);
		groupDeploy.setCreator(command.getLoginUsername());
		groupDeploy.setCreateDate(command.getRequestTime());
		groupDeploy.setModifier(command.getLoginUsername());
		groupDeploy.setModifyDate(command.getRequestTime());
		groupDeployDao.save(groupDeploy);

		deployGroupTaskCopyToTerminal(deploy, command);
		// group 本身任务不需要添加来源
		deploy.setSourceDeploy(null);
		deploy.setDeploySourceGroup(null);
		deploy.update();

		if (!"PAX".equalsIgnoreCase(group.getName())) {
			auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.ADD_DEPLOY_GROUP,
					deploy.getPkg().getName() + ", Version " + deploy.getPkg().getVersion() + " to Group "
							+ group.getNamePath());
		} else {
			auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.ADD_DEPLOY_GROUP,
					deploy.getPkg().getName() + ", Version " + deploy.getPkg().getVersion() + " to Group PAX ");
		}

		eventService.addEventLog(group.getId(),
				"Group add deploy:" + deploy.getPkg().getName() + "/" + deploy.getPkg().getVersion());

	}
	
	@Override
    public void deploykey(GroupDeployForm command) {
        Group group = groupService.validateGroup(command.getGroupId());
        AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        Model model = modelService.validateModel(command.getDestModel());
        command.setGroupName(group.getName());
        Deploy deploy = createDeploy(command, model, null);
        deploy.setDeployType(Deploy.TYPE_KEY);
        deploy.setLatestType(command.getLatestType());
        deploy.save();
        GroupDeploy groupDeploy = new GroupDeploy();
        groupDeploy.setDeployTime(deploy.getDeployTime());
        groupDeploy.setGroup(group);
        groupDeploy.setDeploy(deploy);
        groupDeploy.setCreator(command.getLoginUsername());
        groupDeploy.setCreateDate(command.getRequestTime());
        groupDeploy.setModifier(command.getLoginUsername());
        groupDeploy.setModifyDate(command.getRequestTime());
        groupDeployDao.save(groupDeploy);
        
        deployGroupKeyTaskCopyToTerminal(deploy, command);
        // group 本身任务不需要添加来源
        deploy.setSourceDeploy(null);
        deploy.setDeploySourceGroup(null);
        deploy.update();
        String keyVersion = command.getLatestType() == 1 ? "Highest Version":"Lastest Upload Version";
        if (!"PAX".equalsIgnoreCase(group.getName())) {
            auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.ADD_DEPLOY_GROUP_KEY, "Offline RKI Keys, Version " + keyVersion + " to Group " + group.getNamePath());
        } else {
            auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.ADD_DEPLOY_GROUP_KEY, "Offline RKI Keys, Version " + keyVersion + " to Group PAX ");
        }
        
        eventService.addEventLog(group.getId(), "Group add deploy: Offline RKI Keys" + "/" +  keyVersion);
    }

	private void processDeployParams(Pkg pkg, Deploy deploy, AbstractGroupDeployForm command) {
		if (command.getPkgSchemaId() != null) {
			PkgSchema pkgSchema = pkgSchemaService.get(command.getPkgSchemaId());
			Document doc = pkgSchemaService.getPkgSchemaBySchemaPath(pkgSchema.getFilePath());
			Map<String, String> defaultParams = pkgSchemaService.getParamSetFromMongoDB(pkg.getParamSet());
			Map<String, String> resultMap = pkgSchemaService.setParamValueForSchema(doc, defaultParams,
					command.getParamMap());
			resultMap.put("_id", UUID.randomUUID().toString());
			// mongoTemplate.save(resultMap, "bposparam");
			String paraSetId = resultMap.get("_id");
			deploy.setPkgSchema(pkgSchema);
			deploy.setParamSet(paraSetId);
			deploy.setParamVersion(DateTimeUtils.date2String(command.getRequestTime(), "yyyyMMddHHmmss"));
		}
	}

	private Deploy createDeploy(AbstractGroupDeployForm command, Model model, Pkg pkg) {
		Deploy deploy = new Deploy();
		deploy.setPkg(pkg);
		deploy.setModel(model);
		deploy.setTimeZone(command.getTimeZone());
		if (command.getPkgSchemaId() != null) {
			deploy.setPkgSchema(pkgSchemaService.get(command.getPkgSchemaId()));
		}
		deploy.setDaylightSaving(command.isDaylightSaving());
		deploy.setDeletedWhenDone(command.isDeletedWhenDone());
		deploy.setTrmInheritGroup(groupService.get(command.getGroupId()));
		deploy.setDwnlStartTime(command.getDwnlStartTime());
		if (command.getDwnlEndTime() != null) {
			deploy.setDwnlEndTime(command.getDwnlEndTime());
		}
		deploy.setActvStartTime(command.getActvStartTime());
		deploy.setTimeZone(command.getTimeZone());
		deploy.setDeploySource(command.getGroupName());
		deploy.setDeployTime(command.getRequestTime().getTime());

		deploy.setDownReTryCount(command.getDownReTryCount() == null ? 1 : command.getDownReTryCount());
		deploy.setActvReTryCount(command.getActvReTryCount() == null ? 1 : command.getActvReTryCount());
		deploy.setCreator(command.getLoginUsername());
		deploy.setCreateDate(command.getRequestTime());
		deploy.setModifier(command.getLoginUsername());
		deploy.setModifyDate(command.getRequestTime());
		return deploy;
	}

	private void deployGroupTaskCopyToTerminal(Deploy groupDeploy, GroupDeployForm command) {

		List<TerminalDeployInfo> terminalDeployInfos = terminalDeployDao.getTerminalDeployInfos(command.getGroupId(),
				command.getDestModel());
		if (CollectionUtils.isEmpty(terminalDeployInfos)) {
			groupDeployDao.flush();
			deployParaService.doProcessDeployList();
			return;
		}

		Map<String, Deploy> tsnDeployMap = new HashMap<String, Deploy>();
		Map<String, String> groupParamMap = null;
		for (TerminalDeployInfo terminalDeployInfo : terminalDeployInfos) {

			// Group 部署了应用和参数
			if (StringUtils.isNotEmpty(groupDeploy.getParamVersion())) {

				if (groupParamMap == null || groupParamMap.isEmpty()) {
					groupParamMap = pkgSchemaService.getParamSetFromMongoDB(groupDeploy.getParamSet());
				}
				// 应用名相同
				if (StringUtils.equals(groupDeploy.getPkg().getName(), terminalDeployInfo.getPkgName())) {

					mergeTerminalParamToGroup(groupParamMap, command, groupDeploy, tsnDeployMap, terminalDeployInfo);

				} else {
					// terminal 没有部署任务，直接copy group 任务
					if (terminalDeployInfo.getDeployId() == null) {

						copyGroupParamToTerminal(groupParamMap, command, groupDeploy, tsnDeployMap, terminalDeployInfo,
								false);

					}
					// terminal部署了任务，但应用名不相同
					else {
						Deploy deploy = createDeploy(command, groupDeploy.getModel(), groupDeploy.getPkg());
						deploy.setSourceDeploy(groupDeploy);
						deploy.setDeployTime(groupDeploy.getDeployTime());
						tsnDeployMap.put(terminalDeployInfo.getTsn(), deploy);
					}

				}

			}
			// group 只部署了应用，没部署参数
			else {
				Deploy deploy = createDeploy(command, groupDeploy.getModel(), groupDeploy.getPkg());
				deploy.setSourceDeploy(groupDeploy);
				tsnDeployMap.put(terminalDeployInfo.getTsn(), deploy);

			} // end 是否group 部署应用

		} // end for 循环

		groupDeployDao.copyGroupTaskToTerminals(tsnDeployMap, command);
		groupDeployDao.flush();
		deployParaService.doProcessDeployList();

		// send redis message
		List<Map<String, String>> msgList = terminalService.getTerminalStatusChangedMessage(tsnDeployMap.keySet(),
				RedisOperatorForm.DEPLOY);
		terminalService.sendTerminalStatusChangedMessage(msgList);

	}
	
	private void deployGroupKeyTaskCopyToTerminal(Deploy groupDeploy, GroupDeployForm command) {
	    //查询组下的offlinekey
        List<Pkg> offlineKeyList = pkgService.getLatestPkgByGroupAndModel(command.getGroupId(),command.getDestModel(), command.getLatestType());
        
        Map<String, Deploy> tsnDeployMap = new HashMap<>();
        for (Pkg pkg : offlineKeyList) {
            Deploy deploy = createDeploy(command, groupDeploy.getModel(), pkg);
            deploy.setSourceDeploy(groupDeploy);
            deploy.setDeployTime(groupDeploy.getDeployTime());
            tsnDeployMap.put(pkg.getSn(), deploy);
        }

        groupDeployDao.copyGroupTaskToTerminals(tsnDeployMap, command);
        groupDeployDao.flush();

        // send redis message
        List<Map<String, String>> msgList = terminalService.getTerminalStatusChangedMessage(tsnDeployMap.keySet(),
                RedisOperatorForm.DEPLOY);
        terminalService.sendTerminalStatusChangedMessage(msgList);

    }

	private void copyGroupParamToTerminal(Map<String, String> groupParamMap, AbstractGroupDeployForm command,
			Deploy groupDeploy, Map<String, Deploy> tsnDeployMap, TerminalDeployInfo terminalDeployInfo,
			boolean isChangeDeloy) {
		Deploy deploy = createDeploy(command, groupDeploy.getModel(), groupDeploy.getPkg());
		deploy.setSourceDeploy(groupDeploy);
		groupParamMap.put("_id", UUID.randomUUID().toString());
		// mongoTemplate.save(groupParamMap, "bposparam");
		deploy.setParamSet(groupParamMap.get("_id"));
		if (isChangeDeloy) {
			deploy.setParamVersion(DateTimeUtils.date2String(command.getRequestTime(), "yyyyMMddHHmmss"));
		}

		tsnDeployMap.put(terminalDeployInfo.getTsn(), deploy);

	}

	private void mergeTerminalParamToGroup(Map<String, String> groupParamMap, GroupDeployForm command,
			Deploy groupDeploy, Map<String, Deploy> tsnDeployMap, TerminalDeployInfo terminalDeployInfo) {
		Deploy deploy = createDeploy(command, groupDeploy.getModel(), groupDeploy.getPkg());

		Map<String, String> terminalParamMap = pkgSchemaService
				.getParamSetFromMongoDB(terminalDeployInfo.getParamSet());

		Iterator<Entry<String, String>> it = groupParamMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String key = entry.getKey();
			if (StringUtils.isEmpty(entry.getValue()) && terminalParamMap != null
					&& terminalParamMap.containsKey(key)) {
				groupParamMap.put(key, terminalParamMap.get(key));
			}
		}
		groupParamMap.put("_id", UUID.randomUUID().toString());
		// mongoTemplate.save(groupParamMap, "bposparam");

		deploy.setParamSet(groupParamMap.get("_id"));
		deploy.setParamVersion(DateTimeUtils.date2String(command.getRequestTime(), "yyyyMMddHHmmss"));
		tsnDeployMap.put(terminalDeployInfo.getTsn(), deploy);

	}

	private void validateInput(GroupDeployForm command) {

		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (command.getPkgId() == null) {
			throw new BusinessException("msg.pkg.required");
		}

	}

	@Override
	public void changeDeployParas(GroupChangeParaForm command) {
		validateInput(command);
		Group group = groupService.validateGroup(command.getGroupId());
		Pkg pkg = pkgService.validatePkg(command.getPkgId());
		AclManager.checkPermissionOfPkg(command.getLoginUserId(), pkg);
		Model model = modelService.validateModel(command.getDestModel());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		command.setGroupName(group.getName());
		// record history deploy info
		List<DeployInfo> deployInfos = deployDao.getDeployInfos(group.getId(), model.getId(), pkg.getId());
		// bath insert to deploy history table;
		groupDeployHistoryDao.insertDeployHistorys(deployInfos, command);

		Deploy deploy = createDeploy(command, model, pkg);
		processDeployParams(pkg, deploy, command);

		List<Long> deployIds = deployDao.getDeployIds(group.getId(), model.getId(), pkg.getId());
		deployDao.updateDeploys(deployIds, deploy, command);
		deployParaService.deleteDeployParas(deployIds);
		changeDeployGroupTaskToTerminals(command, deploy);
		if(null != deploy.getPkg()){
		    auditLogService.addAuditLog(Arrays.asList(group.getNamePath()), command, OperatorLogForm.GROUP,
	                " global change deploy " + deploy.getPkg().getName() + "  " + deploy.getPkg().getVersion());

	        eventService.addEventLog(group.getId(),
	                "Group global change deploy:" + deploy.getPkg().getName() + "/" + deploy.getPkg().getVersion());
        }else{
            String keyVersion = deploy.getLatestType() == 1 ? "Highest Version":"Lastest Upload Version";
            auditLogService.addAuditLog(Arrays.asList(group.getNamePath()), command, OperatorLogForm.GROUP,
                    " global change deploy Offline RKI Keys " + keyVersion);

            eventService.addEventLog(group.getId(),
                    "Group global change deploy Offline RKI Keys / "+ keyVersion );
        }

	}

	private void changeDeployGroupTaskToTerminals(GroupChangeParaForm command, Deploy groupDeploy) {

		List<TerminalDeployInfo> terminalDeployInfos = terminalDeployDao.getTerminalDeployInfos(command.getGroupId(),
				command.getDestModel());
		if (CollectionUtils.isEmpty(terminalDeployInfos)) {
			groupDeployDao.flush();
			deployParaService.doProcessDeployList();
			return;
		}

		Map<String, Deploy> tsnDeployMap = new HashMap<String, Deploy>();
		// 给所有的tsn 增加group 任务
		Map<String, String> groupParamMap = null;
		for (TerminalDeployInfo terminalDeployInfo : terminalDeployInfos) {

			// Group 部署了应用和参数

			if (StringUtils.isNotEmpty(groupDeploy.getParamVersion())) {
				if (groupParamMap == null || groupParamMap.isEmpty()) {
					groupParamMap = pkgSchemaService.getParamSetFromMongoDB(groupDeploy.getParamSet());
				}
				// 部署的应用名相同
				if (StringUtils.equals(groupDeploy.getPkg().getName(), terminalDeployInfo.getPkgName())) {

					mergeGroupParamToTerminal(groupParamMap, command, groupDeploy, tsnDeployMap, terminalDeployInfo);

				} else {
					// terminal 没有部署任务，直接copy group 任务
					if (terminalDeployInfo.getDeployId() == null) {

						copyGroupParamToTerminal(groupParamMap, command, groupDeploy, tsnDeployMap, terminalDeployInfo,
								true);
					}
					// terminal部署了任务，但应用名不相同
					else {
						Deploy deploy = createDeploy(command, groupDeploy.getModel(), groupDeploy.getPkg());
						tsnDeployMap.put(terminalDeployInfo.getTsn(), deploy);
					}

				}

			}
			// group 只部署了应用，没部署参数
			else {
				Deploy deploy = createDeploy(command, groupDeploy.getModel(), groupDeploy.getPkg());
				deploy.setDeployTime(groupDeploy.getDeployTime());
				tsnDeployMap.put(terminalDeployInfo.getTsn(), deploy);

			} // end 是否group 部署应用

		} // end for 循环
		groupDeployDao.copyGroupTaskToTerminals(tsnDeployMap, command);
		groupDeployDao.flush();
		deployParaService.doProcessDeployList();

		// send redis message
		List<Map<String, String>> msgList = terminalService.getTerminalStatusChangedMessage(tsnDeployMap.keySet(),
				RedisOperatorForm.EDIT_DEPLOY);
		terminalService.sendTerminalStatusChangedMessage(msgList);

	}

	private void mergeGroupParamToTerminal(Map<String, String> groupParamMap, GroupChangeParaForm command,
			Deploy groupDeploy, Map<String, Deploy> tsnDeployMap, TerminalDeployInfo terminalDeployInfo) {

		Deploy deploy = createDeploy(command, groupDeploy.getModel(), groupDeploy.getPkg());

		Map<String, String> terminalParamMap = pkgSchemaService
				.getParamSetFromMongoDB(terminalDeployInfo.getParamSet());
		// 应用名相同但有可能是pxmaster的包，没有参数
		if (terminalParamMap == null || terminalParamMap.isEmpty()) {

			terminalParamMap = groupParamMap;

		} else {
			Iterator<Entry<String, String>> it = terminalParamMap.entrySet().iterator();

			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				String key = entry.getKey();
				if (StringUtils.isNotEmpty(groupParamMap.get(key)) && groupParamMap.containsKey(key)) {
					terminalParamMap.put(key, groupParamMap.get(key));
				}
			}

		}

		terminalParamMap.put("_id", UUID.randomUUID().toString());
		// mongoTemplate.save(terminalParamMap, "bposparam");
		deploy.setParamSet(terminalParamMap.get("_id"));
		deploy.setParamVersion(DateTimeUtils.date2String(command.getRequestTime(), "yyyyMMddHHmmss"));
		tsnDeployMap.put(terminalDeployInfo.getTsn(), deploy);

	}

	private void validateInput(GroupChangeParaForm command) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (command.getPkgId() == null) {
			throw new BusinessException("msg.pkg.required");
		}
		if (StringUtils.isEmpty(command.getDestModel())) {
			throw new BusinessException("model.Required");
		}
		if (StringUtils.isEmpty(command.getTimeZone())) {
			throw new BusinessException("msg.timeZone.Required");
		}

	}

	@Override
	public Page<DeployInfo> getCurrentDeploysByGroupId(QueryCurrentGroupDeployForm command) {
		return getLineDeploysByGroupId(command);
	}

	@Override
	public Page<DeployInfo> getLineDeploysByGroupId(QueryCurrentGroupDeployForm command) {
		if (command.getGroupId() == null) {
			Collections.emptyList();
		}
		Group group = groupService.get(command.getGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		Page<DeployInfo> page = super.page(command);
		DeployInfo.updateGroupInheritStatus(group.getId(), page.getItems());
		return page;
	}

	@Override
	public Page<DeployInfo> getHistoryDeploysByGroupId(QueryHistoryGroupDeployForm command) {
		if (command.getGroupId() == null) {
			Collections.emptyList();
		}
		Group group = groupService.get(command.getGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		Page<DeployInfo> page = groupDeployHistoryService.getHistoryDeploysByGroupId(command);

		return page;
	}

	@Override
	public void activate(GroupDeployOperatorForm command) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(command.getGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (command.isInherit()) {
			throw new BusinessException("tsn.InheritDeployTask");
		}

		Deploy deploy = deployDao.get(command.getDeployId());
		String pkgName = "Offline RKI Keys";
		String pkgVersion = null;
		if (deploy != null) {
			deploy.setStatus(1);
			deploy.setModifier(command.getLoginUsername());
			deploy.setModifyDate(command.getRequestTime());
			if  (deploy.getPkg() != null) {
				pkgName = deploy.getPkg().getName();
				pkgVersion = deploy.getPkg().getVersion();
			}
		}
		List<Long> deployIds = deployDao.getTerminalInheritDeployIds(command.getGroupId());
		deployDao.updateDeployStatus(deployIds, Deploy.STATUS_ENABLE, command);
		auditLogService.addAuditLog(Arrays.asList(group.getNamePath()), command, OperatorLogForm.GROUP,
				" active deploy " + pkgName + "  " + pkgVersion);

		eventService.addEventLog(group.getId(),
				"Group active deploy:" + pkgName + "/" + pkgVersion);
	}

	@Override
	public void deactivate(GroupDeployOperatorForm command) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(command.getGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (command.isInherit()) {
			throw new BusinessException("tsn.InheritDeployTask");
		}
		String pkgName = null;
		String pkgVersion = null;
		Deploy deploy = deployDao.get(command.getDeployId());
		if (deploy != null) {
			deploy.setStatus(0);
			deploy.setModifier(command.getLoginUsername());
			deploy.setModifyDate(command.getRequestTime());
			if (deploy.getPkg() != null) {
				pkgName = deploy.getPkg().getName();
				pkgVersion = deploy.getPkg().getVersion();
			}
		}
		List<Long> deployIds = deployDao.getTerminalInheritDeployIds(command.getGroupId());
		deployDao.updateDeployStatus(deployIds, Deploy.STATUS_DISABLE, command);
		auditLogService.addAuditLog(Arrays.asList(group.getNamePath()), command, OperatorLogForm.GROUP,
				" deactive deploy " + pkgName + " " + pkgVersion);

		eventService.addEventLog(group.getId(),
				"Group deactive deploy:" + pkgName + "/" + pkgVersion);

	}

	@Override
	public void delete(GroupDeployOperatorForm command) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(command.getGroupId());
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (command.isInherit()) {
			throw new BusinessException("tsn.InheritDeployTask");

		}
		// record to history
		DeployInfo deployInfo = groupDeployDao.getDeployInfo(command.getDeployId());
		if (deployInfo != null) {
			GroupDeployHistory groupDeployHistory = createGroupDeployHistory(deployInfo, command);
			groupDeployHistory.save();
		}
		terminalDeployService.deleteTerminalInheritTaskFromGroup(command.getDeployId());
		deployParaService.deleteDeployParas(Arrays.asList(command.getDeployId()));
		groupDeployDao.deleteGroupDeploy(command.getDeployId());
		Deploy deploy = deployDao.get(command.getDeployId());
		if (deploy != null) {
			deploy.delete();
			if(null != deploy.getPkg()){
			    auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.DELETE_DEPLOY_GROUP,
	                    deploy.getPkg().getName() + ", Version " + deploy.getPkg().getVersion() + " to Group "
	                            + group.getNamePath());

	            eventService.addEventLog(group.getId(),
	                    "Group delete deploy:" + deploy.getPkg().getName() + "/" + deploy.getPkg().getVersion());
			}else{
			    String keyVersion = deploy.getLatestType() == 1 ? "Highest Version":"Lastest Upload Version";
			    auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.DELETE_DEPLOY_GROUP_KEY,
			            "Offline RKI Keys, Version " +keyVersion  + " to Group "
	                            + group.getNamePath());

	            eventService.addEventLog(group.getId(),
	                    "Group delete deploy: Offline RKI Keys/"+keyVersion);
			}
			

		}

	}

	private GroupDeployHistory createGroupDeployHistory(DeployInfo deployInfo, GroupDeployOperatorForm command) {
		GroupDeployHistory groupDeployHistory = new GroupDeployHistory();
		if (StringUtils.isNotEmpty(deployInfo.getDestModel())) {
			groupDeployHistory.setModel(modelService.get(deployInfo.getDestModel()));
		}
		groupDeployHistory.setStatus(deployInfo.getStatus());
		groupDeployHistory.setPkg(pkgService.get(deployInfo.getPkgId()));
		groupDeployHistory.setGroup(groupService.get(command.getGroupId()));
		groupDeployHistory.setPkgName(deployInfo.getPkgName());
		groupDeployHistory.setDeploySource(deployInfo.getDeploySource());
		groupDeployHistory.setPkgVersion(deployInfo.getPkgVersion());
		groupDeployHistory.setParamSet(deployInfo.getParamSet());
		groupDeployHistory.setParamVersion(deployInfo.getParamVersion());
		if (deployInfo.getPkgSchemaId() != null) {
			groupDeployHistory.setPkgSchema(pkgSchemaService.get(deployInfo.getPkgSchemaId()));
		}
		groupDeployHistory.setDwnlStartTime(deployInfo.getDwnlStartTime());
		groupDeployHistory.setDownReTryCount(deployInfo.getDownReTryCount());
		groupDeployHistory.setActvReTryCount(deployInfo.getActvReTryCount());
		groupDeployHistory.setActvStartTime(deployInfo.getActvStartTime());
		groupDeployHistory.setDwnlOrder(deployInfo.isDwnlOrder());
		groupDeployHistory.setForceUpdate(deployInfo.isForceUpdate());
		groupDeployHistory.setOnlyParam(deployInfo.isOnlyParam());
		groupDeployHistory.setCreator(command.getLoginUsername());
		groupDeployHistory.setCreateDate(command.getRequestTime());
		groupDeployHistory.setModifier(command.getLoginUsername());
		groupDeployHistory.setModifyDate(command.getRequestTime());

		return groupDeployHistory;
	}

	@Override
	public void edit(EditGroupDeployForm command) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (StringUtils.isEmpty(command.getTimeZone())) {
			throw new BusinessException("msg.timeZone.Required");
		}
		Group group = groupService.get(command.getGroupId());
		if (command.getPkgSchemaId() == null) {
			throw new BusinessException("template.required");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);

		// record to history
		DeployInfo deployInfo = groupDeployDao.getDeployInfo(command.getDeployId());
		if (deployInfo != null) {
			GroupDeployHistory groupDeployHistory = createGroupDeployHistory(deployInfo, command);
			groupDeployHistory.save();
		}

		Deploy deploy = deployDao.get(command.getDeployId());
		Pkg pkg = deploy.getPkg();
		Document doc = pkgSchemaService.getPkgSchemaBySchemaPath(pkg.getSchemaFilePath());
		Map<String, String> defaultParams = pkgSchemaService.getParamSetFromMongoDB(pkg.getParamSet());
		Map<String, String> resultMap = pkgSchemaService.setParamValueForSchema(doc, defaultParams,
				command.getParaMap());
		resultMap.put(PkgSchema.PARAM_SET_ID_KEY, UUID.randomUUID().toString());
		// mongoTemplate.save(resultMap, PkgSchema.PARAM_COLLECTION);
		deploy.setParamSet(resultMap.get(PkgSchema.PARAM_SET_ID_KEY));
		deploy.setParamStatus(0);
		deploy.setDwnlStartTime(command.getDwnlStartTime());
		deploy.setActvStartTime(command.getActvStartTime());
		deploy.setModifier(command.getLoginUsername());
		deploy.setModifyDate(command.getRequestTime());
		deploy.setParamVersion(DateTimeUtils.date2String(command.getRequestTime(), "yyyyMMddHHmmss"));
		deploy.update();
		groupDeployDao.updateDeployTime(command.getRequestTime(), command.getDeployId());
		deployParaService.deleteDeployParas(Arrays.asList(command.getDeployId()));
		deployParaService.processDeploy(deploy);

		if(null != deploy.getPkg()){
		    auditLogService.addAuditLog(Arrays.asList(group.getNamePath()), command, OperatorLogForm.GROUP,
	                " edit deploy " + deploy.getPkg().getName() + " " + deploy.getPkg().getVersion());
	        eventService.addEventLog(Arrays.asList(group.getNamePath()), OperatorEventForm.GROUP,
	                " edit deploy " + deploy.getPkg().getName() + "  " + deploy.getPkg().getVersion());
		}else{
		    String keyVersion = deploy.getLatestType() == 1 ? "Highest Version":"Lastest Upload Version";
		    auditLogService.addAuditLog(Arrays.asList(group.getNamePath()), command, OperatorLogForm.GROUP,
                    " edit deploy Offline RKI Keys " + keyVersion);
            eventService.addEventLog(Arrays.asList(group.getNamePath()), OperatorEventForm.GROUP,
                    " edit deploy Offline RKI Keys " + keyVersion);
		}
		

	}

	@Override
	public void copyAncestorGroupTaskToTerminals(Long batchId, Collection<String> tsnNeedToCopyTask, Long groupId,
			BaseForm command, boolean isNew) {
		if (CollectionUtils.isEmpty(tsnNeedToCopyTask)) {
			return;
		}
		List<Object[]> groupDeploys = groupDeployDao.getGroupDeploys(groupId);
		if (CollectionUtils.isEmpty(groupDeploys)) {
			return;
		}
		if (isNew) {
			Map<String, List<Deploy>> tsnInheritGroupDeploys = getTsnInheritGroupDeploys(tsnNeedToCopyTask,
					groupDeploys);

			groupDeployDao.insertInheritDeploys(tsnInheritGroupDeploys, command);
		} else {

			Map<String, List<Long>> tsnInheritGroupDeployIds = deployDao.getTsnInheritSourceGroupId(batchId, groupId);
			Map<String, List<Deploy>> tsnNeedToCopyGroupTask = getNeedToCopyGroupTask(groupDeploys,
					tsnInheritGroupDeployIds, tsnNeedToCopyTask);
			groupDeployDao.insertInheritDeploys(tsnNeedToCopyGroupTask, command);

		}

	}

	@Override
	public void copyNewLineGroupTaskToTerminals(Map<String, Collection<Long>> tsnGroupIdsMap, Group targetGroup,
			BaseForm command) {
		if (tsnGroupIdsMap == null || tsnGroupIdsMap.isEmpty()) {
			return;
		}
		Iterator<Entry<String, Collection<Long>>> it = tsnGroupIdsMap.entrySet().iterator();
		Map<String, List<Deploy>> tsnNeedToInheritDeployMap = new HashMap<>();
		while (it.hasNext()) {
			Entry<String, Collection<Long>> entry = it.next();
			String tsn = entry.getKey();
			String destModel = terminalService.get(tsn).getModel().getId();
			Collection<Long> groupIds = entry.getValue();
			if (CollectionUtils.isEmpty(groupIds)) {
				continue;
			}

			Collection<Long> needInheritGroupIds = getNeedInheritGroupIds(groupIds, targetGroup);
			if (CollectionUtils.isEmpty(needInheritGroupIds)) {
				continue;
			}
			List<Object[]> selfGroupDeploys = groupDeployDao.getSelfGroupDeploys(needInheritGroupIds);
			List<Deploy> tsnNeedInheritDeploys = getNeedInheritDeploy(selfGroupDeploys, destModel);
			if (CollectionUtils.isEmpty(tsnNeedInheritDeploys)) {
				continue;
			}
			tsnNeedToInheritDeployMap.put(tsn, tsnNeedInheritDeploys);
		}
		groupDeployDao.insertInheritDeploys(tsnNeedToInheritDeployMap, command);

	}

	private Collection<Long> getNeedInheritGroupIds(Collection<Long> groupIds, Group targetGroup) {
		List<Long> arrayGroupIds = targetGroup.getIdPathArrayList();
		Collections.reverse(arrayGroupIds);
		if (CollectionUtils.isEmpty(groupIds)) {
			return arrayGroupIds;
		}
		Collection<Long> needInheritGroupIds = new ArrayList<Long>();
		for (Long groupId : arrayGroupIds) {
			if (groupIds.contains(groupId)) {
				break;
			}
			needInheritGroupIds.add(groupId);
		}
		return needInheritGroupIds;
	}

	@Override
	public void copyAllGroupTaskToTerminalCascading(Long batchId, BaseForm command) {

		Map<String, List<Object[]>> groupDeploys = groupDeployDao.getAllGroupDeploys(batchId);
		Map<String, List<Deploy>> tsnInheritGroupDeploys = getTsnInheritGroupDeploys(groupDeploys);
		groupDeployDao.insertInheritDeploys(tsnInheritGroupDeploys, command);

	}

	private Map<String, List<Deploy>> getTsnInheritGroupDeploys(Collection<String> tsnNeedToCopyTask,
			List<Object[]> groupDeploys) {

		Map<String, List<Deploy>> tsnInheritGroupDeploys = new HashMap<String, List<Deploy>>();
		for (String tsn : tsnNeedToCopyTask) {
			List<Deploy> needInheritDeploy = getNeedInheritDeploy(groupDeploys,
					terminalService.get(tsn).getModel().getId());

			tsnInheritGroupDeploys.put(tsn, needInheritDeploy);
		}
		return tsnInheritGroupDeploys;
	}

	private Map<String, List<Deploy>> getTsnInheritGroupDeploys(Map<String, List<Object[]>> groupDeploys) {

		if (groupDeploys == null || groupDeploys.isEmpty()) {
			return Collections.emptyMap();

		}
		Map<String, List<Deploy>> tsnInheritGroupDeploys = new HashMap<String, List<Deploy>>();
		Iterator<Entry<String, List<Object[]>>> it = groupDeploys.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, List<Object[]>> entry = it.next();
			String tsn = entry.getKey();
			List<Object[]> groupDeploy = entry.getValue();
			List<Deploy> needInheritDeploy = getNeedInheritDeploy(groupDeploy,
					terminalService.get(tsn).getModel().getId());
			tsnInheritGroupDeploys.put(tsn, needInheritDeploy);

		}
		return tsnInheritGroupDeploys;

	}

	private Map<String, List<Deploy>> getNeedToCopyGroupTask(List<Object[]> groupDeploys,
			Map<String, List<Long>> tsnInheritGroupDeployIds, Collection<String> tsnNeedToCopyTask) {

		if (tsnInheritGroupDeployIds == null || tsnInheritGroupDeployIds.isEmpty()) {
			return getTsnInheritGroupDeploys(tsnNeedToCopyTask, groupDeploys);
		}
		Map<String, List<Deploy>> tsnNedInheritDeploy = new HashMap<>();
		Iterator<Entry<String, List<Long>>> it = tsnInheritGroupDeployIds.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, List<Long>> entry = it.next();
			String tsn = entry.getKey();
			String destModel = terminalService.get(tsn).getModel().getId();
			List<Long> sourceGroupId = tsnInheritGroupDeployIds.get(tsn);
			List<Object[]> needInheritDeployIds = getNeedInheritDeployIds(groupDeploys, sourceGroupId);
			if (CollectionUtils.isEmpty(needInheritDeployIds)) {
				continue;
			}
			List<Deploy> needInheritDeploy = getNeedInheritDeploy(needInheritDeployIds, destModel);

			tsnNedInheritDeploy.put(tsn, needInheritDeploy);
		}

		return tsnNedInheritDeploy;
	}

	private List<Deploy> getNeedInheritDeploy(List<Object[]> groupDeploys, String destModel) {
		List<Deploy> deploys = new ArrayList<Deploy>();
		for (Object[] groupDeploy : groupDeploys) {

			String deployModel = (String) groupDeploy[3];
			Deploy inheritDeploy = deployDao.get((Long) groupDeploy[0]);

			if (StringUtils.isEmpty(deployModel)) {

				Deploy deploy = createDeploy(groupDeploy);
				deploy.setSourceDeploy(inheritDeploy);
				deploys.add(deploy);
			} else {

				if (StringUtils.equals(deployModel, destModel)) {
					Deploy deploy = createDeploy(groupDeploy);
					deploy.setSourceDeploy(inheritDeploy);
					deploys.add(deploy);
				}

			}

		}
		return deploys;
	}

	private Deploy createDeploy(Object[] groupDeploy) {

		Long id = (Long) groupDeploy[0];
		Long groupSourceId = (Long) groupDeploy[1];
		Long deployTime = (Long) groupDeploy[2];
		boolean daylightSaving = (boolean) groupDeploy[4];
		Deploy deploy = deployDao.get(id);
		deploy.setTrmInheritGroup(groupService.get(groupSourceId));
		deploy.setDeployTime(deployTime);
		deploy.setDaylightSaving(daylightSaving);

		return deploy;
	}

	private List<Object[]> getNeedInheritDeployIds(List<Object[]> groupDeployIds, List<Long> sourceGroupIds) {

		List<Object[]> needInheritDeployIds = new ArrayList<Object[]>();
		for (Object[] deployId : groupDeployIds) {
			Long groupId = (Long) deployId[1];

			if (!sourceGroupIds.contains(groupId)) {
				needInheritDeployIds.add(deployId);
			}

		}
		return needInheritDeployIds;
	}

	@Override
	public void copySourceTerminalTask(Collection<String> tsnNeedToCopyTask, BaseForm command, Terminal terminal,
			boolean isNew) {
		if (CollectionUtils.isEmpty(tsnNeedToCopyTask)) {
			return;
		}

		List<Object[]> deployIdAndTimes = deployDao.getTerminalSelfTask(terminal.getTsn());

		if (CollectionUtils.isEmpty(deployIdAndTimes)) {
			return;
		}
		if (isNew) {
			handleNewTsnTask(tsnNeedToCopyTask, deployIdAndTimes, command);
		} else {
			handleExistTsnTask(tsnNeedToCopyTask, deployIdAndTimes, command);
		}

	}

	private void handleExistTsnTask(Collection<String> tsnNeedToCopyTask, List<Object[]> deployIdAndTimes,
			BaseForm command) {
		long batchId = DbHelper.generateId("TMSPTSNS_BATCH_ID", 1);
		terminalDao.addTemporaryTsns(batchId, tsnNeedToCopyTask);
		Map<String, List<Long>> deployTimeTsns = deployDao.getDeployTimeTsns(tsnNeedToCopyTask, batchId);
		Map<String, List<Deploy>> tsnNeedToCopyGroupTask = new HashMap<String, List<Deploy>>();

		for (String tsn : tsnNeedToCopyTask) {
			List<Deploy> deploys = getNeedCopyDeploy(deployIdAndTimes, deployTimeTsns.get(tsn));
			if (CollectionUtils.isEmpty(deploys)) {
				continue;
			}
			tsnNeedToCopyGroupTask.put(tsn, deploys);
		}
		groupDeployDao.insertInheritDeploys(tsnNeedToCopyGroupTask, command);
		terminalDao.deleteTemporaryTsns(batchId);

	}

	private void handleNewTsnTask(Collection<String> tsnNeedToCopyTask, List<Object[]> deployIdAndTimes,
			BaseForm command) {
		Map<String, List<Deploy>> tsnNeedToCopyGroupTask = new HashMap<String, List<Deploy>>();
		for (String tsn : tsnNeedToCopyTask) {
			List<Deploy> deploys = getNeedCopyDeploy(deployIdAndTimes);

			tsnNeedToCopyGroupTask.put(tsn, deploys);
		}
		groupDeployDao.insertInheritDeploys(tsnNeedToCopyGroupTask, command);
	}

	private List<Deploy> getNeedCopyDeploy(List<Object[]> deployIdAndTimes, List<Long> existTsnDeployTime) {
		List<Deploy> deploys = new ArrayList<Deploy>();
		if (CollectionUtils.isEmpty(existTsnDeployTime)) {
			return getNeedCopyDeploy(deployIdAndTimes);
		}
		for (Object[] object : deployIdAndTimes) {
			Long deployTime = (Long) object[1];
			if (existTsnDeployTime.contains(deployTime)) {
				continue;
			}
			Long deployId = (Long) object[0];
			boolean daylightSaving = (boolean) object[2];
			Deploy d = deployDao.get(deployId);
			d.setDeployTime(deployTime);
			d.setDaylightSaving(daylightSaving);
			deploys.add(d);
		}
		return deploys;
	}

	private List<Deploy> getNeedCopyDeploy(List<Object[]> deployIdAndTimes) {
		List<Deploy> deploys = new ArrayList<Deploy>();
		for (Object[] object : deployIdAndTimes) {
			Long deployId = (Long) object[0];
			Long deployTime = (Long) object[1];
			boolean daylightSaving = (boolean) object[2];
			Deploy d = deployDao.get(deployId);
			d.setDeployTime(deployTime);
			d.setDaylightSaving(daylightSaving);
			deploys.add(d);
		}
		return deploys;

	}

	@Override
	public void deleteChildGroupTaskCacading(long batchId, Group targetGroup) {

		Map<String, List<Long>> tsnDeployIdsMap = deployDao.getTsnInheritDeployIds(batchId, targetGroup.getId());
		deleteTaskCascading(tsnDeployIdsMap);
	}

	@Override
	public void deleteAllTaskFromGroup(Long batchId) {

		Map<String, List<Long>> tsnDeployIdsMap = deployDao.getTsnAllInheritDeployIds(batchId);
		deleteTaskCascading(tsnDeployIdsMap);

	}

	private void deleteTaskCascading(Map<String, List<Long>> tsnDeployIdsMap) {
		terminalDeployDao.deleteTerminalDeploys(tsnDeployIdsMap);
		List<Long> deleteDeployIds = getNeedDeleteDeployIds(tsnDeployIdsMap);
		terminalDownloadDao.deleteTerminalDownloads(deleteDeployIds);
		deployParaService.deleteDeployParas(deleteDeployIds);
		deployDao.deleteTaskFromGroupCascading(deleteDeployIds);

	}

	private List<Long> getNeedDeleteDeployIds(Map<String, List<Long>> tsnDeployIdsMap) {
		if (tsnDeployIdsMap == null || tsnDeployIdsMap.isEmpty()) {
			return Collections.emptyList();

		}
		List<Long> deployIds = new ArrayList<Long>();
		Iterator<Entry<String, List<Long>>> it = tsnDeployIdsMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, List<Long>> entry = it.next();
			deployIds.addAll(entry.getValue());
		}
		return deployIds;
	}

	@Override
	public void deleteByGroup(Long groupId) {

		groupDeployDao.deleteGroupDeployCascading(groupId);
	}

	@Override
	public void deleteOverDueDeployment(Date when) {

		List<Long> overDueDeployIds = deployDao.getOverdueDeployIds(when);
		if (CollectionUtils.isEmpty(overDueDeployIds)) {
			return;
		}
		// delete terminalDeploy
		deployParaService.deleteDeployParas(overDueDeployIds);
		terminalDeployDao.deleteTerminalDeploys(overDueDeployIds);
		terminalDownloadDao.updateTerminalDownloadStatus(overDueDeployIds);
		// delete groupDeploy
		groupDeployDao.deleteGroupDeploys(overDueDeployIds);
		for (Long deployId : overDueDeployIds) {
			Deploy deploy = deployDao.get(deployId);
			if (deploy == null) {
				continue;
			}
			if(null != deploy.getPkg()){
			    auditLogService.addAuditLogs("system delete the task of overdue deploying Package "
	                    + deploy.getPkg().getName() + ", Version " + deploy.getPkg().getVersion());
			}else{
			    String keyVersion = deploy.getLatestType() == 1 ? "Highest Version":"Lastest Upload Version";
			    auditLogService.addAuditLogs("system delete the task of overdue deploying Offline RKI Keys, Version " + keyVersion);
			}
			
			deploy.delete();
		}

	}

	@Override
	public void deleteDownloadedTask() {
		List<Long> groupDownloadedTask = groupDeployDao.getGroupDownloadedTask();
		if (CollectionUtils.isEmpty(groupDownloadedTask)) {
			return;
		}
		List<Long> needDeletedDeployIds = new ArrayList<>();
		for (Long groupDeployId : groupDownloadedTask) {
			if (deployDao.get(groupDeployId).isDeletedWhenDone()) {
				needDeletedDeployIds.add(groupDeployId);
			}
		}
		if (CollectionUtils.isEmpty(needDeletedDeployIds)) {
			return;
		}
		// delete groupDeploy
		groupDeployDao.deleteGroupDeploys(needDeletedDeployIds);
		for (Long deployId : needDeletedDeployIds) {
			Deploy deploy = deployDao.get(deployId);
			if (deploy == null) {
				continue;
			}
			if(null != deploy.getPkg()){
			    auditLogService.addAuditLogs("system delete the group downloaded Task , Package Name "
	                    + deploy.getPkg().getName() + ", Version " + deploy.getPkg().getVersion());
            }else{
                String keyVersion = deploy.getLatestType() == 1 ? "Highest Version":"Lastest Upload Version";
                auditLogService.addAuditLogs("system delete the group downloaded Task Offline RKI Keys, Version " + keyVersion);
            }
			
			deploy.delete();
		}

	}

}
