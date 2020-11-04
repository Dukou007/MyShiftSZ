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
package com.pax.tms.download.service.impl;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pax.common.util.DateTimeUtils;
import com.pax.common.util.DateTimeUtils.DateTimeRange;
import com.pax.common.util.HttpClientUtil;
import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.download.dao.SystemConfDao;
import com.pax.tms.download.dao.TerminalDownloadDao;
import com.pax.tms.download.model.Deployment;
import com.pax.tms.download.model.Event;
import com.pax.tms.download.model.GroupMsg;
import com.pax.tms.download.model.PkgFile;
import com.pax.tms.download.model.SystemConf;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.download.model.TerminalLog;
import com.pax.tms.download.model.TerminalStatus;
import com.pax.tms.download.model.TerminalUsageMessage;
import com.pax.tms.download.model.TerminalUsageReport;
import com.pax.tms.download.service.TerminalDownloadService;
import com.pax.tms.pxretailer.constant.AlertConstants;

@Service("terminalDownloadServiceImpl")
@Transactional
public class TerminaDownloadServiceImpl implements TerminalDownloadService {
	private static final String TEMPERED = "9999";
	private static final String NOT_TEMPERED = "0000";
	private static final String[] DOWN_ACTI_STATUS={"FAILED","SUCCESS","CANCELED","PENDING","DOWNLOADING"};
	private static final Logger LOGGER = LoggerFactory.getLogger(TerminaDownloadServiceImpl.class);
	
	private TerminalDownloadDao terminalDao;
	
	private SystemConfDao systemConfDao;

	private int downloadRetryAttempts = 1;

	private boolean enableServerSideDownloadStatistics = false;

	private boolean deleteDeploymentAfterCompleted = true;

	@Autowired
	public void setDeleteDeploymentAfterCompleted(
			@Value("${pxretailer.deleteDeploymentAfterCompleted:true}") boolean deleteDeploymentAfterCompleted) {
		this.deleteDeploymentAfterCompleted = deleteDeploymentAfterCompleted;
	}

	@Autowired
	public void setDownloadRetryAttempts(@Value("${pxretailer.downloadRetryAttempts:1}") int downloadRetryAttempts) {
		this.downloadRetryAttempts = downloadRetryAttempts;
	}

	@Autowired
	public void setEnableServerSideDownloadStatistics(
			@Value("${pxretailer.enableServerSideDownloadStatistics:false}") boolean enableServerSideDownloadStatistics) {
		this.enableServerSideDownloadStatistics = enableServerSideDownloadStatistics;
	}

	@Autowired
	public void setTerminalDao(TerminalDownloadDao terminalDao) {
		this.terminalDao = terminalDao;
	}
    public SystemConfDao getSystemConfDao() {
        return systemConfDao;
    }
    @Autowired
    public void setSystemConfDao(SystemConfDao systemConfDao) {
        this.systemConfDao = systemConfDao;
    }
	// query
	@Override
	public Terminal getTerminalBySn(String deviceType, String deviceSerialNumber) {
		Terminal terminal = terminalDao.getTerminalBySn(deviceType, deviceSerialNumber);
		if (terminal != null) {
			loadAndSetTerminalStatus(terminal);
			loadAndSetScheduledPackage(terminal);
		} else {
			if (terminalDao.isUnregisteredTerminal(deviceType, deviceSerialNumber)) {
				return createUnregisteredTerminal(deviceType, deviceSerialNumber);
			}
		}
		return terminal;
	}

	private Terminal createUnregisteredTerminal(String deviceType, String deviceSerialNumber) {
		Terminal terminal = new Terminal();
		terminal.setTerminalStatus(Terminal.TERMINAL_STATUS_UNREGISTERED);
		terminal.setTerminalSn(deviceSerialNumber);
		terminal.setModelId(deviceType);
		return terminal;
	}

	private void loadAndSetScheduledPackage(Terminal terminal) {
		Deployment deployment = getScheduledPackage(terminal);
		if (deployment == null) {
			terminal.setScheduled("0");
		} else {
			terminal.setScheduled("1");
			terminal.setDwnlStartTm(deployment.getDwnlStartTm()); // UTC
			terminal.setDwnlEndTm(deployment.getDwnlEndTm()); // UTC
			terminal.setDwnlStatus(deployment.getDwnlStatus());
			terminal.setDeployId(deployment.getDeployId());
			terminal.setPkgId(deployment.getPkgId());
		}
	}

	private void loadAndSetTerminalStatus(Terminal terminal) {
		TerminalStatus terminalStatus = getTerminalStatus(terminal);
		terminal.setLastSourceIp(terminalStatus.getLastSourceIp());
		terminal.setOnline(terminalStatus.getOnline());
	}

	private TerminalStatus getTerminalStatus(Terminal terminal) {
		TerminalStatus terminalStatus = terminalDao.getTerminalStatus(terminal.getTerminalId());
		if (terminalStatus != null) {
			return terminalStatus;
		}
		return addTerminalStatus(terminal);
	}

