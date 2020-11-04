/*
 * ============================================================================
 * = COPYRIGHT
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.
 * ============================================================================
 */
package com.pax.tms.pxretailer.action;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.util.DateTimeUtils;
import com.pax.tms.download.HmsLimiter;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.download.model.TerminalStatus;
import com.pax.tms.download.model.TerminalUsageReport;
import com.pax.tms.pxretailer.PxRetailerException;
import com.pax.tms.pxretailer.PxRetailerProtocol;
import com.pax.tms.pxretailer.message.HmsRequest;
import com.pax.tms.pxretailer.message.HmsResponse;
import com.pax.tms.pxretailer.message.TerminalUsage;

public class HmsAction extends AbstractTerminalAction <HmsRequest> {

    private static final String HMS_UPLOAD_STATUS_ROUTE = "/hms/uploadStatus";

    private int terminalAccessoryDetached = 2;
    private int terminalAccessoryAttached = 1;

    private HmsLimiter hmsLimiter;

    @Override
    public void registerAction(Router router, JsonObject config) {
        if (config.containsKey("tms.terminalAccessoryDetached")) {
            terminalAccessoryDetached = config.getInteger("tms.terminalAccessoryDetached");
        }

        if (config.containsKey("tms.terminalAccessoryAttached")) {
            terminalAccessoryAttached = config.getInteger("tms.terminalAccessoryAttached");
        }

        router.post(HMS_UPLOAD_STATUS_ROUTE).handler(createBodyHandler(config));
        router.post(HMS_UPLOAD_STATUS_ROUTE).handler(this::handle).failureHandler(this::handleException);
    }

    @Autowired
    public void setHmsLimiter(HmsLimiter hmsLimiter) {
        this.hmsLimiter = hmsLimiter;
    }

    /**
     * Handle UPDATEINFORMATION requests
     *
     * @param routingContext
     */
    @Override
    public void handle(RoutingContext routingContext) {
        HmsRequest hmsRequest = decodeJson(routingContext.getBodyAsString(), HmsRequest.class);
        if (hmsRequest.getTerminalState() == null) {
            throw new PxRetailerException(PxRetailerProtocol.INVALID_REQUEST_RESPONSE, "Terminal status is empty");
        }
        auth(routingContext, hmsRequest, TerminalActionType.HMS);
    }

    @Override
    public void process(RoutingContext routingContext, HmsRequest req, Terminal terminal) {
        Date accessTime = req.getRequestTime();
        terminal.setLastAccessTime(accessTime);
        // req.getTerminalState().getOnline() is discarded
        terminal.setOnline(TerminalStatus.ONLINE_STATUS);

        int privacyShieldStatus = req.getTerminalState().getPrivacyShieldAttached();
        if (terminalAccessoryAttached == privacyShieldStatus) {
            privacyShieldStatus = TerminalStatus.TERMINAL_ACCESSORY_ATTACHED;
        } else if (terminalAccessoryDetached == privacyShieldStatus) {
            privacyShieldStatus = TerminalStatus.TERMINAL_ACCESSORY_DETACHED;
        }
        terminal.setPrivacyShield(privacyShieldStatus);

        int stylusStatus = req.getTerminalState().getStylusAttached();
        if (terminalAccessoryAttached == stylusStatus) {
            stylusStatus = TerminalStatus.TERMINAL_ACCESSORY_ATTACHED;
        } else if (terminalAccessoryDetached == stylusStatus) {
            stylusStatus = TerminalStatus.TERMINAL_ACCESSORY_DETACHED;
        }
        terminal.setStylus(stylusStatus);
        terminal.setTampered(req.getTerminalState().getTampered());
        terminal.setSredEnabled(req.getTerminalState().getSREDEnabled());
        terminal.setRkiCapable(req.getTerminalState().getRKICapable());
        terminal.setLocaltime(req.getTerminalState().getLocaltime());

        if (req.getTerminalUsage() != null) {
            saveTerminalUsageReport(req, terminal);
        }
        if (ArrayUtils.isNotEmpty(req.getTerminalInstallations())) {
            terminal.setTerminalInstallations(Json.encode(req.getTerminalInstallations()));
        }
        
        if (req.getSysmetricKeys() != null) {
            terminal.setTerminalSysmetricKeys(Json.encode(req.getSysmetricKeys()));
        }

        terminalManager.updateTerminalInstallApps(terminal, res -> {
            if (res.failed()) {
                logger.error("deviceType={}, deviceSerialNumber={} - failed to update terminal install apps",
                        res.cause(), req.getDeviceType(), req.getDeviceSerialNumber());
                routingContext.fail(res.cause());
            } else {
                terminalManager.updateTerminalSysmetricKeys(terminal, reqs -> {
                    if (reqs.failed()) {
                        logger.error("deviceType={}, deviceSerialNumber={} - failed to update terminal SysmetricKeys",
                                reqs.cause(), req.getDeviceType(), req.getDeviceSerialNumber());
                        routingContext.fail(reqs.cause());
                    } else {
                        saveHealthMonitorInfo(terminal, routingContext, req);
                    }
                });
            }
        });


    }

