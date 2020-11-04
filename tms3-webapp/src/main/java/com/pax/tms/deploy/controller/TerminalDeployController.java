/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: Create/Delete/List/ terminal deployment
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Aaron           	    Modify TerminalDeployController
 * ============================================================================		
 */
package com.pax.tms.deploy.controller;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.Page;
import com.pax.common.security.CsrfProtect;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.common.web.support.editor.UTCDateEditor;
import com.pax.tms.app.broadpos.template.SchemaUtil;
import com.pax.tms.deploy.domain.DeployInfo;
import com.pax.tms.deploy.model.Deploy;
import com.pax.tms.deploy.model.TerminalDeploy;
import com.pax.tms.deploy.service.DeployService;
import com.pax.tms.deploy.service.TerminalDeployService;
import com.pax.tms.deploy.web.form.EditTerminalDeployForm;
import com.pax.tms.deploy.web.form.QueryCurrentTsnDeployForm;
import com.pax.tms.deploy.web.form.QueryHistotyTsnDeployForm;
import com.pax.tms.deploy.web.form.TerminalDeployForm;
import com.pax.tms.deploy.web.form.TerminalDeployOperatorForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.location.service.AddressService;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgSchema;
import com.pax.tms.res.service.PkgSchemaService;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.security.AclManager;

import io.vertx.core.json.JsonArray;

@Controller
@RequestMapping(value = "/terminalDeploy")
public class TerminalDeployController extends BaseController {

	@Autowired
	private GroupService groupService;

	@Autowired
	private TerminalService terminalService;

	@Autowired
	private TerminalDeployService terminalDeployService;

	@Autowired
	private PkgService pkgService;

	@Autowired
	private PkgSchemaService schemaService;

	@Autowired
	private AddressService addressService;

	@Autowired
	@Qualifier("deployServiceImpl")
	private DeployService deployService;

	@RequiresPermissions(value = { "tms:terminal:deployments:view" })
	@RequestMapping(value = "/list/{groupId}/{tsn}")
	public ModelAndView list(@PathVariable Long groupId, @PathVariable String tsn, BaseForm command) {
		ModelAndView mav = new ModelAndView("terminal/deployment/list");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		Terminal terminal = terminalService.get(tsn);
		if (terminal == null) {
			throw new BusinessException("tsn.notFound", new String[] { tsn });
		}
		mav.addObject("group", group);
		mav.addObject("activeUrl", TerminalDeploy.LIST_URL);
		mav.addObject("title", TerminalDeploy.TITLE);
		mav.addObject("terminal", terminal);
		return mav;
	}

	@ResponseBody
	@RequestMapping(value = "/service/getCurrentDeploysByTsn/{groupId}/{tsn}")
	public Page<DeployInfo> getCurrentDeploysByTsn(QueryCurrentTsnDeployForm command, @PathVariable Long groupId,
			@PathVariable String tsn) {
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		Terminal terminal = terminalService.get(tsn);
		if (terminal == null) {
			throw new BusinessException("tsn.notFound", new String[] { tsn });
		}
		command.setGroupId(groupId);
		command.setTsn(tsn);
		command.setQueryType(Pkg.QUERY_PKG);
		String nowActvStartTimeText = "Immediately";//ActvStartTimeText为now时，则显示immediately
		Page<DeployInfo> page = terminalDeployService.getCurrentDeploysByTsn(command);
		List<DeployInfo> deployInfos = page.getItems();
		for (DeployInfo deployInfo : deployInfos) {
			deployInfo.setDwnlStartTimeText(UTCDateEditor.formatDate(deployInfo.getDwnlStartTime(),
					deployInfo.getTimeZone(), true, "HH:mm MM/dd/yyyy"));
			if(null == deployInfo.getActvStartTime()){
				deployInfo.setActvStartTimeText(nowActvStartTimeText);
			}
			else {
				deployInfo.setActvStartTimeText(UTCDateEditor.formatDate(deployInfo.getActvStartTime(),
						deployInfo.getTimeZone(), true, "HH:mm MM/dd/yyyy"));
			}
			if (deployInfo.getDwnlEndTime() != null) {
				deployInfo.setDwnlEndTimeText(UTCDateEditor.formatDate(deployInfo.getDwnlEndTime(),
						deployInfo.getTimeZone(), true, "HH:mm MM/dd/yyyy"));
			}

		}
		return page;
	}

