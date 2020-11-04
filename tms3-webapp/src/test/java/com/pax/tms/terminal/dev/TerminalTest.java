package com.pax.tms.terminal.dev;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.subject.Subject;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.pagination.Page;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.demo.AbstractShiroTest;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.terminal.web.form.AddTerminalForm;
import com.pax.tms.terminal.web.form.CopyTerminalForm;
import com.pax.tms.terminal.web.form.EditTerminalForm;
import com.pax.tms.terminal.web.form.QueryTerminalForm;
import com.pax.login.TmsPac4jPrincipal;

import io.vertx.core.json.Json;

public class TerminalTest extends AbstractShiroTest {

	@Autowired
	private TerminalService terminalService;

	@Before
	public void initialize() {

		CommonProfile profile = new CommonProfile();

		final LinkedHashMap<String, CommonProfile> profiles = new LinkedHashMap<String, CommonProfile>();
		profiles.put("casclient", profile);
		final TmsPac4jPrincipal principal = new TmsPac4jPrincipal(profiles);
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("userId", "1");
		profile.build("admin", attributes);
		// io.buji.pac4j.subject.Pac4jPrincipal
		// 1. Create a mock authenticated Subject instance for the test to run:
		Subject subjectUnderTest = createNiceMock(Subject.class);
		expect(subjectUnderTest.isAuthenticated()).andReturn(true);
		expect(subjectUnderTest.getPrincipal()).andReturn(principal);
		EasyMock.replay(subjectUnderTest);

		// 2. Bind the subject to the current thread:
		setSubject(subjectUnderTest);

		// perform test logic here. Any call to
		// SecurityUtils.getSubject() directly (or nested in the
		// call stack) will work properly.
	}

	@After
	public void tearDownSubject() {
		// 3. Unbind the subject from the current thread:
		clearSubject();
	}

	@Test
	public void testAddTerminal() {
		AddTerminalForm command = new AddTerminalForm();
		command.setTsnRanges(new String[] { "102-104" });
		command.setDestModel("S80");
		command.setGroupId(1L);
		Map<String, Object> resultMap = terminalService.save(command);
		System.out.println(Json.encode(resultMap));
	}

	@Test
	public void testCopyTerminal() {
		CopyTerminalForm command = new CopyTerminalForm();
		command.setTsnRanges(new String[] { "100", "101", "1002" });
		command.setGroupId(20L);
		command.setTargetGroupId(21L);

		Map<String, Object> resultMap = terminalService.copy("1002", command);
		System.out.println(resultMap);

	}

	@Test
	public void testEditTerminal() {

		EditTerminalForm command = new EditTerminalForm();
		command.setDescription("this is 100 terminal");
		terminalService.edit("100", command);
	}

	@Test
	public void testDeactivate() {
		BaseForm command = new BaseForm();
		terminalService.deactivate(new String[] { "100", "1000" }, command);

	}

	@Test
	public void testDismiss() {
		BaseForm command = new BaseForm();
		command.setGroupId(30L);
		terminalService.dismiss(new String[] { "1006" }, command);

	}

	@Test
	public void testPage() {
		QueryTerminalForm command = new QueryTerminalForm();
		command.setGroupId(1L);
		Page<Map<String, Object>> page = terminalService.page(command);
		System.out.println(Json.encodePrettily(page));

	}

	@Test
	public void testMoveToGroup() {
		QueryTerminalForm command = new QueryTerminalForm();
		command.setGroupId(20L);
		command.setTargetGroupId(30L);
		terminalService.moveToGroup(new String[] { "1006" }, command);

	}

}
