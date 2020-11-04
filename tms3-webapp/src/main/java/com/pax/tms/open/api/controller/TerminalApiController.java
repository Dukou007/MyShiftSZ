package com.pax.tms.open.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.Page;
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.location.service.AddressService;
import com.pax.tms.open.api.annotation.ApiPermission;
import com.pax.tms.open.api.info.TerminalDetail;
import com.pax.tms.open.api.info.TerminalInfo;
import com.pax.tms.open.api.req.AddTerminalRequest;
import com.pax.tms.open.api.req.QueryTerminalRequest;
import com.pax.tms.open.api.rsp.AddTerminalResponse;
import com.pax.tms.open.api.rsp.QueryTerminalResponse;
import com.pax.tms.open.api.rsp.Result;
import com.pax.tms.open.api.rsp.ResultUtil;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.terminal.web.form.AddTerminalForm;
import com.pax.tms.terminal.web.form.QueryTerminalForm;

@RestController
@Api(tags = { "Terminal" })
@RequestMapping("/terminal/api")
public class TerminalApiController extends BaseController {

	@Autowired
	private TerminalService terminalService;

	@Autowired
	private AddressService addressService;

	@ApiOperation(value = "Add Terminals", notes = "Add Terminals")
	@PostMapping("/add-terminals")
	@ApiPermission(value = "tms:terminal:add")
	public Result<AddTerminalResponse> addTerminals(@RequestBody AddTerminalRequest request) {
		
		if (StringUtils.isEmpty(request.getCountryName())) {
		    throw new BusinessException("msg.countryName.required");
		}
		if (StringUtils.isEmpty(request.getStateProvinceName())) {
		    throw new BusinessException("msg.provinceName.required");
		}
		if (StringUtils.isEmpty(request.getCityName())) {
		    throw new BusinessException("msg.cityName.required");
		}
		if (StringUtils.isEmpty(request.getZipCode())) {
		    throw new BusinessException("msg.zipCode.required");
		}
		
		String daylightSaving = request.isDaylightSaving() ? "1" : "0";
		addressService.checkTimeZoneData(request.getCountryName(), request.getTimeZone(), daylightSaving);
		
		AddTerminalForm command = createAddTerminalForm(request);
		Map<String, Object> resultMap = terminalService.save(command);

		Result<AddTerminalResponse> result = new Result<>();
        result.success(createAddTerminalResponse(resultMap, request));
        return result;

	}

	@SuppressWarnings("unchecked")
	private AddTerminalResponse createAddTerminalResponse(Map<String, Object> resultMap, AddTerminalRequest request) {

		AddTerminalResponse response = new AddTerminalResponse();
		response.setTotalTsns((Set<String>) resultMap.get("totalTsns"));
		response.setExistTsnNeedToAssignGroup((List<String>) resultMap.get("existTsnNeedToAssignGroup"));
		response.setExistTsnIgnoreToAssignGroup((List<String>) resultMap.get("existTsnIgnoreToAssignGroup"));
		response.setNewTsns((Collection<String>) resultMap.get("newTsns"));
		response.setOwnByOtherParallerGroupTsn((List<String>) resultMap.get("ownByOtherParallerGroupTsn"));
		response.setTerminalType((String) resultMap.get("terminalType"));
		response.setCountryName(request.getCountryName());
		response.setStateProvinceName(request.getStateProvinceName());
		response.setCityName(request.getCityName());
		response.setZipCode(request.getZipCode());
		response.setTimeZone(request.getTimeZone());
		response.setDaylightSaving(request.isDaylightSaving());
		response.setSyncToServerTime(request.isSyncToServerTime());
		response.setGroupId((Long) resultMap.get("groupId"));
		response.setGroupNamePath((String) resultMap.get("groupNamePath"));
		response.setAddress((String) resultMap.get("address"));
		response.setDescription((String) resultMap.get("description"));
		response.setCreatedBy((String) resultMap.get("createdBy"));
		response.setCreatedOn((Date) resultMap.get("createdOn"));
		response.setUpdatedOn((Date) resultMap.get("updatedOn"));
		response.setUpdatedBy((String) resultMap.get("updatedBy"));
		return response;
	}

