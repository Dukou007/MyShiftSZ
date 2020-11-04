package com.pax.tms.monitor.dev;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.web.form.BaseForm;
import com.pax.tms.demo.ServiceJunitCase;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.dao.AlertSubscribeDao;
import com.pax.tms.monitor.model.AlertCondition;
import com.pax.tms.monitor.model.AlertOff;
import com.pax.tms.monitor.service.AlertConditionService;
import com.pax.tms.monitor.web.form.AddAlertOffForm;
import com.pax.tms.monitor.web.form.EditConditionForm;
import com.pax.tms.user.security.UTCTime;

import io.vertx.core.json.Json;

public class AlertConditionServiceTest extends ServiceJunitCase {
	@Autowired
	private AlertConditionService condService;
	@Autowired
	private AlertSubscribeDao subscribeDao;
	@Autowired
	private GroupService groupService;

	@Test
	public void testAddCondition() {
		BaseForm command = new BaseForm();
		Group group = groupService.get(1L);
		condService.addAlertCondition(group, command);
	}

	@Test
	public void editCondition() {
		BaseForm command1 = new BaseForm();
		Long groupId = 1L;
		Group group = groupService.get(1L);
		condService.addAlertCondition(group, command1);
		String condJson = "";
		try {
			InputStream in = AlertConditionServiceTest.class.getResourceAsStream("alertCondition.json");
			condJson = IOUtils.toString(in, Charsets.toCharset("UTF-8"));
		} catch (IOException e) {
			// TODO file not exist
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = Json.decodeValue(condJson, List.class);
		EditConditionForm[] command = new EditConditionForm[15];
		Long settingId = condService.findSettingByGroupId(1L).getSettingId();
		List<AlertCondition> condIds = condService.getAlertConditionList(settingId, 1L);
		for (int i = 0; i < list.size(); i++) {
			Map<String, String> map = list.get(i);
			EditConditionForm form = new EditConditionForm();
			form.setCondId(condIds.get(i).getCondId());
			form.setAlertItem(map.get("alertItem"));
			form.setAlertMessage(map.get("alertMessage"));
			form.setAlertSeverity(Integer.valueOf(map.get("alertSeverity")));
			form.setAlertThreshold(map.get("alertThreshold"));
			form.setScbEmail(map.get("scbEmail"));
			form.setScbSms(map.get("scbSms"));
			command[i] = form;
		}
		condService.editCondition(command, groupId);
	}

	@Test
	public void addAlertOff() {
		AddAlertOffForm command = new AddAlertOffForm();
		command.setGroupId(1L);
		command.setOffDate("00:22");
		command.setOffEndTime("00:01");
		command.setOffStartTime("00:00");
		command.setRepeatType(1);
		command.setGroupId(1L);
		condService.addAlertOff(command);

	}

	@Test
	public void deleteAlertOff() {
		AddAlertOffForm command = new AddAlertOffForm();
		command.setGroupId(1L);
		command.setOffDate("00:22");
		command.setOffEndTime("00:01");
		command.setOffStartTime("00:00");
		command.setRepeatType(1);
		Long settingId = condService.findSettingByGroupId(1L).getSettingId();
		command.setGroupId(1L);
		condService.addAlertOff(command);
		condService.deleteAlertOff(condService.getAlertOffList(settingId).get(0).getOffId(), command);
	}

	@Test
	public void listAlertOff() {
		List<AlertOff> list = new ArrayList<>();
		Long settingId = condService.findSettingByGroupId(1L).getSettingId();
		list = condService.getAlertOffList(settingId);
		for (AlertOff a : list) {
			System.out.println(a.getOffStartTime());
		}
	}

	@Test
	public void listCondition() {
		Long groupId = 1132L;
		Long userId = 1L;
		List<AlertCondition> list = condService.getAlertConditionListByGroupId(groupId);
		list.forEach(cond -> {
			subscribeDao.deleteUserScribe(cond.getCondId(), userId);
		});
	}

	@Test
	public void deleteCondition() {
		BaseForm command = new BaseForm();
		condService.deleteCondition(1111L, command);
	}

}
