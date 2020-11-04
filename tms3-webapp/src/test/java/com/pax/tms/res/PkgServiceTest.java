package com.pax.tms.res;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.shiro.subject.Subject;
import org.dom4j.Document;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.pax.common.fs.FileManagerUtils;
import com.pax.fastdfs.proto.storage.DownloadCallback;
import com.pax.login.TmsPac4jPrincipal;
import com.pax.tms.demo.AbstractShiroTest;
import com.pax.tms.res.service.PkgSchemaService;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.res.web.form.AddPkgForm;

public class PkgServiceTest extends AbstractShiroTest {

	@Autowired
	private PkgService pkgService;

	@Autowired
	private PkgSchemaService schemaService;

	@Autowired
	@Qualifier("mongoTemplate")
	private MongoTemplate mongoTemplate;

	@Autowired
	@Value("${tms.terminal.real.interval:300}")
	private int realInterval;

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
	public void testValue() {
		System.out.println(realInterval);
	}

	@Test
	public void getPkgSchemaBySchemaPathTest() {
		Document doc = schemaService.getPkgSchemaBySchemaPath("group1/M00/00/0D/wKgGrlgy-HeAKFnVAAOb0VQVMlc430.xml");
		System.out.println(doc.asXML());
	}

	@Test
	public void getPkgSchemaTemplateTest() {
		Long pkgId = 74L;
		Document doc = schemaService.getPkgSchemaTemplate(pkgId);
		System.out.println(doc.asXML());
	}

	@Test
	public void saveToMongoDB() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("_id", "qwer1234");
		map.put("crazy2", "cool2");
		map.put("crazy3", "cool3");
		map.put("crazy4", "cool4");
		mongoTemplate.save(map, "crazytest");
	}

	@Test
	public void getFromMongoDB() {
		Map<String, String> dbMap = mongoTemplate.findById("753b9bf2-68fe-4cee-b947-f2fc4add72be", Map.class,
				"bposparam");
		Map<String, String> resultMap = new HashMap<String, String>(dbMap.size());
		Iterator<Entry<String, String>> it = dbMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> en = it.next();
			resultMap.put(en.getKey().replace("^", "."), en.getValue());
		}
		System.out.println(resultMap);
	}

	@Test
	public void testAdd() throws Exception {
		AddPkgForm command = new AddPkgForm();
		String filepath = "E:\\Crazy Work\\PAX-TMS-2016\\tms3design\\broadpos_parameter\\PX7-Tsys_20151201.zip";
		String filename = "PX7-Tsys_20151201.zip";
		Long[] groupIds = { 1L };
		command.setGroupIds(groupIds);
		command.setType("BroadPos");
		command.setFilePath(filepath);
		command.setFileName(filename);
		command.setDestModel("PX7L");

		pkgService.save(command);
	}

	@Test
	public void test100Add() throws Exception {
		AddPkgForm command = new AddPkgForm();
		String filepath = "C:\\Users\\crazy.w\\AppData\\Local\\Temp\\161119_admin322385064196509880\\PX7-Tsys_20151201.zip";
		String filename = "PX7-Tsys_20151201.zip";
		Long[] groupIds = { 1001L, 1002L };
		command.setGroupIds(groupIds);
		command.setType("BroadPos");
		command.setFilePath(filepath);
		command.setFileName(filename);
		command.setDestModel("PX7L");

		String downpath = "group1/M00/00/01/wKgGrlgwO0CAaFK7AA7pKsJWTKM247.zip";

		List<Thread> list = new ArrayList<Thread>();
		long start = System.currentTimeMillis();

		// ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < 200; i++) {
			Thread t = new Thread(new Runnable() {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public void run() {
					try {
						// pkgService.save(command);
						// byte[] data = FileUtils.readFileToByteArray(new
						// File(filepath));
						// FileManagerUtils.getFileManager().uploadFile(new
						// ByteArrayInputStream(data), data.length,
						// ".zip");
						FileManagerUtils.getFileManager().downloadFile(downpath, new DownloadCallback() {
							@Override
							public Object recv(InputStream ins) throws IOException {
								byte[] buff = new byte[9182];
								int c = -1;
								while ((c = ins.read(buff)) != -1) {

								}
								return null;
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			list.add(t);

			t.start();
		}

		for (Thread t : list) {
			t.join();
		}

		long end = System.currentTimeMillis();
		System.out.println("##### " + (end - start));
	}
}
