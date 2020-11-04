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
package com.pax.tms.res.service.impl;

import io.vertx.core.json.Json;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;
import com.pax.common.redis.RedisClient;
import com.pax.common.service.impl.BaseService;
import com.pax.common.util.ZipFileUtil;
import com.pax.common.web.form.BaseForm;
import com.pax.common.web.form.SystemForm;
import com.pax.tms.app.broadpos.Signer;
import com.pax.tms.app.phoenix.PackageException;
import com.pax.tms.app.phoenix.PhoenixOfflineKey;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.res.dao.PkgDao;
import com.pax.tms.res.dao.PkgGroupDao;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgType;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.res.service.OfflineKeyService;
import com.pax.tms.res.web.form.AddOfflineKeyForm;
import com.pax.tms.res.web.form.AssignPkgForm;
import com.pax.tms.res.web.form.EditPkgForm;
import com.pax.tms.res.web.form.QueryPkgForm;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.model.User;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.service.UserService;
import com.pax.tms.user.web.form.OperatorLogForm;

@Service("offlineKeyServiceImpl")
public class OfflineKeyServiceImpl extends BaseService<Pkg, Long> implements OfflineKeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfflineKeyServiceImpl.class);

    @Autowired
    private PkgDao offlineKeyDao;
    
    @Autowired
    private Signer signer;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ModelService modelService;
    
    @Autowired
    private TerminalService terminalService;

    @Autowired
    private UserService userService;

    @Autowired
    private PkgGroupDao offlineKeyGroupDao;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private GroupDao groupDao;

    @Autowired(required = false)
    private RedisClient redisClient;

    @Override
    public IBaseDao<Pkg, Long> getBaseDao() {

        return offlineKeyDao;
    }

    public User getUserInfo(BaseForm form) {
        Long userId = form.getLoginUserId();
        User user = userService.get(userId);
        if (user == null) {
            throw new BusinessException("msg.user.notFound");
        }
        return user;

    }

    @Override
    public Map<String, Object> save(AddOfflineKeyForm command) {
        Map<String, Object> resultMap = new HashMap<String, Object>(16);
        //验证输入参数
        validateInput(command);
        //验证是否有组的权限
        Group group = groupService.validateGroup(command.getGroupId());
        AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        //验证上传的文件
        String fileName = command.getFileName();
        if (StringUtils.endsWith(fileName, ".zip")) {
            Map<String, String> zipFileEntries = null;
            Path tempUnZipFilePath = null;
            try {
                String uuid = UUID.randomUUID().toString(); 
                tempUnZipFilePath = Files.createTempDirectory("phoneix-key-"+uuid);
                File file = new File(command.getFilePath());
                zipFileEntries = ZipFileUtil.unzip(file, tempUnZipFilePath.toFile().getPath());
                int totalFile = zipFileEntries.size();
                List<String> existKey = new ArrayList<>(totalFile);
                List<String> notFoundSN = new ArrayList<>(totalFile);
                List<String> errorFile = new ArrayList<>(totalFile);
                for (Entry<String, String> fileInfo : zipFileEntries.entrySet()) {
                    String subFileName = fileInfo.getKey();
                    String subFilePath = fileInfo.getValue();
                    if(!validFileName(subFileName)){
                        errorFile.add(subFileName);
                        continue;
                    }
                    Terminal terminal = validFileName(subFileName, command);
                    if(null == terminal){
                        notFoundSN.add(subFileName);
                        continue;
                    }
                    Pkg oldKey = offlineKeyDao.getPkgByNameAndVersion(subFileName.substring(0, subFileName.lastIndexOf("_")), subFileName.substring(subFileName.lastIndexOf("_") + 1));
                    if (oldKey != null) {
                        existKey.add(subFileName);
                        continue;
                    }
                    File subFile = new File(subFilePath);
                    Pkg offlineKey =  saveOfflineKey(command,subFile, terminal);
                    offlineKeyDao.flush();

                    offlineKeyDao.update(offlineKey);

                    Map<Long, Collection<Long>> pkgAncestorGroupMap = offlineKeyGroupDao
                                .getPkgDistinctGroupAncestor(Arrays.asList(offlineKey.getId()), command.getGroupId());

                    offlineKeyGroupDao.insertPkgGroups(pkgAncestorGroupMap, command);
                    auditLogService.addAuditLog(Arrays.asList(offlineKey.getName() + ", Version " + offlineKey.getVersion()), command,
                                OperatorLogForm.IMPORT_OFFLINEKEY, ", assign Group " + group.getNamePath());
                }
                resultMap.put("notFoundSN", notFoundSN.size());
                resultMap.put("total", totalFile);
                resultMap.put("existKey", existKey.size());
                resultMap.put("errorFile", errorFile.size());
                int imported = totalFile - notFoundSN.size() - existKey.size() - errorFile.size();
                resultMap.put("imported", imported);
                if(0 >= imported){
                    resultMap.put("statusCode", 300);
                    resultMap.put("message", "Failed to import all keys (SN not found or key already exists or invalid key file)");
                    return resultMap;
                }
            } catch (IOException e) {
                throw new PackageException("msg.phonenixPackage.invalidPackageFormat", e);
            } finally {
                if (tempUnZipFilePath != null) {
                    tempUnZipFilePath.toFile().deleteOnExit();
                }
            }
        }else {
            if(!validFileName(fileName)){
                throw new BusinessException("msg.offlineKey.invalid");
            }
            Terminal terminal = validFileName(fileName, command);
            if(null == terminal){
                throw new BusinessException("msg.offlineKey.notGrantedOfflineKey", new String[] { fileName.split("_")[2] });
            }
            Pkg oldKey = offlineKeyDao.getPkgByNameAndVersion(fileName.substring(0, fileName.lastIndexOf("_")), fileName.substring(fileName.lastIndexOf("_") + 1));
            if (oldKey != null) {
                throw new BusinessException("msg.offlineKey.existNameAndVersion");
            }
            File file = new File(command.getFilePath());
            Pkg pkg =  saveOfflineKey(command, file,terminal);
            offlineKeyDao.flush();

            offlineKeyDao.update(pkg);

            Map<Long, Collection<Long>> pkgAncestorGroupMap = offlineKeyGroupDao
                        .getPkgDistinctGroupAncestor(Arrays.asList(pkg.getId()), command.getGroupId());

            offlineKeyGroupDao.insertPkgGroups(pkgAncestorGroupMap, command);
            auditLogService.addAuditLog(Arrays.asList(pkg.getName() + ", Version " + pkg.getVersion()), command,
                        OperatorLogForm.IMPORT_OFFLINEKEY, ", assign Group " + group.getNamePath());
            resultMap.put("total", 1);
            resultMap.put("imported", 1);
            resultMap.put("existKey", 0);
            resultMap.put("errorFile", 0);
            resultMap.put("notFoundSN", 0);
        }
        resultMap.put("statusCode", 200);
        resultMap.put("message", "Success");
        return resultMap;
    }
    
    private Pkg saveOfflineKey(AddOfflineKeyForm command, File file , Terminal terminal) {
        PhoenixOfflineKey phoenixOfflineKey = new PhoenixOfflineKey();
        try {
            //读取文件包信息，保存文件到FastDfs
            phoenixOfflineKey.parse(file, file.getName());
        } catch (IOException e) {
            throw new BusinessException("msg.sysErro", new String[] {""});
        }catch(PackageException e1){
            throw new BusinessException(e1.getErrorCode(), new String[] {""});
        }

        Pkg offlineKey = createOfflineKey(phoenixOfflineKey, command, terminal);
        offlineKeyDao.save(offlineKey);

        return offlineKey;
    }

    private Pkg createOfflineKey(PhoenixOfflineKey phoenixOfflineKey, AddOfflineKeyForm command,Terminal terminal) {
        String name = phoenixOfflineKey.getManifestFile().getPackageName();
        String version = phoenixOfflineKey.getManifestFile().getPackageVersion();

        Pkg offlineKey = new Pkg();
        offlineKey.setUuid(UUID.randomUUID().toString());
        offlineKey.setName(name);
        offlineKey.setVersion(version);
        offlineKey.setModel(terminal.getModel());
        offlineKey.setSn(terminal.getTsn());
        offlineKey.setDesc(phoenixOfflineKey.getManifestFile().getPackageDescription());
        offlineKey.setFileName(phoenixOfflineKey.getOfflineKeyFileName());
        offlineKey.setFileSize(phoenixOfflineKey.getOfflineKeyFileSize());
        offlineKey.setFilePath(phoenixOfflineKey.getOfflineKeyFilePath());
        offlineKey.setFileMD5(phoenixOfflineKey.getOfflineKeyMd5());
        offlineKey.setFileSHA256(phoenixOfflineKey.getOfflineKeySha256());
        offlineKey.setCreator(command.getLoginUsername());
        offlineKey.setCreateDate(command.getRequestTime());
        offlineKey.setModifier(command.getLoginUsername());
        offlineKey.setModifyDate(command.getRequestTime());
        offlineKey.setNotes(command.getNotes());
        offlineKey.setType(PkgType.OFFLINEKEY.getPkgName());
        offlineKey.setPgmType(PkgType.OFFLINEKEY.getPkgName());
        return offlineKey;
    }

    private void validateInput(AddOfflineKeyForm command) {

        if (command.getGroupId() == null) {
            throw new BusinessException("msg.group.groupRequired");
        }
        if (StringUtils.isEmpty(command.getFilePath())) {
            throw new BusinessException("msg.OfflineKeyFilePath.required");
        }
        if (StringUtils.isEmpty(command.getFileName())) {
            throw new BusinessException("OfflineKeyName.Required");
        }

    }
    
    private boolean validFileName(String fileName){
        String[] fileMsg = fileName.split("_");
        if(fileMsg.length != 4){
            return false;
        }
        String version = fileMsg[3];
        if(!StringUtils.isNumeric(version)){
            return false;  
        }
        return true;
    }
    
    private Terminal validFileName(String fileName,AddOfflineKeyForm command){
        String[] fileMsg = fileName.split("_");
        String venderName = fileMsg[0];
        String modelName = fileMsg[1];
        String sn = fileMsg[2];
        String version = fileMsg[3];
        Terminal terminal = terminalService.get(sn);
        if(null == terminal){
            return null;
        }
        if(!AclManager.keyCheckPermissionOfTerminal(command.getLoginUserId(), terminal) ||  !AclManager.keyCheckPermissionOfTerminalByGroup(command.getGroupId(), terminal)){
            return null;
        }
        if (StringUtils.isNotEmpty(command.getSn()) && !StringUtils.equals(sn, command.getSn())) {
            return null;
        }
        if(!StringUtils.equalsIgnoreCase(modelName, terminal.getModel().getName())){
            return null;
        }
        if(!StringUtils.equalsIgnoreCase(venderName, terminal.getModel().getManufacture().getName())){
            return null;
        }
        if(!StringUtils.isNumeric(version)){
            return null;
        }
        modelService.validateModel(modelName);
        return terminal;
    }

    @Override
    public void edit(Long keyId, EditPkgForm command) {

        validateInput(keyId);
        Pkg offlineKey = validateOfflineKey(keyId);

        AclManager.checkPermissionOfPkg(command.getLoginUserId(), offlineKey);

        offlineKey.setDesc(command.getDesc());
        offlineKey.setModifier(command.getLoginUsername());
        offlineKey.setModifyDate(command.getRequestTime());
        offlineKey.setNotes(command.getNotes());

        auditLogService.addAuditLog(Arrays.asList(offlineKey.getName() + ", Version " + offlineKey.getVersion()), command,
                OperatorLogForm.EDIT_OFFLINEKEY, null);

    }

    private void validateInput(Long keyId) {
        if (keyId == null) {
            throw new BusinessException("msg.OfflineKey.required");
        }
    }

  @Override
    public void dismiss(List<Long> keyList, BaseForm command) {
        if (CollectionUtils.isEmpty(keyList)) {
            throw new BusinessException("msg.offlineKey.required");
        }
        if (command.getGroupId() == null) {
            throw new BusinessException("msg.group.groupRequired");
        }

        Group group = groupService.validateGroup(command.getGroupId());
        AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        List<Long> notAccekeyIds = offlineKeyDao.getNotAcceptancePkgIds(keyList, command.getLoginUserId());
        if (CollectionUtils.isNotEmpty(notAccekeyIds)) {
            throw new BusinessException("msg.offlineKey.notGrantedOfflineKey", new String[] { StringUtils.join(notAccekeyIds, ",") });
        }
        dismiss(keyList, group);

        List<Pkg> keyInfo = offlineKeyDao.getNamesById(keyList);
        for (Pkg key : keyInfo) {
            auditLogService.addAuditLog(Arrays.asList(key.getName() + ", Version " + key.getVersion()), command,
                    OperatorLogForm.REMOVE_OFFLINEKEY, " in Group " + group.getNamePath());
        }

    }

    private void dismiss(List<Long> keyList, Group group) {

        Map<Long, Collection<Long>> levelOneGroups = offlineKeyGroupDao.getLevelOneGroup(keyList);
        offlineKeyGroupDao.deletePkgGroupCascade(keyList, group.getId());
        Collection<Long> unAssignKeyIds = offlineKeyGroupDao.getPkgUnAssignToGroup(keyList);

        offlineKeyDao.deactivate(unAssignKeyIds, SystemForm.instance);

        Map<Long, Collection<Long>> levelOneUnAssiginToGroup = getLevelOneUnAssignToGroup(levelOneGroups,
                unAssignKeyIds);

        for (Long groupId : levelOneUnAssiginToGroup.keySet()) {

            offlineKeyGroupDao.insertPkgGroup(levelOneUnAssiginToGroup.get(groupId), groupId, SystemForm.instance);

        }

    }

    private Map<Long, Collection<Long>> getLevelOneUnAssignToGroup(Map<Long, Collection<Long>> levelOneGroups,
                                                                   Collection<Long> unAssignKeyIds) {

        Collection<Long> ut = unAssignKeyIds;
        if (!(ut instanceof Set)) {
            ut = new HashSet<Long>(unAssignKeyIds);
        }

        Collection<Long> values;
        Iterator<Entry<Long, Collection<Long>>> it = levelOneGroups.entrySet().iterator();
        while (it.hasNext()) {
            values = it.next().getValue();
            values.retainAll(ut);
            if (values.isEmpty()) {
                it.remove();
            }
        }

        return levelOneGroups;
    }

    @Override
    public void delete(List<Long> keyList, BaseForm command) {

        if (CollectionUtils.isEmpty(keyList)) {
            throw new BusinessException("msg.offlineKey.required");
        }
        List<Long> notAccekeyIds = offlineKeyDao.getNotAcceptancePkgIds(keyList, command.getLoginUserId());
        if (CollectionUtils.isNotEmpty(notAccekeyIds)) {
            throw new BusinessException("msg.offlineKey.notGrantedOfflineKey", new String[] { StringUtils.join(notAccekeyIds, ",") });
        }

        List<Pkg> existDeployKey = offlineKeyDao.getExistDeployPkgs(keyList);
        if (CollectionUtils.isNotEmpty(existDeployKey)) {
            throw new BusinessException("msg.offlineKey.deploy", new String[] { join(existDeployKey) });
        }

        List<Pkg> keyInfo = delete(keyList);
        for (Pkg key : keyInfo) {
            auditLogService.addAuditLog(Arrays.asList(key.getName() + ", Version " + key.getVersion()), command,
                    OperatorLogForm.DELETE_OFFLINEKEY, null);
        }

    }

    private String join(List<Pkg> existDeployKey) {
        StringBuilder sb = new StringBuilder();
        for (Pkg key : existDeployKey) {
            sb.append(key.getName() + "/" + key.getVersion()).append(", ");

        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    private List<Pkg> delete(List<Long> keyList) {

        if (CollectionUtils.isEmpty(keyList)) {
            return Collections.emptyList();
        }
        offlineKeyGroupDao.deletePkgGroupByPkgId(keyList);

        Collection<Long> unassignedKeyIds = offlineKeyGroupDao.getPkgUnAssignToGroup(keyList);
        if (CollectionUtils.isEmpty(unassignedKeyIds)) {

            return Collections.emptyList();
        }
        List<Pkg> offlineKeyInfo = offlineKeyDao.getNamesById(keyList);
        offlineKeyDao.delete(unassignedKeyIds);
        return offlineKeyInfo;

    }


    @Override
    public void assign(Long[] keyIds, AssignPkgForm command) {

        validateInput(keyIds, command);
        List<Long> groupIds = Arrays.asList(command.getGroupIds());
        List<Group> groups = groupService.validateGroups(command.getGroupIds());
        for (Group group : groups) {
            AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        }
        for (Long keyId : keyIds) {
            Pkg offlineKey = validateOfflineKey(keyId);
            AclManager.checkPermissionOfPkg(command.getLoginUserId(), offlineKey);

        }
        List<Long> keyIdList = Arrays.asList(keyIds);
        for (Long groupId : groupIds) {
            Map<Long, Collection<Long>> keyAncestorGroupMap = offlineKeyGroupDao.getPkgDistinctGroupAncestor(keyIdList,
                    groupId);
            offlineKeyGroupDao.insertPkgGroups(keyAncestorGroupMap, command);
        }
        // batch add audit log
        List<Pkg> keyInfo = offlineKeyDao.getNamesById(keyIdList);
        for (Pkg key : keyInfo) {
            addAuditlog(key, groupIds, command);
        }

    }

    private void addAuditlog(Pkg key, List<Long> groupIds, AssignPkgForm command) {
        for (Long groupId : groupIds) {
            String grpNamePath = groupDao.get(groupId).getNamePath();
            auditLogService.addAuditLog(Arrays.asList(key.getName() + ", Version " + key.getVersion()), command,
                    OperatorLogForm.ASSIGN_OFFLINEKEY, " To group " + grpNamePath);
        }
    }

    private void validateInput(Long[] keyIds, AssignPkgForm command) {
        if (ArrayUtils.isEmpty(keyIds)) {
            throw new BusinessException("msg.offlineKey.required");
        }
        if (ArrayUtils.isEmpty(command.getGroupIds())) {
            throw new BusinessException("msg.group.groupRequired");
        }
    }

    @Override
    public <E, S extends IPageForm> Page<E> page(S command) {
        QueryPkgForm form = (QueryPkgForm) command;
        if (form.getGroupId() == null) {
            throw new BusinessException("msg.group.groupRequired");
        }
        form.setQueryType(Pkg.QUERY_KEY);
        Group group = groupService.validateGroup(form.getGroupId());
        AclManager.checkPermissionOfGroup(form.getLoginUserId(), group);

        return super.page(form);
    }

    @Override
    public List<String> getGroupNames(Long keyId) {
        validateInput(keyId);
        List<String> groupNames = offlineKeyGroupDao.getGroupNamesByPkgId(keyId);
        List<String> result = Terminal.removeParentGroupName(groupNames);
        return result;
    }

    @Override
    public List<String> getOfflineKeyNamesByGroupId(Long groupId, String type) {
        if (groupId == null) {
            throw new BusinessException("msg.group.groupRequired");
        }
        groupService.validateGroup(groupId);

        List<String> pkgNames = offlineKeyGroupDao.getPkgNamesByGroupId(groupId, type);
        return pkgNames;
    }

    @Override
    public List<String> getOfflineKeyNamesByGroupIdAndDestmodel(Long groupId, String destModel, String type) {
        if (groupId == null) {
            throw new BusinessException("msg.group.groupRequired");
        }
        groupService.validateGroup(groupId);

        List<String> pkgNames = offlineKeyGroupDao.getPkgNamesByGroupIdAndDestmodel(groupId, destModel, type);
        return pkgNames;
    }

    @Override
    public List<String> getOfflineKeyVersionsByName(String name, Long groupId) {
        if (StringUtils.isEmpty(name)) {
            return Collections.emptyList();
        }
        List<String> keyVersions = offlineKeyDao.getPkgVersionsByName(name, groupId);
        return keyVersions;
    }

    @Override
    public List<Map<String, Object>> getOfflineKeyListByName(String name, Long groupId) {
        if (StringUtils.isEmpty(name)) {
            return Collections.emptyList();
        }
        return offlineKeyDao.getPkgListByName(name, groupId);
    }

    @Override
    public Pkg getOfflineKeyByNameAndVersion(String name, String version) {
        if (StringUtils.isEmpty(name)) {
            throw new BusinessException("msg.offlineKey.nameRequired");
        }
        if (StringUtils.isEmpty(version)) {
            throw new BusinessException("msg.offlineKey.versionRequired");
        }
        return offlineKeyDao.getPkgByNameAndVersion(name, version);
    }

    @Override
    public Pkg validateOfflineKey(Long keyId) {
        Pkg pkg = offlineKeyDao.get(keyId);
        if (pkg == null) {
            throw new BusinessException("msg.offlineKeyNotFound");
        }
        return pkg;
    }

    @Override
    public void dismissByGroup(Group group, BaseForm command) {

        List<Long> pkgList = offlineKeyDao.getpkgListByGroupId(group.getId(), Pkg.QUERY_KEY);

        if (CollectionUtils.isEmpty(pkgList)) {
            return;
        }
        if (group.isEnterPriceGroup()) {
            delete(pkgList);

        } else {
            dismiss(pkgList, group);

        }

    }

    @Override
    public List<Map<String, Object>> getOfflineKeyStatusChangedMessage(Collection<Long> keyIds, String op) {
        if (CollectionUtils.isEmpty(keyIds)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> msgList = new ArrayList<>();
        for (Long keyId : keyIds) {
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("key", keyId);
            msgMap.put("op", op);
            msgList.add(msgMap);
        }
        return msgList;
    }

    @Override
    public void sendOfflineKeyStatusChangedMessage(List<Map<String, Object>> msgList) {
        if (redisClient != null && CollectionUtils.isNotEmpty(msgList)) {
            try {
                redisClient.sendMessage("tms:topic:packageStatusChanged", Json.encode(msgList));
            } catch (Exception e) {
                LOGGER.error("Failed to publish package status changed message", e);
            }

        }
    }
    

}