	private TerminalStatus addTerminalStatus(Terminal terminal) {
		LOGGER.debug("deviceType={}, deviceSerialNumber={} - add terminal status into database", terminal.getModelId(),
				terminal.getTerminalSn());
		TerminalStatus terminalStatus = new TerminalStatus(terminal);
		terminalStatus.setTamper(NOT_TEMPERED);
		try {
			terminalDao.addTerminalStatus(terminalStatus);
			terminalDao.flush();
		} catch (PersistenceException e) {
			LOGGER.debug("deviceType={}, deviceSerialNumber={} - persistence exception, try again",
					terminal.getModelId(), terminal.getTerminalSn());
			terminalStatus = terminalDao.getTerminalStatus(terminal.getTerminalId());
			if (terminalStatus == null) {
				throw e;
			}
		}
		return terminalStatus;
	}

	@Transactional(readOnly = true)
	@Override
	public Deployment getScheduledPackage(Terminal terminal) {
		return terminalDao.getScheduledPackage(terminal);
	}

	@Transactional(readOnly = true)
	@Override
	public PkgFile getPackageFile(long pkgId) {
		return terminalDao.getPackageFile(pkgId);
	}

	@Transactional(readOnly = true)
	@Override
	public PkgFile getProgramFile(long pkgId, long fileId) {
		return terminalDao.getProgramFile(pkgId, fileId);
	}

	@Transactional(readOnly = true)
	@Override
	public PkgFile getProgramFile(long pkgId, String fileName) {
		return terminalDao.getProgramFile(pkgId, fileName);
	}

	// update
	@Override
	public void addUnregisteredTerminal(String deviceType, String deviceSerialNumber, String sourceIp,
			Date accessTime) {
		LOGGER.debug("deviceType={}, deviceSerialNumber={} - add unregistered terminal into database", deviceType,
				deviceSerialNumber);
		try {
			terminalDao.addUnregisteredTerminal(deviceType, deviceSerialNumber, sourceIp, accessTime);
		} catch (ConstraintViolationException e) {
			// Exception thrown when an attempt to insert or update data,
			// results in violation of an primary key or unique constraint.
			LOGGER.debug("deviceType={}, deviceSerialNumber={} - duplicate unregistered terminal", deviceType,
					deviceSerialNumber);
		} catch (PersistenceException e) {
			if (e.getCause() != null && (e.getCause() instanceof ConstraintViolationException)) {
				LOGGER.debug("deviceType={}, deviceSerialNumber={} - duplicate unregistered terminal",deviceType,
						deviceSerialNumber);
			} else {
				throw e;
			}
		}
	}

	@Override
	public void updateUnregisteredTerminal(String deviceType, String deviceSerialNumber, String sourceIp,
			Date accessTime) {
		int result = terminalDao.updateUnregisteredTerminal(deviceType, deviceSerialNumber, sourceIp, accessTime);
		if (result == 0) {
			Terminal terminal = terminalDao.getTerminalBySn(deviceType, deviceSerialNumber);
			if (terminal == null) {
				LOGGER.debug(
						"deviceType={}, deviceSerialNumber={} - the unregistered terminal had been deleted, add it again",
						deviceType, deviceSerialNumber);
				addUnregisteredTerminal(deviceType, deviceSerialNumber, sourceIp, accessTime);
			}
		}
	}

	@Override
	public void setTerminalOnline(Terminal terminal, String sourceIp, Date accessTime) {
		terminalDao.setTerminalOnline(terminal, sourceIp, accessTime);

		if (terminal.isOffline()) {
			terminalDao.saveEvent(new Event(accessTime, terminal.getTerminalId(), Event.INFO, "Terminal online"));
		}

		if (terminal.getLastSourceIp() != null && !terminal.getLastSourceIp().isEmpty()
				&& !terminal.getLastSourceIp().equals(sourceIp)) {
			terminalDao.saveEvent(
					new Event(accessTime, terminal.getTerminalId(), Event.INFO, "Source IP changed - " + sourceIp));
		}
	}

	@Override
	public void saveHealthMonitorInfo(Terminal terminal) {
		Date accessTime = terminal.getLastAccessTime();
		HashMap<String, Object> changedFields = new HashMap<>();
		List<Event> eventList = new ArrayList<>();

		TerminalStatus terminalStatus = getTerminalStatus(terminal);
		if (terminalStatus.isOffline()) {
			changedFields.put(TerminalStatus.ONLINE_FIELD, terminal.getOnline());
			terminalDao.saveEvent(new Event(accessTime, terminal.getTerminalId(), Event.INFO, "Terminal online"));
		}
		//如果收到HMS消息，则记录到Eventlist中
		terminalDao.saveEvent(new Event(accessTime, terminal.getTerminalId(), Event.INFO, "Terminal heath data received"));
		
		updatePrivacyShieldStatus(terminalStatus, terminal, accessTime, changedFields, eventList);

		updateStylusStatus(terminalStatus, terminal, accessTime, changedFields, eventList);

		updateTamperStatus(terminalStatus, terminal, accessTime, changedFields, eventList);

		updateSredStatus(terminalStatus, terminal, accessTime, changedFields, eventList);
		
		updateRKICapable(terminalStatus, terminal, accessTime, changedFields, eventList);
		
		updateLocalTime(terminal);
		
		if (!changedFields.isEmpty()) {
			terminalDao.updateTerminalStatus(terminal, changedFields);
		}

		if (!eventList.isEmpty()) {
			terminalDao.saveEventList(eventList);
		}

		if (terminal.getUsageReport() != null) {
		    // 更新download history和activation history
	        updateDownloadAndActivationHistory(terminal);
			//更新终端用量
			saveTerminalUsage(terminal);
		}
	}
	