	@ApiOperation(value = "Query Terminals", notes = "Query Terminals")
	@GetMapping("/query-terminals")
	@ApiPermission(value = "tms:terminal:view")
	public Result<QueryTerminalResponse> queryTerminals(QueryTerminalRequest request) {
		QueryTerminalForm command = createQueryTerminalForm(request);
		Page<Map<String, Object>> page = terminalService.page(command);
		QueryTerminalResponse response = createQueryTerminalResponse(page);
		Result<QueryTerminalResponse> result = new Result<>();
        result.success(response);
        return result;
	}
	
	/*
	 * @DeleteMapping 捕捉不到BusinessException
	 * 为了Resetful风格的完整性，不改成@PostMapping
	 * 与杭州之前的接口风格错误码一致，异常则改变responseCode为300
	 */
	@ApiOperation(value = "Delete A Terminal")
	@ApiImplicitParam(name = "sn", value = "Device Serial Number", required = true, dataType = "String", paramType = "path")
	@DeleteMapping("/delete-terminal/{sn}")
	@ApiPermission(value = "tms:terminal:delete")
	public Result<T> deleteTerminal(@PathVariable(value="sn")String sn, HttpServletResponse response){
		BaseForm command = new BaseForm();
		String[] tsns = {sn};
		if (ArrayUtils.isEmpty(tsns)) {
		    throw new BusinessException("tsn.Required");
		}
		boolean checkT = terminalService.checkTerminalInGroup(sn, command.getLoginUserId());
		if(!checkT){
		    throw new BusinessException("tsn.delete.failed");
		}
		try {
			terminalService.delete(tsns, command);
		} catch (BusinessException e) {
		    throw e;
		} catch (Exception e) {
		    throw new BusinessException("msg.operation.failure");
		}
		return ResultUtil.success();
	}
	
	/*
	 * 开放查看某个终端详情
	 */
	@ApiOperation(value = "View A Terminal Details")
	@ApiImplicitParam(name = "sn", value = "Device Serial Number", required = true, dataType = "String", paramType = "query")
	@GetMapping("/view-terminal-details")
	@ApiPermission(value = "tms:terminal:view")
	public Result<TerminalDetail> viewTerminalDetails(@RequestParam(value="sn")String sn){
		BaseForm command = new BaseForm();
		if (null == sn || "".equals(sn)) {
		    throw new BusinessException("tsn.Required");
		}
		boolean checkT = terminalService.checkTerminalInGroup(sn, command.getLoginUserId());
		if(!checkT){
		    throw new BusinessException("tsn.view.failed");
		}
		Terminal terminal = terminalService.get(sn);
		List<String> groupPath = terminalService.getGroupNameByTsn(sn);
		Result<TerminalDetail> result = new Result<>();
        result.success(terminalToTerminalDetail(terminal, groupPath));
        return result;
	}

	private QueryTerminalResponse createQueryTerminalResponse(Page<Map<String, Object>> page) {
		QueryTerminalResponse response = new QueryTerminalResponse();
		response.setTotalCount(page.getTotalCount());
		response.setPageIndex((int) page.getPageIndex());
		response.setPageSize(page.getPageSize());
		List<TerminalInfo> items = createTerminalInfos(page.getItems());
		response.setItems(items);
		return response;
	}

	private List<TerminalInfo> createTerminalInfos(List<Map<String, Object>> items) {
		if (CollectionUtils.isEmpty(items)) {
			return Collections.emptyList();
		}
		List<TerminalInfo> terminalInfos = new ArrayList<>();
		for (Map<String, Object> map : items) {
			TerminalInfo terminalInfo = new TerminalInfo();
			terminalInfo.setTerminalSN(map.get("tsn").toString());
			terminalInfo.setTerminalType(convertToString(map.get("model.name")));
			terminalInfo.setGroupNamePath(splitGroupNamePath(convertToString(map.get("groupNames"))));
			terminalInfo.setLastSourceIp(convertToString(map.get("ts.lastSourceIP")));
			terminalInfo.setStatus(convertToBoolean(map.get("status")));
			terminalInfo.setOnline(convertToBoolean(map.get("ts.isOnline")));
			terminalInfos.add(terminalInfo);

		}
		return terminalInfos;
	}

