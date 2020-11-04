package com.pax.tms.open.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pax.common.exception.BusinessException;
import com.pax.common.web.controller.BaseController;
import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.deploy.service.GroupDeployService;
import com.pax.tms.deploy.service.TerminalDeployService;
import com.pax.tms.deploy.web.form.GroupDeployForm;
import com.pax.tms.deploy.web.form.TerminalDeployForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.open.api.annotation.ApiPermission;
import com.pax.tms.open.api.req.GroupDeployRequest;
import com.pax.tms.open.api.req.TerminalDeployRequest;
import com.pax.tms.open.api.rsp.GroupDeployResponse;
import com.pax.tms.open.api.rsp.Result;
import com.pax.tms.open.api.rsp.TerminalDeployResponse;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.service.PkgService;

@RestController
@Api(tags = {"Deployment"})
@RequestMapping("/deployment/api")
public class DeploymentApiController extends BaseController {

    @Autowired
    private TerminalDeployService terminalDeployService;

    @Autowired
    private GroupDeployService groupDeployService;

    @Autowired
    private PkgService pkgService;

    @Autowired
    private GroupService groupService;

    @ApiOperation(value = "Deploy Terminal", notes = "Deploy Terminal")
    @PostMapping("/terminal-deploy")
    @ApiPermission(value = "tms:terminal:deployments:add")
    public Result<TerminalDeployResponse> deployTerminal(@RequestBody TerminalDeployRequest request) {
        Pkg pkg = pkgService.getPkgByNameAndVersion(request.getPkgName(), request.getPkgVersion());
        if (pkg == null) {
            throw new BusinessException("msg.pkgNotFound");
        }

        this.verifyDateFormat(request.getTimeZone(), request.getDownloadTime(), request.getActivationTime(),
                request.getExpirationTime());
        
        TerminalDeployForm command = new TerminalDeployForm();
        command.setTsn(request.getTsn());
        command.setPkgId(pkg.getId());
        command.setTimeZone(request.getTimeZone());
        command.setDwnlStartTime(request.getDownloadTime());
        command.setActvStartTime(request.getActivationTime());
        command.setDwnlEndTime(request.getExpirationTime());

        terminalDeployService.deploy(command);

        TerminalDeployResponse response = new TerminalDeployResponse();
        response.setTsn(request.getTsn());
        response.setActivationStatus(DownOrActvStatus.PENDING.getName());
        response.setActivationTime(request.getActivationTime());
        response.setDownloadStatus(DownOrActvStatus.PENDING.getName());
        response.setDownloadTime(request.getDownloadTime());
        response.setExpirationTime(request.getExpirationTime());
        response.setTimeZone(request.getTimeZone());
        response.setPkgName(request.getPkgName());
        response.setPkgVersion(request.getPkgVersion());
        Result<TerminalDeployResponse> result = new Result<>();
        result.success(response);
        return result;
    }


    @ApiOperation(value = "Deploy Group", notes = "Deploy Group")
    @PostMapping("/group-deploy")
    @ApiPermission(value = "tms:group:deployments:add")
    public Result<GroupDeployResponse> deployGroup(@RequestBody GroupDeployRequest request) {
        Pkg pkg = pkgService.getPkgByNameAndVersion(request.getPkgName(), request.getPkgVersion());
        if (pkg == null) {
            throw new BusinessException("msg.pkgNotFound");
        }
        Group group = groupService.get(request.getGroupId());
        if (group == null || !(group.getName().equals(request.getGroupName()))) {
            throw new BusinessException("msg.group.groupNotFound");
        }

        this.verifyDateFormat(request.getTimeZone(), request.getDownloadTime(), request.getActivationTime(),
                request.getExpirationTime());

        GroupDeployForm command = new GroupDeployForm();
        command.setGroupId(request.getGroupId());
        command.setGroupName(request.getGroupName());
        command.setDestModel(request.getTerminalType());
        command.setPkgId(pkg.getId());
        command.setTimeZone(request.getTimeZone());
        command.setDwnlStartTime(request.getDownloadTime());
        command.setActvStartTime(request.getActivationTime());
        command.setDwnlEndTime(request.getExpirationTime());

        groupDeployService.deploy(command);

        GroupDeployResponse response = new GroupDeployResponse();
        BeanUtils.copyProperties(request, response);
        response.setGroupName(group.getName());
        
        Result<GroupDeployResponse> result = new Result<>();
        result.success(response);
        return result;
    }

    /*
     * Verify the String time
     */
    private void verifyDateFormat(String timeZone, String downloadTimeStr, String activationTimeStr, String
            expirationTimeStr) {

        if (!ZoneId.getAvailableZoneIds().contains(timeZone)) {
            throw new BusinessException("msg.invalid.timezone", "Invalid TimeZone");
        }
        try {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of(timeZone));
            ZonedDateTime downloadTime = ZonedDateTime.parse(downloadTimeStr, formatter);
            ZonedDateTime activationTime = ZonedDateTime.parse(activationTimeStr, formatter);

            if (downloadTime.compareTo(activationTime) > -1) {
                throw new BusinessException("msg.invalid.deploytime",
                        "Activation time should be later than download time!");
            }
            if (StringUtils.isNotEmpty(expirationTimeStr)) {
                ZonedDateTime.parse(expirationTimeStr, formatter);
            }
        } catch (DateTimeParseException e) {
            throw new BusinessException("msg.invalid.date.format", "Invalid Date format!");
        }
    }
}
