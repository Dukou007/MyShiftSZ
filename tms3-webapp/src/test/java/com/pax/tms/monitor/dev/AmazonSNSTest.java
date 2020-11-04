package com.pax.tms.monitor.dev;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.subject.Subject;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.web.form.BaseForm;
import com.pax.tms.demo.AbstractShiroTest;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.monitor.service.AlertConditionService;
import com.pax.login.TmsPac4jPrincipal;

public class AmazonSNSTest extends AbstractShiroTest {

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

	@Autowired
	private AlertConditionService condService;
	@Autowired
	private GroupService groupService;

	@Test
	public void testAddCondition() {
		Group group = groupService.get(1000L);
		BaseForm command = new BaseForm();

		condService.addAlertCondition(group, command);
	}
}
