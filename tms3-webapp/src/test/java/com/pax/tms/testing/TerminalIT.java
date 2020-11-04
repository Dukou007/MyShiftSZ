package com.pax.tms.testing;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.pax.tms.deploy.dao.TerminalDeployDao;
import com.pax.tms.deploy.service.GroupDeployService;
import com.pax.tms.deploy.web.form.GroupDeployForm;
import com.pax.tms.deploy.web.form.QueryCurrentTsnDeployForm;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.web.form.AddGroupForm;
import com.pax.tms.monitor.dao.EventTrmDao;
import com.pax.tms.monitor.model.EventTrm;
import com.pax.tms.report.service.TerminalDownloadService;
//import com.pax.tms.res.dao.PkgDao;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.res.web.form.AddPkgForm;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.terminal.web.form.AddTerminalForm;
import com.pax.tms.terminal.web.form.AssignTerminalForm;
import com.pax.tms.terminal.web.form.CopyTerminalForm;
import com.pax.tms.terminal.web.form.QueryTerminalForm;
import com.pax.login.TmsPac4jPrincipal;
import com.pax.tms.user.service.AuditLogService;

public class TerminalIT extends AbstractShiroTest {

	@Autowired
	private TerminalDeployDao terminalDeployDao;

	@Autowired
	private EventTrmDao eventTrmDao;

	@Autowired
	private GroupService groupService;

	@Autowired
	private TerminalService terminalService;
	@Autowired
	private TerminalDownloadService terminalDownloadService;

	@Autowired
	private PkgService pkgService;

	@Autowired
	private GroupDeployService groupDeployService;

	@Autowired
	private GroupDao groupDao;
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

	public Map<String, Object> createTerminal(String tsn, Long groupId) {
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
		return terminalService.save(command);
	}

	public void CopyTerminal(String[] tsns, long targetGroupId, long sourceGroupId) {
		AssignTerminalForm command = new AssignTerminalForm();
		command.setGroupId(sourceGroupId);
		Long[] groupIds = new Long[] { targetGroupId };
		command.setGroupIds(groupIds);
		terminalService.assign(tsns, command);
	}

	public Map<String, Object> CloneTerminal(String tsn, long targetGroupId, long sourceGroupId, String[] tsns) {
		CopyTerminalForm command = new CopyTerminalForm();
		command.setGroupId(sourceGroupId);
		command.setTargetGroupId(targetGroupId);
		command.setTsnRanges(tsns);
		return terminalService.copy(tsn, command);
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

	@Test
	public void Terminal_01_AddTerminal_TsnNotExistInSystem() throws Exception {
		// create sourcegroup
		String source = "source99";
		Group sourceGroup = createGroup(1l, source);
		// create terminal(tsn="T0000000")
		String tsn = "T0000000";
		// create group(name="test") and add terminal
		String parentName = "test99";
		Group test01 = createGroup(sourceGroup.getId(), parentName);
		createTerminal(tsn, test01.getId());
		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { sourceGroup.getId(), test01.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);
		// deploy
		createGroupDeploy(sourceGroup.getId(), pkg.getId());
		createGroupDeploy(test01.getId(), pkg.getId());
		// validate event
		List<EventTrm> sourceTrmEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, sourceTrmEvents.size());
		// validate terminal in test01
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(sourceGroup.getId());
		assertEquals(new Long(1), terminalNumber);
		// validate auditlog
		String logString = "Add Terminal " + tsn + " in Group " + test01.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
		// vaildate download report
		assertEquals(new Long(2), terminalDownloadService.getTerminalReportbyTSN(tsn));
		// vaildate getTerminalDeploy
		QueryCurrentTsnDeployForm command = new QueryCurrentTsnDeployForm();
		command.setTsn(tsn);
		assertEquals(2, terminalDeployDao.count(command));
	}

	@Test
	public void Terminal_02_AddTerminal_TsnAlreadExist() {
		// create terminal(name="00000002")
		String tsn = "00000002";
		createTerminal(tsn, 1L);

		// create terminal again(name="00000002")
		Map<String, Object> map = createTerminal(tsn, 1L);
		List<String> list = new ArrayList<String>();
		list.add(tsn);
		assertEquals(list, map.get("existTsnIgnoreToAssignGroup"));
	}