    private void saveHealthMonitorInfo(Terminal terminal, RoutingContext routingContext, HmsRequest req) {
        terminalManager.saveHealthMonitorInfo(terminal, res -> {
            if (res.failed()) {
                logger.error("deviceType={}, deviceSerialNumber={} - failed to save terminal status info", res.cause(), req.getDeviceType(), req.getDeviceSerialNumber());
                routingContext.fail(res.cause());
            } else {
                sendResponse(routingContext, new HmsResponse());
                if (Integer.valueOf(1).equals(req.getTerminalState().getTampered())) {
                    logger.debug("deviceType={}, deviceSerialNumber={} - terminal tampered", req.getDeviceType(), req.getDeviceSerialNumber());
                    terminalManager.clearTerminalCache(terminal);
                }
            }
            hmsLimiter.completeHms(terminal.getTerminalId());
        });
    }

    private void saveTerminalUsageReport(HmsRequest hmsRequest, Terminal terminal) {
        TerminalUsageReport usageReport = new TerminalUsageReport();
        TerminalUsage[] usageData = hmsRequest.getTerminalUsage();
        if(usageData==null||usageData.length==0)
        {
        	return;
        }
        for(TerminalUsage usage:usageData)
        {
        	switch (usage.getCategory()) {
			case "MSRRead":
				usageReport.setMsrTots(usage.getTotal());
				usageReport.setMsrErrs(usage.getFail());
				break;
			case "ContactICRead":
				usageReport.setIcrTots(usage.getTotal());
				usageReport.setIcrErrs(usage.getFail());
				break;
			case "PINEncryption":
				usageReport.setPinTots(usage.getTotal());
				usageReport.setPinFails(usage.getFail());
				break;
			case "Signature":
				usageReport.setSignTots(usage.getTotal());
				usageReport.setSignErr(usage.getFail());
				break;
			case "DownloadHistory":
				usageReport.setDownTots(usage.getTotal());
				usageReport.setDownFails(usage.getFail());
				break;
			case "ActivationHistory":
				usageReport.setActvTots(usage.getTotal());
				usageReport.setActvErrs(usage.getFail());
				break;
			case "ContactlessICRead":
				usageReport.setClIcrTots(usage.getTotal());
				usageReport.setClIcrErrs(usage.getFail());
				break;
			case "Transaction":
				usageReport.setTxnTots(usage.getTotal());
				usageReport.setTxnErrs(usage.getFail());
				break;
			case "PowerCycles":
				usageReport.setPowers(usage.getTotal());
				break;
			default:
				break;
			}
        }
        usageReport.setTerminalId(hmsRequest.getDeviceSerialNumber());
        usageReport.setReportTime(DateTimeUtils.format(hmsRequest.getRequestTime(),DateTimeUtils.PATTERN_STANDARD));
        usageReport.setReportId(null);
        terminal.setUsageReport(usageReport);
    }

    @Override
    public HmsResponse createResponse(int statusCode, String statusMessage) {
        return new HmsResponse(statusCode, statusMessage);
    }
}