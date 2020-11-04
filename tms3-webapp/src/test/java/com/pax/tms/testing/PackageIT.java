package com.pax.tms.testing;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.Subject;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;

import com.pax.common.web.form.BaseForm;
import com.pax.tms.demo.AbstractShiroTest;
import com.pax.tms.deploy.service.GroupDeployService;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.web.form.AddGroupForm;
import com.pax.tms.monitor.dao.EventGrpDao;
import com.pax.tms.monitor.model.EventGrp;
//import com.pax.tms.res.dao.PkgDao;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.res.service.PkgUsageInfoService;
import com.pax.tms.res.web.form.AddPkgForm;
import com.pax.tms.res.web.form.AssignPkgForm;
import com.pax.tms.terminal.service.TerminalService;
import com.pax.tms.terminal.web.form.AddTerminalForm;
import com.pax.login.TmsPac4jPrincipal;
import com.pax.tms.user.service.AuditLogService;

public class PackageIT extends AbstractShiroTest {

	@Autowired
	private GroupService groupService;

	@Autowired
	private TerminalService terminalService;

	@Autowired
	private PkgService pkgService;

	// @Autowired
	// private PkgDao pkgDao;

	@Autowired
	private GroupDeployService groupDeployService;

	@Autowired
	private AuditLogService auditLogService;

	@Autowired
	private GroupDao groupDao;

	@Autowired
	private EventGrpDao eventGrpDao;

	@Autowired
	HibernateTransactionManager htManager;

	@Autowired
	private static String path;

	@Autowired
	private PkgUsageInfoService pkgUsageInfoService;

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

	public void deletePkgs(List<Long> pkgIdList) {
		BaseForm command = new BaseForm();
		pkgService.delete(pkgIdList, command);
	}

	public void deactivePkgs(Long[] pkgIds) {
		BaseForm command = new BaseForm();
		pkgService.deactivate(pkgIds, command);
	}

	public void activePkgs(Long[] pkgIds) {
		BaseForm command = new BaseForm();
		pkgService.activate(pkgIds, command);
	}

	public void assignPkgs(Long[] pkgIds, Long[] groupIds) {
		AssignPkgForm command = new AssignPkgForm();
		command.setGroupIds(groupIds);
		pkgService.assign(pkgIds, command);
	}

	public void deleteGroup(Long groupId) {
		BaseForm command = new BaseForm();
		groupService.delete(groupId, command);
	}

	@Test
	public void testDeleteDeploy() {
		Date when = new Date();
		groupDeployService.deleteOverDueDeployment(when);
	}

	@Test
	public void test() {
		String age = "1";
		if (StringUtils.isEmpty(age)) {
			return;
		}
		int month = Integer.parseInt(age);
		if (month <= 0) {
			return;
		}
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -month);
		Date when = cal.getTime();

