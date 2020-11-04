package com.pax.tms.res;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.subject.Subject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.pax.login.TmsPac4jPrincipal;
import com.pax.tms.app.broadpos.para.ApplicationModule;
import com.pax.tms.app.broadpos.para.SchemaPacker;
import com.pax.tms.app.broadpos.template.ParameterEntity;
import com.pax.tms.app.broadpos.template.SchemaUtil;
import com.pax.tms.demo.AbstractShiroTest;
import com.pax.tms.deploy.dao.DeployDao;
import com.pax.tms.deploy.model.Deploy;
import com.pax.tms.deploy.service.DeployParaService;
import com.pax.tms.res.model.PkgSchema;
import com.pax.tms.res.service.PkgSchemaService;
import com.pax.tms.res.web.form.AddPkgSchemaForm;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;

public class PkgSchemaServiceTest extends AbstractShiroTest {

	@Autowired
	private PkgSchemaService schemaService;

	@Autowired
	private DeployParaService paraService;

	@Autowired
	private DeployDao deployDao;

	@Autowired
	@Qualifier("mongoTemplate")
	private MongoTemplate mongoTemplate;

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
	public void getPkgSchemaBySchemaPathTest() {
		Document doc = schemaService.getPkgSchemaBySchemaPath("group1/M00/00/2F/wKgGrlhE1yqALF3OAAObzjvFzJI078.xml");
		List<ParameterEntity> paraList = SchemaUtil.getEntityListFromSchema(doc);
		System.out.println(Json.encodePrettily(paraList));
	}

	@Test
	public void getPkgSchemaTemplateTest() throws ParseException {
		PkgSchema pkgSchema = schemaService.get(1L);
		Document doc = schemaService.getPkgSchemaBySchemaPath(pkgSchema.getFilePath());
		Map<String, String> paraMap = schemaService.getParamSetFromMongoDB(pkgSchema.getParamSet());
		System.out.println(Json.encodePrettily(paraMap));
		JsonArray jsonArr = new SchemaUtil().documentToHtml(doc, true, paraMap);
	}

	@Test
	public void getFromMongoDB() {
		String paramSetid = "71269d17-5365-4173-8e23-35097d1a6029";
		Map<String, String> map = schemaService.getParamSetFromMongoDB(paramSetid);
		System.out.println(map);
	}

	@Test
	public void readModuleDataFileFromDfsTest() {
		String moduleConfPath = "group1/M00/00/11/wKgGrlg1pqqAUl5VAAPjDydg4S4630.zip";
		Map<String, byte[]> map = ApplicationModule.readModuleDataFileFromDfs(moduleConfPath);
		System.out.println(map);
	}

	@Test
	public void saveSchemaTest() {
		AddPkgSchemaForm command = new AddPkgSchemaForm();
		command.setName("crazy");
		command.setPkgId(76L);
		schemaService.save(command);
	}

	@Test
	public void packSchema() throws IOException, DocumentException {
		String schemaPath = "group1/M00/00/13/wKgGrlg29k-AF0XEAAOb0eKKO-M763.xml";
		String paraSetId = "580ba010-baa1-4aa0-ac68-493dc09025e6";

		Document doc = schemaService.getPkgSchemaBySchemaPath(schemaPath);
		Map<String, String> paraMap = schemaService.getParamSetFromMongoDB(paraSetId);

		SchemaPacker packer = new SchemaPacker(doc);

		Map<String, String> map = packer.pack("123456", paraMap, "20161123");
		System.out.println(map);
	}

	@Test
	public void processDeployTest() {
		List<Deploy> list = deployDao.getUnDealDeployList();
		list.forEach(deploy -> paraService.processDeploy(deploy));
		// Deploy deploy = deployDao.get(6L);
		// paraService.processDeploy(deploy);
	}
}