    private void saveTerminalUsage(Terminal terminal) {
        String terminalSn = terminal.getTerminalSn();
        List<GroupMsg> groupList = terminalDao.getGroupMsgByTerminalId(terminalSn);
        if(null == groupList || groupList.isEmpty()){
            LOGGER.error("## terminal group is empty");
        }
        for(GroupMsg groupMsg : groupList){
            Long groupId = groupMsg.getGroupId();
            String timeZone = groupMsg.getTimeZone();
            Date terminalDate = DateTimeUtils.getTimeZoneDate(new Date(), timeZone);
            LOGGER.info("###groupId={},timeZone={}, ",groupId,timeZone);
            LOGGER.info("###nowDate={},terminalDate={}, ",new Date(),terminalDate);
            DateTimeRange yesterday = DateTimeUtils.getYesterday(terminalDate);
            TerminalUsageReport newTerminalUsageReport = terminal.getUsageReport();
            // 查询截止今天的总用量
            TerminalUsageReport todayTerminalUsageReport = terminalDao.geTerminalUsageReport(terminalSn,groupId,
                    DateTimeUtils.format(DateTimeUtils.getDayStart(terminalDate),DateTimeUtils.PATTERN_STANDARD), DateTimeUtils.format(DateTimeUtils.getDayEnd(terminalDate),DateTimeUtils.PATTERN_STANDARD));
            // 查询截止昨天的总用量
            TerminalUsageReport yesterdayTerminalUsageReport = terminalDao.geTerminalUsageReport(terminalSn,groupId,
                    DateTimeUtils.format(yesterday.getFrom(),DateTimeUtils.PATTERN_STANDARD), DateTimeUtils.format(yesterday.getTo(),DateTimeUtils.PATTERN_STANDARD));
            // 如果截止昨天的总用量没有数据，则将今天第一次的数据保存为昨天的数据
            if (null == yesterdayTerminalUsageReport) {
                LOGGER.info("##add yesterdayTerminalUsageReport");
                yesterdayTerminalUsageReport = (TerminalUsageReport) newTerminalUsageReport.clone();
                yesterdayTerminalUsageReport.setReportTime(DateTimeUtils.format(yesterday.getDate(),DateTimeUtils.PATTERN_STANDARD));
                yesterdayTerminalUsageReport.setGroupId(groupMsg.getGroupId());
                terminalDao.addTerminalUsageReport(yesterdayTerminalUsageReport);
            }
            if (null != todayTerminalUsageReport) {
                // 如果截止今天的总用量有数据，则更新截止今天的总用量
                LOGGER.info("##update todayTerminalUsageReport");
                TerminalUsageReport todayNewTerminalUsageReport = (TerminalUsageReport) newTerminalUsageReport.clone();
                todayNewTerminalUsageReport.setReportId(todayTerminalUsageReport.getReportId());
                todayNewTerminalUsageReport.setReportTime(DateTimeUtils.format(terminalDate,DateTimeUtils.PATTERN_STANDARD));
                terminalDao.updateTerminalUsageReport(todayNewTerminalUsageReport);
            } else {
                // 如果截止今天的总用量没有数据，则新增截止今天的总用量
                LOGGER.info("##add todayTerminalUsageReport");
                todayTerminalUsageReport = (TerminalUsageReport) newTerminalUsageReport.clone();
                todayTerminalUsageReport.setReportTime(DateTimeUtils.format(terminalDate,DateTimeUtils.PATTERN_STANDARD));
                todayTerminalUsageReport.setGroupId(groupMsg.getGroupId());
                terminalDao.addTerminalUsageReport(todayTerminalUsageReport);
            }
            // 保存、更新今天的用量
            addTerminalUsageMessages(yesterdayTerminalUsageReport, newTerminalUsageReport, 0, terminalSn, terminalDate);
        }
    }
	   
    private void addTerminalUsageMessages(TerminalUsageReport yesterdayTerminalUsageReport,
            TerminalUsageReport todayTerminalUsageReport, int beforDay, String terminalSn, Date terminalDate) {
        Long groupId = yesterdayTerminalUsageReport.getGroupId();
        // 查询截止前一天的总用量,beforDay=0位当天
        DateTimeRange beforeDay = DateTimeUtils.lastNDays(terminalDate, beforDay);
        List<TerminalUsageMessage> terminalUsageMessages = terminalDao.getTerminalUsageMessage(terminalSn,groupId,DateTimeUtils.format(beforeDay.getDate(),DateTimeUtils.PATTERN_DATE));
        // 当天用量不为空，则更新，否则新增当天用量
        if (null != terminalUsageMessages && !terminalUsageMessages.isEmpty()) {
            for (TerminalUsageMessage terminalUsageMessage : terminalUsageMessages) {
                terminalUsageMessage.setCreateDate(beforeDay.getDate());
                setTerminalUsageMsg(todayTerminalUsageReport, yesterdayTerminalUsageReport, terminalUsageMessage, terminalUsageMessage.getItemName());
                terminalDao.updateTerminalUsageMessage(terminalUsageMessage);
            }
        } else {
            for (String itemName : AlertConstants.getUsageItems()) {
                TerminalUsageMessage newTerminalUsageMessage = new TerminalUsageMessage();
                newTerminalUsageMessage.setItemName(itemName);
                newTerminalUsageMessage.setTerminalId(terminalSn);
                newTerminalUsageMessage.setCreateDate(beforeDay.getDate());
                newTerminalUsageMessage.setStartTime(DateTimeUtils.format(beforeDay.getFrom(),DateTimeUtils.PATTERN_STANDARD));
                newTerminalUsageMessage.setEndTime(DateTimeUtils.format(beforeDay.getTo(),DateTimeUtils.PATTERN_STANDARD));
                newTerminalUsageMessage.setMsgCycle(DateTimeUtils.format(beforeDay.getDate(),DateTimeUtils.PATTERN_DATE));
                setTerminalUsageMsg(todayTerminalUsageReport, yesterdayTerminalUsageReport, newTerminalUsageMessage,
                        itemName);
                newTerminalUsageMessage.setGroupId(groupId);
                terminalDao.addTerminalUsageMessage(newTerminalUsageMessage);
            }
        }
    }
    