		pkgUsageInfoService.clearPkgAndUsageInfo(when);

	}

	@Test
	public void Package_01_AddPackage_InGroupWithPermission() {
		String groupName = "test";
		Group group = createGroup(1l, groupName);

		Long groupIds[] = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		try {
			Pkg pkg = createPkg(groupIds, filePath, fileName);

			// validate event
			String source = group.getId().toString();
			List<EventGrp> sourceGroupEvents = eventGrpDao.getEventsBySource(source);
			assertEquals(3, sourceGroupEvents.size());

			// validate auditlog
			String logString = "Add Package " + pkg.getName() + ", Version " + pkg.getVersion() + ", assign Group "
					+ group.getNamePath();
			Boolean isHaved = auditLogService.checkAuditLogs(logString);
			assertEquals(new Boolean(true), isHaved);

			// validate groupPath
			List<String> groupNames = pkgService.getGroupNames(pkg.getId());
			assertEquals(groupNames.get(1), group.getNamePath());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void Package_03_AddPackage_SelectOneGroup() throws Exception {
		String groupName = "test";
		Group group = createGroup(1l, groupName);

		Long groupIds[] = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = createPkg(groupIds, filePath, fileName);

		// validate event
		String source = group.getId().toString();
		List<EventGrp> sourceGroupEvents = eventGrpDao.getEventsBySource(source);
		assertEquals(3, sourceGroupEvents.size());

		// validate auditlog
		String logString = "Add Package " + pkg.getName() + ", Version " + pkg.getVersion() + ", assign Group "
				+ group.getNamePath();
		Boolean isHaved = auditLogService.checkAuditLogs(logString);
		assertEquals(new Boolean(true), isHaved);

		// validate groupPath
		List<String> groupNames = pkgService.getGroupNames(pkg.getId());
		assertEquals(groupNames.get(1), group.getNamePath());

	}

	@Test
	public void Package_04_AddPackage_SelectMultipleGroup() {
		Group group1 = createGroup(1l, "group1");
		Group group2 = createGroup(1l, "group2");
		Group group3 = createGroup(1l, "group3");
		Long groupIds[] = { group1.getId(), group2.getId(), group3.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		try {
			Pkg pkg = createPkg(groupIds, filePath, fileName);
			// validate event
			String source1 = group1.getId().toString();
			List<EventGrp> sourceGroupEvents1 = eventGrpDao.getEventsBySource(source1);
			assertEquals(3, sourceGroupEvents1.size());

			String source2 = group2.getId().toString();
			List<EventGrp> sourceGroupEvents2 = eventGrpDao.getEventsBySource(source2);
			assertEquals(3, sourceGroupEvents2.size());

			String source3 = group3.getId().toString();
			List<EventGrp> sourceGroupEvents3 = eventGrpDao.getEventsBySource(source3);
			assertEquals(3, sourceGroupEvents3.size());

			// validate auditlog
			String logString1 = "Add Package " + pkg.getName() + ", Version " + pkg.getVersion() + ", assign Group "
					+ group1.getNamePath();
			Boolean isHaved1 = auditLogService.checkAuditLogs(logString1);
			assertEquals(new Boolean(true), isHaved1);

			String logString2 = "Add Package " + pkg.getName() + ", Version " + pkg.getVersion() + ", assign Group "
					+ group2.getNamePath();
			Boolean isHaved2 = auditLogService.checkAuditLogs(logString2);
			assertEquals(new Boolean(true), isHaved2);

			String logString3 = "Add Package " + pkg.getName() + ", Version " + pkg.getVersion() + ", assign Group "
					+ group3.getNamePath();
			Boolean isHaved3 = auditLogService.checkAuditLogs(logString3);
			assertEquals(new Boolean(true), isHaved3);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void Package_05_AddPackage_WithPackageCombo() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName = "ComboOS2510_4916RMain1004_1521TWHLoffPxR96TPxU1005POS_M98_12052016.zip";
		String filePath = path + fileName;
		try {
			Pkg pkg = createPkg(groupIds, filePath, fileName);

			// validate event
			String source = group.getId().toString();
			List<EventGrp> sourceGroupEvents = eventGrpDao.getEventsBySource(source);
			assertEquals(3, sourceGroupEvents.size());

			// validate auditlog
			String logString = "Add Package " + pkg.getName() + ", Version " + pkg.getVersion() + ", assign Group "
					+ group.getNamePath();
			Boolean isHaved = auditLogService.checkAuditLogs(logString);
			assertEquals(new Boolean(true), isHaved);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void Package_06_AddPackage_AlreadyExist() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		try {
			createPkg(groupIds, filePath, fileName);
			createPkg(groupIds, filePath, fileName);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.pkg.existNameAndVersion", message);
		}
	}

	@Test
	public void Package_07_AddPackage_WithDifferentType() throws Exception {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName1 = "Uninstall_All_Forms.zip";
		String filePath1 = path + fileName1;

		String fileName2 = "Uninstall_All_Forms.aip";
		String filePath2 = path + fileName2;

		String fileName3 = "Uninstall_All_Forms.bin";
		String filePath3 = path + fileName3;

		String fileName4 = "Uninstall_All_Forms.docx";
		String filePath4 = path + fileName4;

		String fileName5 = "Uninstall_All_Forms.xlsx";
		String filePath5 = path + fileName5;

		String fileName6 = "Uninstall_All_Forms.txt";
		String filePath6 = path + fileName6;
		createPkg(groupIds, filePath1, fileName1);
		try {
			createPkg(groupIds, filePath2, fileName2);
		} catch (Exception e) {
		}
		try {
			createPkg(groupIds, filePath3, fileName3);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.phonenixPackage.invalidPackageFormat", message);
		}
		try {
			createPkg(groupIds, filePath4, fileName4);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.phonenixPackage.invalidPackageFormat", message);
		}
		try {
			createPkg(groupIds, filePath5, fileName5);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.phonenixPackage.invalidPackageFormat", message);
		}
		try {
			createPkg(groupIds, filePath6, fileName6);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.phonenixPackage.invalidPackageFormat", message);
		}

	}

	@Test
	public void Package_08_AddPackage_WithIllegalZipFile() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		try {
			createPkg(groupIds, filePath, fileName);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.phonenixPackage.wrongFileNumber", message);
		}
	}

	@Test
	public void Package_09_AddPackage_NotInputRequestField() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		try {
			createPkg(null, filePath, fileName);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.group.groupRequired", message);
		}
		try {
			createPkg(groupIds, null, null);
		} catch (Exception e) {
			String message = e.getMessage();
			assertEquals("msg.packageFilePath.required", message);
		}
	}

	@Test
	public void Package_11_DeletePackage_InGroupWithPermission() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = new Pkg();
		try {
			pkg = createPkg(groupIds, filePath, fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Long pkgId = pkg.getId();
		List<Long> pkgIdList = new ArrayList<>();
		pkgIdList.add(pkgId);
		deletePkgs(pkgIdList);
		List<String> pkgNameList = pkgService.getPkgNamesByGroupId(group.getId(), "Multilane");
		assertEquals(0, pkgNameList.size());
	}

	@Test
	public void Package_13_DeletePackage_OneGroup() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = new Pkg();
		try {
			pkg = createPkg(groupIds, filePath, fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Long pkgId = pkg.getId();
		List<Long> pkgIdList = new ArrayList<>();
		pkgIdList.add(pkgId);
		deletePkgs(pkgIdList);
		List<String> pkgNameList = pkgService.getPkgNamesByGroupId(group.getId(), "Multilane");
		assertEquals(0, pkgNameList.size());
	}

	@Test
	public void Package_14_DeletePackage_MultipleGroup() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName1 = "Uninstall_All_Forms.zip";
		String filePath1 = path + fileName1;

		String fileName2 = "platform-phoenix-1.0.04.1508T_sign_appsigned.zip";
		String filePath2 = path + fileName2;

		Pkg pkg1 = new Pkg();
		Pkg pkg2 = new Pkg();
		try {
			pkg1 = createPkg(groupIds, filePath1, fileName1);
			pkg2 = createPkg(groupIds, filePath2, fileName2);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Long pkgId1 = pkg1.getId();
		Long pkgId2 = pkg2.getId();
		List<Long> pkgIdList = new ArrayList<>();
		pkgIdList.add(pkgId1);
		pkgIdList.add(pkgId2);
		deletePkgs(pkgIdList);
		List<String> pkgNameList = pkgService.getPkgNamesByGroupId(group.getId(), "Multilane");
		assertEquals(0, pkgNameList.size());
	}

	@Test
	public void Package_15_DeactivatePackage_InGroupWithPermission() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = new Pkg();
		try {
			pkg = createPkg(groupIds, filePath, fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Long pkgId = pkg.getId();
		Long pkgIds[] = { pkgId };
		deactivePkgs(pkgIds);
		// assertEquals(pkg.isStatus(), false);
	}

	@Test
	public void Package_17_DeactivatePackage_OneGroup() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = new Pkg();
		try {
			pkg = createPkg(groupIds, filePath, fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Long pkgId = pkg.getId();
		Long pkgIds[] = { pkgId };
		deactivePkgs(pkgIds);
		// assertEquals(pkg.isStatus(), false);
	}

	@Test
	public void Package_22_ActivatePackage_MultipleGroup() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName1 = "Uninstall_All_Forms.zip";
		String filePath1 = path + fileName1;

		String fileName2 = "platform-phoenix-1.0.04.1508T_sign_appsigned.zip";
		String filePath2 = path + fileName2;

		Pkg pkg1 = new Pkg();
		Pkg pkg2 = new Pkg();
		try {
			pkg1 = createPkg(groupIds, filePath1, fileName1);
			pkg2 = createPkg(groupIds, filePath2, fileName2);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Long pkgId1 = pkg1.getId();
		Long pkgId2 = pkg2.getId();
		Long[] pkgIds = { pkgId1, pkgId2 };
		deactivePkgs(pkgIds);
		// assertEquals(pkg1.isStatus(), false);
		// assertEquals(pkg2.isStatus(), false);
		activePkgs(pkgIds);
		// assertEquals(pkg1.isStatus(), true);
		// assertEquals(pkg2.isStatus(), true);
	}

	@Test
	public void Package_19_ActivatePackage_InGroupWithPermission() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = new Pkg();
		try {
			pkg = createPkg(groupIds, filePath, fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Long pkgId = pkg.getId();
		Long pkgIds[] = { pkgId };
		deactivePkgs(pkgIds);
		// assertEquals(pkg.isStatus(), false);
		activePkgs(pkgIds);
		// assertEquals(pkg.isStatus(), true);
	}

	@Test
	public void Package_21_ActivatePackage_OneGroup() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = new Pkg();
		try {
			pkg = createPkg(groupIds, filePath, fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Long pkgId = pkg.getId();
		Long pkgIds[] = { pkgId };
		deactivePkgs(pkgIds);
		// assertEquals(pkg.isStatus(), false);
		activePkgs(pkgIds);
		// assertEquals(pkg.isStatus(), true);
	}

	@Test
	public void Package_22_DeactivatePackage_MultipleGroup() {
		Group group = createGroup(1l, "testGroup");
		Long groupIds[] = { group.getId() };
		String fileName1 = "Uninstall_All_Forms.zip";
		String filePath1 = path + fileName1;

		String fileName2 = "platform-phoenix-1.0.04.1508T_sign_appsigned.zip";
		String filePath2 = path + fileName2;

		Pkg pkg1 = new Pkg();
		Pkg pkg2 = new Pkg();
		try {
			pkg1 = createPkg(groupIds, filePath1, fileName1);
			pkg2 = createPkg(groupIds, filePath2, fileName2);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Long pkgId1 = pkg1.getId();
		Long pkgId2 = pkg2.getId();
		Long[] pkgIds = { pkgId1, pkgId2 };
		deactivePkgs(pkgIds);
		// assertEquals(pkg1.isStatus(), false);
		// assertEquals(pkg2.isStatus(), false);
	}

	@Test
	public void Package_24_AssignPackage_InGroupWithoutPermission() {
		Group group1 = createGroup(1l, "testGroup");
		Group group2 = createGroup(1l, "testGroup2");
		Long groupIds1[] = { group1.getId() };
		Long groupIds2[] = { group2.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = new Pkg();
		try {
			pkg = createPkg(groupIds1, filePath, fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Long pkgId = pkg.getId();
		Long pkgIds[] = { pkgId };
		assignPkgs(pkgIds, groupIds2);
		List<String> groupNames = pkgService.getGroupNames(pkg.getId());
		assertEquals(groupNames.get(2), group2.getNamePath());
	}

	@Test
	public void Package_25_AssignPackage_OneGroup() {
		Group group1 = createGroup(1l, "testGroup");
		Group group2 = createGroup(1l, "testGroup2");
		Long groupIds1[] = { group1.getId() };
		Long groupIds2[] = { group2.getId() };
		String fileName = "Uninstall_All_Forms.zip";
		String filePath = path + fileName;
		Pkg pkg = new Pkg();
		try {
			pkg = createPkg(groupIds1, filePath, fileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Long pkgId = pkg.getId();
		Long pkgIds[] = { pkgId };
		assignPkgs(pkgIds, groupIds2);
		List<String> groupNames = pkgService.getGroupNames(pkg.getId());
		assertEquals(groupNames.get(2), group2.getNamePath());
	}

	@Test
	public void Package_26_AssignPackage_MultipleGroup() {
		Group group1 = createGroup(1l, "testGroup");
		Group group2 = createGroup(1l, "testGroup2");
		Group group3 = createGroup(1l, "testGroup3");
		Long groupIds1[] = { group1.getId() };
		Long groupIds2[] = { group2.getId(), group3.getId() };
		String fileName1 = "Uninstall_All_Forms.zip";
		String filePath1 = path + fileName1;

		String fileName2 = "Uninstall_PXRetailer_Forms.zip";
		String filePath2 = path + fileName2;

		String fileName3 = "platform-phoenix-1.0.04.1508T_sign_appsigned.zip";
		String filePath3 = path + fileName3;

		Pkg pkg1 = new Pkg();
		Pkg pkg2 = new Pkg();
		Pkg pkg3 = new Pkg();
		try {
			pkg1 = createPkg(groupIds1, filePath1, fileName1);
			pkg2 = createPkg(groupIds1, filePath2, fileName2);
			pkg3 = createPkg(groupIds1, filePath3, fileName3);

		} catch (Exception e) {
			e.printStackTrace();
		}
		Long pkgId1 = pkg1.getId();
		Long pkgId2 = pkg2.getId();
		Long pkgId3 = pkg3.getId();
		Long pkgIds[] = { pkgId1, pkgId2, pkgId3 };
		assignPkgs(pkgIds, groupIds2);
		List<String> groupNames1 = pkgService.getGroupNames(pkg1.getId());
		List<String> groupNames2 = pkgService.getGroupNames(pkg2.getId());
		List<String> groupNames3 = pkgService.getGroupNames(pkg3.getId());
		assertEquals(groupNames1.get(2), group2.getNamePath());
		assertEquals(groupNames1.get(3), group3.getNamePath());
		assertEquals(groupNames2.get(2), group2.getNamePath());
		assertEquals(groupNames3.get(3), group3.getNamePath());
	}
}
