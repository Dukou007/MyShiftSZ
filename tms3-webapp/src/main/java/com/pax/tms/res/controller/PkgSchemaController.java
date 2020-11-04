/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: Create/Modify/Delete/List pkgSchema
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Aaron           	    Modify PkgSchemaController
 * ============================================================================		
 */
package com.pax.tms.res.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.Page;
import com.pax.common.security.CsrfProtect;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.http.UploadFileItem;
import com.pax.http.UploadFileUtils;
import com.pax.tms.app.broadpos.template.SchemaUtil;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgSchema;
import com.pax.tms.res.service.PkgSchemaService;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.res.web.form.AddPkgSchemaForm;
import com.pax.tms.res.web.form.EditPkgSchemaForm;
import com.pax.tms.res.web.form.QueryPkgSchemaForm;
import com.pax.tms.user.security.AclManager;

import io.vertx.core.json.JsonArray;

@Controller
@RequestMapping(value = "/pkgSchema")
public class PkgSchemaController extends BaseController {

	@Autowired
	private GroupService groupService;

	@Autowired
	private PkgService pkgService;

	@Autowired
	private PkgSchemaService schemaService;

	@RequiresPermissions(value = { "tms:template:view" })
	@RequestMapping(value = "/list/{groupId}")
	public ModelAndView list(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("pkgSchema/list");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		mav.addObject("group", group);
		mav.addObject("activeUrl", PkgSchema.LIST_URL);
		mav.addObject("title", Pkg.TITLE);
		return mav;
	}

