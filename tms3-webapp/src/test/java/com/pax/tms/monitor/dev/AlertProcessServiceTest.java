package com.pax.tms.monitor.dev;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;

import com.pax.common.pagination.Page;
import com.pax.common.util.DateTimeUtils;
import com.pax.common.util.DateTimeUtils.DateTimeRange;
import com.pax.tms.demo.ServiceJunitCase;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.model.UsageThreshold;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.service.UsageThresholdService;
import com.pax.tms.monitor.dao.AlertProcessDao;
import com.pax.tms.monitor.domain.ResultCount;
import com.pax.tms.monitor.domain.UsageMessageInfo;
import com.pax.tms.monitor.service.AlertProcessService;
import com.pax.tms.monitor.service.DashboardService;
import com.pax.tms.monitor.service.EventGrpService;
import com.pax.tms.monitor.web.AlertConstants;
import com.pax.tms.monitor.web.form.QueryTerminalEventForm;
import com.pax.tms.monitor.web.form.TerminalUsagePie;
import com.pax.tms.monitor.web.form.UsagePie;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.user.security.UTCTime;
import com.pax.tms.user.security.UTCTime.TimeRange;

import io.vertx.core.json.Json;

public class AlertProcessServiceTest extends ServiceJunitCase {

	public static final String PATTERN_STANDARD = "yyyy-MM-dd HH:mm:ss";

