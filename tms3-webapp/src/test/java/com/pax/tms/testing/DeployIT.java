package com.pax.tms.testing;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import com.pax.tms.demo.AbstractShiroTest;
import com.pax.tms.deploy.dao.DeployDao;
import com.pax.tms.deploy.dao.GroupDeployDao;
import com.pax.tms.deploy.dao.TerminalDeployDao;
import com.pax.tms.deploy.service.GroupDeployService;
import com.pax.tms.deploy.service.TerminalDeployService;
import com.pax.tms.deploy.web.form.GroupDeployForm;
import com.pax.tms.deploy.web.form.GroupDeployOperatorForm;
import com.pax.tms.deploy.web.form.QueryCurrentGroupDeployForm;
import com.pax.tms.deploy.web.form.QueryCurrentTsnDeployForm;
import com.pax.tms.deploy.web.form.TerminalDeployForm;
import com.pax.tms.deploy.web.form.TerminalDeployOperatorForm;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.web.form.AddGroupForm;
import com.pax.tms.monitor.dao.EventGrpDao;
import com.pax.tms.monitor.dao.EventTrmDao;
import com.pax.tms.monitor.model.EventGrp;
import com.pax.tms.monitor.model.EventTrm;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.res.web.form.AddPkgForm;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.terminal.web.form.AddTerminalForm;
import com.pax.login.TmsPac4jPrincipal;
import com.pax.tms.user.service.AuditLogService;

public class DeployIT extends AbstractShiroTest {

	@Autowired
	private GroupService groupService;

	@Autowired
	private TerminalService terminalService;

	@Autowired
	private PkgService pkgService;

	@Autowired
	private GroupDeployService groupDeployService;

	@Autowired
	private TerminalDeployService terminalDeployService;

	@Autowired
	private GroupDao groupDao;

	@Autowired
	private DeployDao deployDao;

	@Autowired
	private TerminalDeployDao terminalDeployDao;

	@Autowired
	private EventGrpDao eventGrpDao;

	@Autowired
	private EventTrmDao eventTrmDao;

	@Autowired
	private GroupDeployDao groupDeployDao;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private static String path;

	@BeforeClass
	public static void initPath() throws IOException {
		File directory = new File("");
		path = directory.getCanonicalPath() + "/src/test/resources/package/";
	}

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

	public void deleteGroup(Long groupId) {
		BaseForm command = new BaseForm();
		groupService.delete(groupId, command);
	}

	public String convertDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	public String nextDay() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		String nextDay = convertDate(c.getTime());