	@Test
	public void Terminal_03_AddTerminal_TsnExistInOtherGroupWithPermission() throws Exception {
		// create sourcegroup
		String source = "source3";
		Group sourceGroup = createGroup(1l, source);
		// create terminal(tsn="00000003")
		String tsn = "00000003";
		// create group(name="test03") and add terminal
		String parentName = "test03";
		Group test01 = createGroup(sourceGroup.getId(), parentName);
		createTerminal(tsn, test01.getId());
		// create group(name="test04")
		String name = "test04";
		Group test02 = createGroup(sourceGroup.getId(), name);
		createTerminal(tsn, test02.getId());
		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { sourceGroup.getId(), test01.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);
		// deploy
		createGroupDeploy(sourceGroup.getId(), pkg.getId());
		createGroupDeploy(test01.getId(), pkg.getId());
		// validate event
		List<EventTrm> sourceTrmEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, sourceTrmEvents.size());
		// validate terminal in test01
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(sourceGroup.getId());
		assertEquals(new Long(1), terminalNumber);
		// validate auditlog
		String logString = "Add Terminal " + tsn + " in Group " + test01.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
		// vaildate download report
		assertEquals(new Long(2), terminalDownloadService.getTerminalReportbyTSN(tsn));
		// vaildate getTerminalDeploy
		QueryCurrentTsnDeployForm command = new QueryCurrentTsnDeployForm();
		command.setTsn(tsn);
		assertEquals(2, terminalDeployDao.count(command));
	}

	@Test
	public void Terminal_04_AddTerminal_AddMultipleTsn() {
		String[] tsnRanges = { "00000000", "00000001" };
		AddTerminalForm command = new AddTerminalForm();
		command.setTsnRanges(tsnRanges);
		command.setDestModel("Px7");
		command.setGroupId(1L);
		command.setCountryId(1l);
		command.setProvinceId(101l);
		command.setCityName("qweq");
		command.setZipCode("11111");
		command.setTimeZone("US/Alaska");
		command.setDaylightSaving(true);
		command.setSyncToServerTime(true);
		Map<String, Object> map = terminalService.save(command);
		Set<String> list = new HashSet<String>();
		for (String tsn : tsnRanges) {
			list.add(tsn);
		}
		assertEquals(list, map.get("totalTsns"));
	}

	@Test
	public void Terminal_6_CopyTerminal_NotExistInTargetGroup() throws Exception {
		// create sourcegroup
		String source = "source6";
		Group sourceGroup = createGroup(1l, source);
		long sourceGroupId = sourceGroup.getId();
		// create terminal(tsn="00000006")
		String tsn = "00000006";
		createTerminal(tsn, sourceGroup.getId());
		// create targetGroup
		String targetGroupName = "targetGroup";
		Group targetGroup = createGroup(1l, targetGroupName);
		// terminal copy
		long targetGroupId = targetGroup.getId();
		String[] tsns = new String[] { tsn };
		CopyTerminal(tsns, targetGroupId, sourceGroupId);
		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { sourceGroup.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);
		// deploy
		createGroupDeploy(sourceGroup.getId(), pkg.getId());
		// validate event
		List<EventTrm> sourceTrmEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(2, sourceTrmEvents.size());
		// validate auditlog
		String logString = "Add Terminal " + tsn + " in Group " + sourceGroup.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
		// vaildate download report
		assertEquals(new Long(1), terminalDownloadService.getTerminalReportbyTSN(tsn));
		// vaildate getTerminalDeploy
		QueryCurrentTsnDeployForm command = new QueryCurrentTsnDeployForm();
		command.setTsn(tsn);
		assertEquals(1, terminalDeployDao.count(command));

	}

	@Test
	public void Terminal_7_CopyTerminal_MultipleTsn() {
		// create terminal(tsn="00000000")
		String[] tsns = new String[] { "00000000", "00000001" };
		for (String tsn : tsns) {
			createTerminal(tsn, 1L);
		}
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1L, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		for (String tsn : tsns) {
			createTerminal(tsn, sourceGroup.getId());
		}
		// create targetGroup
		String targetGroupName = "targetGroup";
		Group targetGroup = createGroup(1l, targetGroupName);
		// terminal copy
		long targetGroupId = targetGroup.getId();
		CopyTerminal(tsns, targetGroupId, sourceGroupId);
	}

	@Test
	public void Terminal_9_CopyTerminal_ExistInTargetGroup() {
		// create terminal(tsn="00000000")
		String[] tsns = new String[] { "00000000", "00000001" };
		for (String tsn : tsns) {
			createTerminal(tsn, 1L);
		}
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1L, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		// create targetGroup
		String targetGroupName = "targetGroup";
		Group targetGroup = createGroup(1l, targetGroupName);
		// add terminal
		for (String tsn : tsns) {
			createTerminal(tsn, sourceGroup.getId());
			createTerminal(tsn, targetGroup.getId());
		}
		// terminal copy
		long targetGroupId = targetGroup.getId();
		CopyTerminal(tsns, targetGroupId, sourceGroupId);
	}

	@Test
	public void Terminal_10_CloneTerminal_WithSameTsn() {
		// create terminal(tsn="00000000")
		String[] tsns = new String[] { "00000000" };
		String tsn = "00000000";
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		createTerminal(tsn, sourceGroup.getId());
		long sourceGroupId = sourceGroup.getId();
		// create targetGroup
		String targetGroupName = "targetGroup";
		Group targetGroup = createGroup(1l, targetGroupName);
		// terminal clone
		long targetGroupId = targetGroup.getId();
		CloneTerminal(tsn, targetGroupId, sourceGroupId, tsns);

	}

	@Test
	public void Terminal_11_CloneTerminal_WithDifferentTsn() {
		// create terminal(tsn="00000000")
		String[] tsns = new String[] { "00000001" };
		String tsn = "00000000";
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		createTerminal(tsn, sourceGroup.getId());
		long sourceGroupId = sourceGroup.getId();
		// create targetGroup
		String targetGroupName = "targetGroup";
		Group targetGroup = createGroup(1l, targetGroupName);
		createTerminal(tsn, targetGroup.getId());
		// terminal clone
		long targetGroupId = targetGroup.getId();
		CloneTerminal(tsn, targetGroupId, sourceGroupId, tsns);
	}

	@Test
	public void Terminal_13_CloneTerminal_ExistInOtherGroupWithPermission() throws Exception {
		// create terminal(tsn="00000131")
		String[] tsns = new String[] { "00000013" };
		String tsn = "00000131";
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		createTerminal(tsn, sourceGroup.getId());
		long sourceGroupId = sourceGroup.getId();
		// create targetGroup
		String targetGroupName = "targetGroup";
		Group targetGroup = createGroup(1l, targetGroupName);
		// create otherGroup
		String otherGroupName = "otherGroup";
		Group otherGroup = createGroup(targetGroup.getId(), otherGroupName);
		createTerminal(tsn, otherGroup.getId());
		// terminal clone
		long targetGroupId = targetGroup.getId();
		CloneTerminal(tsn, targetGroupId, sourceGroupId, tsns);
		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { targetGroup.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);
		// deploy
		createGroupDeploy(targetGroup.getId(), pkg.getId());
		// validate event
		List<EventTrm> sourceTrmEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, sourceTrmEvents.size());
		// validate auditlog
		String logString = "Add Terminal " + tsn + " in Group " + sourceGroup.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
		// vaildate download report
		assertEquals(new Long(1), terminalDownloadService.getTerminalReportbyTSN(tsns[0]));
		// vaildate getTerminalDeploy
		QueryCurrentTsnDeployForm command = new QueryCurrentTsnDeployForm();
		command.setTsn(tsns[0]);
		assertEquals(1, terminalDeployDao.count(command));

	}

	@Test
	public void Terminal_14_CloneTerminal_TsnNotExist() throws Exception {
		// create terminal(tsn="00000141")
		String[] tsns = new String[] { "00000014" };
		String tsn = "00000141";
		// create sourceGroup
		String sourceGroupName = "sourceGroup14";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		createTerminal(tsn, sourceGroupId);
		// create targetGroup
		String targetGroupName = "targetGroup14";
		Group targetGroup = createGroup(1l, targetGroupName);
		// terminal clone
		long targetGroupId = targetGroup.getId();
		CloneTerminal(tsn, targetGroupId, sourceGroupId, tsns);
		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { targetGroup.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);
		// deploy
		createGroupDeploy(targetGroup.getId(), pkg.getId());
		// validate event
		List<EventTrm> sourceTrmEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(1, sourceTrmEvents.size());
		// validate auditlog
		String logString = "Add Terminal " + tsn + " in Group " + sourceGroup.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
		// vaildate download report
		assertEquals(new Long(1), terminalDownloadService.getTerminalReportbyTSN(tsns[0]));
		// vaildate getTerminalDeploy
		QueryCurrentTsnDeployForm command = new QueryCurrentTsnDeployForm();
		command.setTsn(tsns[0]);
		assertEquals(1, terminalDeployDao.count(command));
	}

	@Test
	public void Terminal_15_MoveTerminal_ExistInTargetGroup() {
		// create terminal(tsn="00000000")
		String[] tsns = new String[] { "00000000" };
		String tsn = "00000000";
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		// create targetGroup
		String targetGroupName = "targetGroup";
		Group targetGroup = createGroup(1l, targetGroupName);
		createTerminal(tsn, targetGroup.getId());
		long targetGroupId = targetGroup.getId();
		// add terminal to group
		createTerminal(tsn, sourceGroupId);
		// move terminal
		QueryTerminalForm command = new QueryTerminalForm();
		command.setGroupId(sourceGroup.getId());
		command.setTargetGroupId(targetGroupId);
		terminalService.moveToGroup(tsns, command);
	}

	@Test
	public void Terminal_17_MoveTerminal_MultipleTsn() {
		// create terminal(tsn="00000000")
		String[] tsns = new String[] { "00000000", "00000001" };
		String tsn = "00000000";
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		for (String tsn2 : tsns) {
			createTerminal(tsn2, sourceGroupId);
		}
		// create targetGroup
		String targetGroupName = "targetGroup";
		Group targetGroup = createGroup(1l, targetGroupName);
		long targetGroupId = targetGroup.getId();
		// add terminal to group
		createTerminal(tsn, sourceGroupId);
		// move terminal
		QueryTerminalForm command = new QueryTerminalForm();
		command.setGroupId(sourceGroup.getId());
		command.setTargetGroupId(targetGroupId);
		terminalService.moveToGroup(tsns, command);
		// validate terminal in targetGroup
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(targetGroupId);
		assertEquals(new Long(2), terminalNumber);
	}

	@Test
	public void Terminal_18_MoveTerminal_NotExistInTargetGroup() throws Exception {
		// create terminal(tsn="00000000")
		String[] tsns = new String[] { "00000018" };
		// create sourceGroup
		String sourceGroupName = "sourceGroup18";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		for (String tsn2 : tsns) {
			createTerminal(tsn2, sourceGroup.getId());
		}
		// create targetGroup
		String targetGroupName = "targetGroup";
		Group targetGroup = createGroup(1l, targetGroupName);
		long targetGroupId = targetGroup.getId();
		// move terminal
		QueryTerminalForm command = new QueryTerminalForm();
		command.setGroupId(sourceGroup.getId());
		command.setTargetGroupId(targetGroupId);
		terminalService.moveToGroup(tsns, command);
		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { targetGroup.getId() };
		Pkg pkg = createPkg(groupIds, filePath, fileName);
		// deploy
		createGroupDeploy(targetGroup.getId(), pkg.getId());
		// validate terminal in targetGroup
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(targetGroupId);
		assertEquals(new Long(1), terminalNumber);
		// validate auditlog
		String logString = "Add Terminal " + tsns[0] + " in Group " + sourceGroup.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
		// vaildate download report
		assertEquals(new Long(1), terminalDownloadService.getTerminalReportbyTSN(tsns[0]));
		// validate event
		List<EventTrm> sourceTrmEvents = eventTrmDao.getEventsBySource(tsns[0]);
		assertEquals(2, sourceTrmEvents.size());
	}

	@Test
	public void Temrinal_19_RemoveTerminal_InNormalGroup() throws Exception {
		// create terminal(tsn="00000019")
		String[] tsns = new String[] { "00000019" };
		String tsn = "00000019";
		// create sourceGroup
		String sourceGroupName = "sourceGroup19";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		// create normalGroup
		String normalName = "normalGroup19";
		Group normalGroup = createGroup(sourceGroupId, normalName);
		long normalGroupId = normalGroup.getId();
		// add terminal
		createTerminal(tsn, normalGroupId);
		// remove terminal
		BaseForm command = new BaseForm();
		command.setGroupId(normalGroupId);
		terminalService.dismiss(tsns, command);
		// validate terminal in sourceGroup
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(normalGroupId);
		assertEquals(new Long(0), terminalNumber);
		// create Pkg
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Long[] groupIds = { normalGroupId };
		Pkg pkg = createPkg(groupIds, filePath, fileName);
		// deploy
		createGroupDeploy(sourceGroupId, pkg.getId());
		// validate event
		List<EventTrm> sourceTrmEvents = eventTrmDao.getEventsBySource(tsn);
		assertEquals(2, sourceTrmEvents.size());
		// validate auditlog
		String logString = "Remove Terminal " + tsns[0] + " in Group " + normalGroup.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);
		// vaildate download report
		assertEquals(new Long(1), terminalDownloadService.getTerminalReportbyTSN(tsns[0]));
		// vaildate getTerminalDeploy
		QueryCurrentTsnDeployForm command2 = new QueryCurrentTsnDeployForm();
		command2.setTsn(tsns[0]);
		assertEquals(1, terminalDeployDao.count(command2));
	}

	@Test
	public void Temrinal_21_RemoveTerminal_MultipleTsn() {
		// create terminal(tsn="00000020")
		String[] tsns = new String[] { "00000020", "00000021" };
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		// add terminal
		for (String tsn : tsns) {
			createTerminal(tsn, sourceGroupId);
		}
		// remove terminal
		BaseForm command = new BaseForm();
		command.setGroupId(sourceGroupId);
		terminalService.dismiss(tsns, command);
		// validate terminal in sourceGroup
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(sourceGroupId);
		assertEquals(new Long(0), terminalNumber);
	}

	@Test
	public void Termianl_22_RemoveTerminal_InEnterprise() throws Exception {
		// create terminal(tsn="00000022")
		String[] tsns = new String[] { "00000022" };
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		// add terminal
		createTerminal(tsns[0], sourceGroupId);
		// remove terminal
		BaseForm command = new BaseForm();
		command.setGroupId(sourceGroupId);
		terminalService.dismiss(tsns, command);
		// validate terminal in sourceGroup
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(sourceGroupId);
		assertEquals(new Long(0), terminalNumber);
	}

	@Test
	public void Terminal_23_DeleteTerminal_InGroupWithPermission() {
		// create terminal(tsn="00000000")
		String[] tsns = new String[] { "00000000" };
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		// add terminal
		for (String tsn : tsns) {
			createTerminal(tsn, sourceGroupId);
		}
		// validate terminal in sourceGroup
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(sourceGroupId);
		assertEquals(new Long(1), terminalNumber);
		BaseForm command = new BaseForm();
		command.setGroupId(sourceGroupId);
		terminalService.delete(tsns, command);
		// validate terminal in sourceGroup
		Long terminalNumber2 = groupService.getTerminalNumbersByGroupId(sourceGroupId);
		assertEquals(new Long(0), terminalNumber2);
	}

	@Test
	public void Terminal_25_DeleteTerminal_MultipleTsn() {
		// create terminal(tsn="00000000")
		String[] tsns = new String[] { "00000000" };
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		// add terminal
		for (String tsn : tsns) {
			createTerminal(tsn, sourceGroupId);
		}
		// validate terminal in sourceGroup
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(sourceGroupId);
		assertEquals(new Long(1), terminalNumber);
		BaseForm command = new BaseForm();
		command.setGroupId(sourceGroupId);
		terminalService.activate(tsns, command);
		// validate terminal in sourceGroup
		for (String tsn : tsns) {
			assertEquals(true, terminalService.get(tsn).isStatus());
		}

	}

	@Test
	public void Terminal_28_ActivateTerminal_MultipleTsn() {
		// create terminal(tsn="00000000")
		String[] tsns = new String[] { "00000000", "00000001" };
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		// add terminal
		for (String tsn : tsns) {
			createTerminal(tsn, sourceGroupId);
		}
		// validate terminal in sourceGroup
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(sourceGroupId);
		assertEquals(new Long(2), terminalNumber);
		BaseForm command = new BaseForm();
		command.setGroupId(sourceGroupId);
		terminalService.activate(tsns, command);
		// validate terminal in sourceGroup
		for (String tsn : tsns) {
			assertEquals(true, terminalService.get(tsn).isStatus());
		}
	}

	@Test
	public void Terminal_29_DeactivateTerminal_InGroupWithPermission() {
		// create terminal(tsn="00000000")
		String[] tsns = new String[] { "00000000" };
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		// add terminal
		for (String tsn : tsns) {
			createTerminal(tsn, sourceGroupId);
		}
		// validate terminal in sourceGroup
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(sourceGroupId);
		assertEquals(new Long(1), terminalNumber);
		BaseForm command = new BaseForm();
		command.setGroupId(sourceGroupId);
		terminalService.deactivate(tsns, command);
		// validate terminal in sourceGroup
		for (String tsn : tsns) {
			assertEquals(false, terminalService.get(tsn).isStatus());
		}
	}

	@Test
	public void Terminal_31_DeactivateTerminal_MultipleTsn() {
		// create terminal(tsn="00000000")
		String[] tsns = new String[] { "00000000" };
		// create sourceGroup
		String sourceGroupName = "sourceGroup";
		Group sourceGroup = createGroup(1l, sourceGroupName);
		long sourceGroupId = sourceGroup.getId();
		// add terminal
		for (String tsn : tsns) {
			createTerminal(tsn, sourceGroupId);
		}
		// validate terminal in sourceGroup
		Long terminalNumber = groupService.getTerminalNumbersByGroupId(sourceGroupId);
		assertEquals(new Long(1), terminalNumber);
		BaseForm command = new BaseForm();
		command.setGroupId(sourceGroupId);
		terminalService.deactivate(tsns, command);
		// validate terminal in sourceGroup
		for (String tsn : tsns) {
			assertEquals(false, terminalService.get(tsn).isStatus());
		}
	}
}
