/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: Create/Delete/List group deployment
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Aaron           	    Modify GroupDeployController
 * ============================================================================		
 */
package com.pax.tms.deploy.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.terminal.web.form.QueryTerminalForm;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
import com.pax.tms.deploy.model.GroupDeploy;
import com.pax.tms.deploy.service.DeployService;
import com.pax.tms.deploy.service.GroupDeployService;
import com.pax.tms.deploy.web.form.EditGroupDeployForm;
import com.pax.tms.deploy.web.form.GroupChangeParaForm;
import com.pax.tms.deploy.web.form.GroupDeployForm;
import com.pax.tms.deploy.web.form.GroupDeployOperatorForm;
import com.pax.tms.deploy.web.form.QueryCurrentGroupDeployForm;
import com.pax.tms.deploy.web.form.QueryHistoryGroupDeployForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.location.service.AddressService;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgSchema;
import com.pax.tms.res.service.ModelService;
import com.pax.tms.res.service.PkgSchemaService;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.user.security.AclManager;

import io.vertx.core.json.JsonArray;

@Controller
@RequestMapping(value = "/groupDeploy")
public class GroupDeployController extends BaseController {

	@Autowired
	private GroupService groupService;

	@Autowired
	private ModelService modelService;

	@Autowired
	private PkgService pkgService;

	@Autowired
	private GroupDeployService groupDeployService;

	@Autowired
	private PkgSchemaService schemaService;

	@Autowired
	private AddressService addressService;

    @Autowired
    private TerminalService terminalService;

	@Autowired
	@Qualifier("deployServiceImpl")
	private DeployService deployService;

	@RequiresPermissions(value = { "tms:group:deployments:view" })
	@RequestMapping(value = "/list/{groupId}")
	public ModelAndView list(@PathVariable Long groupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("group/deployment/list");
		Group group = groupService.get(groupId);
		if (group == null) {
			throw new BusinessException("msg.group.groupNotFound");
		}
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		mav.addObject("group", group);
		mav.addObject("activeUrl", GroupDeploy.LIST_URL);
		mav.addObject("title", GroupDeploy.TITLE);
		return mav;
	}

	@ResponseBody
	@RequestMapping(value = "/service/getPkgNamesByGroupIdAndDestmodel")
	public List<String> getPkgNamesByGroupIdAndDestmodel(Long groupId, String destModel, int type) {
		List<String> pkgNameList = new ArrayList<>();
		if (type == 0) {
			pkgNameList = pkgService.getPkgNamesByGroupIdAndDestmodel(groupId, destModel, "BroadPos");
		} else if (type == 1) {
			pkgNameList = pkgService.getPkgNamesByGroupIdAndDestmodel(groupId, destModel, null);
		}
		return pkgNameList;
	}

