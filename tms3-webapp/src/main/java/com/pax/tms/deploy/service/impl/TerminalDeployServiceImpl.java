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

import java.text.ParseException;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.Page;
import com.pax.common.service.impl.BaseService;
import com.pax.common.util.DateTimeUtils;
import com.pax.tms.app.broadpos.template.SchemaUtil;
import com.pax.tms.deploy.dao.DeployDao;
import com.pax.tms.deploy.dao.TerminalDeployDao;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.domain.TerminalDeployInfo;
import com.pax.tms.deploy.model.Deploy;
import com.pax.tms.deploy.model.TerminalDeploy;
import com.pax.tms.deploy.model.TerminalDeployHistory;
import com.pax.tms.deploy.service.DeployParaService;
import com.pax.tms.deploy.service.TerminalDeployHistoryService;
import com.pax.tms.deploy.service.TerminalDeployService;
import com.pax.tms.deploy.web.form.EditTerminalDeployForm;
import com.pax.tms.deploy.web.form.QueryCurrentTsnDeployForm;
import com.pax.tms.deploy.web.form.QueryHistotyTsnDeployForm;
import com.pax.tms.deploy.web.form.TerminalDeployForm;
import com.pax.tms.deploy.web.form.TerminalDeployOperatorForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.service.EventTrmService;
import com.pax.tms.monitor.web.form.OperatorEventForm;
import com.pax.tms.pub.web.form.RedisOperatorForm;
import com.pax.tms.report.dao.TerminalDownloadDao;
import com.pax.tms.report.domain.ParamHistoryInfo;
import com.pax.tms.report.service.TerminalDownloadService;
import com.pax.tms.res.model.Model;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgSchema;
import com.pax.tms.res.model.PkgType;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.res.service.PkgSchemaService;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.web.form.OperatorLogForm;

import io.vertx.core.json.JsonArray;

@Service("terminalDeployService")
public class TerminalDeployServiceImpl extends BaseService<TerminalDeploy, Long> implements TerminalDeployService {

    @Autowired
    private TerminalDeployDao terminalDeployDao;

    @Autowired
    private DeployDao deployDao;

    @Autowired
    private GroupService groupService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private DeployParaService deployParaService;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private TerminalDownloadDao terminalDownloadDao;
    @Autowired
    private EventTrmService eventService;
    @Autowired
    private TerminalDownloadService terminalDownloadService;
    @Autowired
    private TerminalDeployHistoryService terminalDeployHistoryService;
    @Autowired
    private PkgService pkgService;
    @Autowired
    private PkgSchemaService pkgSchemaService;
    @Autowired
    private ModelService modelService;

    @Override
    public IBaseDao<TerminalDeploy, Long> getBaseDao() {

        return terminalDeployDao;
    }

