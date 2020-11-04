package com.pax.tms.group.dev;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.subject.Subject;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.pagination.Page;
import com.pax.tms.demo.AbstractShiroTest;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.web.form.AddGroupForm;
import com.pax.tms.group.web.form.CopyGroupForm;
import com.pax.tms.group.web.form.MoveGroupForm;
import com.pax.tms.group.web.form.QueryGroupForm;
import com.pax.tms.terminal.model.TerminalGroup;
import com.pax.login.TmsPac4jPrincipal;

import io.vertx.core.json.Json;

public class GroupTest extends AbstractShiroTest {

	@Autowired
	private GroupService groupService;

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

		// perform test logic here. Any call to
		// SecurityUtils.getSubject() directly (or nested in the
		// call stack) will work properly.
	}

	@After
	public void tearDownSubject() {
		// 3. Unbind the subject from the current thread:
		clearSubject();
	}

	private AddGroupForm createGroup() {
		AddGroupForm command = new AddGroupForm();
		String uuid = UUID.randomUUID().toString();
		command.setName("a");
		command.setCode(uuid);
		command.setParentId(1L);
		command.setCountryId(1l);
		command.setProvinceId(101l);
		command.setCityName("qweq");
		command.setZipCode("11111");
		command.setTimeZone("US/Alaska");
		command.setDaylightSaving(true);
		groupService.save(command);
		System.out.println(1111);
		return command;
	}

	@Test
	public void testAddGroup() {
		createGroup();
	}

	@Test
	public void testLoginUserKeyWordsQuery() {
		List<String> namePaths = groupService.searchGroupByNamePath("pax/group2", (long) 2);
		for (String namePath : namePaths) {
			System.out.println(namePath);
		}
	}

	@Test
	public void testMoveGroup() throws CloneNotSupportedException {
		MoveGroupForm command = new MoveGroupForm();
		command.setSourceGroupId((long) 30);
		command.setTargetGroupId((long) 31);
		groupService.move(command);
		System.out.println("Move Success");
	}

	@Test
	public void testGetGroupByNamePath() {
		Group group = groupService.getGroupByNamePath("pax");
		System.out.println(group.getName());

	}

	@Test
	public void testGetTerminalTypesByGroupId() {
		List<String> termianlTypes = groupService.getTerminalTypesByGroupId((long) 30);
		for (String terminalType : termianlTypes) {
			System.out.println(terminalType);

		}

	}

	@Test
	public void testGetTerminalNumbersByGroupId() {
		long num = groupService.getTerminalNumbersByGroupId((long) 30);
		System.out.println(num);

	}

	@Test
	public void testGetTerminalGroupsById() {
		List<TerminalGroup> terminalGroups = groupService.getTerminalGroupsById((long) 2000);
		for (TerminalGroup terminalGroup : terminalGroups) {
			System.out.println(terminalGroup.getTerminal().getTsn());

		}

	}

	@Test
	public void testGetSelfAndAncestorGroup() {
		List<Group> groups = groupService.getSelfAndAncestorGroup((long) 3001);
		if (CollectionUtils.isNotEmpty(groups)) {
			for (Group group : groups) {
				System.out.println(group.getName());
			}

		}

	}

	@Test
	public void tesGetDescantstPageGroup() {
		QueryGroupForm command = new QueryGroupForm();
		command.setFuzzyCondition("-");
		command.setSearchType(QueryGroupForm.SEARCH_GROUP);
		Page<Map<String, Object>> page = groupService.page(command);
		System.out.println(Json.encode(page));
	}

	@Test
	public void testUserChildGroupPage() {
		QueryGroupForm command = new QueryGroupForm();
		command.setSearchType(QueryGroupForm.GET_TOP_GROUP);
		Page<Map<String, Object>> page = groupService.page(command);
		System.out.println(Json.encode(page));
	}

	@Test
	public void testChildGroupPage() {
		QueryGroupForm command = new QueryGroupForm();
		command.setGroupId(30L);
		command.setSearchType(QueryGroupForm.GET_CHILD_GROUP);
		Page<Map<String, Object>> page = groupService.page(command);
		System.out.println(Json.encode(page));
	}

	@Test
	public void testCopyGroup() {
		CopyGroupForm command = new CopyGroupForm();
		command.setSourceGroupId(1L);
		command.setTargetGroupId(1004L);
		groupService.copy(command);

	}

}