		return nextDay;
	}

	public void createGroupDeploy(Long groupId, Long pkgId) {
		GroupDeployForm command = new GroupDeployForm();
		command.setGroupId(groupId);
		command.setPkgId(pkgId);
		command.setDestModel("Px7");
		command.setTimeZone("Canada/Pacific");
		command.setActvStartTime(convertDate(command.getRequestTime()));
		command.setActvEndTime(nextDay());
		command.setDwnlStartTime(convertDate(command.getRequestTime()));
		command.setDwnlEndTime(nextDay());
		command.setDaylightSaving(true);
		groupDeployService.deploy(command);

	}

	public void createGroupDeploy(Long groupId, Long pkgId, String terminalType) {
		GroupDeployForm command = new GroupDeployForm();
		command.setGroupId(groupId);
		command.setPkgId(pkgId);
		command.setDestModel(terminalType);
		command.setTimeZone("Canada/Pacific");
		command.setActvStartTime(convertDate(command.getRequestTime()));
		command.setActvEndTime(nextDay());
		command.setDwnlStartTime(convertDate(command.getRequestTime()));
		command.setDwnlEndTime(nextDay());
		command.setDaylightSaving(true);
		groupDeployService.deploy(command);
	}

	public void createGroupDeploy(Long groupId, Long pkgId, Date actvStrartTime, Date dwnlSatrtTime) {
		GroupDeployForm command = new GroupDeployForm();
		command.setGroupId(groupId);
		command.setPkgId(pkgId);
		command.setDestModel("Px7");
		command.setTimeZone("Canada/Pacific");
		command.setActvStartTime(convertDate(command.getRequestTime()));
		command.setActvEndTime(nextDay());
		command.setDwnlStartTime(convertDate(command.getRequestTime()));
		command.setDwnlEndTime(nextDay());
		command.setDaylightSaving(true);
		groupDeployService.deploy(command);
	}

	public void createTerminalDeploy(Long groupId, String tsn, Long pkgId) {
		TerminalDeployForm command = new TerminalDeployForm();
		command.setGroupId(groupId);
		command.setTsn(tsn);
		command.setPkgId(pkgId);
		command.setTimeZone("Canada/Pacific");
		command.setActvStartTime(convertDate(command.getRequestTime()));
		command.setActvEndTime(nextDay());
		command.setDwnlStartTime(convertDate(command.getRequestTime()));
		command.setDwnlEndTime(nextDay());
		command.setDaylightSaving(true);
		terminalDeployService.deploy(command);
	}

	public void createTerminalDeploy(Long groupId, String tsn, Long pkgId, Date actvStrartTime, Date dwnlSatrtTime) {
		TerminalDeployForm command = new TerminalDeployForm();
		command.setGroupId(groupId);
		command.setTsn(tsn);
		command.setPkgId(pkgId);
		command.setTimeZone("Canada/Pacific");
		command.setActvStartTime(convertDate(command.getRequestTime()));
		command.setActvEndTime(nextDay());
		command.setDwnlStartTime(convertDate(command.getRequestTime()));
		command.setDwnlEndTime(nextDay());
		command.setDaylightSaving(true);
		terminalDeployService.deploy(command);
	}

	public void deleteGroupDeploy(Long deployId, Long groupId, Boolean isInherit) {
		GroupDeployOperatorForm command = new GroupDeployOperatorForm();
		command.setDeployId(deployId);
		command.setGroupId(groupId);
		command.setInherit(isInherit);
		groupDeployService.delete(command);
	}

	public void deleteTerminalDeploy(Long deployId, String tsn) {
		TerminalDeployOperatorForm command = new TerminalDeployOperatorForm();
		command.setDeployId(deployId);
		command.setTsn(tsn);
		terminalDeployService.delete(command);
	}

	@Test
	public void DEPLOY_02_AddDeployment_Group_InGroupWithPermission() throws Exception {
		// create group
		Group group = createGroup(1l, "IT");

		// create child group
		Group childGroup = createGroup(group.getId(), "ITChild");

		// create terminal
		String tsn = "D0000002";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// groupDeploy
		createGroupDeploy(group.getId(), pkg.getId());

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		QueryCurrentGroupDeployForm command = new QueryCurrentGroupDeployForm();
		command.setGroupId(childGroup.getId());
		assertEquals(1, groupDeployDao.count(command));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(4, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Group "
				+ group.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);

	}

	@Test
	public void DEPLOY_03_AddDeployment_Group_WithMultiplePackage() throws Exception {
		// create group
		Group group = createGroup(1l, "IT03");

		// create child group
		Group childGroup = createGroup(group.getId(), "ITChild03");

		// create terminal
		String tsn = "D0000003";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// groupDeploy
		createGroupDeploy(group.getId(), pkg.getId());
		createGroupDeploy(group.getId(), pkg.getId());

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(2, terminalDeployDao.count(form));

		QueryCurrentGroupDeployForm command = new QueryCurrentGroupDeployForm();
		command.setGroupId(childGroup.getId());
		assertEquals(2, groupDeployDao.count(command));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(5, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Group "
				+ group.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_04_AddDeployment_Group_WithTerminalType() throws Exception {
		// create group
		Group group = createGroup(1l, "IT04");

		// create child group
		Group childGroup = createGroup(group.getId(), "ITChild04");

		// create terminal
		String tsn = "D0000003";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// groupDeploy
		createGroupDeploy(group.getId(), pkg.getId(), null);

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		QueryCurrentGroupDeployForm command = new QueryCurrentGroupDeployForm();
		command.setGroupId(childGroup.getId());
		assertEquals(1, groupDeployDao.count(command));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(4, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Group "
				+ group.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_05_AddDeployment_Group_WithoutTerminalType() throws Exception {
		// create group
		Group group = createGroup(1l, "IT5");

		// create child group
		Group childGroup = createGroup(group.getId(), "ITChild5");

		// create terminal
		String tsn = "D0000005";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// groupDeploy
		createGroupDeploy(group.getId(), pkg.getId(), "Px7");

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		QueryCurrentGroupDeployForm command = new QueryCurrentGroupDeployForm();
		command.setGroupId(childGroup.getId());
		assertEquals(1, groupDeployDao.count(command));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(4, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Group "
				+ group.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_07_AddDeployment_Group_WithoutReqiredField() throws Exception {
		// create group
		Group group = createGroup(1l, "IT");

		// create terminal
		String tsn = "11111111";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// groupDeploy
		try {
			createGroupDeploy(null, pkg.getId());
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.group.groupRequired", message);
		}

	}

	@Test
	public void DEPLOY_08_AddDeployment_Group_SamePackageDifferentTime() throws Exception {
		// create group
		Group group = createGroup(1l, "IT8");

		// create child group
		Group childGroup = createGroup(group.getId(), "ITChild8");

		// create terminal
		String tsn = "D0000008";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// groupDeploy
		createGroupDeploy(group.getId(), pkg.getId());
		createGroupDeploy(group.getId(), pkg.getId());

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(2, terminalDeployDao.count(form));

		QueryCurrentGroupDeployForm command = new QueryCurrentGroupDeployForm();
		command.setGroupId(childGroup.getId());
		assertEquals(2, groupDeployDao.count(command));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(5, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Group "
				+ group.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_09_AddDeployment_Group_SelectPastTime() throws Exception {
		// create group
		Group group = createGroup(1l, "IT9");

		// create child group
		Group childGroup = createGroup(group.getId(), "ITChild9");

		// create terminal
		String tsn = "D0000009";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// groupDeploy
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);// date 换成已经已知的Date对象
		cal.add(Calendar.HOUR_OF_DAY, -8);// before 8 hour
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date olderTime = formatter.parse(formatter.format(cal.getTime()));

		createGroupDeploy(group.getId(), pkg.getId(), olderTime, olderTime);

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		QueryCurrentGroupDeployForm command = new QueryCurrentGroupDeployForm();
		command.setGroupId(childGroup.getId());
		assertEquals(1, groupDeployDao.count(command));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(4, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Group "
				+ group.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_10_AddDeployment_Group_SelectNow() throws Exception {
		// create group
		Group group = createGroup(1l, "IT10");

		// create child group
		Group childGroup = createGroup(group.getId(), "ITChild10");

		// create terminal
		String tsn = "D0000010";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// groupDeploy
		createGroupDeploy(group.getId(), pkg.getId());

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		QueryCurrentGroupDeployForm command = new QueryCurrentGroupDeployForm();
		command.setGroupId(childGroup.getId());
		assertEquals(1, groupDeployDao.count(command));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(4, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Group "
				+ group.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_11_AddDeployment_Group_SelectFuture() throws Exception {
		// create group
		Group group = createGroup(1l, "IT11");

		// create child group
		Group childGroup = createGroup(group.getId(), "ITChild11");

		// create terminal
		String tsn = "D0000011";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// groupDeploy
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);// date 换成已经已知的Date对象
		cal.add(Calendar.HOUR_OF_DAY, +8);// before 8 hour
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date olderTime = formatter.parse(formatter.format(cal.getTime()));

		createGroupDeploy(group.getId(), pkg.getId(), olderTime, olderTime);

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		QueryCurrentGroupDeployForm command = new QueryCurrentGroupDeployForm();
		command.setGroupId(childGroup.getId());
		assertEquals(1, groupDeployDao.count(command));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(4, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Group "
				+ group.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_12_AddDeployment_Terminal_UsePackgeInCurrentGroup() throws Exception {
		// create group
		Group group = createGroup(1l, "IT");

		// create terminal
		String tsn = "D0000022";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// termianlDeploy
		createTerminalDeploy(group.getId(), tsn, pkg.getId());

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(3, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(2, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Terminal " + tsn;
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_13_AddDeployment_Terminal_UsePackgeInOtherGroup() throws Exception {
		// create group
		Group group = createGroup(1l, "IT13");

		// create other group
		Group otherGroup = createGroup(1l, "Other");

		// create terminal
		String tsn = "D0000013";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { otherGroup.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		try {
			// terminalDeploy
			createTerminalDeploy(group.getId(), tsn, pkg.getId());
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.pkg.notGrantedPkg", message);
		}
	}

	@Test
	public void DEPLOY_15_AddDeployment_Terminal_WithMultiplePackage() throws Exception {
		// create group
		Group group = createGroup(1l, "IT");

		// create terminal
		String tsn = "D0000015";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// termianlDeploy
		createTerminalDeploy(group.getId(), tsn, pkg.getId());
		createTerminalDeploy(group.getId(), tsn, pkg.getId());

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(2, terminalDeployDao.count(form));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(3, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(3, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Terminal " + tsn;
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_16_AddDeployment_Terminal_SamePackageDifferentTime() throws Exception {
		// create group
		Group group = createGroup(1l, "IT16");

		// create terminal
		String tsn = "D0000016";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// termianlDeploy
		createTerminalDeploy(group.getId(), tsn, pkg.getId());
		createTerminalDeploy(group.getId(), tsn, pkg.getId());

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(2, terminalDeployDao.count(form));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(3, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(3, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Terminal " + tsn;
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_19_AddDeployment_Terminal_WithoutRequiredField() throws Exception {
		// create group
		Group group = createGroup(1l, "IT");

		// create terminal
		String tsn = "11111111";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// termianlDeploy
		try {
			createTerminalDeploy(group.getId(), null, pkg.getId());
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("tsn.Required", message);
		}

	}

	@Test
	public void DEPLOY_20_AddDeployment_Terminal_SelectPastTime() throws Exception {
		// create group
		Group group = createGroup(1l, "IT20");

		// create terminal
		String tsn = "D0000020";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// termianlDeploy
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);// date 换成已经已知的Date对象
		cal.add(Calendar.HOUR_OF_DAY, -8);// before 8 hour
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date olderTime = formatter.parse(formatter.format(cal.getTime()));
		createTerminalDeploy(group.getId(), tsn, pkg.getId(), olderTime, olderTime);

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(3, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(2, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Terminal " + tsn;
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_21_AddDeployment_Terminal_SelectNow() throws Exception {
		// create group
		Group group = createGroup(1l, "IT21");

		// create terminal
		String tsn = "D0000021";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// termianlDeploy
		createTerminalDeploy(group.getId(), tsn, pkg.getId());

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(3, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(2, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Terminal " + tsn;
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_22_AddDeployment_Terminal_SelectFuture() throws Exception {
		// create group
		Group group = createGroup(1l, "IT22");

		// create terminal
		String tsn = "D0000022";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// termianlDeploy
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);// date 换成已经已知的Date对象
		cal.add(Calendar.HOUR_OF_DAY, +8);// before 8 hour
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date olderTime = formatter.parse(formatter.format(cal.getTime()));
		createTerminalDeploy(group.getId(), tsn, pkg.getId(), olderTime, olderTime);

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(3, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(2, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Terminal " + tsn;
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
	}

	@Test
	public void DEPLOY_24_DeleteDeployment_Group_OwnDeployment() throws Exception {
		// create group
		Group group = createGroup(1l, "IT24");

		// create terminal
		String tsn = "D0000024";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// groupDeploy
		createGroupDeploy(group.getId(), pkg.getId());

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(4, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Group "
				+ group.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);

		groupDeployService.deleteByGroup(group.getId());

		// validate delete
		assertEquals(0, groupDeployDao.getGroupDeploys(group.getId()).size());

	}

	@Test
	public void DEPLOY_25_DeleteDeployment_Group_InheritDeployment() throws Exception {
		// create group
		Group group = createGroup(1l, "IT25");

		// create child group
		Group childGroup = createGroup(group.getId(), "ITChild25");

		// create terminal
		String tsn = "D0000025";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// groupDeploy
		createGroupDeploy(group.getId(), pkg.getId());

		List<Long> deployIds = deployDao.getTerminalInheritDeployIds(group.getId());

		try {
			deleteGroupDeploy(deployIds.get(0), childGroup.getId(), true);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("tsn.InheritDeployTask", message);
		}

	}

	@Test
	public void DEPLOY_26_DeleteDeployment_Terminal_OwnDeployment() throws Exception {
		// create group
		Group group = createGroup(1l, "IT26");

		// create terminal
		String tsn = "D0000026";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// termianlDeploy
		createTerminalDeploy(group.getId(), tsn, pkg.getId());

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(3, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(2, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Terminal " + tsn;
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);

		deleteTerminalDeploy(terminalDeployDao.getLatedDeployId(tsn).get(0), tsn);

		// validate delete
		assertEquals(0, terminalDeployDao.getLatedDeployId(tsn).size());
	}

	@Test
	public void DEPLOY_27_DeleteDeployment_Terminal_InheritDeployment() throws Exception {
		// create group
		Group group = createGroup(1l, "IT27");

		// create child group
		Group childGroup = createGroup(group.getId(), "ITChild27");

		// create terminal
		String tsn = "D0000027";
		createTerminal(tsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// groupDeploy
		createGroupDeploy(group.getId(), pkg.getId());

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		QueryCurrentGroupDeployForm command = new QueryCurrentGroupDeployForm();
		command.setGroupId(childGroup.getId());
		assertEquals(1, groupDeployDao.count(command));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(4, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Group "
				+ group.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);

		deleteTerminalDeploy(terminalDeployDao.getLatedDeployId(tsn).get(0), tsn);

		// validate delete
		assertEquals(0, terminalDeployDao.getLatedDeployId(tsn).size());
	}

	@Test
	public void DEPLOY_28_DeleteDeployment_Terminal_NoPermissionTsn() throws Exception {
		// create group
		Group group = createGroup(1l, "IT28");

		// create terminal
		String tsn = "D0000028";
		createTerminal(tsn, group.getId());

		// create OperateTerminal
		String operateTsn = "D0000228";
		createTerminal(operateTsn, group.getId());

		// create Pkg
		Long[] groupIds = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// termianlDeploy
		createTerminalDeploy(group.getId(), tsn, pkg.getId());

		// validate getDeploy
		QueryCurrentTsnDeployForm form = new QueryCurrentTsnDeployForm();
		form.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(form));

		// validate event
		String source = group.getId().toString();

		List<EventGrp> groupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(3, groupEvents.size());

		List<EventTrm> terminalEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(2, terminalEvents.size());

		// validate auditlog
		String logString = "Deploy Package " + pkg.getName() + ", Version " + pkg.getVersion() + " to Terminal " + tsn;
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
		try {
			deleteTerminalDeploy(terminalDeployDao.getLatedDeployId(tsn).get(0), operateTsn);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("tsn.noPermissionOperator", message);
		}

	}

}