	@ResponseBody
	@RequestMapping(value = "/service/getCurrentDeploysByGroupId/{deployGroupId}")
	public Page<DeployInfo> getCurrentDeploysByGroupId(QueryCurrentGroupDeployForm command,
			@PathVariable Long deployGroupId) {
		if (deployGroupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		command.setGroupId(deployGroupId);
		command.setQueryType(Pkg.QUERY_PKG);
		String nowActvStartTimeText = "Immediately";//ActvStartTimeText为now时，则显示immediately
		Page<DeployInfo> page = groupDeployService.getLineDeploysByGroupId(command);
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
	@RequestMapping(value = "/service/getHistoryDeploysByGroupId/{deployGroupId}")
	public Page<DeployInfo> getHistoryDeploysByGroupId(QueryHistoryGroupDeployForm command,
			@PathVariable Long deployGroupId) {
		if (deployGroupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		command.setGroupId(deployGroupId);
		command.setQueryType(Pkg.QUERY_PKG);
		Page<DeployInfo> list = groupDeployService.getHistoryDeploysByGroupId(command);
		return list;
	}

	public Map<String, String> transformParam(Map<String, String[]> inputParaMap) {
		Map<String, String> parasMap = new HashMap<String, String>();
		Iterator<Entry<String, String[]>> it = inputParaMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String[]> en = it.next();
			if (en.getValue().length > 0 && StringUtils.isNotEmpty(en.getValue()[0])) {
				parasMap.put(en.getKey(), en.getValue()[0]);
			}
		}
		return parasMap;
	}

	@RequiresPermissions(value = { "tms:group:deployments:add" })
	@RequestMapping(value = "/toDeploy/{groupId}")
	public ModelAndView toDeploy(@PathVariable Long groupId, Long pkgId, BaseForm command) {
		ModelAndView mav = new ModelAndView("group/deployment/add");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		List<String> pkgNameList = pkgService.getPkgNamesByGroupId(groupId, "Multilane");
		List<Map<String, String>> timeZoneList = addressService.getTimeZones();
		Map<String, Object> parentTimeZone = new HashMap<>();
		if (StringUtils.isNotEmpty(group.getCountry())) {
			parentTimeZone = addressService.getParentTimeZone(group.getCountry(), group.getTimeZone());
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
		mav.addObject("modelList", modelService.getList());
		mav.addObject("activeUrl", GroupDeploy.DEPLOY_URL);
		mav.addObject("title", GroupDeploy.TITLE);
		return mav;
	}



    @RequiresPermissions(value = {"tms:group:deployments:add"})
    @ResponseBody
	@RequestMapping(value = "/service/deployConfirm")
    @CsrfProtect
	public Map<String,Long> getImpactNumber(@RequestParam("groupId") Long groupId, @RequestParam(value = "modelId",required = false) String modelId){
        if (groupId == null) {
            throw new BusinessException("msg.group.groupRequired");
        }

        Map<String,Long> confirmInfo= new HashMap<>();
        QueryTerminalForm queryTerminalForm = new QueryTerminalForm();

        queryTerminalForm.setGroupId(groupId);

        if(StringUtils.isNotEmpty(modelId)){
            queryTerminalForm.setDestModel(modelId);
            long count = terminalService.count(queryTerminalForm);

            confirmInfo.put(modelId,count);
            confirmInfo.put("Count",count);
        }else{
            List<String> modelList = terminalService.getTerminalModels(groupId);
            long count = 0;
            for (String model : modelList) {
                queryTerminalForm.setDestModel(model);
                long modelNum = terminalService.count(queryTerminalForm);
                confirmInfo.put(model,modelNum);
                count = count+modelNum;
            }
            confirmInfo.put("Count",count);
        }

        return confirmInfo;
    }

	@RequiresPermissions(value = { "tms:group:deployments:add" })
	@ResponseBody
	@RequestMapping(value = "/service/deploy")
	@CsrfProtect
	public Map<String, Object> deploy(GroupDeployForm command, HttpServletRequest request) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (command.getPkgId() == null) {
			throw new BusinessException("msg.pkg.required");
		}
		Map<String, String[]> inputParaMap = request.getParameterMap();
		Map<String, String> parasMap = transformParam(inputParaMap);
		command.setParamMap(parasMap);

		groupDeployService.deploy(command);

		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:group:deployments:disable" })
	@ResponseBody
	@RequestMapping(value = "/service/deactivate")
	@CsrfProtect
	public Map<String, Object> deactivate(GroupDeployOperatorForm command) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (StringUtils.equals(command.getIsInherited(), "true")) {
			command.setInherit(true);
		} else {
			command.setInherit(false);
		}
		groupDeployService.deactivate(command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:group:deployments:enable" })
	@ResponseBody
	@RequestMapping(value = "/service/activate")
	@CsrfProtect
	public Map<String, Object> activate(GroupDeployOperatorForm command) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (StringUtils.equals(command.getIsInherited(), "true")) {
			command.setInherit(true);
		} else {
			command.setInherit(false);
		}
		groupDeployService.activate(command);
		return this.ajaxDoneSuccess();
	}

	@RequiresPermissions(value = { "tms:group:deployments:delete" })
	@ResponseBody
	@RequestMapping(value = "/service/delete")
	@CsrfProtect
	public Map<String, Object> delete(GroupDeployOperatorForm command) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		groupDeployService.delete(command);
		return this.ajaxDoneSuccess();
	}

	@RequestMapping(value = "/toChange/{groupId}/{deployGroupId}")
	public ModelAndView toChange(@PathVariable Long groupId, @PathVariable Long deployGroupId, BaseForm command) {
		ModelAndView mav = new ModelAndView("group/deployment/change");
		if (deployGroupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		Group deployGroup = groupService.get(deployGroupId);
		mav.addObject("group", group);
		mav.addObject("deployGroup", deployGroup);
		mav.addObject("modelList", modelService.list());
		mav.addObject("activeUrl", Group.LIST_URL);
		mav.addObject("title", Group.TITLE);
		return mav;
	}

	@ResponseBody
	@RequestMapping(value = "/service/changeDeployParas")
	@CsrfProtect
	public Map<String, Object> changeDeployParas(GroupChangeParaForm command, HttpServletRequest request) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (command.getPkgId() == null) {
			throw new BusinessException("msg.pkg.required");
		}
		if (StringUtils.isEmpty(command.getDestModel())) {
			throw new BusinessException("model.Required");
		}
		Map<String, String[]> inputParaMap = request.getParameterMap();
		Map<String, String> parasMap = transformParam(inputParaMap);
		command.setParamMap(parasMap);
		groupDeployService.changeDeployParas(command);
		return this.ajaxDoneSuccess();
	}

	@RequestMapping(value = "/toEdit/{groupId}/{deployGroupId}/{currentDeployGroupId}/{deployId}")
	public ModelAndView toEdit(@PathVariable Long groupId, @PathVariable Long deployGroupId,
			@PathVariable Long currentDeployGroupId, @PathVariable Long deployId, BaseForm command) {
		ModelAndView mav = new ModelAndView("group/deployment/edit");
		String inherited;
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (deployGroupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (deployId == null) {
			throw new BusinessException("msg.deploy.deployRequired");
		}
		if (deployGroupId.equals(currentDeployGroupId)) {
			inherited = "false";
		} else {
			inherited = "true";
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		Group deployGroup = groupService.get(deployGroupId);
		Deploy deploy = deployService.get(deployId);
		mav.addObject("inherited", inherited);
		mav.addObject("group", group);
		mav.addObject("deployGroup", deployGroup);
		mav.addObject("deploy", deploy);
		mav.addObject("activeUrl", Group.LIST_URL);
		mav.addObject("title", Group.TITLE);
		return mav;
	}

	@RequestMapping(value = "/toView/{groupId}/{deployGroupId}/{pkgId}/{pkgSchemaId}/{paramSet}")
	public ModelAndView toView(@PathVariable Long groupId, @PathVariable Long deployGroupId, @PathVariable Long pkgId,
			@PathVariable Long pkgSchemaId, @PathVariable String paramSet, BaseForm command) {
		ModelAndView mav = new ModelAndView("group/deployment/view");
		if (groupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		if (deployGroupId == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		Group deployGroup = groupService.get(deployGroupId);
		Pkg pkg = pkgService.get(pkgId);
		mav.addObject("pkg", pkg);
		mav.addObject("group", group);
		mav.addObject("deployGroup", deployGroup);
		mav.addObject("pkgSchemaId", pkgSchemaId);
		mav.addObject("paramSet", paramSet);
		mav.addObject("activeUrl", Group.LIST_URL);
		mav.addObject("title", Group.TITLE);
		return mav;
	}

	@ResponseBody
	@RequestMapping(value = "/service/editDeployParam")
	@CsrfProtect
	public Map<String, Object> editDeployParam(EditGroupDeployForm command, HttpServletRequest request) {
		if (command.getPkgSchemaId() == null) {
			throw new BusinessException("template.required");
		}
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Map<String, String[]> inputParaMap = request.getParameterMap();
		Map<String, String> parasMap = transformParam(inputParaMap);
		command.setParaMap(parasMap);
		groupDeployService.edit(command);
		return this.ajaxDoneSuccess();
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
		JsonArray jsonArr = new SchemaUtil().documentToHtml(doc, true, paraMap);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("statusCode", SUCCESS_STATUS_CODE);
		result.put("Group", jsonArr.toString());
		return result;
	}

}