	@ResponseBody
	@RequestMapping(value = "/service/getHistorytDeploysByTsn/{groupId}/{tsn}")
	public Page<DeployInfo> getHistorytDeploysByTsn(QueryHistotyTsnDeployForm command, @PathVariable Long groupId,
			@PathVariable String tsn) {
		if (StringUtils.isEmpty(tsn)) {
			Collections.emptyList();
		}
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		command.setTsn(tsn);
		command.setGroupId(groupId);
		command.setQueryType(Pkg.QUERY_PKG);
		Page<DeployInfo> page = terminalDeployService.getHistorytDeploysByTsn(command);
		return page;
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

	@RequestMapping(value = "/toDeploy/{groupId}/{tsn}")
	public ModelAndView toDeploy(@PathVariable Long groupId, @PathVariable String tsn, Long pkgId, BaseForm command) {
		ModelAndView mav = new ModelAndView("terminal/deployment/add");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		Terminal terminal = terminalService.get(tsn);
		if (terminal == null) {
			throw new BusinessException("tsn.notFound", new String[] { tsn });
		}
		List<String> pkgNameList = pkgService.getPkgNamesByGroupId(groupId, "Multilane");
		List<Map<String, String>> timeZoneList = addressService.getTimeZones();
		Map<String, Object> parentTimeZone;
		if (StringUtils.isNotEmpty(terminal.getCountry())) {
			parentTimeZone = addressService.getParentTimeZone(terminal.getCountry(), terminal.getTimeZone());
		} else {
			parentTimeZone = Collections.emptyMap();
		}
		if (pkgId != null) {
			Pkg pkg = pkgService.get(pkgId);
			mav.addObject("pkg", pkg);
		}
		mav.addObject("parentTimeZone", parentTimeZone);
		mav.addObject("timeZoneList", timeZoneList);
		mav.addObject("pkgNameList", pkgNameList);
		mav.addObject("group", group);
		mav.addObject("activeUrl", TerminalDeploy.DEPLOY_URL);
		mav.addObject("title", TerminalDeploy.TITLE);
		mav.addObject("terminal", terminal);
		return mav;
	}

	@RequiresPermissions(value = { "tms:terminal:deployments:add" })
	@ResponseBody
	@RequestMapping(value = "/service/deploy")
	@CsrfProtect
	public Map<String, Object> deploy(TerminalDeployForm command, HttpServletRequest request) {
		if (StringUtils.isEmpty(command.getTsn())) {
			Collections.emptyList();
		}
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (command.getPkgId() == null) {
			throw new BusinessException("msg.pkg.required");
		}
		Map<String, String[]> inputParaMap = request.getParameterMap();
		Map<String, String> parasMap = transformParam(inputParaMap);
		command.setParaMap(parasMap);

		terminalDeployService.deploy(command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:terminal:deployments:disable" })
	@ResponseBody
	@RequestMapping(value = "/service/deactivate")
	@CsrfProtect
	public Map<String, Object> deactivate(TerminalDeployOperatorForm command) {
		if (StringUtils.isEmpty(command.getTsn())) {
			throw new BusinessException("tsn.Required");
		}
		if (command.getDeployId() == null) {
			throw new BusinessException("tsn.deployTask.Required");
		}
		if (StringUtils.equals(command.getIsInherited(), "true")) {
			command.setInherit(true);
		} else {
			command.setInherit(false);
		}
		terminalDeployService.deactivate(command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:terminal:deployments:enable" })
	@ResponseBody
	@RequestMapping(value = "/service/activate")
	@CsrfProtect
	public Map<String, Object> activate(TerminalDeployOperatorForm command) {
		if (StringUtils.isEmpty(command.getTsn())) {
			throw new BusinessException("tsn.Required");
		}
		if (command.getDeployId() == null) {
			throw new BusinessException("tsn.deployTask.Required");
		}
		if (StringUtils.equals(command.getIsInherited(), "true")) {
			command.setInherit(true);
		} else {
			command.setInherit(false);
		}
		terminalDeployService.activate(command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:terminal:deployments:delete" })
	@ResponseBody
	@RequestMapping(value = "/service/delete")
	@CsrfProtect
	public Map<String, Object> delete(TerminalDeployOperatorForm command) {
		if (StringUtils.isEmpty(command.getTsn())) {
			throw new BusinessException("tsn.Required");
		}
		if (command.getDeployId() == null) {
			throw new BusinessException("tsn.deployTask.Required");
		}
		terminalDeployService.delete(command);
		return this.ajaxDoneSuccess();
	}

	@ResponseBody
	@RequestMapping(value = "/service/getSchemaByPkgSchemaId/{pkgSchemaId}/{tsn}")
	public Object getSchemaByPkgSchemaId(@PathVariable Long pkgSchemaId, @PathVariable String tsn)
			throws ParseException {
		JsonArray jsonArr = terminalDeployService.getTrmlastSchemaHtml(pkgSchemaId, tsn);
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

	@RequestMapping(value = "/toEdit/{groupId}/{tsn}/{deployId}")
	public ModelAndView toEdit(@PathVariable Long groupId, @PathVariable String tsn, @PathVariable Long deployId,
			BaseForm command) {
		ModelAndView mav = new ModelAndView("terminal/deployment/edit");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		if (deployId == null) {
			throw new BusinessException("msg.deploy.deployRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		Terminal terminal = terminalService.get(tsn);
		Deploy deploy = deployService.get(deployId);
		mav.addObject("group", group);
		mav.addObject("terminal", terminal);
		mav.addObject("deploy", deploy);
		mav.addObject("activeUrl", Terminal.LIST_URL);
		mav.addObject("title", Terminal.TITLE);
		return mav;
	}

	@RequestMapping(value = "/toView/{groupId}/{tsn}/{pkgId}/{pkgSchemaId}/{paramSet}")
	public ModelAndView toView(@PathVariable Long groupId, @PathVariable String tsn, @PathVariable Long pkgId,
			@PathVariable Long pkgSchemaId, @PathVariable String paramSet, BaseForm command) {
		ModelAndView mav = new ModelAndView("terminal/deployment/view");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (StringUtils.isEmpty(tsn)) {
			throw new BusinessException("tsn.Required");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		Terminal terminal = terminalService.get(tsn);
		Pkg pkg = pkgService.get(pkgId);
		mav.addObject("pkg", pkg);
		mav.addObject("group", group);
		mav.addObject("terminal", terminal);
		mav.addObject("pkgSchemaId", pkgSchemaId);
		mav.addObject("paramSet", paramSet);
		mav.addObject("activeUrl", Terminal.LIST_URL);
		mav.addObject("title", Terminal.TITLE);
		return mav;
	}

	@ResponseBody
	@RequestMapping(value = "/service/editDeployParam")
	@CsrfProtect
	public Map<String, Object> editDeployParam(EditTerminalDeployForm command, HttpServletRequest request) {
		if (command.getPkgSchemaId() == null) {
			throw new BusinessException("template.required");
		}
		Map<String, String[]> inputParaMap = request.getParameterMap();
		Map<String, String> parasMap = transformParam(inputParaMap);
		command.setParaMap(parasMap);
		terminalDeployService.edit(command);
		return this.ajaxDoneSuccess();
	}
	
}
