package com.pax.tms.report.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.common.util.DateTimeUtils;
import com.pax.common.util.ExcelWritter;
import com.pax.common.util.HttpServletUtils;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.model.EventTrm;
import com.pax.tms.monitor.service.EventGrpService;
import com.pax.tms.monitor.service.EventTrmService;
import com.pax.tms.monitor.web.form.QueryTerminalEventForm;
import com.pax.tms.report.dao.EventListDao;
import com.pax.tms.report.web.controller.EventListController;
import com.pax.tms.user.security.AclManager;
import com.pax.tms.user.security.UTCTime;
import com.pax.tms.user.security.UTCTime.TimeRange;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.web.form.OperatorLogForm;

@Service("EventListServiceImpl")
public class EventListServiceImpl extends BaseService<EventTrm, Long> implements EventListService {
	
	//主题列数
	private static final int CAPTION_COLUMNS = 4;
	//Excel单元格宽度大小
	private static final int CELL_WIDTH = 25;
	//search type
	private static final String SERACH_ALL = "All";
	private static final String SERACH_GROUP = "Group";
	private static final String SERACH_TERMINAL = "Terminal";
	//预警等级
	private static final String SEVERITY_INFORMATIONAL = "Informational";
	private static final String SEVERITY_CRITICAL = "Critical";
	private static final String SEVERITY_WARNING = "Warning";
	
	protected Logger logger = LoggerFactory.getLogger(EventListController.class);
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private AuditLogService auditLogService;
	
	@Autowired
	private EventGrpService eventService;
	
	@Autowired
	private EventTrmService eventTrmService;
	
	@Autowired
	private EventListDao eventListDao;
	
	@Autowired(required = false)
	@Qualifier("messageSource")
	protected ReloadableResourceBundleMessageSource messageSource;

	@Override
	public IBaseDao<EventTrm, Long> getBaseDao() {
		return eventListDao;
	}
	
	
	@Override
	public void export(QueryTerminalEventForm command, Long groupId, String searchType, @PathVariable int days, List<Map<String, Object>> list, HttpServletResponse response) {
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		String caption = this.getMessage("title.export.event") + "_"
				+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String filename = caption + ".xlsx";
		HttpServletUtils.setDownloadExcel(filename, response);
		try {
			ExcelWritter ew = new ExcelWritter(response.getOutputStream());
			ew.open();
			ew.writeCaption(caption, CAPTION_COLUMNS);
			ew.writeTitle(new String[] { this.getMessage("form.event.source"), this.getMessage("form.event.severity"),
					this.getMessage("form.event.message"), this.getMessage("form.event.eventTime") });
			ew.setWidths(new int[] { CELL_WIDTH, CELL_WIDTH, CELL_WIDTH*2, CELL_WIDTH });
			list.forEach(eventLog -> {
				try {
					String eventTime = (DateTimeUtils.date2String( (Date) eventLog.get("EVENTTIME"), "HH:mm MM-dd-yyyy")).replaceAll("-", "/");
					String eventseverity = eventLog.get("EVENTSEVERITY").toString();
					switch (eventseverity) {
					case "1":
						eventseverity = SEVERITY_INFORMATIONAL;
						break;
					case "2":
						eventseverity = SEVERITY_WARNING;
						break;
					case "3":
						eventseverity = SEVERITY_CRITICAL;
						break;
					default:
						eventseverity = "";
						break;
					}
					ew.writeContent(
							eventLog.get("EVENTSOURCE"), 
							eventseverity, 
							eventLog.get("EVENTMSG"),
							eventTime );
				} catch (IOException e) {
					logger.error("User Maintenance", e);
				}
			});
			ew.close();
		} catch (IOException e) {
			logger.error("IOException:",e);
		}
		// add audit log
		auditLogService.addAuditLog(Arrays.asList(""), command, OperatorLogForm.EVENT_LIST_REPORT, null);
		
	}
	
	@Override
	public List<Map<String, Object>> listAllEvents(QueryTerminalEventForm command, Long groupId, String searchType, int days) {
		List<Map<String, Object>> list = new ArrayList<>();
		Long eventGrpCounts = eventService.count();
		Long eventTrmCounts = eventTrmService.count();
		Group group = groupService.get(groupId);
		AclManager.checkPermissionOfGroup(command.getLoginUserId(), group);
		TimeRange dtr = UTCTime.getLastNDayForUTC(days);
		command.setFromDate(dtr.getFrom());
		command.setToDate(dtr.getTo());
		command.setGroupId(groupId);
		command.setSearchType(searchType);
		if (SERACH_ALL.equals(command.getSearchType())) {
			command.setSearchType(SERACH_GROUP);
			List<Map<String, Object>> listG = eventService.callGrp(command, 0, eventGrpCounts.intValue());
			command.setSearchType(SERACH_TERMINAL);
			List<Map<String, Object>> listT = eventService.callTrm(command, 0, eventTrmCounts.intValue());
			list.addAll(listG);
			list.addAll(listT);
			this.sortListDesc(list);
			return list;
		} else if (SERACH_GROUP.equals(command.getSearchType())) {
			list = eventService.callGrp(command, 0, eventGrpCounts.intValue());
			this.sortListDesc(list);
			return list;
		} else {
			list = eventService.callTrm(command, 0, eventTrmCounts.intValue());
			this.sortListDesc(list);
			if((null != command.getTerminalId()) && (!"".equals(command.getTerminalId()))) {
				List<Map<String, Object>> listT = new ArrayList<>();
				for(Map<String, Object> m:list){
					String eventSource = m.get("EVENTSOURCE").toString();
					String tSn = command.getTerminalId();
					if(eventSource.equals(tSn)){
						listT.add(m);
					}
				}
				return listT;
			}
			return list;
		}
	}

	/**
	 * Sort by eventTime, eventId desc
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> sortListDesc(List<Map<String, Object>> list) {
		list.sort((map1, map2) -> {
			BigDecimal eventId1 = (BigDecimal) map1.get("EVENTID");
			BigDecimal eventId2 = (BigDecimal) map2.get("EVENTID");
			Date eventTime1 = (Date) map1.get("EVENTTIME");
			Date eventTime2 = (Date) map2.get("EVENTTIME");

			int timeRs = eventTime2.compareTo(eventTime1);

			if (timeRs == 0) {
				return eventId2.compareTo(eventId1);
			} else {
				return timeRs;
			}
		});
		return list;
	}
	
	private String getMessage(String code) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		Locale locale = localeResolver.resolveLocale(request);
		return messageSource.getMessage(code,  new Object[] {}, locale);
	}

}