    @Override
    public void deploy(TerminalDeployForm command) {

        validateInput(command);
        Terminal terminal = terminalService.validateTerminal(command.getTsn());
        AclManager.checkPermissionOfTerminal(command.getLoginUserId(), terminal);
//        AclManager.checkPermissionOfTerminalByGroup(command.getGroupId(), terminal);
        
        Pkg pkg = pkgService.validatePkg(command.getPkgId());
        AclManager.checkPermissionOfPkg(command.getLoginUserId(), pkg);
        Model model = modelService.validateModel(terminal.getModel().getId());
//        AclManager.checkPermissionOfPkgByGroup(command.getGroupId(), pkg);
        
        if(StringUtils.equals(pkg.getType(), PkgType.OFFLINEKEY.getPkgName()) && !StringUtils.equals(pkg.getSn(), terminal.getTsn())){
            throw new BusinessException("msg.offlineKey.invalid");
        }

        Deploy deploy = createDeploy(command, model, pkg);
        List<ParamHistoryInfo> paramHisList = new LinkedList<>();
        processDeployParams(pkg, deploy, command, paramHisList);
        deploy.save();

        TerminalDeploy terminalDeploy = createTerminalDeploy(terminal, deploy, command);
        terminalDeployDao.save(terminalDeploy);
        terminalDeployDao.flush();

        if (command.getPkgSchemaId() != null) {
            deployParaService.processDeploy(deploy);
        } else {
            deploy.setParamStatus(Deploy.DEPARA_STATUS);
        }
        terminalDownloadService.save(terminal, deploy);
        
        if(StringUtils.equals(pkg.getType(), PkgType.OFFLINEKEY.getPkgName())){
            auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.DEPLOY_TERMINAL_KEY,
                    pkg.getName() + ", Version " + pkg.getVersion() + " to Terminal " + terminal.getTid());
        }else{
            auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.DEPLOY_TERMINAL,
                    pkg.getName() + ", Version " + pkg.getVersion() + " to Terminal " + terminal.getTid());
        }
        eventService.addEventLog(Arrays.asList(terminal.getTid()), OperatorEventForm.TERMINAL,
                " add deploy " + pkg.getName() + "  " + pkg.getVersion());

        // send redis message
        List<Map<String, String>> msgList = terminalService
                .getTerminalStatusChangedMessage(Arrays.asList(command.getTsn()), RedisOperatorForm.DEPLOY);
        terminalService.sendTerminalStatusChangedMessage(msgList);

    }

    private TerminalDeploy createTerminalDeploy(Terminal terminal, Deploy deploy, TerminalDeployForm command) {
        TerminalDeploy terminalDeploy = new TerminalDeploy();
        terminalDeploy.setTerminal(terminal);
        terminalDeploy.setDeploy(deploy);
        terminalDeploy.setDeployTime(command.getRequestTime().getTime());
        terminalDeploy.setCreator(command.getLoginUsername());
        terminalDeploy.setCreateDate(command.getRequestTime());
        terminalDeploy.setModifier(command.getLoginUsername());
        terminalDeploy.setModifyDate(command.getRequestTime());
        terminalDeploy.setAvtvFailCount(0);
        terminalDeploy.setDownFailCount(0);
        terminalDeploy.setDownSuccCount(0);
        return terminalDeploy;
    }

    @Override
    public void edit(EditTerminalDeployForm command) {
        if (StringUtils.isEmpty(command.getTsn())) {
            Collections.emptyList();
        }
        Terminal terminal = terminalService.get(command.getTsn());
        if (command.getPkgSchemaId() == null) {
            throw new BusinessException("template.required");
        }
        AclManager.checkPermissionOfTerminal(command.getLoginUserId(), terminal);

        // record to history
        DeployInfo deployInfo = terminalDeployDao.getDeployInfo(command.getDeployId());
        if (deployInfo != null) {
            TerminalDeployHistory terminalDeployHistory = createTerminalDeployHistory(deployInfo, command);
            terminalDeployHistory.save();
        }

        // Update TerminalDeploy

        updateTeminalDeploy(command);
        Deploy deploy = deployDao.get(command.getDeployId());
        auditLogService.addAuditLog(Arrays.asList(terminal.getTid()), command, OperatorLogForm.TERMINAL,
                " edit deploy " + deploy.getPkg().getName() + "  " + deploy.getPkg().getVersion());

        eventService.addEventLog(Arrays.asList(terminal.getTid()), OperatorEventForm.TERMINAL,
                " Edit deploy " + deploy.getPkg().getName() + "  " + deploy.getPkg().getVersion());

        // send redis message
        List<Map<String, String>> msgList = terminalService
                .getTerminalStatusChangedMessage(Arrays.asList(command.getTsn()), RedisOperatorForm.EDIT_DEPLOY);
        terminalService.sendTerminalStatusChangedMessage(msgList);

    }

    private void updateTeminalDeploy(EditTerminalDeployForm command) {
        Deploy deploy = deployDao.get(command.getDeployId());
        Pkg pkg = deploy.getPkg();
        Document doc = pkgSchemaService.getPkgSchemaBySchemaPath(pkg.getSchemaFilePath());
        Map<String, String> defaultParams = pkgSchemaService.getParamSetFromMongoDB(pkg.getParamSet());
        Map<String, String> oldParams = pkgSchemaService.getParamSetFromMongoDB(deploy.getParamSet());

        List<ParamHistoryInfo> paramHisList = new LinkedList<ParamHistoryInfo>();
        Map<String, String> resultMap = pkgSchemaService.setParamValueForSchema(doc, defaultParams,
                command.getParaMap(), oldParams, paramHisList);
        resultMap.put(PkgSchema.PARAM_SET_ID_KEY, UUID.randomUUID().toString());
        deploy.setParamSet(resultMap.get(PkgSchema.PARAM_SET_ID_KEY));
        deploy.setParamStatus(0);
        deploy.setTimeZone(command.getTimeZone());
        deploy.setDwnlStartTime(command.getDwnlStartTime());
        deploy.setActvStartTime(command.getActvStartTime());
        deploy.setParamVersion(DateTimeUtils.date2String(command.getRequestTime(), "yyyyMMddHHmmss"));
        deploy.setDaylightSaving(command.isDaylightSaving());
        deploy.setModifier(command.getLoginUsername());
        deploy.setModifyDate(command.getRequestTime());
        deploy.update();
        terminalDeployDao.updateDeployTime(command.getRequestTime(), command.getDeployId());
        // paramHisService.saveParamHistory(terminalId, terminalDeploy.getId(),
        // paramHisList);
        deployParaService.deleteDeployParas(Arrays.asList(command.getDeployId()));
        deployParaService.processDeploy(deploy);
    }

    private void processDeployParams(Pkg pkg, Deploy deploy, TerminalDeployForm command,
                                     List<ParamHistoryInfo> paramHisList) {
        if (command.getPkgSchemaId() != null) {

            PkgSchema pkgSchema = pkgSchemaService.get(command.getPkgSchemaId());
            Document doc = pkgSchemaService.getPkgSchemaBySchemaPath(pkgSchema.getFilePath());
            Map<String, String> defaultParams = pkgSchemaService.getParamSetFromMongoDB(pkg.getParamSet());
            Map<String, String> oldParams = this.getTrmLastParams(command.getTsn());

            Map<String, String> resultMap = pkgSchemaService.setParamValueForSchema(doc, defaultParams,
                    command.getParaMap(), oldParams, paramHisList);
            resultMap.put(PkgSchema.PARAM_SET_ID_KEY, UUID.randomUUID().toString());
            // mongoTemplate.save(resultMap, PkgSchema.PARAM_COLLECTION);
            String paraSetId = resultMap.get(PkgSchema.PARAM_SET_ID_KEY);
            deploy.setPkgSchema(pkgSchema);
            deploy.setParamSet(paraSetId);
            deploy.setParamVersion(DateTimeUtils.date2String(command.getRequestTime(), "yyyyMMddHHmmss"));
        }
    }

    @Override
    public Map<String, String> getTrmLastParams(String terminalId) {
        TerminalDeployInfo tdInfo = terminalDeployDao.getTerminalLastedDeploy(terminalId);
        if (tdInfo != null && StringUtils.isNotEmpty(tdInfo.getParamSet())) {
            return pkgSchemaService.getParamSetFromMongoDB(tdInfo.getParamSet());
        }
        return null;
    }

    @Override
    public JsonArray getTrmlastSchemaHtml(Long pkgSchemaId, String terminalId) {
        PkgSchema pkgSchema = pkgSchemaService.get(pkgSchemaId);
        if (pkgSchema == null) {
            throw new BusinessException("template.notFound");
        }
        Pkg pkg = pkgSchema.getPkg();
        Document doc = pkgSchemaService.getPkgSchemaBySchemaPath(pkg.getSchemaFilePath());
        Map<String, String> paraMap;
        TerminalDeployInfo tdInfo = terminalDeployDao.getTerminalLastedDeploy(terminalId);
        if (tdInfo != null && StringUtils.isNotEmpty(tdInfo.getParamSet())
                && pkg.getName().equals(tdInfo.getPkgName())) {
            paraMap = pkgSchemaService.getParamSetFromMongoDB(tdInfo.getParamSet());
        } else {
            paraMap = pkgSchemaService.getParamSetFromMongoDB(pkgSchema.getParamSet());
        }
        try {
            return new SchemaUtil().documentToHtml(doc, false, paraMap);
        } catch (ParseException e) {
            throw new BusinessException("msg.pkg.schema.invalid", e);
        }
    }

    private Deploy createDeploy(TerminalDeployForm command, Model model, Pkg pkg) {
        Deploy deploy = new Deploy();
        deploy.setPkg(pkg);
        deploy.setModel(model);
        deploy.setTimeZone(command.getTimeZone());
        deploy.setDwnlStartTime(command.getDwnlStartTime());
        if (command.getDwnlEndTime() != null) {
            deploy.setDwnlEndTime(command.getDwnlEndTime());
        }
        deploy.setTimeZone(command.getTimeZone());
        deploy.setActvStartTime(command.getActvStartTime());
        deploy.setDownReTryCount(command.getDownReTryCount() == null ? 1 : command.getDownReTryCount());
        deploy.setActvReTryCount(command.getActvReTryCount() == null ? 1 : command.getActvReTryCount());
        deploy.setDaylightSaving(command.isDaylightSaving());
        deploy.setCreator(command.getLoginUsername());
        deploy.setCreateDate(command.getRequestTime());
        deploy.setModifier(command.getLoginUsername());
        deploy.setModifyDate(command.getRequestTime());
        if(StringUtils.equals(pkg.getType(), PkgType.OFFLINEKEY.getPkgName())){
            deploy.setDeployType(Deploy.TYPE_KEY);
        }else{
            deploy.setDeployType(Deploy.TYPE_PKG);
        }
        return deploy;
    }

    private void validateInput(TerminalDeployForm command) {

        if (StringUtils.isEmpty(command.getTsn())) {
            throw new BusinessException("tsn.Required");
        }
        if (command.getPkgId() == null) {
            throw new BusinessException("msg.pkg.required");
        }
    }


    @Override
    public Page<DeployInfo> getCurrentDeploysByTsn(QueryCurrentTsnDeployForm command) {
        if (StringUtils.isEmpty(command.getTsn())) {
            Collections.emptyList();
        }
        if (command.getGroupId() == null) {
            throw new BusinessException("msg.group.groupRequired");
        }

        Group group = groupService.get(command.getGroupId());
        AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);

        Page<DeployInfo> page = super.page(command);
        List<DeployInfo> deployInfos = page.getItems();
        DeployInfo.updateTerminalInheritStatus(deployInfos);
        return page;
    }

    @Override
    public Page<DeployInfo> getHistorytDeploysByTsn(QueryHistotyTsnDeployForm command) {
        if (StringUtils.isEmpty(command.getTsn())) {
            Collections.emptyList();
        }
        if (command.getGroupId() == null) {
            throw new BusinessException("msg.group.groupRequired");
        }

        Group group = groupService.get(command.getGroupId());
        AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        Page<DeployInfo> page = terminalDeployHistoryService.getHistorytDeploysByTsn(command);

        return page;
    }

    @Override
    public void activate(TerminalDeployOperatorForm command) {

        validateInput(command);

        Terminal terminal = terminalService.validateTerminal(command.getTsn());
        AclManager.checkPermissionOfTerminal(command.getLoginUserId(), terminal);

        if (command.isInherit()) {
            throw new BusinessException("tsn.InheritDeployTask");
        }
        String pkgName = null;
        String pkgVersion = null;
        Deploy deploy = deployDao.get(command.getDeployId());
        if (deploy != null) {
            deploy.setStatus(1);
            deploy.setModifier(command.getLoginUsername());
            deploy.setModifyDate(command.getRequestTime());
            if (deploy.getPkg() != null) {
                pkgName = deploy.getPkg().getName();
                pkgVersion = deploy.getPkg().getVersion();
            }
        }
        auditLogService.addAuditLog(Arrays.asList(terminal.getTid()), command, OperatorLogForm.TERMINAL,
                " activate deploy " + pkgName + "_" + pkgVersion);

        eventService.addEventLog(Arrays.asList(terminal.getTid()), OperatorEventForm.TERMINAL,
                " activate deploy " + pkgName + "  " + pkgVersion);

    }

    private void validateInput(TerminalDeployOperatorForm command) {
        if (StringUtils.isEmpty(command.getTsn())) {
            throw new BusinessException("tsn.Required");
        }
        if (command.getDeployId() == null) {
            throw new BusinessException("tsn.deployTask.Required");
        }

    }

    @Override
    public void deactivate(TerminalDeployOperatorForm command) {
        validateInput(command);
        Terminal terminal = terminalService.validateTerminal(command.getTsn());
        AclManager.checkPermissionOfTerminal(command.getLoginUserId(), terminal);

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
        auditLogService.addAuditLog(Arrays.asList(terminal.getTid()), command, OperatorLogForm.TERMINAL,
                " deactivate deploy " + pkgName + "  " + pkgVersion);
        eventService.addEventLog(Arrays.asList(terminal.getTid()), OperatorEventForm.TERMINAL,
                " deactivate deploy " + pkgName + "  " + pkgVersion);

    }

    @Override
    public void delete(TerminalDeployOperatorForm command) {
        validateInput(command);
        Terminal terminal = terminalService.validateTerminal(command.getTsn());
        AclManager.checkPermissionOfTerminal(command.getLoginUserId(), terminal);

        TerminalDeploy terminalDeploy = terminalDeployDao.getTerminalDeploy(command.getDeployId(), command.getTsn());
        if (terminalDeploy == null) {
            throw new BusinessException("tsn.noPermissionOperator");
        }

        // record to history
        DeployInfo deployInfo = terminalDeployDao.getDeployInfo(command.getDeployId());
        if (deployInfo != null) {
            TerminalDeployHistory terminalDeployHistory = createTerminalDeployHistory(deployInfo, command);
            terminalDeployHistory.save();
        }
        terminalDownloadDao.deleteTerminalDownload(command.getDeployId());
        deployParaService.deleteDeployParas(Arrays.asList(command.getDeployId()));
        terminalDeployDao.deleteTerminalDeploy(command.getTsn(), command.getDeployId());
        Deploy deploy = deployDao.get(command.getDeployId());
        if(StringUtils.equals(deploy.getPkg().getType(), PkgType.OFFLINEKEY.getPkgName())){
            auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.DELETE_DEPLOY_TERMINAL_KEY,
                    deploy.getPkg().getName() + ", Version " +deploy.getPkg().getVersion()+ " to Terminal " + terminal.getTid());
        }else{
            auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.DELETE_DEPLOY_TERMINAL,
                    deploy.getPkg().getName() + ", Version " +deploy.getPkg().getVersion()+ " to Terminal " + terminal.getTid());
        }
        eventService.addEventLog(Arrays.asList(terminal.getTid()), OperatorEventForm.TERMINAL,
                " delete deploy " + deploy.getPkg().getName() + "  " + deploy.getPkg().getVersion());
        deploy.delete();

        // send redis message
        List<Map<String, String>> msgList = terminalService
                .getTerminalStatusChangedMessage(Arrays.asList(command.getTsn()), RedisOperatorForm.DELETE_DEPLOY);
        terminalService.sendTerminalStatusChangedMessage(msgList);

    }

    private TerminalDeployHistory createTerminalDeployHistory(DeployInfo deployInfo,
                                                              TerminalDeployOperatorForm command) {
        TerminalDeployHistory terminalDeployHistory = new TerminalDeployHistory();
        if (StringUtils.isNotEmpty(deployInfo.getDestModel())) {
            terminalDeployHistory.setModel(modelService.get(deployInfo.getDestModel()));
        }
        terminalDeployHistory.setTerminal(terminalService.get(deployInfo.getTsn()));
        terminalDeployHistory.setStatus(deployInfo.getStatus());
        terminalDeployHistory.setPkg(pkgService.get(deployInfo.getPkgId()));
        terminalDeployHistory.setPkgName(deployInfo.getPkgName());
        if (StringUtils.isEmpty(deployInfo.getDeploySource())) {
            terminalDeployHistory.setDeploySource(deployInfo.getTsn());
        } else {
            terminalDeployHistory.setDeploySource(deployInfo.getDeploySource());
        }
        terminalDeployHistory.setPkgVersion(deployInfo.getPkgVersion());
        terminalDeployHistory.setParamVersion(deployInfo.getParamVersion());
        terminalDeployHistory.setParamSet(deployInfo.getParamSet());
        if (deployInfo.getPkgSchemaId() != null) {
            terminalDeployHistory.setPkgSchema(pkgSchemaService.get(deployInfo.getPkgSchemaId()));
        }
        terminalDeployHistory.setDwnlStartTime(deployInfo.getDwnlStartTime());
        terminalDeployHistory.setDwnlTime(deployInfo.getDwnlTime());
        terminalDeployHistory.setActvTime(deployInfo.getActvTime());
        terminalDeployHistory.setDownStatus(deployInfo.getDwnStatus());
        terminalDeployHistory.setActvStatus(deployInfo.getActvStatus());
        terminalDeployHistory.setDownReTryCount(deployInfo.getDownReTryCount());
        terminalDeployHistory.setActvReTryCount(deployInfo.getActvReTryCount());
        terminalDeployHistory.setActvStartTime(deployInfo.getActvStartTime());
        terminalDeployHistory.setDwnlOrder(deployInfo.isDwnlOrder());
        terminalDeployHistory.setForceUpdate(deployInfo.isForceUpdate());
        terminalDeployHistory.setOnlyParam(deployInfo.isOnlyParam());
        terminalDeployHistory.setCreator(command.getLoginUsername());
        terminalDeployHistory.setCreateDate(command.getRequestTime());
        terminalDeployHistory.setModifier(command.getLoginUsername());
        terminalDeployHistory.setModifyDate(command.getRequestTime());

        return terminalDeployHistory;
    }

    @Override
    public void deleteTerminalInheritTaskFromGroup(Long deployId) {

        List<Long> deployIds = terminalDeployDao.getDeployIdsByGroupId(deployId);
        if (CollectionUtils.isEmpty(deployIds)) {
            return;
        }

        List<String> tsnList = terminalDeployDao.getTsnsByDeployGroupId(deployId);
        // send redis message
        List<Map<String, String>> msgList = terminalService.getTerminalStatusChangedMessage(tsnList,
                RedisOperatorForm.DELETE_DEPLOY);
        terminalService.sendTerminalStatusChangedMessage(msgList);
        terminalDownloadDao.deleteTerminalDownloads(deployIds);
        deployDao.deleteTaskFromGroupCascading(deployIds);
        deployParaService.deleteDeployParas(deployIds);
        terminalDeployDao.deleteTerminalDeploys(deployIds);

    }

    @Override
    public List<Long> getInheritGroupDeployIds(Long groupId) {

        return terminalDeployDao.getInheritGroupDeployIds(groupId);
    }

}
