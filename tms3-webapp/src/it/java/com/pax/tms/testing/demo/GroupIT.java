package com.pax.tms.testing.demo;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.shiro.subject.Subject;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.web.form.BaseForm;
import com.pax.tms.testing.AbstractShiroTest;
import com.pax.tms.deploy.dao.GroupDeployDao;
import com.pax.tms.deploy.dao.TerminalDeployDao;
import com.pax.tms.deploy.service.GroupDeployService;
import com.pax.tms.deploy.web.form.GroupDeployForm;
import com.pax.tms.deploy.web.form.QueryCurrentGroupDeployForm;
import com.pax.tms.deploy.web.form.QueryCurrentTsnDeployForm;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.web.form.AddGroupForm;
import com.pax.tms.group.web.form.CopyGroupForm;
import com.pax.tms.group.web.form.MoveGroupForm;
import com.pax.tms.monitor.dao.EventGrpDao;
import com.pax.tms.monitor.dao.EventTrmDao;
import com.pax.tms.monitor.model.EventGrp;
import com.pax.tms.monitor.model.EventTrm;
import com.pax.tms.res.dao.PkgDao;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.res.web.form.AddPkgForm;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.terminal.web.form.AddTerminalForm;
import com.pax.tms.user.service.AuditLogService;

public class GroupIT extends AbstractShiroTest {

	@Autowired
	private GroupService groupService;

	@Autowired
	private TerminalService terminalService;

	@Autowired
	private PkgService pkgService;

	@Autowired
	private GroupDeployService groupDeployService;

	@Autowired
	private GroupDao groupDao;

	@Autowired
	private EventGrpDao eventGrpDao;

	@Autowired
	private EventTrmDao eventTrmDao;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private TerminalDeployDao terminalDeployDao;

	@Autowired
	private GroupDeployDao groupDeployDao;

	@Autowired
	private PkgDao pkgDao;

	@Autowired
	private static String path;

	@BeforeClass
	public static void initPath() throws IOException {
		File directory = new File("");
		path = directory.getCanonicalPath() + "\\src\\it\\resources\\package\\";
	}