    private void setTerminalUsageMsg(TerminalUsageReport terminalUsageReport,
            TerminalUsageReport yesterdayTerminalUsageReport, TerminalUsageMessage terminalUsageMessage, String itemName) {
        int msrErrs = 0;
        int msrTots = 0;
        int icrErrs = 0;
        int icrTots = 0;
        int pinFails = 0;
        int pinTots = 0;
        int signErr = 0;
        int signTots = 0;
        int clIcrErrs = 0;
        int clIcrTots = 0;
        int txnErrs = 0;
        int txnTots = 0;
        int powers = 0;
        int downFails =0;
        int downPending = 0;
        int downTots = 0;
        int actvFails = 0 ;
        int actvPending = 0;
        int actvTots = 0;
        // 今天和昨天的数据都不为空，则用量为今天总量-昨天总量，否则用量为0
        if (null != terminalUsageReport && null != yesterdayTerminalUsageReport) {
            msrErrs = terminalUsageReport.getMsrErrs() - yesterdayTerminalUsageReport.getMsrErrs();
            msrTots = terminalUsageReport.getMsrTots() - yesterdayTerminalUsageReport.getMsrTots();
            icrErrs = terminalUsageReport.getIcrErrs() - yesterdayTerminalUsageReport.getIcrErrs();
            icrTots = terminalUsageReport.getIcrTots() - yesterdayTerminalUsageReport.getIcrTots();
            pinFails = terminalUsageReport.getPinFails() - yesterdayTerminalUsageReport.getPinFails();
            pinTots = terminalUsageReport.getPinTots() - yesterdayTerminalUsageReport.getPinTots();
            signErr = terminalUsageReport.getSignErr() - yesterdayTerminalUsageReport.getSignErr();
            signTots = terminalUsageReport.getSignTots() - yesterdayTerminalUsageReport.getSignTots();
            clIcrErrs = terminalUsageReport.getClIcrErrs() - yesterdayTerminalUsageReport.getClIcrErrs();
            clIcrTots = terminalUsageReport.getClIcrTots() - yesterdayTerminalUsageReport.getClIcrTots();
            txnErrs = terminalUsageReport.getTxnErrs() - yesterdayTerminalUsageReport.getTxnErrs();
            txnTots = terminalUsageReport.getTxnTots() - yesterdayTerminalUsageReport.getTxnTots();
            powers = terminalUsageReport.getPowers() - yesterdayTerminalUsageReport.getPowers();
            downFails = terminalUsageReport.getDownFails() - yesterdayTerminalUsageReport.getDownFails();
            downPending = terminalUsageReport.getDownPending() - yesterdayTerminalUsageReport.getDownPending();
            downTots = terminalUsageReport.getDownTots() - yesterdayTerminalUsageReport.getDownTots();
            actvFails = terminalUsageReport.getActvErrs() - yesterdayTerminalUsageReport.getActvErrs();
            actvPending = terminalUsageReport.getActvPending() - yesterdayTerminalUsageReport.getActvPending();
            actvTots = terminalUsageReport.getActvTots() - yesterdayTerminalUsageReport.getActvTots();
        }
        switch (itemName) {
            case AlertConstants.DOWNLOAD_HISTORY:
                terminalUsageMessage.setItemTotals(downTots);
                terminalUsageMessage.setItemErrors(downFails);
                terminalUsageMessage.setItemPending(downPending);
                break;
            case AlertConstants.ACTIVATION_HISTORY:
                terminalUsageMessage.setItemTotals(actvTots);
                terminalUsageMessage.setItemErrors(actvFails);
                terminalUsageMessage.setItemPending(actvPending);
                break;
            case AlertConstants.MSR_READ:
                terminalUsageMessage.setItemTotals(msrTots);
                terminalUsageMessage.setItemErrors(msrErrs);
                break;
            case AlertConstants.CONTACT_IC_READ:
                terminalUsageMessage.setItemTotals(icrTots);
                terminalUsageMessage.setItemErrors(icrErrs);
                break;
            case AlertConstants.PIN_ENCRYPTION_FAILURE:
                terminalUsageMessage.setItemTotals(pinTots);
                terminalUsageMessage.setItemErrors(pinFails);
                break;
            case AlertConstants.SIGNATURE:
                terminalUsageMessage.setItemTotals(signTots);
                terminalUsageMessage.setItemErrors(signErr);
                break;
            case AlertConstants.CONTACTLESS_IC_READ:
                terminalUsageMessage.setItemTotals(clIcrTots);
                terminalUsageMessage.setItemErrors(clIcrErrs);
                break;
            case AlertConstants.TRANSACTIONS:
                terminalUsageMessage.setItemTotals(txnTots);
                terminalUsageMessage.setItemErrors(txnErrs);
                break;
            case AlertConstants.POWER_CYCLES:
                terminalUsageMessage.setItemTotals(powers);
                terminalUsageMessage.setItemErrors(0);
                break;
            default:
                break;
        }
    }
	
