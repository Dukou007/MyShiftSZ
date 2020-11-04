package com.pax.tms.user.dev;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.Subject;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.ldaptive.LdapException;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.util.DateTimeUtils;
import com.pax.tms.demo.AbstractShiroTest;
import com.pax.tms.user.InactiveAccountMailSender;
import com.pax.tms.user.model.User;
import com.pax.login.TmsPac4jPrincipal;
import com.pax.tms.user.service.AuditLogService;
import com.pax.tms.user.service.UserService;
import com.pax.tms.user.web.form.AddUserForm;
import com.pax.tms.user.web.form.EditUserForm;
import com.pax.tms.user.web.form.QueryAuditLogForm;

public class UserServiceTest extends AbstractShiroTest {

	@Autowired
	private UserService userSvc;

	@Autowired
	private AuditLogService auditlogSvc;

	@Before
	public void initialize() {
		CommonProfile profile = new CommonProfile();
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("userId", "1");
		profile.build("admin", attributes);

		final LinkedHashMap<String, CommonProfile> profiles = new LinkedHashMap<String, CommonProfile>();
		profiles.put("casclient", profile);
		final TmsPac4jPrincipal principal = new TmsPac4jPrincipal(profiles);

		// 1. Create a mock authenticated Subject instance for the test to run:
		Subject subjectUnderTest = createNiceMock(Subject.class);
		expect(subjectUnderTest.isAuthenticated()).andReturn(true).anyTimes();
		expect(subjectUnderTest.getPrincipal()).andReturn(principal).anyTimes();
		EasyMock.replay(subjectUnderTest);

		// 2. Bind the subject to the current thread:
		setSubject(subjectUnderTest);

	}

	@Test
	public void testAddUser() {
		AddUserForm form = new AddUserForm();
		form.setUsername("bbb");
		form.setPassword("123456");
		form.setEmail("alan.j@paxdata.com");
		Long[] id = { 1L };
		form.setGroupIds(id);
		form.setRoleIds(id);
		userSvc.add(form);
	}

	@Test
	public void testEditUser() {
		EditUserForm form = new EditUserForm();
		form.setUsername("testEdit");
		form.setCountryId(1L);
		form.setProvinceId(102L);
		Long[] id = { 1008L };
		form.setGroupIds(id);
		userSvc.edit(1010L, form);
	}

	@Test
	public void testGetCriteria() throws ParseException {
		QueryAuditLogForm command = new QueryAuditLogForm();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		command.setStartTime(sdf.parse("2016-10-20"));
		command.setStartTime(new Date());
		command.setRoleName("Installer");
		Long[] ids = { 1L };
		command.setGroupIds(ids);
		auditlogSvc.page(command);
	}

	public static void main(String[] args) {
		Date endLastChangeDate = DateTimeUtils.addDate(new Date(), "day", -28);
		Date startLastChangeDate = DateTimeUtils.addDate(endLastChangeDate, "day", -1);
		System.out.println("---endLoginDate===" + endLastChangeDate + "  ----startLoginDate===" + startLastChangeDate);
	}

	@Test
	public void testUpdateLdapInfo() throws LdapException {
		userSvc.updateAllLdapUsersInfo();
	}

	@Autowired(required = false)
	private InactiveAccountMailSender inactiveAccountMailSender;

	private String inactiveUserLockAlertInDaysBefore = "7";

	@Test
	public void alertInactiveUser() {
		if (inactiveAccountMailSender == null) {
			return;
		}

		int lockDays = 30;
		if (lockDays <= 0) {
			return;
		}
		try {
			System.out.println("----start to alert inactive user--");
			List<Integer> alertInDaysBeforeList = getInactiveUserLockAlertInDaysBefore();
			for (Integer alertDays : alertInDaysBeforeList) {
				if (alertDays > 0) {
					alertInactiveUserInDaysBefore(lockDays, alertDays);
				}
			}
			System.out.println("----finish to alert inactive user--");
		} catch (Exception e) {
			System.out.println("----failed to alert inactive user--");
		}
	}

	private void alertInactiveUserInDaysBefore(int lockDays, int alertDays) {
		int inactiveDays = lockDays - alertDays;
		System.out.println("-----------------inactiveDays===" + inactiveDays);
		if (inactiveDays <= 0) {
			return;
		}

		Date endLoginDate = DateTimeUtils.addDate(new Date(), "day", -inactiveDays);
		Date startLoginDate = DateTimeUtils.addDate(endLoginDate, "day", -1);
		System.out.println("---endLoginDate===" + endLoginDate + "  ----startLoginDate===" + startLoginDate);
		List<User> inactiveUsers = userSvc.getInactiveUser(startLoginDate, endLoginDate);
		for (User user : inactiveUsers) {
			try {
				System.out.println("--------------username=" + user.getUsername());
				inactiveAccountMailSender.sendInactiveAccountEmail(user, inactiveDays, alertDays);
			} catch (Exception e) {
				System.out.println("----alert inactive user - failed to send email --");
			}
		}
	}

	private List<Integer> getInactiveUserLockAlertInDaysBefore() {
		return paserNumberList(inactiveUserLockAlertInDaysBefore);
	}

	private List<Integer> paserNumberList(String numberListStr) {
		if (StringUtils.isEmpty(numberListStr)) {
			return Collections.emptyList();
		}

		String[] arr = numberListStr.split(",");
		List<Integer> list = new ArrayList<Integer>(arr.length);
		for (String s : arr) {
			s = StringUtils.trimToNull(s);
			if (s != null) {
				list.add(Integer.parseInt(s));
			}
		}
		return list;
	}

}