	static SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_STANDARD);

	@Autowired
	HibernateTransactionManager txManager;

	@Autowired
	private AlertProcessDao alertProcessDao;
	@Autowired
	private UsageThresholdService thdService;
	@Autowired
	private AlertProcessService alertProcessService;
	@Autowired
	private EventGrpService eventService;
	@Autowired
	private DashboardService dashboardService;
	@Autowired
	private TerminalService terminalService;
	@Autowired
	private GroupService groupService;

	@Test
	public void getTransaction() throws Exception {
		int nThreads = 100;
		Long userId = 1L;
		Group group = groupService.get(1000L);
		final String groupIdPath = group.getIdPath().intern();
		ExecutorService exec = Executors.newFixedThreadPool(nThreads);
		final long start = System.currentTimeMillis();
		for (int i = 0; i < nThreads; i++) {
			exec.submit(() -> {
				try {
					synchronized (groupIdPath) {
						alertProcessService.getRealDashboard(group, userId);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			});
		}
		exec.shutdown();
		while(true) {
			if (exec.isTerminated()) {
				break;
			}
		}
		final long end = System.currentTimeMillis();
		
		System.out.println(end - start);
	}

	@Test
	public void getUsagePielistTest() {
		Long userId = 1L;
		List<UsagePie> list = dashboardService.getUsagePielist(1001L, userId);
		System.out.println(Json.encodePrettily(list));
	}

	@Test
	public void processTerminalUsageStatus() {
		alertProcessService.usageMessageByDay();
		alertProcessService.usageStatusByThreshold();
		alertProcessService.doProcessTerminalUsageStatus();
	}

	@Test
	public void getUsageMessage() throws ParseException {
		List<UsageMessageInfo> list = new LinkedList<UsageMessageInfo>();
		String day = null;
		Date start = sdf.parse("2016-12-30 00:00:00");
		Date end = sdf.parse("2016-12-30 23:59:59");
		for (String str : AlertConstants.getUsageItems()) {
			list.addAll(alertProcessDao.getUsageMessageList(str, start, end, "per day"));
			alertProcessDao.getGroupRealStatus(1L, str, start);
			alertProcessDao.getUsageMessageList(str, start, end, day);
		}
		list.forEach(usage -> System.out.println(Json.encodePrettily(usage)));
		System.out.println(list.size());
	}

	@Test
	public void getUsageStatusByThreshold() throws Exception {
		Date start = sdf.parse("2016-11-11 00:00:00");
		Date end = sdf.parse("2016-11-11 23:59:59");
		alertProcessService.usageStatusByThreshold();
		Long groupId = 1088L;
		List<UsageThreshold> thdList = thdService.list(groupId);
		thdList.forEach(thd -> {
			alertProcessDao.getUsageStatusList(thd, start, end);
		});
	}

	/**
	 * clear
	 * @CacheEvict(value = "dashboardCache", allEntries=true, beforeInvocation=true) 
	 * 
	 * evict
	 * @CacheEvict(value = "dashboardCache", key="'dashboard_'+#group.getIdPath()")
	 */

	@Test
	public void doProcessGroupRealStatus() {
		alertProcessService.doProcessGroupRealStatus();
	}

	@Test
	public void getGroupRealStatusTest() throws Exception {
		Long userId = 1L;
		Group group = groupService.get(1000L);
		Group group1 = groupService.get(1001L);
		List list = alertProcessService.getRealDashboard(group, userId);
				System.out.println(Json.encodePrettily(list));
//		Thread.sleep(3000);
//		alertProcessService.getRealDashboard(group1, userId);
//		Thread.sleep(3000);
//		alertProcessService.getRealDashboard(group, userId);
//
//		alertProcessService.getRealDashboard(group, userId);

	}

	@Test
	public void TerminalUsageStatusBarList() {
		List<List<TerminalUsagePie>> list = terminalService.getUsageStatusBarList("11110000", 1L);
		for (int i = 0; i < list.size(); i++) {
			System.out.println(list);
		}
	}

	@Test
	public void getGroupUsageStatusTest() throws Exception {
		Date startTime = sdf.parse("2016-12-10 00:00:00");
		Date endTime = sdf.parse("2016-12-10 23:59:59");
		List<ResultCount> list = alertProcessDao.getGroupUsageStatus(1006L, "MSR Read Rate", startTime, endTime, "per day");
		System.out.println(Json.encode(list));
		list.forEach(result -> System.out.println(result.getItemStatus()));
	}

	@Test
	public void listAllEvents() throws Exception {
		QueryTerminalEventForm command = new QueryTerminalEventForm();
		command.setPaginationStatus("firstPage");
		int days = 36500;
		String searchType = "All";
		command.setSearchType(searchType);
		DateTimeRange dtr = DateTimeUtils.lastNDays(days);
		command.setFromDate(dtr.getFrom());
		command.setToDate(dtr.getTo());
		command.setGroupId(1L);
		command.setSearchType(searchType);
		if ("All".equals(command.getSearchType())) {
			int index = 1;
			int size = 13;
			long totalCount = eventService.countDao(command);
			int firstResult = Page.getPageStart(totalCount, index, size);
			ExecutorService executor = Executors.newFixedThreadPool(2);
			Future<List<Map<String, Object>>> future1 = executor
					.submit(new TerminalCallable(command, firstResult, size));
			Future<List<Map<String, Object>>> future2 = executor.submit(new GroupCallable(command, firstResult, size));
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			try {
				while (!future1.isDone()) {
				}
				list.addAll(future1.get());
				while (!future2.isDone()) {
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}

			String pageStatus = command.getPaginationStatus();
			String type = "desc";
			if ("lastPage".equals(pageStatus) || "previousPage".equals(pageStatus)) {
				type = "asc";
			}
			this.sortList(list, type);
			if (list.size() > 10) {
				list = list.subList(0, 10);
			}
			this.sortList(list, "desc");

		} else {
		}

	}

	private class GroupCallable implements Callable<List<Map<String, Object>>> {
		private QueryTerminalEventForm command;
		private int start;
		private int length;

		public GroupCallable(QueryTerminalEventForm command, int start, int length) {
			super();
			this.command = command;
			this.start = start;
			this.length = length;
		}

		@Override
		public List<Map<String, Object>> call() throws Exception {
			return eventService.callGrp(command, start, length);
		}

	}

	private class TerminalCallable implements Callable<List<Map<String, Object>>> {
		private QueryTerminalEventForm command;
		private int start;
		private int length;

		public TerminalCallable(QueryTerminalEventForm command, int start, int length) {
			super();
			this.command = command;
			this.start = start;
			this.length = length;
		}

		@Override
		public List<Map<String, Object>> call() throws Exception {
			return eventService.callTrm(command, start, length);
		}

	}

	private List<Map<String, Object>> sortList(List<Map<String, Object>> list, final String type) {
		list.sort((map1, map2) -> {
			BigDecimal n1 = (BigDecimal) map1.get("eventId");
			BigDecimal n2 = (BigDecimal) map2.get("eventId");
			if ("asc".equals(type)) {
				return n1.compareTo(n2);
			} else {
				return n2.compareTo(n1);
			}
		});
		return list;
	}

	@Test
	public void testSub() {
		String a = "(+8:00)";
		System.out.println(a.substring(a.indexOf(":") + 1, a.indexOf(")")));
	}

	@Test
	public void cleanUsageData() {
		TimeRange dtr = UTCTime.getLastNDayForUTC(10);
		alertProcessService.deleteUsageData(dtr.getFrom());
	}
}