	private void updateDownloadAndActivationHistory(Terminal terminal){
		TerminalUsageReport usageReport = terminal.getUsageReport();
		String terminalSN = terminal.getTerminalSn();
		Map<String, Integer> downloads = getDownloadAndActivation(AlertConstants.DOWNLOAD_HISTORY, terminalSN);
		Map<String, Integer> activations = getDownloadAndActivation(AlertConstants.ACTIVATION_HISTORY, terminalSN);
		int downTots = 0;
		int actvTots = 0;
		int abNormals = 0;
		int normals = 0; 
		int unknowns = 0;
		for(String status:DOWN_ACTI_STATUS)
		{
			int cnt = downloads.get(status);
			switch (status) {
			case "SUCCESS":
				normals += cnt;
				break;
			case "PENDING":
				unknowns += cnt;
				break;
			case "DOWNLOADING":
				unknowns += cnt;
				break;
			case "FAILED":
				abNormals += cnt;
				break;
			case "CANCELED":
				normals += cnt;
				break;
			default:
				break;
			}
		}
		downTots = abNormals+normals+unknowns;
		usageReport.setDownTots(downTots);
		usageReport.setDownFails(abNormals);
		usageReport.setDownPending(unknowns);
		abNormals = 0;
		normals = 0; 
		unknowns = 0;
		for(String status:DOWN_ACTI_STATUS)
		{
			int cnt = activations.get(status);
			switch (status) {
			case "SUCCESS":
				normals += cnt;
				break;
			case "PENDING":
				unknowns += cnt;
				break;
			case "DOWNLOADING":
				unknowns += cnt;
				break;
			case "FAILED":
				abNormals += cnt;
				break;
			case "CANCELED":
				normals += cnt;
				break;
			default:
				break;
			}
		}
		actvTots = abNormals+normals+unknowns;
		usageReport.setActvTots(actvTots);
		usageReport.setActvErrs(abNormals);
		usageReport.setActvPending(unknowns);
	}
	
	private Map<String, Integer> getDownloadAndActivation(String itemName, String terminalSN) {
		Map<String, Integer> result = new HashMap<>();
		for(String status:DOWN_ACTI_STATUS)
		{
			Integer total = terminalDao.getDownloadAndActivation(itemName, status, terminalSN); 
			result.put(status, total);
		}
		return result;
	}
	
	private void updateTamperStatus(TerminalStatus terminalStatus, Terminal terminal, Date accessTime,
			HashMap<String, Object> changedFields, List<Event> eventList) {
		if (Integer.valueOf(1).equals(terminal.getTampered())) {
			if (StringUtils.isEmpty(terminalStatus.getTamper()) || NOT_TEMPERED.equals(terminalStatus.getTamper())) {
				changedFields.put(TerminalStatus.TAMPER_FIELD, TEMPERED);
				eventList.add(new Event(accessTime, terminal.getTerminalId(), Event.CRITICAL, "Terminal tampered"));
			}
		} else {
			if (!NOT_TEMPERED.equals(terminalStatus.getTamper())) {
				changedFields.put(TerminalStatus.TAMPER_FIELD, NOT_TEMPERED);
			}
		}
	}

	private void updateStylusStatus(TerminalStatus terminalStatus, Terminal terminal, Date accessTime,
			HashMap<String, Object> changedFields, List<Event> eventList) {
		if ((terminalStatus.getStylus() == null && null != terminal.getStylus()) || (terminalStatus.getStylus() != null && !terminalStatus.getStylus().equals(terminal.getStylus()))) {

			changedFields.put(TerminalStatus.STYLUS_FIELD, terminal.getStylus());
			if (null == terminal.getStylus()) {
                return;
            }
			switch (terminal.getStylus()) {
			case TerminalStatus.TERMINAL_ACCESSORY_ATTACHED:
				eventList.add(new Event(accessTime, terminal.getTerminalId(), Event.INFO, "Stylus attached"));
				break;
			case TerminalStatus.TERMINAL_ACCESSORY_DETACHED:
				eventList.add(new Event(accessTime, terminal.getTerminalId(), Event.WARNNING, "Stylus removed"));
				break;
			default:
			}
		}
	}

	private void updatePrivacyShieldStatus(TerminalStatus terminalStatus, Terminal terminal, Date accessTime,
			HashMap<String, Object> changedFields, List<Event> eventList) {
		if ((terminalStatus.getPrivacyShield() == null && null != terminal.getPrivacyShield())
				|| (terminalStatus.getPrivacyShield() != null && !terminalStatus.getPrivacyShield().equals(terminal.getPrivacyShield()))) {

			changedFields.put(TerminalStatus.PRIVACY_SHIELD_FIELD, terminal.getPrivacyShield());
			if (null == terminal.getPrivacyShield()) {
			    return;
			}
			switch (terminal.getPrivacyShield()) {
			case TerminalStatus.TERMINAL_ACCESSORY_ATTACHED:
				eventList.add(new Event(accessTime, terminal.getTerminalId(), Event.INFO, "Privacy shield attached"));
				break;
			case TerminalStatus.TERMINAL_ACCESSORY_DETACHED:
				eventList
						.add(new Event(accessTime, terminal.getTerminalId(), Event.WARNNING, "Privacy shield removed"));
				break;
			default:
			}
		}
	}
	
