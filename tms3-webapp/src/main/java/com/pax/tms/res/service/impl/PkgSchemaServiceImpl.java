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

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.fs.FileManagerUtils;
import com.pax.common.pagination.IPageForm;
import com.pax.common.pagination.Page;
import com.pax.common.service.impl.BaseService;
import com.pax.common.util.DateTimeUtils;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.app.broadpos.para.ApplicationModule;
import com.pax.tms.app.broadpos.para.ApplicationSchema;
import com.pax.tms.app.broadpos.template.ParameterEntity;
import com.pax.tms.app.broadpos.template.SchemaUtil;
import com.pax.tms.deploy.dao.DeployDao;
import com.pax.tms.deploy.dao.GroupDeployHistoryDao;
import com.pax.tms.deploy.dao.TerminalDeployHistoryDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.report.domain.ParamHistoryInfo;
import com.pax.tms.res.dao.PkgSchemaDao;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgSchema;
import com.pax.tms.res.service.PkgSchemaService;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.res.web.form.AddPkgSchemaForm;
import com.pax.tms.res.web.form.EditPkgSchemaForm;
import com.pax.tms.res.web.form.QueryPkgSchemaForm;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.web.form.OperatorLogForm;

import io.vertx.core.json.JsonArray;

@Service("templateServiceImpl")
public class PkgSchemaServiceImpl extends BaseService<PkgSchema, Long> implements PkgSchemaService {

	@Autowired
	private PkgSchemaDao schemaDao;

	@Autowired
	private DeployDao deployDao;

	@Autowired
	private GroupService groupService;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private PkgService pkgService;

	@Autowired
	private TerminalDeployHistoryDao terminalDeployHistoryDao;

	@Autowired
	private GroupDeployHistoryDao groupDeployHistoryDao;

	// @Autowired
	// @Qualifier("mongoTemplate")
	// private MongoTemplate mongoTemplate;

	@Override
	public IBaseDao<PkgSchema, Long> getBaseDao() {

		return schemaDao;
	}

	@Override
	public List<PkgSchema> getPkgSchemaList(String pkgName, String pkgVersion) {
		if (StringUtils.isEmpty(pkgName)) {
			throw new BusinessException("msg.pkg.nameRequired");
		}
		if (StringUtils.isEmpty(pkgVersion)) {
			throw new BusinessException("msg.pkg.versionRequired");
		}
		return schemaDao.getPkgSchemaList(pkgName, pkgVersion);
	}

	@Override
	public PkgSchema validatePkgSchema(Long pkgSchemaId) {
		PkgSchema template = schemaDao.get(pkgSchemaId);
		if (template == null) {
			throw new BusinessException("template.notFound");
		}
		return template;
	}

