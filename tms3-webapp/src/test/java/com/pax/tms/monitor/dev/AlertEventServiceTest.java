package com.pax.tms.monitor.dev;

import java.util.Date;
import java.util.Random;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.tms.demo.ServiceJunitCase;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.dao.AlertEventDao;
import com.pax.tms.monitor.model.AlertCondition;
import com.pax.tms.monitor.model.AlertEvent;
import com.pax.tms.monitor.service.AlertConditionService;
import com.pax.tms.monitor.service.AlertEventService;
import com.pax.tms.monitor.service.EventGrpService;
import com.pax.tms.monitor.service.EventTrmService;
import com.pax.tms.monitor.service.TerminalUsageStatisticsService;
import com.pax.tms.monitor.web.form.QueryAlertEventForm;
import com.pax.tms.monitor.web.form.QueryStatisticsForm;
import com.pax.tms.user.security.UTCTime;
import com.pax.tms.user.security.UTCTime.TimeRange;

public class AlertEventServiceTest extends ServiceJunitCase {
	@Autowired
	private AlertEventService eventService;
	@Autowired
	private AlertConditionService condService;
	@Autowired
	private GroupService groupService;

	@Autowired
	private EventGrpService eventGrpService;

	@Autowired
	private EventTrmService eventTrmService;

	@Autowired
	private AlertEventDao eventDao;
	@Autowired
	private TerminalUsageStatisticsService statisticDao;

	@Test
	public void addEvent() {
		Group group = groupService.get(1L);
		AlertCondition cond = condService.getAlertConditionList(16L, 1L).get(1);
		Random rnd = new Random();
		AlertEvent event = new AlertEvent();
		event.setCondId(cond.getCondId());
		event.setAlertMsg(cond.getAlertMessage());
		event.setAlertSeverity(rnd.nextInt(4) - 1);
		event.setAlertTime(cond.getCreateDate());
		event.setAlertTime(new Date());
		eventService.save(event);

	}

	@Test
	public void listEvent() {
		QueryAlertEventForm command = new QueryAlertEventForm();
		eventService.page(command);
	}

	@Test
	public void listStatistice() {
		Long groupId = 1L;
		QueryStatisticsForm command = new QueryStatisticsForm();
		command.setGroupId(groupId);
		statisticDao.page(command);

	}

	@Test
	public void deleteEvent() {
		TimeRange dtr = UTCTime.getLastNDayForUTC(10);
		int a = eventGrpService.deleteEventGrp(dtr.getFrom());
		System.out.println(a);
		int b = eventTrmService.deleteEventTrm(dtr.getFrom());
		System.out.println(b);
	}
}