	private void updateSredStatus(TerminalStatus terminalStatus, Terminal terminal, Date accessTime,
            HashMap<String, Object> changedFields, List<Event> eventList) {
        if ((terminalStatus.getSred() == null && null != terminal.getSredEnabled())
                || (terminalStatus.getSred() != null && !terminalStatus.getSred().equals(terminal.getSredEnabled()))) {
            changedFields.put(TerminalStatus.SRED_FIELD, terminal.getSredEnabled());
            if (null == terminal.getSredEnabled()) {
                return;
            }
            switch (terminal.getSredEnabled()) {
            case TerminalStatus.TERMINAL_SRED_ENCRYPTED:
                eventList.add(new Event(accessTime, terminal.getTerminalId(), Event.INFO, "SRED encrypted"));
                break;
            case TerminalStatus.TERMINAL_SRED_NOTENCRYPTED:
                eventList.add(new Event(accessTime, terminal.getTerminalId(), Event.WARNNING, "SRED not encrypted"));
                break;
            default:
            }
        }
    }
	
	private void updateRKICapable(TerminalStatus terminalStatus, Terminal terminal, Date accessTime,
            HashMap<String, Object> changedFields, List<Event> eventList) {
        if ((terminalStatus.getRki() == null && null != terminal.getRkiCapable())
                || (terminalStatus.getRki() != null && !terminalStatus.getRki().equals(terminal.getRkiCapable()))) {
            changedFields.put(TerminalStatus.RKI_FIELD, terminal.getRkiCapable());
            if (null == terminal.getRkiCapable()) {
                return;
            }
            switch (terminal.getRkiCapable()) {
            case TerminalStatus.TERMINAL_RKICAPABLE_YES:
                eventList.add(new Event(accessTime, terminal.getTerminalId(), Event.INFO, "RKI support"));
                break;
            case TerminalStatus.TERMINAL_RKICAPABLE_NO:
                eventList.add(new Event(accessTime, terminal.getTerminalId(), Event.WARNNING, "RKI not support"));
                break;
            default:
                
            }
        }
    }
	
	private void updateLocalTime(Terminal terminal) {
		if("".equals(terminal.getLocaltime()) || null == terminal.getLocaltime()) {
			terminalDao.updateTerminalLocalTime(terminal.getTerminalSn(), null);
		} else {
			terminalDao.updateTerminalLocalTime(terminal.getTerminalSn(), terminal.getLocaltime());
		}
	}

	@Override
	public void startDownload(Terminal terminal, Deployment deployment, Date accessTime) {
		Event event = createStartDownloadEvent(terminal, deployment, accessTime);
		terminalDao.saveEvent(event);
		terminalDao.updateTerminalStatusAtDwnlStart(terminal, deployment, accessTime);
		terminalDao.updateDownloadReportAtDwnlStart(terminal, deployment, accessTime);
		terminalDao.updateDeployStatusAtDwnlStart(terminal, deployment, accessTime);
	}

	private Event createStartDownloadEvent(Terminal terminal, Deployment deployment, Date date) {
		String msg = "";
		if (deployment.getPgmType() != null) {
			msg += deployment.getPgmType().toUpperCase() + " ";
		}
		if(StringUtils.equals(deployment.getPkgType(), "offlinekey")){
		    msg += "Key '" + deployment.getPkgName() + "' Download Started";
		}else{
		    msg += "Package '" + deployment.getPkgName() + "' Download Started";
		}
		return new Event(date, terminal.getTerminalId(), Event.INFO, msg);
	}

	@Override
	public void updateDeploymentStatus(Terminal terminal, Date accessTime, long deployId,
			DownOrActvStatus downloadStatus, DownOrActvStatus activationStatus, String activationCode) {

		Deployment deployment = terminalDao.getDeployment(deployId, terminal.getTerminalId());
		if (deployment == null) {
			deployment = terminalDao.getDeploymentHistory(deployId, terminal.getTerminalId());
			if (deployment != null) {
				deployment.setHistoryRecord(true);
			}
		}

		TerminalUsageReport usageReport = null;
		if (this.enableServerSideDownloadStatistics) {
			usageReport = new TerminalUsageReport();
			usageReport.setTerminalId(terminal.getTerminalSn());
			usageReport.setReportTime(DateTimeUtils.format(new Date(),DateTimeUtils.PATTERN_STANDARD));
		}

		boolean downloadFailedAndCanceled = updateDownloadStatus(terminal, accessTime, deployment, downloadStatus,
				usageReport);

		if (!downloadFailedAndCanceled) {
			updateActivationStatus(terminal, accessTime, deployment, activationStatus, activationCode, usageReport);
		}
		/**
		 * 2019-08-21 V1.20.00版本注释在此添加UsageReport，这里只设置download、Activation的值，不合理
		 */
//		if (usageReport != null && usageReport.hasValue()) {
//			this.terminalDao.saveUsageReport(usageReport);
//		}
	}

