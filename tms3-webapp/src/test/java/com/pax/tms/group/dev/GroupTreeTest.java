package com.pax.tms.group.dev;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.shiro.subject.Subject;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.pagination.Page;
import com.pax.tms.demo.AbstractShiroTest;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.service.GroupTreeService;
import com.pax.tms.group.web.form.AddGroupForm;
import com.pax.tms.group.web.form.QueryGroupForm;

import io.vertx.core.json.Json;

public class GroupTreeTest extends AbstractShiroTest {

	@Autowired
	private GroupTreeService groupTreeService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private GroupDao groupDao;

	@Before
	public void initialize() {
		CommonProfile profile = new CommonProfile();
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("userId", "1");
		profile.build("admin", attributes);

		// 1. Create a mock authenticated Subject instance for the test to run:
		Subject subjectUnderTest = createNiceMock(Subject.class);
		expect(subjectUnderTest.isAuthenticated()).andReturn(true).anyTimes();
		expect(subjectUnderTest.getPrincipal()).andReturn(profile).anyTimes();
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
		command.setName(uuid);
		command.setCode(uuid);
		command.setParentId(1L);
		groupService.save(command);
		return command;
	}

	@Test
	public void testGetTopGroup() {
		QueryGroupForm command = new QueryGroupForm();
		Page<Map<String, Object>> page = groupTreeService.getUserTopGroups(command);
		System.out.println(Json.encodePrettily(page));
	}

	@Test
	public void testGetSubgroup() {
		AddGroupForm form = createGroup();
		Group group = groupDao.getGroup(form.getParentId(), form.getName());

		for (int i = 0; i < 2; i++) {
			AddGroupForm command = new AddGroupForm();
			String uuid = UUID.randomUUID().toString();
			command.setName(uuid);
			command.setCode(uuid);
			command.setParentId(group.getId());
			groupService.save(command);
		}

		QueryGroupForm command = new QueryGroupForm();
		command.setGroupId(group.getId());
		Page<Map<String, Object>> page = groupTreeService.getSubgroup(command);
		System.out.println(Json.encodePrettily(page));
	}

	@Test
	public void testGetGroupContext10() {
		for (int n = 0; n < 2; n++) {
			AddGroupForm form = createGroup();
			Group group = groupDao.getGroup(form.getParentId(), form.getName());
			for (int i = 0; i < 2; i++) {
				AddGroupForm command = new AddGroupForm();
				String uuid = UUID.randomUUID().toString();
				command.setName(uuid);
				command.setCode(uuid);
				command.setParentId(group.getId());
				groupService.save(command);

				Group group2 = groupDao.getGroup(command.getParentId(), command.getName());

				for (int j = 0; j < 2; j++) {
					command = new AddGroupForm();
					uuid = UUID.randomUUID().toString();
					command.setName(uuid);
					command.setCode(uuid);
					command.setParentId(group2.getId());
					groupService.save(command);

					Group group3 = groupDao.getGroup(command.getParentId(), command.getName());
					for (int k = 0; k < 2; k++) {
						command = new AddGroupForm();
						uuid = UUID.randomUUID().toString();
						command.setName(uuid);
						command.setCode(uuid);
						command.setParentId(group3.getId());
						groupService.save(command);

						Group group4 = groupDao.getGroup(command.getParentId(), command.getName());
						for (int l = 0; l < 2; l++) {
							command = new AddGroupForm();
							uuid = UUID.randomUUID().toString();
							command.setName(uuid);
							command.setCode(uuid);
							command.setParentId(group4.getId());
							groupService.save(command);

							Group group5 = groupDao.getGroup(command.getParentId(), command.getName());
							for (int o = 0; o < 2; o++) {
								command = new AddGroupForm();
								uuid = UUID.randomUUID().toString();
								command.setName(uuid);
								command.setCode(uuid);
								command.setParentId(group5.getId());
								groupService.save(command);

								Group group6 = groupDao.getGroup(command.getParentId(), command.getName());
								for (int p = 0; p < 2; p++) {
									command = new AddGroupForm();
									uuid = UUID.randomUUID().toString();
									command.setName(uuid);
									command.setCode(uuid);
									command.setParentId(group6.getId());
									groupService.save(command);

									Group group7 = groupDao.getGroup(command.getParentId(), command.getName());
									for (int q = 0; q < 2; q++) {
										command = new AddGroupForm();
										uuid = UUID.randomUUID().toString();
										command.setName(uuid);
										command.setCode(uuid);
										command.setParentId(group7.getId());
										groupService.save(command);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Test
	public void testGetGroupContext() {
		AddGroupForm form = createGroup();
		Group group = groupDao.getGroup(form.getParentId(), form.getName());

		for (int i = 0; i < 2; i++) {
			AddGroupForm command = new AddGroupForm();
			String uuid = UUID.randomUUID().toString();
			command.setName(uuid);
			command.setCode(uuid);
			command.setParentId(group.getId());
			groupService.save(command);
		}

		AddGroupForm command = new AddGroupForm();
		String uuid = UUID.randomUUID().toString();
		command.setName(uuid);
		command.setCode(uuid);
		command.setParentId(group.getId());
		groupService.save(command);

		group = groupDao.getGroup(command.getParentId(), command.getName());

		for (int i = 0; i < 2; i++) {
			command = new AddGroupForm();
			uuid = UUID.randomUUID().toString();
			command.setName(uuid);
			command.setCode(uuid);
			command.setParentId(group.getId());
			groupService.save(command);

			Group group5 = groupDao.getGroup(command.getParentId(), command.getName());
			for (int j = 0; j < 2; j++) {
				command = new AddGroupForm();
				uuid = UUID.randomUUID().toString();
				command.setName(uuid);
				command.setCode(uuid);
				command.setParentId(group5.getId());
				groupService.save(command);
			}
		}

		QueryGroupForm queryCommand = new QueryGroupForm();
		queryCommand.setGroupId(group.getId());
		Page<Map<String, Object>> page = groupTreeService.getGroupContext(queryCommand);
		System.out.println(Json.encodePrettily(page));
	}

	@Test
	public void testSearchGroup() {
		QueryGroupForm queryCommand = new QueryGroupForm();
		queryCommand.setKeyword("f");
		Page<Map<String, Object>> page = groupTreeService.searchGroupTree(queryCommand);
		System.out.println(Json.encodePrettily(page));
	}
}
