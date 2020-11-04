package com.pax.tms.group.dev;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.shiro.subject.Subject;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.web.form.BaseForm;
import com.pax.tms.demo.AbstractShiroTest;
import com.pax.tms.group.dao.impl.GroupDaoImpl;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.web.form.AddGroupForm;
import com.pax.tms.group.web.form.QueryGroupForm;
import com.pax.login.TmsPac4jPrincipal;

import io.vertx.core.json.Json;

public class GroupDaoTest extends AbstractShiroTest {

	@Autowired
	private GroupDaoImpl groupDao;

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

	@Test
	public void testUserId() {
		BaseForm form = new BaseForm();
		System.out.println(form.getLoginUserId());
	}

	private AddGroupForm createGroup() {
		AddGroupForm command = new AddGroupForm();
		String uuid = UUID.randomUUID().toString();
		command.setName(uuid);
		command.setCode(uuid);
		command.setParentId(1L);
		groupService.save(command);
		return command;
	}

	@Test
	public void testExistGroupName() {
		AddGroupForm command = createGroup();
		boolean result = groupDao.existGroupName(command.getParentId(), command.getName());
		assertTrue(result);
	}

	@Test
	public void testDeleteGroupAnscetor() {
		groupDao.deleteGroupAncestor(99999999);
	}

	@Test
	public void testSearchGroupByNamePath() {
		List<String> result = groupDao.searchGroupByNamePath("pax", 1L);
		System.out.println(Json.encodePrettily(result));
	}

	@Test
	public void testGetChildGroup() {
		QueryGroupForm command = new QueryGroupForm();
		command.setSearchType(QueryGroupForm.GET_CHILD_GROUP);
		command.setGroupId(1L);
		List<String> result = groupDao.page(command, 0, 200);
		System.out.println(Json.encodePrettily(result));
	}

}