	private boolean updateDownloadStatus(Terminal terminal, Date accessTime, Deployment deployment,
			DownOrActvStatus downloadStatus, TerminalUsageReport usageReport) {
		if (deployment != null && downloadStatus != null) {
			switch (downloadStatus) {
			case SUCCESS:
				downloadSuccess(terminal, accessTime, deployment, downloadStatus, usageReport);
				return false;
			case FAILED:
				downloadFailed(terminal, accessTime, deployment, downloadStatus, usageReport);
				return true;
			case CANCELED:
				downloadCanceled(terminal, accessTime, deployment, downloadStatus);
				return true;
			default:
			}
		}
		return false;
	}

	private void downloadCanceled(Terminal terminal, Date accessTime, Deployment deployment,
			DownOrActvStatus downloadStatus) {
		if (!DownOrActvStatus.CANCELED.name().equals(deployment.getDwnlStatus())) {
			updateDownloadStatus(terminal, deployment, downloadStatus, accessTime, Event.INFO, " Download Canceled");
			updateActivationStatusToNoActivation(terminal, accessTime, deployment);
		}
	}

	private void downloadFailed(Terminal terminal, Date accessTime, Deployment deployment,
			DownOrActvStatus downloadStatus, TerminalUsageReport usageReport) {
		if (!deployment.isHistoryRecord() && (deployment.getDwnlFailCount() + 1) < this.downloadRetryAttempts) {
			terminalDao.addDownloadRetryCount(terminal, deployment);
		} else if (!DownOrActvStatus.FAILED.name().equals(deployment.getDwnlStatus())) {
			updateDownloadStatus(terminal, deployment, downloadStatus, accessTime, Event.CRITICAL, " Download Failed");
			updateActivationStatusToNoActivation(terminal, accessTime, deployment);
		}

		if (usageReport != null) {
			usageReport.setDownTots(1);
			usageReport.setDownFails(1);
		}
	}

	private void downloadSuccess(Terminal terminal, Date accessTime, Deployment deployment,
			DownOrActvStatus downloadStatus, TerminalUsageReport usageReport) {
		if (!DownOrActvStatus.SUCCESS.name().equals(deployment.getDwnlStatus())) {
			updateDownloadStatus(terminal, deployment, downloadStatus, accessTime, Event.INFO, " Download Completed");
			usageReport.setDownTots(1);
			usageReport.setDownFails(0);
		}
	}

	private void updateDownloadStatus(Terminal terminal, Deployment deployment, DownOrActvStatus downloadStatus,
			Date accessTime, int serverity, String msg) {
	    String packageInfo ;
	    if(StringUtils.equals(deployment.getPkgType(), "offlinekey")){
	           packageInfo = deployment.getPgmType().toUpperCase() + " Key";
	    }else{
	        packageInfo = deployment.getPgmType().toUpperCase() + " Package";
	    }
		if (!StringUtils.isBlank(deployment.getPkgName())) {
			packageInfo = packageInfo + " '" + deployment.getPkgName() + "'";
		}
		terminalDao.saveEvent(new Event(accessTime, terminal.getTerminalId(), serverity, packageInfo + msg));
		terminalDao.updateDownloadStatus(terminal, downloadStatus, accessTime);
		terminalDao.updateDownloadReportAtDwnlFinish(terminal, deployment, downloadStatus, accessTime);
		terminalDao.updateDeploymentStatusAtDwnlFinish(terminal, deployment, downloadStatus, accessTime);
	}

	private void updateActivationStatus(Terminal terminal, Date accessTime, Deployment deployment,
			DownOrActvStatus activationStatus,String activationCode, TerminalUsageReport usageReport) {
		if (activationStatus != null) {
			switch (activationStatus) {
			case SUCCESS:
				activationSuccess(terminal, accessTime, deployment, activationStatus, usageReport);
				break;
			case FAILED:
				activationFailed(terminal, accessTime, deployment, activationStatus, activationCode, usageReport);
				break;
			case CANCELED:
				activationCanceled(terminal, accessTime, deployment, activationStatus);
				break;
			default:
			}
		}
	}

	private void activationCanceled(Terminal terminal, Date accessTime, Deployment deployment,
			DownOrActvStatus activationStatus) {
		if (deployment == null || !DownOrActvStatus.CANCELED.name().equals(deployment.getActvStatus())) {
			updateActivationStatus(terminal, deployment, activationStatus, accessTime, Event.INFO,
					" Activation Canceled");
		}
	}

	private void activationFailed(Terminal terminal, Date accessTime, Deployment deployment,
			DownOrActvStatus activationStatus,String activationCode, TerminalUsageReport usageReport) {
		if(activationCode==null||"".equals(activationCode))
		{
			activationCode="";
		}
		else
		{
			activationCode=": "+activationCode;
		}
		if (deployment == null || !DownOrActvStatus.FAILED.name().equals(deployment.getActvStatus())) {
			updateActivationStatus(terminal, deployment, activationStatus, accessTime, Event.CRITICAL,
					" Activation Failed"+activationCode);
		}

		if (usageReport != null) {
			usageReport.setActvTots(1);
			usageReport.setActvErrs(1);
		}
	}

	private void activationSuccess(Terminal terminal, Date accessTime, Deployment deployment,
			DownOrActvStatus activationStatus, TerminalUsageReport usageReport) {
		if (deployment == null || !DownOrActvStatus.SUCCESS.name().equals(deployment.getActvStatus())) {
			updateActivationStatus(terminal, deployment, activationStatus, accessTime, Event.INFO,
					" Activation Completed");
		}

		if (usageReport != null) {
			usageReport.setActvTots(1);
			usageReport.setActvErrs(0);
		}
	}