	private String splitGroupNamePath(String groupNamePath) {
		if (groupNamePath == null) {
			return "";
		}
		String[] groupNamePaths = groupNamePath.split("//");
		List<String> groupNamePathList = new ArrayList<>();
		for (String path : groupNamePaths) {
			if (StringUtils.equals("null", path)) {
				continue;
			}
			groupNamePathList.add(path);
		}
		return String.join(",", groupNamePathList);
	}

	private String convertToString(Object object) {
		if (object == null) {
			return null;
		}
		return object.toString();

	}

	private boolean convertToBoolean(Object object) {
		if (object == null) {
			return false;
		}
		if (StringUtils.equals("1", object.toString())) {
			return true;
		}
		return Boolean.parseBoolean(object.toString());

	}

	private QueryTerminalForm createQueryTerminalForm(QueryTerminalRequest request) {
		QueryTerminalForm command = new QueryTerminalForm();
		command.setGroupId(request.getGroupId());
		command.setFuzzyCondition(request.getKeyword());
		command.setPageIndex(request.getPageNum());
		command.setPageSize(request.getPageSize());

		return command;
	}

	private AddTerminalForm createAddTerminalForm(AddTerminalRequest request) {
		AddTerminalForm command = new AddTerminalForm();
		command.setGroupId(request.getGroupId());
		command.setTsnRanges(new String[] { StringUtils.isNotEmpty(request.getTerminalSN())
				? request.getTerminalSN().trim().toUpperCase() : request.getTerminalSN() });
		command.setDestModel(request.getTerminalType());
		command.setTimeZone(request.getTimeZone());
		command.setDaylightSaving(request.isDaylightSaving());
		command.setSyncToServerTime(request.isSyncToServerTime());
		command.setOverride(true);
		command.setCountryName(request.getCountryName());
		command.setProvinceName(request.getStateProvinceName());
		command.setCityName(request.getCityName());
		command.setZipCode(request.getZipCode());
		command.setAddress(request.getAddress());
		command.setDescription(request.getDescription());
		return command;
	}
	
	private TerminalDetail terminalToTerminalDetail(Terminal terminal,List<String> groupPath){
		TerminalDetail td = new TerminalDetail();
		List<Map<String, String>> timeZoneList = addressService.getTimeZonesByCountry(terminal.getCountry());
		String timeZone = "";
		String timeZoneNameID = "timeZoneName";
		td.setAddress(terminal.getAddress());
		td.setCity(terminal.getCity());
		td.setCountry(terminal.getCountry());
		td.setDaylightSaving(true == terminal.isDaylightSaving()?"Enable":"Disable");
		td.setDescription(terminal.getDescription());
		td.setGroupPath(groupPath);
		td.setInstallApps(terminal.getInstallApps());
		td.setProvince(terminal.getProvince());
		td.setStatus(true == terminal.isStatus()?"Active":"Deactive");
		td.setSyncToServerTime(true == terminal.isSyncToServerTime()?"Enable":"Disable");
		td.setTerminalType(null == terminal.getModel()?"":terminal.getModel().getName());
		for(Map<String, String> tz:timeZoneList){
			Set<String> keySet = tz.keySet();
			Iterator<String> it1 = keySet.iterator();
			while(it1.hasNext()){
				String ID = it1.next();
				String str = tz.get(ID);
				if(str.equals(terminal.getTimeZone())){
					timeZone = tz.get(timeZoneNameID);
				}
			}
		}
		td.setTimeZone(timeZone);
		td.setTsn(terminal.getTsn());
		td.setZipCode(terminal.getZipCode());
		return td;
	}
	
}