	@Override
	public <E, S extends IPageForm> Page<E> page(S command) {
		QueryPkgSchemaForm form = (QueryPkgSchemaForm) command;
		if (form.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.validateGroup(form.getGroupId());
		AclManager.checkPermissionOfGroup(form.getLoginUserId(), group);

		return super.page(form);
	}

	@Override
	public void save(AddPkgSchemaForm command) {

		if (command.getPkgId() == null) {
			throw new BusinessException("msg.pkg.required");
		}
		if (schemaDao.existNameAndPkgId(command.getName(), command.getPkgId())) {
			throw new BusinessException("msg.pkg.schema.existsNameAndPkgId");
		}
		Pkg pkg = pkgService.validatePkg(command.getPkgId());
		// broadpos 包上传生成pkgSchema 不需要验证权限
		if (command.isCheckPermission()) {
			AclManager.checkPermissionOfPkg(command.getLoginUserId(), pkg);
		}

		Document doc = this.getPkgSchemaBySchemaPath(pkg.getSchemaFilePath());
		Map<String, String> defaultParams = new HashMap<String, String>();
		// mongoTemplate.findById(pkg.getParamSet(),
		// Map.class,PkgSchema.PARAM_COLLECTION);
		Map<String, String> inputParams = command.getParasMap();
		if (inputParams == null || inputParams.isEmpty()) {
			inputParams = defaultParams;
		}
		Map<String, String> paramMapDB = this.setParamValueForSchema(doc, defaultParams, inputParams);
		paramMapDB.put(PkgSchema.PARAM_SET_ID_KEY, UUID.randomUUID().toString());
		// mongoTemplate.save(paramMapDB, PkgSchema.PARAM_COLLECTION);
		PkgSchema template = new PkgSchema();
		template.setName(command.getName());
		template.setPkg(pkg);
		template.setVersion(DateTimeUtils.date2String(command.getRequestTime(), "yyyMMddHHmmss"));
		template.setFilePath(pkg.getSchemaFilePath());
		template.setFileSize((long) doc.asXML().length());
		template.setParamSet(paramMapDB.get(PkgSchema.PARAM_SET_ID_KEY));
		template.setSys(command.isSys());
		template.setCreator(command.getLoginUsername());
		template.setCreateDate(command.getRequestTime());
		template.setModifier(command.getLoginUsername());
		template.setModifyDate(command.getRequestTime());
		schemaDao.save(template);

		auditLogService.addAuditLog(Arrays.asList(template.getName()), command, OperatorLogForm.ADD_PKG_SCHEMA,
				" source package " + pkg.getName() + "  " + pkg.getVersion());
	}

	@Override
	public Document getPkgSchemaTemplate(Long pkgId) {
		List<ApplicationModule> moduleList = schemaDao.getAppModuleList(pkgId);
		return getPkdSchemaTemplateByModules(moduleList);
	}

	private Document getPkdSchemaTemplateByModules(List<ApplicationModule> moduleList) {
		ApplicationSchema schema = new ApplicationSchema(moduleList);
		return schema.createSchemaDocument();
	}

	@Override
	public Document getPkgSchemaBySchemaPath(String schemaFilePath) {
		return ApplicationSchema.loadSchemaFromDfs(schemaFilePath);
	}

	@Override
	public JsonArray getSchemaHtmlByPkgSchemaId(Long pkgSchemaId, boolean isTemp) {
		PkgSchema pkgSchema = this.get(pkgSchemaId);
		if (pkgSchema == null) {
			throw new BusinessException("template.notFound");
		}
		Document doc = this.getPkgSchemaBySchemaPath(pkgSchema.getFilePath());
		Map<String, String> paraMap = this.getParamSetFromMongoDB(pkgSchema.getParamSet());
		try {
			return new SchemaUtil().documentToHtml(doc, isTemp, paraMap);
		} catch (ParseException e) {
			throw new BusinessException("msg.pkg.schema.invalid", e);
		}
	}

	@Override
	public Map<String, String> getParamSetFromMongoDB(String paramSetId) {
		Map<String, String> dbMap = new HashMap<String, String>();
		// mongoTemplate.findById(paramSetId, Map.class,
		// PkgSchema.PARAM_COLLECTION);
		return dbMap;
	}

	@Override
	public Map<String, String> setParamValueForSchema(Document doc, Map<String, String> defaultParams,
			Map<String, String> newParams) {
		return this.setParamValueForSchema(doc, defaultParams, newParams, null, null);
	}

	public Map<String, String> setParamValueForSchema(Document doc, Map<String, String> defaultParams,
			Map<String, String> newParams, Map<String, String> oldParams) {
		return this.setParamValueForSchema(doc, defaultParams, newParams, oldParams, null);
	}

	/**
	 * @param doc
	 * @param defaultParams
	 * @param oldParams
	 * @param newParams
	 * @param paramHisList
	 * @return
	 */
	@Override
	public Map<String, String> setParamValueForSchema(Document doc, Map<String, String> defaultParams,
			Map<String, String> newParams, Map<String, String> oldParams, List<ParamHistoryInfo> paramHisList) {
		if (oldParams == null || oldParams.isEmpty()) {
			oldParams = defaultParams;
		}
		Map<String, String> paramMapDB = new HashMap<String, String>();
		List<ParameterEntity> paraList = SchemaUtil.getEntityListFromSchema(doc);
		String pid = "", defaultValue = "", oldValue = "", newValue = "", inputType = "";
		for (ParameterEntity entity : paraList) {
			inputType = entity.getInputType();
			pid = entity.getFinalPid();
			defaultValue = defaultParams.get(pid) == null ? "" : defaultParams.get(pid);
			oldValue = oldParams.get(pid) == null ? "" : oldParams.get(pid);
			newValue = newParams.get(pid) == null ? "" : newParams.get(pid);
			if (!entity.isInherit()) {
				newValue = defaultValue;
			} else {
				if (!entity.verifyPidValue(newValue)) {
					throw new BusinessException("msg.pkg.schema.param.invalid", new String[] { pid, newValue });
				}
			}
			if ("upload".equalsIgnoreCase(inputType) && StringUtils.isNotEmpty(newValue)) {
				newValue = this.getUploadFileFdfsPath(newValue, entity.getProgramAbbrName());
			}
			if (!StringUtils.equals(oldValue, newValue) && paramHisList != null) {
				paramHisList.add(new ParamHistoryInfo(pid, inputType, oldValue, newValue));
			}
			paramMapDB.put(pid, newValue);
		}
		return paramMapDB;
	}

	private String getUploadFileFdfsPath(String value, String pgmAbbrName) {
		String[] nameAndPath = value.split("\\|");
		if (nameAndPath.length == 2) {
			// get by web
			return nameAndPath[0] + "|FDFS|" + FileManagerUtils.saveLocalFileToFdfs(nameAndPath[1], "data");
		}
		return value;
	}

	@Override
	public void edit(EditPkgSchemaForm command) {
		if (command.getPkgId() == null) {
			throw new BusinessException("msg.pkg.required");
		}
		Pkg pkg = pkgService.validatePkg(command.getPkgId());
		if (command.getPkgSchemaId() == null) {
			throw new BusinessException("template.required");
		}
		PkgSchema pkgSchema = validatePkgSchema(command.getPkgSchemaId());
		if (!StringUtils.equals(pkgSchema.getName(), command.getName())
				&& schemaDao.existNameAndPkgId(command.getName(), command.getPkgId())) {
			throw new BusinessException("msg.pkg.schema.existsNameAndPkgId");
		}
		AclManager.checkPermissionOfPkg(command.getLoginUserId(), pkg);

		Map<String, String> inputParas = command.getParasMap();
		Document doc = getPkgSchemaBySchemaPath(pkg.getSchemaFilePath());
		Map<String, String> defaultParams = this.getParamSetFromMongoDB(pkg.getParamSet());
		Map<String, String> paramMapDB = this.setParamValueForSchema(doc, defaultParams, inputParas);
		paramMapDB.put(PkgSchema.PARAM_SET_ID_KEY, pkgSchema.getParamSet());
		// mongoTemplate.save(paramMapDB, PkgSchema.PARAM_COLLECTION);
		if (StringUtils.isNotEmpty(command.getName())) {
			pkgSchema.setName(command.getName());
		}
		pkgSchema.setModifier(command.getLoginUsername());
		pkgSchema.setModifyDate(command.getRequestTime());
		schemaDao.update(pkgSchema);

		auditLogService.addAuditLog(Arrays.asList(pkgSchema.getName()), command, OperatorLogForm.EDIT_PKG_SCHEMA,
				" source package " + pkg.getName() + " " + pkg.getVersion());
	}

	@Override
	public Long getSysInitPkgSchema(Long pkgId) {
		if (pkgId == null) {
			throw new BusinessException("msg.pkg.required");
		}
		return schemaDao.getSysInitPkgSchema(pkgId);
	}

	@Override
	public void deletePkgSchemas(List<Long> pkgIds) {
		schemaDao.deletePkgSchemas(pkgIds);

	}

	@Override
	public List<String> getPkgSchemaIds(List<Long> pkgList) {

		return schemaDao.getPkgSchemaIds(pkgList);
	}

	@Override
	public void delete(Long pkgId, Long pkgSchemaId, BaseForm command) {
		if (pkgId == null) {
			throw new BusinessException("msg.pkg.required");
		}
		Pkg pkg = pkgService.validatePkg(pkgId);
		if (pkgSchemaId == null) {
			throw new BusinessException("template.required");
		}
		PkgSchema pkgSchema = validatePkgSchema(pkgSchemaId);
		AclManager.checkPermissionOfPkg(command.getLoginUserId(), pkg);

		if (pkgSchema.isSys()) {
			throw new BusinessException("msg.pkg.schema.sysInit");
		}
		if (deployDao.isDeploy(pkgSchemaId)) {

			throw new BusinessException("msg.pkg.shema.isDeploy");
		}
		if (terminalDeployHistoryDao.isDeploy(pkgSchemaId)) {
			throw new BusinessException("msg.pkg.shema.isDeploy");
		}
		if (groupDeployHistoryDao.isDeploy(pkgSchemaId)) {
			throw new BusinessException("msg.pkg.shema.isDeploy");
		}
		// mongoTemplate.remove(new
		// Query(Criteria.where("_id").is(pkgSchema.getParamSet())),
		// "bposparam");

		auditLogService.addAuditLog(Arrays.asList(pkgSchema.getName()), command, OperatorLogForm.DELETE_PKG_SCHEMA,
				" source package " + pkg.getName() + " " + pkg.getVersion());

		schemaDao.delete(pkgSchemaId);

	}

}
