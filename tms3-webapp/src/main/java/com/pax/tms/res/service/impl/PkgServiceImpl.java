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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.fs.FileManagerUtils;
import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;
import com.pax.common.redis.RedisClient;
import com.pax.common.service.impl.BaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.common.web.form.SystemForm;
import com.pax.tms.app.broadpos.BroadposPackage;
import com.pax.tms.app.broadpos.Signer;
import com.pax.tms.app.broadpos.para.ApplicationModule;
import com.pax.tms.app.broadpos.para.ApplicationSchema;
import com.pax.tms.app.broadpos.template.ParameterEntity;
import com.pax.tms.app.broadpos.template.SchemaUtil;
import com.pax.tms.app.phoenix.FileInfo;
import com.pax.tms.app.phoenix.PackageException;
import com.pax.tms.app.phoenix.PhoenixPackage;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.res.dao.PkgDao;
import com.pax.tms.res.dao.PkgGroupDao;
import com.pax.tms.res.dao.PkgOptLogDao;
import com.pax.tms.res.model.MetaInfo;
import com.pax.tms.res.model.Model;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgProgram;
import com.pax.tms.res.model.PkgSchema;
import com.pax.tms.res.model.PkgType;
import com.pax.tms.res.model.ProgramFile;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.res.service.PkgSchemaService;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.res.web.form.AddPkgForm;
import com.pax.tms.res.web.form.AddPkgSchemaForm;
import com.pax.tms.res.web.form.AssignPkgForm;
import com.pax.tms.res.web.form.EditPkgForm;
import com.pax.tms.res.web.form.QueryPkgForm;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.user.model.User;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.service.UserService;
import com.pax.tms.user.web.form.OperatorLogForm;

