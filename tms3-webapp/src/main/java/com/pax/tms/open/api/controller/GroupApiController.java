package com.pax.tms.open.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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
import com.pax.common.web.controller.BaseController;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.GroupInSearch;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.service.GroupTreeService;
import com.pax.tms.group.web.form.AddGroupForm;
import com.pax.tms.group.web.form.QueryGroupForm;
import com.pax.tms.location.service.AddressService;
import com.pax.tms.open.api.annotation.ApiPermission;
import com.pax.tms.open.api.info.GroupSearch;
import com.pax.tms.open.api.req.AddGroupRequest;
import com.pax.tms.open.api.rsp.AddGroupResponse;
import com.pax.tms.open.api.rsp.Result;
import com.pax.tms.open.api.rsp.ResultUtil;

@RestController
@Api(tags = { "Group" })
@RequestMapping("/group/api")
public class GroupApiController extends BaseController {

	@Autowired
	private GroupService groupService;

	@Autowired
	private AddressService addressService;
	
	@Autowired
	private GroupTreeService groupTreeService;

	@ApiOperation(value = "Add Group", notes = "Add Group")
	@PostMapping("/add-group")
	@ApiPermission(value = "tms:group:add")
	public Result<AddGroupResponse> addGroup(@RequestBody AddGroupRequest request) {

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

		AddGroupForm command = createAddGroupForm(request);
		Result<AddGroupResponse> result = new Result<>();
        result.success(groupService.save(command));
        return result;

	}
	
	/*
	 * @DeleteMapping 捕捉不到BusinessException
	 * 为了Resetful风格的完整性，不改成@PostMapping
	 * 与杭州之前的接口风格错误码一致，异常则改变responseCode为500
	 */
	@ApiOperation(value = "Delete Group", notes = "Delete Group")
	@ApiImplicitParam(name = "groupId", value = "ID of the group to delete", required = true, dataType = "Long", paramType = "path")
	@DeleteMapping("/delete-group/{groupId}")
	@ApiPermission(value = "tms:group:delete")
	public Result<T> deleteGroup(@PathVariable(value="groupId") Long groupId, HttpServletResponse response){
		BaseForm command = new BaseForm();
		if (groupId == null) {
		    throw new BusinessException("msg.group.groupRequired");
		}
		Group group = groupService.get(groupId);
		if (group == null) {
		    throw new BusinessException("msg.group.groupNotFound");
		}
		boolean isGroup = groupService.isUserHasGroup(groupId, command.getLoginUserId());
		if(!isGroup){
		    throw new BusinessException("msg.user.cannotDeleteGroup");
		}
		try {
			groupService.delete(groupId, command);
		} catch (BusinessException e) {
		    throw e;
		} catch (Exception e) {
			throw new BusinessException("msg.operation.failure");
		}
		return ResultUtil.success();
	}
	
	/*
	 * 开放模糊搜索分组path
	 */
	@ApiOperation(value = "Search Group", notes = "Search Group")
	@ApiImplicitParam(name = "keyword", value = "Keywords for fuzzy search", required = true, dataType = "String", paramType = "query")
	@GetMapping("/search-group")
	public Result<List<GroupSearch>> searchGroup(@RequestParam(value="keyword") String keyword){
		QueryGroupForm command = new QueryGroupForm();
		command.setKeyword(keyword);
		List<GroupInSearch> list = groupTreeService.searchGroup(command);
		Result<List<GroupSearch>> result = new Result<>();
        result.success(groupOrTerminalsToGroupSearchs(list));
        return result;
	}

	private AddGroupForm createAddGroupForm(AddGroupRequest request) {
		AddGroupForm command = new AddGroupForm();
		command.setParentId(request.getParentId());
		command.setName(request.getGroupName());
		command.setTimeZone(request.getTimeZone());
		command.setDaylightSaving(request.isDaylightSaving());
		command.setCountryName(request.getCountryName());
		command.setProvinceName(request.getStateProvinceName());
		command.setCityName(request.getCityName());
		command.setZipCode(request.getZipCode());
		command.setDescription(request.getDescription());
		command.setAddress(request.getAddress());
		return command;
	}
	
	
	private List<GroupSearch> groupOrTerminalsToGroupSearchs(List<GroupInSearch> gts){
		List<GroupSearch> result = new ArrayList<GroupSearch>();
		for(GroupInSearch gt:gts){
			GroupSearch temp = new GroupSearch();
			temp.setGroupId(gt.getGroupId());
			temp.setGroupPath(gt.getGroupPath());
			result.add(temp);
		}
		return result;
	}

}