	@RequiresPermissions(value = { "tms:template:view" })
	@RequestMapping(value = "/service/ajaxList/{groupId}")
	@ResponseBody
	public Page<Map<String, Object>> getPkgSchemaList(QueryPkgSchemaForm command, @PathVariable Long groupId) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		command.setGroupId(groupId);
		Page<Map<String, Object>> page = schemaService.page(command);
		return page;
	}

	@RequiresPermissions(value = { "tms:template:add" })
	@RequestMapping(value = "/toAdd/{groupId}")
	public ModelAndView toAdd(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("pkgSchema/add");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		List<String> pkgNameList = pkgService.getPkgNamesByGroupId(groupId, "BroadPos");
		mav.addObject("pkgNameList", pkgNameList);
		mav.addObject("group", group);
		mav.addObject("activeUrl", PkgSchema.LIST_URL);
		mav.addObject("title", Pkg.TITLE);
		return mav;
	}

	@RequiresPermissions(value = { "tms:template:add" })
	@ResponseBody
	@RequestMapping(value = "/service/add")
	@CsrfProtect
	public Map<String, Object> add(AddPkgSchemaForm command, HttpServletRequest request) {
		if (StringUtils.isEmpty(command.getPkgName())) {
			throw new BusinessException("msg.pkg.nameRequired");
		}
		if (StringUtils.isEmpty(command.getPkgVersion())) {
			throw new BusinessException("msg.pkg.versionRequired");
		}
		Map<String, String[]> inputParaMap = request.getParameterMap();
		Map<String, String> parasMap = transformParam(inputParaMap);
		command.setParasMap(parasMap);
		schemaService.save(command);
		return this.ajaxDoneSuccess();
	}

	public Map<String, String> transformParam(Map<String, String[]> inputParaMap) {
		Map<String, String> parasMap = new HashMap<>();
		Iterator<Entry<String, String[]>> it = inputParaMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String[]> en = it.next();
			if (en.getValue().length > 0 && StringUtils.isNotEmpty(en.getValue()[0])) {
				parasMap.put(en.getKey(), en.getValue()[0]);
			}
		}
		return parasMap;
	}

	@RequestMapping(value = "/service/getPkgAndPkgSchemaList")
	@ResponseBody
	public Object getPkgAndPkgSchemaList(String pkgName, String pkgVersion) throws ParseException {
		if (StringUtils.isEmpty(pkgName)) {
			throw new BusinessException("msg.pkg.nameRequired");
		}
		if (StringUtils.isEmpty(pkgVersion)) {
			throw new BusinessException("msg.pkg.versionRequired");
		}

		Pkg pkg = pkgService.getPkgByNameAndVersion(pkgName, pkgVersion);
		List<PkgSchema> pkgSchemaList = schemaService.getPkgSchemaList(pkgName, pkgVersion);
		List<Map<String, Object>> list = getPkgAndPkgSchemaInfoList(pkg, pkgSchemaList);
		return list;

	}

	public List<Map<String, Object>> getPkgAndPkgSchemaInfoList(Pkg pkg, List<PkgSchema> pkgSchemaList) {
		List<Map<String, Object>> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		List<Object> pkgSchemaInfoList = new ArrayList<>();
		map.put("pkgId", pkg.getId());
		map.put("pkgType", pkg.getType());
		for (PkgSchema pkgSchema : pkgSchemaList) {
			Map<String, Object> m = new HashMap<>();
			m.put("pkgSchemaId", pkgSchema.getId());
			m.put("pkgSchemaName", pkgSchema.getName());
			pkgSchemaInfoList.add(m);
		}
		map.put("pkgSchemaInfoList", pkgSchemaInfoList);
		list.add(map);
		return list;
	}

	@RequiresPermissions(value = { "tms:template:delete" })
	@ResponseBody
	@RequestMapping(value = "/service/delete")
	@CsrfProtect
	public Map<String, Object> delete(Long pkgSchemaId, Long pkgId, BaseForm command) {
		if (pkgSchemaId == null) {
			throw new BusinessException("template.required");
		}
		if (pkgId == null) {
			throw new BusinessException("msg.pkg.required");
		}
		if (schemaService.get(pkgSchemaId) == null) {
			throw new BusinessException("template.notFound");
		}
		schemaService.delete(pkgId, pkgSchemaId, command);
		return this.ajaxDoneSuccess();
	}

	@ResponseBody
	@RequestMapping(value = "/service/getSchemaByPkgSchemaId/{pkgSchemaId}")
	public Object getSchemaByPkgSchemaId(@PathVariable Long pkgSchemaId) throws ParseException {
		JsonArray jsonArr = schemaService.getSchemaHtmlByPkgSchemaId(pkgSchemaId, true);
		Map<String, Object> result = new HashMap<>();
		result.put("statusCode", SUCCESS_STATUS_CODE);
		result.put("Group", jsonArr.toString());
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/service/getSchemaByPkgSchemaIdAndParamSet/{pkgSchemaId}/{paramSet}")
	public Object getSchemaByPkgSchemaIdAndParamSet(@PathVariable Long pkgSchemaId, @PathVariable String paramSet)
			throws ParseException {
		PkgSchema pkgSchema = schemaService.get(pkgSchemaId);
		if (pkgSchema == null) {
			throw new BusinessException("template.notFound");
		}
		Document doc = schemaService.getPkgSchemaBySchemaPath(pkgSchema.getFilePath());
		Map<String, String> paraMap = schemaService.getParamSetFromMongoDB(paramSet);
		JsonArray jsonArr = new SchemaUtil().documentToHtml(doc, false, paraMap);
		Map<String, Object> result = new HashMap<>();
		result.put("statusCode", SUCCESS_STATUS_CODE);
		result.put("Group", jsonArr.toString());
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/service/getSchemaByPkgId/{pkgId}")
	public Object getSchemaByPkgId(@PathVariable Long pkgId) throws ParseException {
		Pkg pkg = pkgService.get(pkgId);
		Document doc = schemaService.getPkgSchemaTemplate(pkgId);
		Map<String, String> paraMap = schemaService.getParamSetFromMongoDB(pkg.getParamSet());
		JsonArray jsonArr = new SchemaUtil().documentToHtml(doc, true, paraMap);
		Map<String, Object> result = new HashMap<>();
		result.put("statusCode", SUCCESS_STATUS_CODE);
		result.put("Group", jsonArr.toString());
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/service/getSysInitPkgSchema/{pkgId}")
	public Object getSysInitPkgSchema(@PathVariable Long pkgId) {
		if (pkgId == null) {
			throw new BusinessException("msg.pkg.required");
		}
		Long pkgSchemaId = schemaService.getSysInitPkgSchema(pkgId);
		JsonArray jsonArr = schemaService.getSchemaHtmlByPkgSchemaId(pkgSchemaId, true);
		Map<String, Object> result = new HashMap<>();
		result.put("pkgSchemaId", pkgSchemaId);
		result.put("statusCode", SUCCESS_STATUS_CODE);
		result.put("Group", jsonArr.toString());
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/service/upload", method = RequestMethod.PUT)
	public Map<String, Object> fileUpload(BaseForm command, HttpServletRequest request) throws FileUploadException {
		Map<String, Object> map = new HashMap<>();
		String user = command.getLoginUsername();
		UploadFileItem uploadFileItem = UploadFileUtils.storeUploadFile(request, user);

		if (uploadFileItem == null) {
			map.put("message", "fail");
			return map;
		} else {
			map.put("message", "success");
			map.put("filePath", uploadFileItem.getUrl());
			map.put("fileName", uploadFileItem.getFilename());
			return map;
		}
	}

	@RequiresPermissions(value = { "tms:template:view" })
	@RequestMapping(value = "/profile/{groupId}/{pkgSchemaId}")
	public ModelAndView profile(@PathVariable Long groupId, @PathVariable Long pkgSchemaId, BaseForm command) {
		ModelAndView mav = new ModelAndView("pkgSchema/profile");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		PkgSchema pkgSchema = schemaService.get(pkgSchemaId);
		mav.addObject("pkgSchema", pkgSchema);
		mav.addObject("group", group);
		mav.addObject("activeUrl", PkgSchema.LIST_URL);
		mav.addObject("title", Pkg.TITLE);
		return mav;
	}

	@RequiresPermissions(value = { "tms:template:edit" })
	@ResponseBody
	@RequestMapping(value = "/service/edit", method = RequestMethod.POST)
	@CsrfProtect
	public Map<String, Object> edit(EditPkgSchemaForm command, HttpServletRequest request) {
		if (command.getPkgSchemaId() == null) {
			throw new BusinessException("template.required");
		}
		Map<String, String[]> inputParaMap = request.getParameterMap();
		Map<String, String> parasMap = transformParam(inputParaMap);
		command.setParasMap(parasMap);
		schemaService.edit(command);
		return this.ajaxDoneSuccess();
	}

}