	private void updateActivationStatus(Terminal terminal, Deployment deployment, DownOrActvStatus activationStatus,
			Date accessTime, int serverity, String msg) {
		String packageInfo = "Package";
		if (deployment != null) {
			if (!StringUtils.isBlank(deployment.getPgmType())) {
			    if(StringUtils.equals(deployment.getPkgType(), "offlinekey")){
			        packageInfo = deployment.getPgmType().toUpperCase() + " Key";
			    }else {
			        packageInfo = deployment.getPgmType().toUpperCase() + " Package";
                }
			}
			if (!StringUtils.isBlank(deployment.getPkgName())) {
				packageInfo = packageInfo + " '" + deployment.getPkgName() + "'";
			}
		}

		terminalDao.saveEvent(new Event(accessTime, terminal.getTerminalId(), serverity, packageInfo + msg));
		terminalDao.updateActivationStatus(terminal, activationStatus, accessTime);
		if (deployment != null) {
			terminalDao.updateDownloadReportAtActvFinish(terminal, deployment, activationStatus, accessTime);
			if (deleteDeploymentAfterCompleted) {
				terminalDao.deleteDeploymentAtActvFinish(terminal, deployment, activationStatus, accessTime);
			} else {
				terminalDao.updateDeploymentStatusAtActvFinish(terminal, deployment, activationStatus, accessTime);
			}
		}
	}

	private void updateActivationStatusToNoActivation(Terminal terminal, Date accessTime, Deployment deployment) {
		terminalDao.updateActivationStatus(terminal, DownOrActvStatus.NOACTIVITION, accessTime);
		if (deployment != null) {
			terminalDao.updateDownloadReportAtActvFinish(terminal, deployment, DownOrActvStatus.NOACTIVITION,
					accessTime);
			if (deleteDeploymentAfterCompleted) {
				terminalDao.deleteDeploymentAtActvFinish(terminal, deployment, DownOrActvStatus.NOACTIVITION,
						accessTime);
			} else {
				terminalDao.updateDeploymentStatusAtActvFinish(terminal, deployment, DownOrActvStatus.NOACTIVITION,
						accessTime);
			}
		}
	}

	// batch update
	@Override
	public void setTerminalOffline(Set<String> terminals, long maxLastAccessTime) {
		if (!terminals.isEmpty()) {
			terminalDao.updateTerminalOnlineStatus(terminals, maxLastAccessTime);
		}
	}

	@Override
	public void setTerminalOffline(long maxLastAccessTime) {
		terminalDao.setTerminalOffline(maxLastAccessTime);
	}

	@Override
	public void updateTerminalAccessTime(Map<String, String> map) {
		terminalDao.updateTerminalLastAccessTime(map);
	}

	@Override
	public void updateStorgeTerminalInstallAppsAndEvent(Terminal terminal) {
		if (StringUtils.isEmpty(terminal.getTerminalInstallations())) {
			return;
		}
		terminalDao.updateTerminalInstallApps(terminal.getTerminalSn(), terminal.getTerminalInstallations());
		terminalDao.saveEvent(new Event(terminal.getLastAccessTime(), terminal.getTerminalSn(), Event.INFO,
				"Terminal Installed applications update"));

	}

	@Override
	public void updateTerminalInstallAppReportTime(String terminalSn) {

		terminalDao.updateTerminalInstallAppReportTime(terminalSn);
	}
	@Override
	public String getTerminalInstalledApps(String terminalSn) {
	
		return terminalDao.getTerminalInstalledApps(terminalSn);
	}
	
	@Override
	public void saveTerminalLog(TerminalLog terminalLog){
	    terminalDao.addTerminalLog(terminalLog);
	}
	
	@Async
	@Override
	public void sendTerminalLogToSplunk(String splunkUrl, String content,String splunkToken){
	    LOGGER.debug("terminalLog send to splunk,reqJson="+content);
	    LOGGER.debug("terminalLog send to splunk,url="+splunkUrl);
	    LOGGER.debug("terminalLog send to splunk,token="+splunkToken);
	    String result = HttpClientUtil.doPostAuth(splunkUrl, content, splunkToken);
	    LOGGER.debug("terminalLog send to splunk,result="+result);
	}
	
	@Override
    public SystemConf findByParaKey(String paraKey) {
	   return systemConfDao.findByParaKey(paraKey);
	}
	
	@Override
    public void updateStorgeTerminalSysmetricKeysAndEvent(Terminal terminal) {
        if (StringUtils.isEmpty(terminal.getTerminalInstallations())) {
            return;
        }
        terminalDao.updateTerminalSysmetricKeys(terminal.getTerminalSn(), terminal.getTerminalSysmetricKeys());
        terminalDao.saveEvent(new Event(terminal.getLastAccessTime(), terminal.getTerminalSn(), Event.INFO,
                "Terminal SysmetricKeys update"));
    }
	
	@Override
    public void updateTerminalSysmetricKeysReportTime(String terminalSn) {

        terminalDao.updateTerminalSysmetricKeysReportTime(terminalSn);
    }
	
	@Override
    public String getTerminalSysmetricKeys(String terminalSn) {
    
        return terminalDao.getTerminalSysmetricKeys(terminalSn);
    }
}

