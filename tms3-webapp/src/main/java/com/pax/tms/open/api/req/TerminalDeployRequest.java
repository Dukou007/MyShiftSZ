package com.pax.tms.open.api.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel
public class TerminalDeployRequest implements Serializable {

    private static final long serialVersionUID = -6385614083959997510L;

    @ApiModelProperty(value = "Terminal SN", required = true)
    private String tsn;

    @ApiModelProperty(value = "Package Name", required = true)
    private String pkgName;

    @ApiModelProperty(value = "Package Version", required = true)
    private String pkgVersion;

    @ApiModelProperty(value = "Time Zone", required = true)
    private String timeZone;

    @ApiModelProperty(value = "Download Time", required = true, example = "yyyy-MM-dd HH:mm:ss")
    private String downloadTime;

    @ApiModelProperty(value = "Activation Time", required = true, example = "yyyy-MM-dd HH:mm:ss")
    private String activationTime;

    @ApiModelProperty(value = "Expiration Time", example = "yyyy-MM-dd HH:mm:ss")
    private String expirationTime;

    public String getTsn() {
        return tsn;
    }

    public void setTsn(String tsn) {
        this.tsn = tsn;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getPkgVersion() {
        return pkgVersion;
    }

    public void setPkgVersion(String pkgVersion) {
        this.pkgVersion = pkgVersion;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(String downloadTime) {
        this.downloadTime = downloadTime;
    }

    public String getActivationTime() {
        return activationTime;
    }

    public void setActivationTime(String activationTime) {
        this.activationTime = activationTime;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }
}