@Service("pkgServiceImpl")
public class PkgServiceImpl extends BaseService<Pkg, Long> implements PkgService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PkgServiceImpl.class);

    @Autowired
    PkgDao pkgDao;
    @Autowired
    private Signer signer;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ModelService modelService;

    // @Autowired
    // @Qualifier("mongoTemplate")
    // private MongoTemplate mongoTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private PkgGroupDao pkgGroupDao;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private PkgSchemaService pkgSchemaService;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private PkgOptLogDao pkgOptLogDao;

    @Autowired(required = false)
    private RedisClient redisClient;

    @Override
    public IBaseDao<Pkg, Long> getBaseDao() {

        return pkgDao;
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
    public Pkg save(AddPkgForm command) {

        validateInput(command);
        List<Group> groups = groupService.validateGroups(command.getGroupIds());
        List<Long> groupIds = Arrays.asList(command.getGroupIds());
        Model model = modelService.validateModel(command.getDestModel());
        for (Group group : groups) {
            AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        }
        Pkg pkg = null;

        // parse broadpos package
        if (PkgType.BROADPOS.getPkgName().equals(StringUtils.trimToNull(command.getType()))) {

            pkg = saveBroadPos(command, model);

            pkgDao.flush();

            this.updatePkgSchemaInfo(pkg);

            savePkgSchema(pkg);

        } else if (PkgType.MULTILANE.getPkgName().equals(StringUtils.trimToNull(command.getType()))) {
            pkg = saveMultilane(command, model);
            pkgDao.flush();

        } else {
            throw new BusinessException("msg.invalidPackage");
        }
        pkgDao.update(pkg);

        for (Long groupId : groupIds) {
            Map<Long, Collection<Long>> pkgAncestorGroupMap = pkgGroupDao
                    .getPkgDistinctGroupAncestor(Arrays.asList(pkg.getId()), groupId);

            pkgGroupDao.insertPkgGroups(pkgAncestorGroupMap, command);
        }
        for (Group group : groups) {
            auditLogService.addAuditLog(Arrays.asList(pkg.getName() + ", Version " + pkg.getVersion()), command,
                    OperatorLogForm.ADD_PACKAGE, ", assign Group " + group.getNamePath());
        }

        return pkg;
    }

    private void updatePkgSchemaInfo(Pkg pkg) {
        List<ApplicationModule> moduleList = pkgDao.getAppModuleList(pkg.getId());
        ApplicationSchema schema = new ApplicationSchema(moduleList);
        Document doc = schema.createSchemaDocument();
        String schemeFdfsPath = FileManagerUtils.saveBytesToFdfs(doc.asXML().getBytes(), "xml");
        Map<String, byte[]> uploadFileMap = this.getPkgSchemaUploadFile(moduleList);
        Map<String, String> paramMapDB = this.getSchemaInitValue(doc, uploadFileMap);
        paramMapDB.put(PkgSchema.PARAM_SET_ID_KEY, UUID.randomUUID().toString());
        // mongoTemplate.save(paramMapDB, PkgSchema.PARAM_COLLECTION);

        pkg.setSchemaFilePath(schemeFdfsPath);
        pkg.setSchemaFileSize((long) doc.asXML().length());
        pkg.setParamSet(paramMapDB.get(PkgSchema.PARAM_SET_ID_KEY));
    }

    /**
     * @param doc
     * @param uploadFileMap
     *            参数文件中Data文件的内容, key 为 abbrName_filename
     * @return parameters' key and value
     */
    private Map<String, String> getSchemaInitValue(Document doc, Map<String, byte[]> uploadFileMap) {
        Map<String, String> paramMapDB = new HashMap<String, String>();
        List<ParameterEntity> paraList = SchemaUtil.getEntityListFromSchema(doc);
        String pid = "", defaultValue = "";
        for (ParameterEntity entity : paraList) {
            pid = entity.getFinalPid();
            defaultValue = entity.getDefaultValue() == null ? "" : entity.getDefaultValue();

            if ("upload".equalsIgnoreCase(entity.getInputType())) {
                byte[] uploadFile = uploadFileMap.get(entity.getProgramAbbrName() + "_" + defaultValue);
                defaultValue = defaultValue + "|FDFS|" + FileManagerUtils.saveBytesToFdfs(uploadFile, "data");
            }

            paramMapDB.put(pid, defaultValue);
        }
        return paramMapDB;
    }

    private Map<String, byte[]> getPkgSchemaUploadFile(List<ApplicationModule> moduleList) {
        Map<String, byte[]> uploadFileMap = new HashMap<String, byte[]>();
        for (ApplicationModule module : moduleList) {
            Map<String, byte[]> map = ApplicationModule.readModuleDataFileFromDfs(module.getPctFilePath());
            if (map == null || map.isEmpty()) {
                continue;
            }
            /*
             * 防止不同module中包含相同名称的参数文件, 使用Module简称拼接key
             */
            for (Entry<String, byte[]> entry : map.entrySet()) {
                uploadFileMap.put(module.getAbbrName() + "_" + entry.getKey(), entry.getValue());
            }
        }
        return uploadFileMap;
    }

    private void savePkgSchema(Pkg pkg) {
        AddPkgSchemaForm pkgSchemaForm = new AddPkgSchemaForm();
        pkgSchemaForm.setName(pkg.getName() + "_" + pkg.getVersion());
        pkgSchemaForm.setPkgName(pkg.getName());
        pkgSchemaForm.setPkgVersion(pkg.getVersion());
        pkgSchemaForm.setPkgId(pkg.getId());
        pkgSchemaForm.setSys(true);
        pkgSchemaForm.setCheckPermission(false);
        pkgSchemaService.save(pkgSchemaForm);

    }

    private Pkg saveMultilane(AddPkgForm command, Model model) {
        File file = new File(command.getFilePath());
        PhoenixPackage phoenixPackage = new PhoenixPackage();
        try {
            //读取文件包信息，保存文件到FastDfs
            phoenixPackage.parse(file, command.getFileName());
        } catch (IOException e) {
            throw new BusinessException("msg.pkg.sysErro", new String[] {""});
        }catch(PackageException e1){
            throw new BusinessException(e1.getErrorCode(), new String[] {""});
        }

        Pkg pkg = createMultilanePkg(phoenixPackage, command, model);
        pkgDao.save(pkg);

        List<FileInfo> fileInfos = phoenixPackage.getFileInfos();
        if (CollectionUtils.isEmpty(fileInfos)) {
            return pkg;
        }
        for (FileInfo fileInfo : fileInfos) {

            PkgProgram pkgProgram = new PkgProgram();
            pkgProgram.setName(fileInfo.getPgmName());
            pkgProgram.setVersion(fileInfo.getPgmVersion());
            pkgProgram.setType(fileInfo.getPgmType());
            pkgProgram.setDesc(fileInfo.getPgmDesc());
            pkgProgram.setCreator(command.getLoginUsername());
            pkgProgram.setCreateDate(command.getRequestTime());
            pkgProgram.setModifier(command.getLoginUsername());
            pkgProgram.setModifyDate(command.getRequestTime());
            pkg.addProgram(pkgProgram);
            pkgProgram.save();

            ProgramFile programFile = new ProgramFile();
            programFile.setType(ProgramFile.PROGRAM_FILE_KEY);
            programFile.setName(fileInfo.getName());
            programFile.setVersion(fileInfo.getVersion());
            programFile.setSize(fileInfo.getFileSize());
            programFile.setFilePath(FileManagerUtils.saveLocalFileToFdfs(fileInfo.getFilePath()));
            programFile.setFileMD5(fileInfo.getMd5());
            programFile.setFileSHA256(fileInfo.getSha256());
            programFile.setCreator(command.getLoginUsername());
            programFile.setCreateDate(command.getRequestTime());
            programFile.setModifier(command.getLoginUsername());
            programFile.setModifyDate(command.getRequestTime());

            pkgProgram.addProgrmFile(programFile);
            pkg.addProgramFile(programFile);
            programFile.save();
            pkgProgram.update();
        }
        return pkg;

    }

    private Pkg createMultilanePkg(PhoenixPackage phoenixPackage, AddPkgForm command, Model model) {
        String name = phoenixPackage.getManifestFile().getPackageName();
        String version = phoenixPackage.getManifestFile().getPackageVersion();

        Pkg oldPkg = pkgDao.getPkgByNameAndVersion(name, version);
        if (oldPkg != null) {
            boolean isSamePackage = false;
            if (oldPkg.getFileSHA256() != null && oldPkg.getFileSHA256().equals(phoenixPackage.getPackageSha256())) {
                isSamePackage = true;
            }
            String[] args = new String[] { isSamePackage ? "1" : "0", oldPkg.getId().toString() };
            throw new BusinessException("msg.pkg.existNameAndVersion", args);
        }
        Pkg pkg = new Pkg();
        pkg.setUuid(UUID.randomUUID().toString());
        pkg.setName(name);
        pkg.setVersion(version);
        pkg.setType(command.getType());
        pkg.setPgmType(phoenixPackage.getManifestFile().getPackageType());
        if (model != null) {
            pkg.setModel(model);
        }
        pkg.setDesc(phoenixPackage.getManifestFile().getPackageDescription());
        pkg.setFileName(phoenixPackage.getPackageFileName());
        pkg.setFileSize(phoenixPackage.getPackageFileSize());
        pkg.setFilePath(phoenixPackage.getPackageFilePath());
        pkg.setFileMD5(phoenixPackage.getPackageMd5());
        pkg.setFileSHA256(phoenixPackage.getPackageSha256());
        pkg.setCreator(command.getLoginUsername());
        pkg.setCreateDate(command.getRequestTime());
        pkg.setModifier(command.getLoginUsername());
        pkg.setModifyDate(command.getRequestTime());
        pkg.setNotes(command.getNotes());
        pkg.setSigned(phoenixPackage.isPackageSigned());
        return pkg;
    }

    @SuppressWarnings("resource")
    private Pkg saveBroadPos(AddPkgForm command, Model model) {

        File file = new File(command.getFilePath());

        BroadposPackage pack = new BroadposPackage(command.getFilePath(), command.getFileName(), file.length(),
                command.getDestModel(), signer);
        try {
            pack.parse();
        } catch (IOException e) {

            throw new BusinessException("msg.pkg.sysErro", e);
        }

        Pkg pkg = createBroadPkg(pack, command, model);

        pkgDao.save(pkg);

        List<Map<String, String>> firmwarePrograms = pack.getConfigFile().getFirmwarePrograms();

        if (CollectionUtils.isNotEmpty(firmwarePrograms)) {

            for (Map<String, String> firmwareMap : firmwarePrograms) {

                PkgProgram pkgProgram = createFirmwarePkgProgram(firmwareMap, command);
                pkg.addProgram(pkgProgram);
                pkgProgram.save();
                addFirmwarePkgProgramFile(pkg, pkgProgram, command, firmwareMap);
            }
        }
        List<Map<String, String>> modulePrograms = pack.getConfigFile().getModulePrograms();
        if (CollectionUtils.isNotEmpty(modulePrograms)) {
            for (Map<String, String> moduleProgramMap : modulePrograms) {

                PkgProgram pkgProgram = createModulePkgProgram(moduleProgramMap, command);
                pkg.addProgram(pkgProgram);
                pkgProgram.save();
                addModulePkgProgramFile(pkg, pkgProgram, command, moduleProgramMap);
            }

        }

        return pkg;

    }

    private void addModulePkgProgramFile(Pkg pkg, PkgProgram pkgProgram, AddPkgForm command,
                                         Map<String, String> moduleProgramMap) {
        for (String fileType : ProgramFile.getFileTypes()) {
            String fileVersion = "";
            String pathKey = "";

            switch (fileType) {
                case ProgramFile.PROGRAM_FILE:
                    pathKey = ProgramFile.PROGRAM_FILE_KEY;
                    fileVersion = moduleProgramMap.get("APM_VER");
                    break;
                case ProgramFile.SIGN_FILE:
                    pathKey = ProgramFile.SIGN_FILE_KEY;
                    fileVersion = moduleProgramMap.get("SIGN_VER");
                    break;
                case ProgramFile.CONF_FILE:
                    pathKey = ProgramFile.CONF_FILE_KEY;
                    break;
                case ProgramFile.DIGEST_FILE:
                    pathKey = ProgramFile.DIGEST_FILE_KEY;
                    break;
                default:
                    break;
            }

            String filepath = moduleProgramMap.get(pathKey);
            if (StringUtils.isNotEmpty(filepath)) {
                ProgramFile programFile = createProgramFile(fileType, command, filepath, fileVersion);
                pkgProgram.addProgrmFile(programFile);
                pkg.addProgramFile(programFile);
                programFile.save();
            }
        }

        pkgProgram.update();

    }

    private PkgProgram createModulePkgProgram(Map<String, String> moduleProgramMap, AddPkgForm command) {
        PkgProgram pkgProgram = new PkgProgram();
        pkgProgram.setName(moduleProgramMap.get("APM_NAME"));
        pkgProgram.setType(moduleProgramMap.get("PIP_TYPE"));
        pkgProgram.setAbbrName(moduleProgramMap.get("APM_ABRNAM"));
        pkgProgram.setDisplayName(moduleProgramMap.get("APMV_DSPNAM"));
        pkgProgram.setVersion(moduleProgramMap.get("APM_VER"));
        pkgProgram.setDesc(moduleProgramMap.get("APMV_DESC"));
        pkgProgram.setSignVersion(moduleProgramMap.get("SIGN_VER"));
        pkgProgram.setCreator(command.getLoginUsername());
        pkgProgram.setCreateDate(command.getRequestTime());
        pkgProgram.setModifier(command.getLoginUsername());
        pkgProgram.setModifyDate(command.getRequestTime());

        return pkgProgram;
    }

    private void addFirmwarePkgProgramFile(Pkg pkg, PkgProgram pkgProgram, AddPkgForm command,
                                           Map<String, String> firmwareMap) {
        for (String fileType : ProgramFile.getFileTypes()) {
            String fileVersion = "";
            String pathKey = "";

            switch (fileType) {
                case ProgramFile.PROGRAM_FILE:
                    pathKey = ProgramFile.PROGRAM_FILE_KEY;
                    fileVersion = firmwareMap.get("PIP_VER");
                    break;
                case ProgramFile.SIGN_FILE:
                    pathKey = ProgramFile.SIGN_FILE_KEY;
                    fileVersion = firmwareMap.get("SIGN_VER");
                    break;
                case ProgramFile.CONF_FILE:
                    pathKey = ProgramFile.CONF_FILE_KEY;
                    break;
                case ProgramFile.DIGEST_FILE:
                    pathKey = ProgramFile.DIGEST_FILE_KEY;
                    break;
                default:
                    break;
            }

            String filepath = firmwareMap.get(pathKey);
            if (StringUtils.isNotEmpty(filepath)) {
                ProgramFile programFile = createProgramFile(fileType, command, filepath, fileVersion);
                pkgProgram.addProgrmFile(programFile);
                pkg.addProgramFile(programFile);
                programFile.save();
            }
        }

        pkgProgram.update();

    }

    private PkgProgram createFirmwarePkgProgram(Map<String, String> firmwareMap, AddPkgForm command) {
        PkgProgram pkgProgram = new PkgProgram();
        pkgProgram.setType(firmwareMap.get("PIP_TYPE"));
        pkgProgram.setAbbrName(firmwareMap.get("APM_ABRNAM"));
        pkgProgram.setName(firmwareMap.get("PIP_NAME"));
        pkgProgram.setVersion(firmwareMap.get("PIP_VER"));
        pkgProgram.setDesc(firmwareMap.get("PIP_DESC"));
        pkgProgram.setSignVersion(firmwareMap.get("SIGN_VER"));
        pkgProgram.setCreator(command.getLoginUsername());
        pkgProgram.setCreateDate(command.getRequestTime());
        pkgProgram.setModifier(command.getLoginUsername());
        pkgProgram.setModifyDate(command.getRequestTime());
        return pkgProgram;
    }

    private Pkg createBroadPkg(BroadposPackage pack, AddPkgForm command, Model model) {
        Map<String, String> app = pack.getConfigFile().getApp();
        String name = app.get("APP_NAME");
        String version = app.get("APP_VER");
        if (pkgDao.existNameAndVersion(name, version)) {
            throw new BusinessException("msg.pkg.existsNameAndVersion");
        }
        Pkg pkg = new Pkg();
        pkg.setUuid(UUID.randomUUID().toString());
        pkg.setName(name);
        pkg.setVersion(version);
        pkg.setType(command.getType());
        if (model != null) {
            pkg.setModel(model);
        }
        pkg.setDesc(app.get("APPV_DESC"));
        pkg.setFileName(pack.getPackagefileName());
        pkg.setFileSize(pack.getPackageFileSize());
        pkg.setFilePath(pack.getPackageFilePath());
        pkg.setCreator(command.getLoginUsername());
        pkg.setCreateDate(command.getRequestTime());
        pkg.setModifier(command.getLoginUsername());
        pkg.setModifyDate(command.getRequestTime());
        pkg.setNotes(command.getNotes());
        return pkg;
    }

    private ProgramFile createProgramFile(String type, AddPkgForm command, String filePath, String version) {
        ProgramFile programFile = new ProgramFile();
        programFile.setType(type);
        programFile.setName(type + ".zip");
        programFile.setVersion(version);
        programFile.setSize(new File(filePath).length());
        programFile.setFilePath(FileManagerUtils.saveLocalFileToFdfs(filePath));
        programFile.setCreator(command.getLoginUsername());
        programFile.setCreateDate(command.getRequestTime());
        programFile.setModifier(command.getLoginUsername());
        programFile.setModifyDate(command.getRequestTime());
        return programFile;
    }

    private void validateInput(AddPkgForm command) {

        if (ArrayUtils.isEmpty(command.getGroupIds())) {
            throw new BusinessException("msg.group.groupRequired");
        }
        if (StringUtils.isEmpty(command.getFilePath())) {

            throw new BusinessException("msg.packageFilePath.required");
        }
        if (StringUtils.isEmpty(command.getType())) {
            throw new BusinessException("pkgType.Required");
        }
        if (StringUtils.isEmpty(command.getFileName())) {
            throw new BusinessException("pkgName.Required");
        }

    }

    @Override
    public void edit(Long pkgId, EditPkgForm command) {

        validateInput(pkgId);
        Pkg pkg = validatePkg(pkgId);

        AclManager.checkPermissionOfPkg(command.getLoginUserId(), pkg);

        pkg.setDesc(command.getDesc());
        pkg.setModifier(command.getLoginUsername());
        pkg.setModifyDate(command.getRequestTime());
        pkg.setNotes(command.getNotes());

        auditLogService.addAuditLog(Arrays.asList(pkg.getName() + ", Version " + pkg.getVersion()), command,
                OperatorLogForm.EDIT_PACKAGE, null);

    }

    private void validateInput(Long pkgId) {
        if (pkgId == null) {
            throw new BusinessException("msg.pkg.required");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.pax.tms.res.service.PkgService#saveOrUpdate(com.pax.tms.res.web.form.
     * AddPkgForm)
     */
    @Override
    public Pkg saveOrUpdate(AddPkgForm form) {
        try {
            return save(form);
        } catch (BusinessException e) {
            if ("msg.pkg.existNameAndVersion".equals(e.getErrorCode()) || "1".equals(e.getArgs()[0])) {
                Pkg oldPkg = pkgDao.get(Long.parseLong(e.getArgs()[1]));
                if (oldPkg == null) {
                    throw e;
                }

                AssignPkgForm command = new AssignPkgForm();
                command.setLoginUserProfile(form.getLoginUserPofile() );
                command.setGroupIds(form.getGroupIds());
                assign(new Long[] { oldPkg.getId() }, command);
                return oldPkg;
            } else {
                throw e;
            }
        }
    }

    @Override
    public void deactivate(Long[] pkgId, BaseForm command) {

        if (ArrayUtils.isEmpty(pkgId)) {
            throw new BusinessException("msg.pkg.required");
        }

        List<Long> pkgList = Arrays.asList(pkgId);
        List<Long> notAccepkgIds = pkgDao.getNotAcceptancePkgIds(pkgList, command.getLoginUserId());
        if (CollectionUtils.isNotEmpty(notAccepkgIds)) {
            throw new BusinessException("msg.pkg.notGrantedPkg", new String[] { StringUtils.join(notAccepkgIds, ",") });
        }

        pkgDao.deactivate(pkgList, command);

        List<Pkg> pkgInfo = pkgDao.getNamesById(pkgList);
        for (Pkg pkg : pkgInfo) {
            auditLogService.addAuditLog(Arrays.asList(pkg.getName() + ", Version " + pkg.getVersion()), command,
                    OperatorLogForm.DEACTIVATE_PACKAGE, null);
        }

    }

    @Override
    public void activate(Long[] pkgId, BaseForm command) {

        if (ArrayUtils.isEmpty(pkgId)) {
            throw new BusinessException("msg.pkg.required");
        }

        List<Long> pkgList = Arrays.asList(pkgId);
        List<Long> notAccepkgIds = pkgDao.getNotAcceptancePkgIds(pkgList, command.getLoginUserId());
        if (CollectionUtils.isNotEmpty(notAccepkgIds)) {
            throw new BusinessException("msg.pkg.notGrantedPkg", new String[] { StringUtils.join(notAccepkgIds, ",") });
        }

        pkgDao.activate(pkgList, command);
        List<Pkg> pkgInfo = pkgDao.getNamesById(pkgList);
        for (Pkg pkg : pkgInfo) {
            auditLogService.addAuditLog(Arrays.asList(pkg.getName() + ", Version " + pkg.getVersion()), command,
                    OperatorLogForm.ACTIVATE_PACKAGE, null);
        }

    }

    @Override
    public void dismiss(List<Long> pkgList, BaseForm command) {
        if (CollectionUtils.isEmpty(pkgList)) {
            throw new BusinessException("msg.pkg.required");
        }
        if (command.getGroupId() == null) {
            throw new BusinessException("msg.group.groupRequired");
        }

        Group group = groupService.validateGroup(command.getGroupId());
        AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        List<Long> notAccepkgIds = pkgDao.getNotAcceptancePkgIds(pkgList, command.getLoginUserId());
        if (CollectionUtils.isNotEmpty(notAccepkgIds)) {
            throw new BusinessException("msg.pkg.notGrantedPkg", new String[] { StringUtils.join(notAccepkgIds, ",") });
        }
        dismiss(pkgList, group);

        List<Pkg> pkgInfo = pkgDao.getNamesById(pkgList);
        for (Pkg pkg : pkgInfo) {
            auditLogService.addAuditLog(Arrays.asList(pkg.getName() + ", Version " + pkg.getVersion()), command,
                    OperatorLogForm.REMOVE_PACKAGE, " in Group " + group.getNamePath());
        }

    }

    private void dismiss(List<Long> pkgList, Group group) {

        Map<Long, Collection<Long>> levelOneGroups = pkgGroupDao.getLevelOneGroup(pkgList);
        pkgGroupDao.deletePkgGroupCascade(pkgList, group.getId());
        Collection<Long> unAssignPkgIds = pkgGroupDao.getPkgUnAssignToGroup(pkgList);

        pkgDao.deactivate(unAssignPkgIds, SystemForm.instance);

        Map<Long, Collection<Long>> levelOneUnAssiginToGroup = getLevelOneUnAssignToGroup(levelOneGroups,
                unAssignPkgIds);

        for (Long groupId : levelOneUnAssiginToGroup.keySet()) {

            pkgGroupDao.insertPkgGroup(levelOneUnAssiginToGroup.get(groupId), groupId, SystemForm.instance);

        }

    }

    private Map<Long, Collection<Long>> getLevelOneUnAssignToGroup(Map<Long, Collection<Long>> levelOneGroups,
                                                                   Collection<Long> unAssignPkgIds) {

        Collection<Long> ut = unAssignPkgIds;
        if (!(ut instanceof Set)) {
            ut = new HashSet<Long>(unAssignPkgIds);
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
    public void delete(List<Long> pkgList, BaseForm command) {

        if (CollectionUtils.isEmpty(pkgList)) {
            throw new BusinessException("msg.pkg.required");
        }
        List<Long> notAccepkgIds = pkgDao.getNotAcceptancePkgIds(pkgList, command.getLoginUserId());
        if (CollectionUtils.isNotEmpty(notAccepkgIds)) {
            throw new BusinessException("msg.pkg.notGrantedPkg", new String[] { StringUtils.join(notAccepkgIds, ",") });
        }

        List<Pkg> existDeployPkg = pkgDao.getExistDeployPkgs(pkgList);
        if (CollectionUtils.isNotEmpty(existDeployPkg)) {
            throw new BusinessException("msg.pkg.deploy", new String[] { join(existDeployPkg) });
        }

        List<Pkg> pkgInfo = delete(pkgList);
        for (Pkg pkg : pkgInfo) {
            auditLogService.addAuditLog(Arrays.asList(pkg.getName() + ", Version " + pkg.getVersion()), command,
                    OperatorLogForm.DELETE_PACKAGE, null);
        }

    }

    private String join(List<Pkg> existDeployPkg) {
        StringBuilder sb = new StringBuilder();
        for (Pkg pkg : existDeployPkg) {
            sb.append(pkg.getName() + "/" + pkg.getVersion()).append(",");

        }
        return sb.toString().substring(0, sb.length() - 1);
    }

    private List<Pkg> delete(List<Long> pkgList) {

        if (CollectionUtils.isEmpty(pkgList)) {
            return Collections.emptyList();
        }
        pkgGroupDao.deletePkgGroupByPkgId(pkgList);

        Collection<Long> unassignedPkgIds = pkgGroupDao.getPkgUnAssignToGroup(pkgList);
        if (CollectionUtils.isEmpty(unassignedPkgIds)) {

            return Collections.emptyList();
        }
        List<String> optLogFilePaths = getNeedRecordLogFilePaths(pkgList);
        pkgOptLogDao.insertOptLogs(optLogFilePaths);
        List<Pkg> pkgInfo = pkgDao.getNamesById(pkgList);
        pkgDao.delete(unassignedPkgIds);
        return pkgInfo;

    }

    private List<String> getNeedRecordLogFilePaths(List<Long> pkgList) {
        List<String> packageFilePaths = pkgDao.getPkgFilePaths(pkgList);
        List<String> programFilePaths = pkgDao.programFilePaths(pkgList);
        packageFilePaths.addAll(programFilePaths);
        return packageFilePaths;
    }

    @Override
    public void assign(Long[] pkgIds, AssignPkgForm command) {

        validateInput(pkgIds, command);
        List<Long> groupIds = Arrays.asList(command.getGroupIds());
        List<Group> groups = groupService.validateGroups(command.getGroupIds());
        for (Group group : groups) {
            AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
        }
        for (Long pkgId : pkgIds) {
            Pkg pkg = validatePkg(pkgId);
            AclManager.checkPermissionOfPkg(command.getLoginUserId(), pkg);

        }
        List<Long> pkgIdList = Arrays.asList(pkgIds);
        for (Long groupId : groupIds) {
            Map<Long, Collection<Long>> pkgAncestorGroupMap = pkgGroupDao.getPkgDistinctGroupAncestor(pkgIdList,
                    groupId);
            pkgGroupDao.insertPkgGroups(pkgAncestorGroupMap, command);
        }
        // batch add audit log
        List<Pkg> pkgInfo = pkgDao.getNamesById(pkgIdList);
        for (Pkg pkg : pkgInfo) {
            addAuditlog(pkg, groupIds, command);
        }

    }

    private void addAuditlog(Pkg pkg, List<Long> groupIds, AssignPkgForm command) {
        for (Long groupId : groupIds) {
            String grpNamePath = groupDao.get(groupId).getNamePath();
            auditLogService.addAuditLog(Arrays.asList(pkg.getName() + ", Version " + pkg.getVersion()), command,
                    OperatorLogForm.ASSIGN_PACKAGE, " To group " + grpNamePath);
        }
    }

    private void validateInput(Long[] pkgIds, AssignPkgForm command) {
        if (ArrayUtils.isEmpty(pkgIds)) {
            throw new BusinessException("msg.pkg.required");
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
        form.setQueryType(Pkg.QUERY_PKG);
        Group group = groupService.validateGroup(form.getGroupId());
        AclManager.checkPermissionOfGroup(form.getLoginUserId(), group);

        return super.page(form);
    }

    @Override
    public List<String> getGroupNames(Long pkgId) {
        validateInput(pkgId);
        List<String> groupNames = pkgGroupDao.getGroupNamesByPkgId(pkgId);
        List<String> result = Terminal.removeParentGroupName(groupNames);
        return result;
    }

    @Override
    public List<String> getPkgNamesByGroupId(Long groupId, String type) {
        if (groupId == null) {
            throw new BusinessException("msg.group.groupRequired");
        }
        groupService.validateGroup(groupId);

        List<String> pkgNames = pkgGroupDao.getPkgNamesByGroupId(groupId, type);
        return pkgNames;
    }

    @Override
    public List<String> getPkgNamesByGroupIdAndDestmodel(Long groupId, String destModel, String type) {
        if (groupId == null) {
            throw new BusinessException("msg.group.groupRequired");
        }
        groupService.validateGroup(groupId);

        List<String> pkgNames = pkgGroupDao.getPkgNamesByGroupIdAndDestmodel(groupId, destModel, type);
        return pkgNames;
    }

    @Override
    public List<String> getPkgVersionsByName(String name, Long groupId) {
        if (StringUtils.isEmpty(name)) {
            return Collections.emptyList();
        }
        List<String> pkgVersions = pkgDao.getPkgVersionsByName(name, groupId);
        return pkgVersions;
    }

    @Override
    public List<Map<String, Object>> getPkgListByName(String name, Long groupId) {
        if (StringUtils.isEmpty(name)) {
            return Collections.emptyList();
        }
        return pkgDao.getPkgListByName(name, groupId);
    }

    @Override
    public Pkg getPkgByNameAndVersion(String name, String version) {
        if (StringUtils.isEmpty(name)) {
            throw new BusinessException("msg.pkg.nameRequired");
        }
        if (StringUtils.isEmpty(version)) {
            throw new BusinessException("msg.pkg.versionRequired");
        }
        return pkgDao.getPkgByNameAndVersion(name, version);
    }

    @Override
    public Pkg validatePkg(Long pkgId) {
        Pkg pkg = pkgDao.get(pkgId);
        if (pkg == null) {
            throw new BusinessException("msg.pkgNotFound");
        }
        return pkg;
    }

    @Override
    public List<MetaInfo> getMetaInfo(Long pkgId) {
        validateInput(pkgId);
        List<MetaInfo> list = pkgDao.getMetaInfo(pkgId);
        return list;
    }

    @Override
    public List<PkgProgram> getPkgProgramInfo(Long pkgId) {
        validateInput(pkgId);
        List<PkgProgram> pkgProgramList = pkgDao.getPkgProgramInfo(pkgId);
        return pkgProgramList;
    }

    @Override
    public void dismissByGroup(Group group, BaseForm command) {

        List<Long> pkgList = pkgDao.getpkgListByGroupId(group.getId(), Pkg.QUERY_PKG);

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
    public List<Map<String, Object>> getPkgStatusChangedMessage(Collection<Long> pkgIds, String op) {
        if (CollectionUtils.isEmpty(pkgIds)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> msgList = new ArrayList<>();
        for (Long pkgId : pkgIds) {
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("pkg", pkgId);
            msgMap.put("op", op);
            msgList.add(msgMap);
        }
        return msgList;
    }

    @Override
    public void sendPkgStatusChangedMessage(List<Map<String, Object>> msgList) {
        if (redisClient != null && CollectionUtils.isNotEmpty(msgList)) {
            try {
                redisClient.sendMessage("tms:topic:packageStatusChanged", Json.encode(msgList));
            } catch (Exception e) {
                LOGGER.error("Failed to publish package status changed message", e);
            }

        }
    }
    
    @Override
    public List<Pkg> getPkgByName(String name, Long groupId){
        return pkgDao.getPkgByName(name, groupId);
    }
    
    @Override
    public Map<String, Object> checkHistoryPackage(){
        Map<String, Object> resultMap =  new HashMap<String, Object>();
        List<Pkg> pkgList = pkgDao.getPackageUnCheck();
        if(null == pkgList || pkgList.isEmpty()){
            resultMap.put("result", "no unchecked package");
            return resultMap;
        }
        String tempFilePath = null;
        try {
            tempFilePath = Files.createTempDirectory("phoneix-pack-").toFile().getPath();
        } catch (IOException e1) {
            throw new BusinessException("msg.pkg.tempFilePath.sysErro");
        }
        for (Pkg pkg : pkgList) {
            try {
                File file = new File(tempFilePath,pkg.getFileName());
                FileManagerUtils.getFileManager().downloadFile(pkg.getFilePath(), ins -> {
                    FileUtils.copyInputStreamToFile(ins, file);
                    return null;
                });
                PhoenixPackage phoenixPackage = new PhoenixPackage();
                //读取文件包信息
                phoenixPackage.checkFile(file, pkg.getFileName());
                pkg.setSigned(phoenixPackage.isPackageSigned());
                pkg.setModifyDate(new Date());
                pkgDao.update(pkg);
                if(null != file){
                    file.delete();
                }
            } catch (IOException e) {
                throw new BusinessException("msg.pkg.sysErro");
            }
        }
       
        resultMap.put("result", "checked and updated "+pkgList.size()+" packages");
        return resultMap;
    }
    
    @Override
    public List<Pkg> getLatestPkgByGroup(Long groupId, Integer queryType){
        return pkgDao.getLatestPkgByGroup(groupId, queryType);
    }
    
    @Override
    public List<Pkg> getLatestPkgByGroupAndModel(Long groupId,String model, Integer latestType){
        return pkgDao.getLatestPkgByGroupAndModel(groupId, model, latestType);
    }

}
