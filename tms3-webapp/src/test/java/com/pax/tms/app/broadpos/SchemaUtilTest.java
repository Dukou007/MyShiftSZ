package com.pax.tms.app.broadpos;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pax.tms.app.broadpos.template.SchemaUtil;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class SchemaUtilTest {
	static Document doc;

	public static void readDocument(String xmlpath) throws Exception {
		File file = new File(xmlpath);
		String textxml = FileUtils.readFileToString(file, "UTF-8");
		doc = DocumentHelper.parseText(textxml);
	}

	@Test
	public void testSchema() throws Exception {
		String xmlPath = "E:\\temp\\schema.xml";
		readDocument(xmlPath);
		Map<String, String> inputMap = new HashMap<String, String>();
		inputMap.put("sys.cap.receiptHeader4", "crazy");
		inputMap.put("sys.cap.receiptTrailer0", "crazy");
		
		JsonArray jsonArr = new SchemaUtil().documentToHtml(doc, true, inputMap);
		System.out.println(jsonArr);
	}

	@Test
	public void testHeader() throws Exception {
		String xmlPath = "E:\\temp\\testheader.xml";
		readDocument(xmlPath);
		Element root = doc.getRootElement();
		Element header = root.element("Header");
		List<Element> paramElements = header.elements("Parameter");
		String result = new SchemaUtil().drawHtmlForHeader(header, paramElements, false);
		System.out.println(result);
	}

	@Test
	public void testPara() throws Exception {
		String xmlPath = "E:\\temp\\testparameter.xml";
		readDocument(xmlPath);
		Element root = doc.getRootElement();
		List<Element> paramElements = root.elements("Parameter");
		String result = new SchemaUtil().drawHtmlForParam(paramElements.get(0));
		System.out.println(result);
	}

	@Test
	public void testPath() {
		System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));

		System.out.println(SchemaUtilTest.class.getClassLoader().getResource(""));

		System.out.println(ClassLoader.getSystemResource(""));
		System.out.println(SchemaUtilTest.class.getResource(""));
		System.out.println(SchemaUtilTest.class.getResource("/")); // Class文件所在路径
		System.out.println(new File("/").getAbsolutePath());
		System.out.println(System.getProperty("user.dir"));
	}
	
}