	@Before
	public void initialize() {
		CommonProfile profile = new CommonProfile();
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("userId", "1");
		profile.build("admin", attributes);
		final LinkedHashMap<String, CommonProfile> profiles = new LinkedHashMap<String, CommonProfile>();
		profiles.put("casclient", profile);
		final com.pax.login.TmsPac4jPrincipal principal = new com.pax.login.TmsPac4jPrincipal(profiles);
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

	public Group createGroup(Long parentGroupId, String name) {
		AddGroupForm command = new AddGroupForm();
		String uuid = UUID.randomUUID().toString();

		command.setName(name);
		command.setCode(uuid);
		command.setParentId(parentGroupId);
		command.setCountryId(1l);
		command.setProvinceId(101l);
		command.setCityName("qweq");
		command.setZipCode("11111");
		command.setTimeZone("US/Alaska");
		command.setDaylightSaving(true);
		groupService.save(command);

		return groupDao.getGroup(parentGroupId, name);
	}

	public Group createGroup(Long parentGroupId, String name, String cityName) {
		AddGroupForm command = new AddGroupForm();
		String uuid = UUID.randomUUID().toString();

		command.setName(name);
		command.setCode(uuid);
		command.setParentId(parentGroupId);
		command.setCountryId(1l);
		command.setProvinceId(101l);
		command.setCityName(cityName);
		command.setZipCode("11111");
		command.setTimeZone("US/Alaska");
		command.setDaylightSaving(true);
		groupService.save(command);

		return groupDao.getGroup(parentGroupId, name);
	}

	public Group createGroup(Long parentGroupId, String name, Long countryId, String zipCode) {
		AddGroupForm command = new AddGroupForm();
		String uuid = UUID.randomUUID().toString();

		command.setName(name);
		command.setCode(uuid);
		command.setParentId(parentGroupId);
		command.setCountryId(countryId);
		command.setProvinceId(101l);
		command.setCityName("qweq");
		command.setZipCode(zipCode);
		command.setTimeZone("US/Alaska");
		command.setDaylightSaving(true);
		groupService.save(command);

		return groupDao.getGroup(parentGroupId, name);
	}

	public void createTerminal(String tsn, Long groupId) {
		AddTerminalForm command = new AddTerminalForm();
		String[] tsns = new String[] { tsn };
		command.setTsnRanges(tsns);
		command.setDestModel("Px7");
		command.setGroupId(groupId);
		command.setCountryId(1l);
		command.setProvinceId(101l);
		command.setCityName("qweq");
		command.setZipCode("11111");
		command.setTimeZone("US/Alaska");
		command.setDaylightSaving(true);
		command.setSyncToServerTime(true);
		terminalService.save(command);
	}

	public Pkg createPkg(Long[] groupIds, String filePath, String fileName) throws Exception {
		AddPkgForm command = new AddPkgForm();
		command.setGroupIds(groupIds);
		command.setType("Multilane");
		command.setFilePath(filePath);
		command.setFileName(fileName);
		command.setDestModel("PX7L");
		return pkgService.save(command);
	}

	public void MoveGroup(Long sourceGroupId, Long targetGroupId) throws CloneNotSupportedException {
		MoveGroupForm command = new MoveGroupForm();
		command.setSourceGroupId(sourceGroupId);
		command.setTargetGroupId(targetGroupId);
		groupService.move(command);
	}

	public void CopyGroup(Long sourceGroupId, Long targetGroupId) {
		CopyGroupForm command = new CopyGroupForm();
		command.setSourceGroupId(sourceGroupId);
		command.setTargetGroupId(targetGroupId);
		groupService.copy(command);

	}

	public void deleteGroup(Long groupId) {
		BaseForm command = new BaseForm();
		groupService.delete(groupId, command);
	}

	public void createGroupDeploy(Long groupId, Long pkgId) {
		GroupDeployForm command = new GroupDeployForm();
		command.setGroupId(groupId);
		command.setPkgId(pkgId);
		command.setDestModel("Px7");
		command.setActvStartTime(command.getRequestTime());
		command.setDwnlStartTime(command.getRequestTime());
		groupDeployService.deploy(command);
	}

	@Test
	public void GROUP_01_CreateGroup_NameAlreadyExist() {
		// create group(name="test")
		String groupName = "test";
		createGroup(1l, groupName);

		// create group again(name="test")
		try {
			createGroup(1l, groupName);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.group.groupNameExist", message);
		}
	}

	@Test
	public void GROUP_02_CreateGroup_NameExistInOtherGroup() {
		// create group(name="test")
		String groupName = "test";
		Group parent = createGroup(1l, groupName);

		// create group(name="test")
		createGroup(parent.getId(), groupName);

		// validate event
		String source = parent.getId().toString();
		List<EventGrp> sourceGroupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(3, sourceGroupEvents.size());

		// validate auditlog
		String logString = "Add Group " + parent.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void GROUP_03_CreateGroup_NotInputName() {
		try {
			createGroup(1l, null);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.group.nameRequired", message);
		}
	}

	@Test
	public void GROUP_04_CreateGroup_UseNotAllowedCharacters() {
		try {
			createGroup(1l, "/()<>{}");
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.group.illegalName", message);
		}
	}

	@Test
	public void GROUP_05_CreateGroup_UseAllowedCharacters() {
		String name = "!@#$%'^&*_+?:|- ";

		Group group = createGroup(1l, name);

		// validate event
		String source = group.getId().toString();
		List<EventGrp> sourceGroupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(3, sourceGroupEvents.size());

		// validate auditlog
		String logString = "Add Group " + group.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void GROUP_06_CreateGroup_GroupNameMoreThan60Characters() {
		String name = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
		try {
			createGroup(1l, name);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.group.illegalNameLength", message);
		}
	}

	@Test
	public void GROUP_07_CreateGroup_CheckCountry() {
		String name = "CanadaGroup";
		try {
			createGroup(1l, name, 2l, "11111");
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.illegalZipCode", message);
		}
	}

	@Test
	public void GROUP_09_CreateGroup_UserNotAllowCharactorForCity() {
		String name = "group";
		try {
			createGroup(1l, name, "!@#%......&()");
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.illegalCityName", message);
		}
	}

	@Test
	public void GROUP_10_CopyGroup_NameExistInTargetGroup() throws Exception {
		// create sourceParentGroup
		String sourceGroupParentName = "SourceParent";
		Group sourceParentGroup = createGroup(1l, sourceGroupParentName);
		// create sourceGroup
		String sourceGroupName = "Source";
		Group sourceGroup = createGroup(sourceParentGroup.getId(), sourceGroupName);

		// create targetGroup
		String targetGroupName = "Target";
		Group targetGroup = createGroup(1l, targetGroupName);
		createGroup(targetGroup.getId(), sourceGroupName);

		// create sourceGroup Terminal
		String tsn = "P1111111";
		createTerminal(tsn, sourceGroup.getId());

		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { sourceGroup.getId(), targetGroup.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// sourceGroupDeploy
		createGroupDeploy(sourceGroup.getId(), pkg.getId());
		// targetGroupDeploy
		createGroupDeploy(targetGroup.getId(), pkg.getId());

		// Copy group
		try {
			CopyGroup(sourceGroup.getId(), targetGroup.getId());
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.group.targetExistSource", message);
		}
	}

	@Test
	public void GROUP_11_CopyGroup_NameNotExistInTargetGroup() throws Exception {
		// create sourceParentGroup
		String sourceGroupParentName = "SourceParent";
		Group sourceParentGroup = createGroup(1l, sourceGroupParentName);
		// create sourceGroup
		String sourceGroupName = "Source";
		Group sourceGroup = createGroup(sourceParentGroup.getId(), sourceGroupName);

		// create targetGroup
		String targetGroupName = "Target";
		Group targetGroup = createGroup(1l, targetGroupName);

		// create sourceGroup Terminal
		String tsn = "P1111111";
		createTerminal(tsn, sourceGroup.getId());

		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { sourceGroup.getId(), targetGroup.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// sourceGroupDeploy
		createGroupDeploy(sourceGroup.getId(), pkg.getId());
		// targetGroupDeploy
		createGroupDeploy(targetGroup.getId(), pkg.getId());

		// copy group
		CopyGroup(sourceGroup.getId(), targetGroup.getId());

		// validate newGroup
		Group newGroup = groupDao.getGroup(targetGroup.getId(), sourceGroupName);
		QueryCurrentGroupDeployForm command = new QueryCurrentGroupDeployForm();
		command.setGroupId(newGroup.getId());
		assertEquals(1, groupDeployDao.count(command));
		assertEquals(1, pkgDao.getpkgListByGroupId(newGroup.getId()).size());

		// validate targetGroup Temrinl
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(targetGroup.getId());
		assertEquals(new Long(1), terminalNumber);

		// validate getTerminalDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(2, terminalDeployDao.count(form));

		// validate event
		String source = sourceGroup.getId().toString();
		String target = targetGroup.getId().toString();

		List<EventGrp> sourceGroupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(4, sourceGroupEvents.size());
		List<EventGrp> targetGroupEvents = eventGrpDao.getEventsBySource(target);
		assertEquals(4, targetGroupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, terminalEvents.size());

		// validate auditlog
		String logString = "Copy Group " + sourceGroup.getNamePath() + " to Group " + targetGroup.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);

	}

	@Test
	public void GROUP_12_CopyGroup_ToSubGroup() throws Exception {
		// create sourceParentGroup
		String sourceGroupParentName = "SourceParent";
		Group sourceParentGroup = createGroup(1l, sourceGroupParentName);
		// create sourceGroup
		String sourceGroupName = "Source";
		Group sourceGroup = createGroup(sourceParentGroup.getId(), sourceGroupName);

		// create sourceGroup Terminal
		String tsn = "P1111111";
		createTerminal(tsn, sourceParentGroup.getId());

		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { sourceParentGroup.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// sourceGroupDeploy
		createGroupDeploy(sourceParentGroup.getId(), pkg.getId());

		// copy group
		try {
			CopyGroup(sourceParentGroup.getId(), sourceGroup.getId());
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.group.cannotCopyToChildGroup", message);
		}

	}

	@Test
	public void GROUP_14_MoveGroup_NameExistInTargetGroup() throws Exception {
		// create sourceParentGroup
		String sourceGroupParentName = "SourceParent";
		Group sourceParentGroup = createGroup(1l, sourceGroupParentName);
		// create sourceGroup
		String sourceGroupName = "Source";
		Group sourceGroup = createGroup(sourceParentGroup.getId(), sourceGroupName);

		// create targetGroup
		String targetGroupName = "Target";
		Group targetGroup = createGroup(1l, targetGroupName);
		createGroup(targetGroup.getId(), sourceGroupName);

		// create sourceGroup Terminal
		String tsn = "P1111111";
		createTerminal(tsn, sourceGroup.getId());

		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { sourceGroup.getId(), targetGroup.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// sourceGroupDeploy
		createGroupDeploy(sourceGroup.getId(), pkg.getId());
		// targetGroupDeploy
		createGroupDeploy(targetGroup.getId(), pkg.getId());

		// move group
		try {
			MoveGroup(sourceGroup.getId(), targetGroup.getId());
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.group.targetExistSource", message);
		}

	}

	@Test
	public void GROUP_15_MoveGroup_ToSubGroup() throws Exception {
		// create sourceParentGroup
		String sourceGroupParentName = "SourceParent";
		Group sourceParentGroup = createGroup(1l, sourceGroupParentName);
		// create sourceGroup
		String sourceGroupName = "Source";
		Group sourceGroup = createGroup(sourceParentGroup.getId(), sourceGroupName);

		// create sourceGroup Terminal
		String tsn = "P1111111";
		createTerminal(tsn, sourceParentGroup.getId());

		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { sourceParentGroup.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// sourceGroupDeploy
		createGroupDeploy(sourceParentGroup.getId(), pkg.getId());

		// move group
		try {
			MoveGroup(sourceParentGroup.getId(), sourceGroup.getId());
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.group.cannotMoveToChildGroup", message);
		}
	}

	@Test
	public void GROUP_17_MoveGroup_NameNotExistInTargetGroup() throws Exception {
		// create sourceParentGroup
		String sourceGroupParentName = "SourceParent";
		Group sourceParentGroup = createGroup(1l, sourceGroupParentName);
		// create sourceGroup
		String sourceGroupName = "Source";
		Group sourceGroup = createGroup(sourceParentGroup.getId(), sourceGroupName);

		// create targetGroup
		String targetGroupName = "Target";
		Group targetGroup = createGroup(1l, targetGroupName);

		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { sourceGroup.getId(), targetGroup.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// sourceGroupDeploy
		createGroupDeploy(sourceGroup.getId(), pkg.getId());
		// targetGroupDeploy
		createGroupDeploy(targetGroup.getId(), pkg.getId());

		// create sourceGroup Terminal
		String tsn = "P1111111";
		createTerminal(tsn, sourceGroup.getId());

		// move group
		String sourceGroupNamePath = sourceGroup.getNamePath();
		MoveGroup(sourceGroup.getId(), targetGroup.getId());

		// validate getSourceGroup Temrinl
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(targetGroup.getId());
		assertEquals(new Long(1), terminalNumber);

		// validate getTerminalDeploy
		QueryCurrentTsnDeployForm command = new QueryCurrentTsnDeployForm();
		command.setTsn(tsn);
		assertEquals(2, terminalDeployDao.count(command));

		// validate event
		String source = sourceGroup.getId().toString();
		String target = targetGroup.getId().toString();

		List<EventGrp> sourceGroupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(4, sourceGroupEvents.size());
		List<EventGrp> targetGroupEvents = eventGrpDao.getEventsBySource(target);
		assertEquals(4, targetGroupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, terminalEvents.size());

		// validate auditlog
		String logString = "Move Group " + sourceGroupNamePath + " to Group " + targetGroup.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void GROUP_18_DeleteGroup_NormalGroup() throws Exception {
		// create sourceGroup
		String sourceGroupName = "Source";
		Group sourceGroup = createGroup(1l, sourceGroupName);

		// create sourceGroup Terminal
		String tsn = "P1111111";
		createTerminal(tsn, sourceGroup.getId());

		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { sourceGroup.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// sourceGroupDeploy
		createGroupDeploy(sourceGroup.getId(), pkg.getId());

		// delet group
		deleteGroup(sourceGroup.getId());

		assertEquals(null, groupService.getExistGroup(1l, sourceGroupName));

	}

	@Test
	public void GROUP_19_DeleteGroup_FirstLevelGroup() throws Exception {
		try {
			// delet group
			deleteGroup(1l);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.group.cannotDeleteSystemGroup", message);
		}

	}

}
