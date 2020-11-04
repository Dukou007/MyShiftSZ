/*
 * ============================================================================
 * = COPYRIGHT				
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.		
 * ============================================================================		
 */
package com.pax.tms.app.broadpos.template;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class SchemaUtil {

	private static final char[][] specialCharactersRepresentation = new char[63][];
	static {
		specialCharactersRepresentation[38] = "&amp;".toCharArray();// &
		specialCharactersRepresentation[60] = "&lt;".toCharArray();// <
		specialCharactersRepresentation[62] = "&gt;".toCharArray();// >
		specialCharactersRepresentation[34] = "&#034;".toCharArray();// "
		specialCharactersRepresentation[39] = "&#039;".toCharArray();// '
	}
	private static final String[] NOT_PAYMENTHOSTS = { "sys", "bpagt" };

	private boolean isTemplate;
	private Map<String, String> paraMap;

	/**
	 * <Root> <Program> <Schema> <Groups> <Group> </Group> </Groups> <Files>
	 * </Files> <Parameters> <Header> <Parameter> </parameter> </Header>
	 * <Parameter> </Parameter> </Parameters> </Schema> </Program> </Root>
	 */
	@SuppressWarnings("unchecked")
	public JsonArray documentToHtml(Document doc, boolean isTemp, Map<String, String> inputParaMap)
			throws ParseException {
		isTemplate = isTemp;
		if (inputParaMap != null && !inputParaMap.isEmpty()) {
			paraMap = inputParaMap;
		}

		Element root = doc.getRootElement();
		List<JsonObject> groupList = getGroupList(root);
		if (groupList.isEmpty()) {
			return null;
		}
		sortGroupList(groupList);

		JsonArray jsonArr = new JsonArray();
		Map<String, JsonObject> groupMap = new HashMap<>();
		for (JsonObject json : groupList) {
			jsonArr.add(json);
			groupMap.put(json.getString("ID"), json);
		}

		List<Element> programElements = root.elements("Program");
		for (Element program : programElements) {
			Element schema = program.element("Schema");
			if (schema == null || schema.isTextOnly()) {
				continue;
			}
			parametersToHtml(schema.element("Parameters"), groupMap, false);
		}
		return jsonArr;
	}

	@SuppressWarnings("unchecked")
	private void parametersToHtml(Element parametersElement, Map<String, JsonObject> groupMap, boolean isCheckBox) {
		if (parametersElement == null || parametersElement.isTextOnly()) {
			return;
		}

		String groupId = "";
		JsonObject groupJson = null;
		List<Element> headerElements = parametersElement.elements("Header");
		for (Element header : headerElements) { // 默认同一个Header下的GroupID都是相同的
			String display = header.elementTextTrim("Display");
			if ("true".equalsIgnoreCase(display)) {
				Element parameter = header.element("Parameter");
				groupId = parameter.elementTextTrim("GroupID");
				groupJson = groupMap.get(groupId);
				groupJson.put("Data", groupJson.getString("Data")
						+ drawHtmlForHeader(header, header.elements("Parameter"), isCheckBox));
			}
		}

		List<Element> paramElements = parametersElement.elements("Parameter");
		for (Element parameter : paramElements) {
			groupId = parameter.elementTextTrim("GroupID");
			groupJson = groupMap.get(groupId);
			if (isCheckBox) {
				groupJson.put("Data", groupJson.getString("Data") + drawCheckBoxHtmlForParam(parameter));
			} else {
				groupJson.put("Data", groupJson.getString("Data") + drawHtmlForParam(parameter));
			}
		}
	}

	public String drawHtmlForParam(Element parameter) {
		String display = parameter.elementTextTrim("Display");
		/*
		 * <Display>false</Display> The 'Display' value set false
		 */
		if (!"true".equalsIgnoreCase(display)) {
			return "";
		}

		String type = parameter.elementTextTrim("Type");
		String pid = parameter.elementTextTrim("PID");
		if ("combo".equalsIgnoreCase(type)) {
			String index = parameter.elementTextTrim("Index");
			pid = StringUtils.isNotEmpty(index) ? (pid + "_" + index) : (pid);
		}

		String dataType = parameter.elementTextTrim("DataType");
		String inputType = parameter.elementTextTrim("InputType");
		String title = escapeXml(parameter.elementTextTrim("Title"));
		int length = NumberUtils.toInt(parameter.elementTextTrim("Length"), 225);
		String required = parameter.elementTextTrim("Required");
		String readonly = parameter.elementTextTrim("Readonly");

		String defaultvalue = parameter.elementTextTrim("Defaultvalue");
		if (paraMap != null && !paraMap.isEmpty() && paraMap.containsKey(pid)) {
			defaultvalue = paraMap.get(pid);
		}
		defaultvalue = escapeXml(defaultvalue);

		String description = parameter.elementTextTrim("Description");

		boolean isRequired = !isTemplate && "true".equalsIgnoreCase(required);
		boolean isReadonly = "true".equalsIgnoreCase(readonly);
		String maxlengthHtml = "maxlength=\"" + length + "\" ";
		String regexHtml = "regex_" + dataType;

		if ("select".equalsIgnoreCase(inputType)) {
			// Select
			return drawHtmlForSelectPara(parameter, pid, title, defaultvalue, isRequired, isReadonly, description);
		} else if ("upload".equalsIgnoreCase(inputType)) {
			// Upload
			return drawHtmlForUploadPara(parameter, pid, title, defaultvalue, isRequired, isReadonly, description);
		} else if ("text".equalsIgnoreCase(inputType)) {
			// text
			return drawHtmlForTextPara(parameter, pid, title, defaultvalue, isRequired, isReadonly, regexHtml,
					maxlengthHtml, description);
		} else if ("Password".equalsIgnoreCase(inputType)) {
			// password
			return drawHtmlForPasswordPara(parameter, pid, title, defaultvalue, isRequired, isReadonly, regexHtml,
					maxlengthHtml, description);
		} else if ("label".equalsIgnoreCase(inputType)) {
			// label
			return drawHtmlForLabelPara(title);
		}

		return "";
	}

	private String drawHtmlForLabelPara(String title) {
		StringBuilder paramHtml = new StringBuilder();
		paramHtml.append("<div class=\"col-md-12 fenge-line\">"); // s-1
		if (StringUtils.isNotEmpty(title)) {
			paramHtml.append("<span>").append(title).append("</span>");
		}
		paramHtml.append("</div>"); // e-1
		return paramHtml.toString();
	}

	private String drawHtmlForPasswordPara(Element parameter, String pid, String title, String defaultvalue,
			boolean isRequired, boolean isReadonly, String regexHtml, String maxlengthHtml, String description) {
		StringBuilder paramHtml = new StringBuilder();
		paramHtml.append("<div class=\"col-md-6\">"); // s-1
		paramHtml.append("<div class=\"form-edit-group\">"); // s-2
		paramHtml.append("<div class=\"edit-name\" ").append("title=\"").append(description).append("\">"); // s-2-1
		paramHtml.append(title);
		if (isRequired) {
			paramHtml.append("<span class=\"icon-required\">*</span>");
		}
		paramHtml.append("</div>"); // e-2-1
		paramHtml.append("<div class=\"edit-value\">"); // s-2-2

		paramHtml.append("<input type=\"password\" class=\"form-control ");
		paramHtml.append(isRequired ? " required " : "").append(" ").append(regexHtml).append("\" ");
		paramHtml.append("name=\"").append(pid).append("\" ");
		paramHtml.append("value=\"").append(defaultvalue).append("\" ");
		paramHtml.append(maxlengthHtml).append(" ");
		paramHtml.append(isReadonly ? " disabled " : "").append(">");

		paramHtml.append("</div>"); // e-2-2
		paramHtml.append("</div>"); // e-2
		paramHtml.append("</div>"); // e-1

		return paramHtml.toString();
	}

	private String drawHtmlForTextPara(Element parameter, String pid, String title, String defaultvalue,
			boolean isRequired, boolean isReadonly, String regexHtml, String maxlengthHtml, String description) {
		StringBuilder paramHtml = new StringBuilder();
		paramHtml.append("<div class=\"col-md-6\">"); // s-1
		paramHtml.append("<div class=\"form-edit-group\">"); // s-2
		paramHtml.append("<div class=\"edit-name\" ").append("title=\"").append(description).append("\">"); // s-2-1
		paramHtml.append(title);
		if (isRequired) {
			paramHtml.append("<span class=\"icon-required\">*</span>");
		}
		paramHtml.append("</div>"); // e-2-1
		paramHtml.append("<div class=\"edit-value\">"); // s-2-2

		paramHtml.append("<input type=\"text\" class=\"form-control ");
		paramHtml.append(isRequired ? " required " : "").append(" ").append(regexHtml).append("\" ");
		paramHtml.append("name=\"").append(pid).append("\" ");
		paramHtml.append("value=\"").append(defaultvalue).append("\" ");
		paramHtml.append(maxlengthHtml).append(" ");
		paramHtml.append(isReadonly ? " disabled " : "").append(">");

		paramHtml.append("</div>"); // e-2-2
		paramHtml.append("</div>"); // e-2
		paramHtml.append("</div>"); // e-1

		return paramHtml.toString();
	}

	private String drawHtmlForUploadPara(Element parameter, String pid, String title, String defaultvalue,
			boolean isRequired, boolean isReadonly, String description) {
		StringBuilder uploadHtml = new StringBuilder();
		uploadHtml.append("<div class=\"col-md-12\">"); // s-1
		uploadHtml.append("<div class=\"row\">"); // s-2
		uploadHtml.append("<div class=\"col-md-6\">"); // s-3
		uploadHtml.append("<div class=\"form-edit-group\">"); // s-3-1
		uploadHtml.append("<div class=\"edit-name\" ").append("title=\"").append(description).append("\">");
		uploadHtml.append(title).append("</div>");
		uploadHtml.append("<div class=\"edit-value\">"); // s-3-1-2
		uploadHtml.append("<input type=\"text\" class=\"form-control ");
		uploadHtml.append(isRequired ? " required " : "").append(" filename\"");
		uploadHtml.append("name=\"").append(pid).append("\" ");
		uploadHtml.append("value=\"").append(defaultvalue).append("\" readonly >");
		uploadHtml.append("<div class=\"progress hide\" style=\"margin-top:10px\">"); // s-3-1-2-2
		uploadHtml.append("<div class=\"progress-bar\" role=\"progressbar\" ");
		uploadHtml.append("aria-valuemin=\"0\" aria-valuemax=\"100\" style=\"width: 0%;\"></div>");
		uploadHtml.append("</div>"); // e-3-1-2-2
		uploadHtml.append("</div>"); // e-3-1-2
		uploadHtml.append("</div>"); // e-3-1

		uploadHtml.append("<div class=\"parameters-file-btn\">"); // s-3-2

		String readonlyHtml = "";
		if (isReadonly) {
			readonlyHtml = "disabled";
		}
		uploadHtml.append("<div class=\"btn btn-primary ").append(readonlyHtml).append("\" >");
		uploadHtml.append("Select<input type=\"file\" class=\"fileupload\" ").append(readonlyHtml).append(">");
		uploadHtml.append("</div>");
		uploadHtml.append("</div>"); // e-3-2
		uploadHtml.append("</div>"); // e-3
		uploadHtml.append("</div>"); // e-2
		uploadHtml.append("</div>"); // e-1

		return uploadHtml.toString();
	}

	private String drawHtmlForSelectPara(Element parameter, String pid, String title, String defaultvalue,
			boolean isRequired, boolean isReadonly, String description) {
		StringBuilder paramHtml = new StringBuilder();

		paramHtml.append("<div class=\"col-md-6\">"); // s-1
		paramHtml.append("<div class=\"form-edit-group\">"); // s-2
		paramHtml.append("<div class=\"edit-name\" ").append("title=\"").append(description).append("\">"); // s-2-1
		paramHtml.append(title);
		if (isRequired) {
			paramHtml.append("<span class=\"icon-required\">*</span>");
		}
		paramHtml.append("</div>"); // e-2-1
		paramHtml.append("<div class=\"edit-value\">"); // s-2-2

		paramHtml.append("<select class=\"form-control ").append(isRequired ? " required " : "").append("\"");
		paramHtml.append(" name=\"").append(pid).append("\"").append(isReadonly ? " disabled " : "").append(">");
		String selectSource = parameter.elementTextTrim("Select").replace("\t", " ").replace("\n", " ");
		JsonObject json = new JsonObject(selectSource);
		Iterator<Entry<String, Object>> it = json.iterator();
		paramHtml.append("<option value=\"\" >--Please Select--</option>");
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			if (entry.getKey().equalsIgnoreCase(defaultvalue)) {
				paramHtml.append("<option value=\"").append(entry.getKey()).append("\" selected>");
			} else {
				paramHtml.append("<option value=\"").append(entry.getKey()).append("\" >");
			}
			paramHtml.append(entry.getValue()).append("</option>");
		}
		paramHtml.append("</select>");

		paramHtml.append("</div>"); // e-2-2
		paramHtml.append("</div>"); // e-2
		paramHtml.append("</div>"); // e-1
		return paramHtml.toString();
	}

	public String drawHtmlForHeader(Element header, List<Element> paramElements, boolean isCheckBox) {
		String display = header.elementTextTrim("Display");
		if (!"true".equalsIgnoreCase(display)) {
			return "";
		}

		StringBuilder headHtml = new StringBuilder();
		String title = StringUtils.trimToEmpty(header.elementTextTrim("Title"));
		// foldable or common
		String displayStyle = header.elementTextTrim("DisplayStyle");
		// open or close
		String targetId = title.replace(" ", "_").replace("/", "_");
		headHtml.append("<div class=\"parameters-panel\">"); // s-1
		if (isCheckBox) {
			headHtml.append("<div class=\"checkAllGroup\"><div class=\"checkbox\">");
			headHtml.append("<input class=\"styled\" type=\"checkbox\"><label></label>");
			headHtml.append("</div></div>");
		}
		if ("foldable".equalsIgnoreCase(displayStyle)) {
			String defaultStyle = header.elementTextTrim("DefaultStyle");
			if ("open".equalsIgnoreCase(defaultStyle)) {
				headHtml.append("<div class=\"parameters-title\" data-toggle=\"collapse\" ");
				headHtml.append(" data-target=\"#" + targetId + "\"");
				headHtml.append(" aria-expanded=\"true\" aria-controls=\"parameters-collapse\" >");
				headHtml.append(title);
				headHtml.append("<i class=\"iconfont\">&#xe60e;</i></div>");
				headHtml.append("<div  class=\"collapse in\" id=\"" + targetId + "\">"); // s-2
			} else {
				headHtml.append("<div class=\"parameters-title collapsed\" data-toggle=\"collapse\" ");
				headHtml.append(" data-target=\"#" + targetId + "\"");
				headHtml.append(" aria-expanded=\"true\" aria-controls=\"parameters-collapse\" >");
				headHtml.append(title);
				headHtml.append("<i class=\"iconfont\">&#xe60e;</i></div>");
				headHtml.append("<div  class=\"collapse\" id=\"" + targetId + "\">"); // s-2
			}
		} else {
			headHtml.append("<div class=\"parameters-title\">");
			headHtml.append(title);
			headHtml.append("</div>");
		}
		headHtml.append("<div class=\"parameters-body form-horizontal\">"); // s-3
		headHtml.append("<div class=\"row\">"); // s-4

		for (Element parameter : paramElements) {
			if (isCheckBox) {
				headHtml.append(drawCheckBoxHtmlForParam(parameter));
			} else {
				headHtml.append(drawHtmlForParam(parameter));
			}
		}

		headHtml.append("</div>"); // e-4
		headHtml.append("</div>"); // e-3
		if ("foldable".equalsIgnoreCase(displayStyle)) {
			headHtml.append("</div>"); // e-2
		}

		headHtml.append("</div>"); // e-1

		return headHtml.toString();

	}

	private String escapeXml(String buffer) {
		if (buffer == null) {
			return "";
		}

		int start = 0;
		int length = buffer.length();
		char[] arrayBuffer = buffer.toCharArray();
		StringBuilder escapedBuffer = new StringBuilder(length + 5);
		for (int i = 0; i < length; ++i) {
			char c = arrayBuffer[i];
			if (c <= '>') {
				char[] escaped = specialCharactersRepresentation[c];
				if (escaped == null) {
					continue;
				}
				if (start < i) {
					escapedBuffer.append(arrayBuffer, start, i - start);
				}
				start = i + 1;
				escapedBuffer.append(escaped);
			}
		}

		if (start == 0) {
			return buffer;
		}

		if (start < length) {
			escapedBuffer.append(arrayBuffer, start, length - start);
		}
		return escapedBuffer.toString();
	}

	@SuppressWarnings("unchecked")
	private List<JsonObject> getGroupList(Element root) {
		List<JsonObject> groupList = new ArrayList<JsonObject>();
		List<Element> programElements = root.elements("Program");
		for (Element program : programElements) {
			Element schema = program.element("Schema");
			if (schema == null || schema.isTextOnly()) {
				continue;
			}
			Element groupsElement = schema.element("Groups");
			if (groupsElement == null || groupsElement.isTextOnly()) {
				continue;
			}
			List<Element> groupElements = groupsElement.elements("Group");
			for (Element group : groupElements) {
				String id = group.elementTextTrim("ID");
				String title = group.elementTextTrim("Title");
				String description = group.elementTextTrim("Description");
				String order = group.elementTextTrim("Order");
				Integer index = NumberUtils.toInt(order, 0);
				JsonObject json = new JsonObject();
				json.put("ID", id);
				json.put("Title", title);
				json.put("Description", description);
				json.put("Data", "");
				json.put("Order", index);
				groupList.add(json);
			}
		}
		return groupList;
	}

	private List<JsonObject> sortGroupList(List<JsonObject> list) {
		int maxOrder = 0;
		for (JsonObject obj : list) {
			int order = obj.getInteger("Order");
			if (order > maxOrder) {
				maxOrder = order;
			}
		}
		/*
		 * order by "Order", "Group Id"
		 */
		final int maxValue = maxOrder + 1;
		Collections.sort(list, (JsonObject a, JsonObject b) -> {
			final String ORDER = "Order";
			final String GROUP_ID = "ID";

			String aName = a.getString(GROUP_ID);
			String bName = b.getString(GROUP_ID);
			int akey = a.getInteger(ORDER);
			int bkey = b.getInteger(ORDER);
			String aType = aName.split("_")[0];
			String bType = bName.split("_")[0];
			for (String str : NOT_PAYMENTHOSTS) {
				if (str.equalsIgnoreCase(aType)) {
					akey += maxValue;
				}
				if (str.equalsIgnoreCase(bType)) {
					bkey += maxValue;
				}
			}
			if (akey > bkey) {
				return 1;
			} else if (akey < bkey) {
				return -1;
			} else {
				return aName.compareTo(bName);
			}
		});
		return list;
	}

	@SuppressWarnings("unchecked")
	public JsonArray documentToReportHtml(Document doc) {
		Element root = doc.getRootElement();
		List<JsonObject> groupList = this.getGroupList(root);
		if (groupList.isEmpty()) {
			return null;
		}
		this.sortGroupList(groupList);

		JsonArray jsonArr = new JsonArray();
		Map<String, JsonObject> groupMap = new HashMap<String, JsonObject>();
		for (JsonObject json : groupList) {
			jsonArr.add(json);
			groupMap.put(json.getString("ID"), json);
		}

		List<Element> programElements = root.elements("Program");
		for (Element program : programElements) {
			Element schema = program.element("Schema");
			if (schema == null || schema.isTextOnly()) {
				continue;
			}
			parametersToHtml(schema.element("Parameters"), groupMap, true);
		}
		return jsonArr;
	}

	private String drawCheckBoxHtmlForParam(Element parameter) {
		String display = parameter.elementTextTrim("Display");
		/*
		 * <Display>false</Display> The 'Display' value set false
		 */
		if (!"true".equalsIgnoreCase(display)) {
			return "";
		}

		String inputType = parameter.elementTextTrim("InputType");
		String type = parameter.elementTextTrim("Type");
		String pid = parameter.elementTextTrim("PID");
		if ("combo".equalsIgnoreCase(type)) {
			String index = parameter.elementTextTrim("Index");
			pid = StringUtils.isNotEmpty(index) ? (pid + "_" + index) : (pid);
		}

		String title = escapeXml(parameter.elementTextTrim("Title"));

		if ("label".equalsIgnoreCase(inputType)) {
			return drawHtmlForLabelPara(title);
		}

		StringBuilder paramHtml = new StringBuilder();
		paramHtml.append("<div class=\"col-sm-3\">");
		paramHtml.append("<div class=\"checkbox\">");
		paramHtml.append("<input id=\"").append(pid).append("\" class=\"styled\" type=\"checkbox\" ");
		paramHtml.append("name=\"").append(pid).append("\"").append(" value=\"\">");
		paramHtml.append("<label for=\"").append(pid).append("\">").append(title).append("</label>");
		paramHtml.append("</div>");
		paramHtml.append("</div>");
		return paramHtml.toString();
	}

	@SuppressWarnings("unchecked")
	public static List<ParameterEntity> getEntityListFromSchema(Document doc) {
		List<ParameterEntity> paramsList = new ArrayList<ParameterEntity>();
		Element root = doc.getRootElement();
		List<Element> programElements = root.elements("Program");
		for (Element program : programElements) {
			String programId = program.elementTextTrim("ID");
			String programAbbrName = program.elementTextTrim("AbbrName");
			Element schema = program.element("Schema");
			if (schema == null || schema.isTextOnly()) {
				continue;
			}
			Element parametersElement = schema.element("Parameters");
			if (parametersElement == null || parametersElement.isTextOnly()) {
				continue;
			}
			List<Element> headerElements = parametersElement.elements("Header");
			for (Element header : headerElements) {
				if (header == null || header.isTextOnly()) {
					continue;
				}
				List<Element> param2Elements = header.elements("Parameter");
				doParameterElement(param2Elements, paramsList, programId, programAbbrName);
			}
			List<Element> param1Elements = parametersElement.elements("Parameter");
			if (param1Elements != null && !param1Elements.isEmpty()) {
				doParameterElement(param1Elements, paramsList, programId, programAbbrName);
			}
		}
		return paramsList;
	}

	public static List<ParameterEntity> doParameterElement(List<Element> parmElements, List<ParameterEntity> paramsList,
			String programId, String programAbbrName) {
		for (int p = 0; p < parmElements.size(); p++) {
			Element parameter = parmElements.get(p);
			if (parameter == null || parameter.isTextOnly()
					|| "label".equalsIgnoreCase(parameter.elementTextTrim("InputType"))
					|| StringUtils.isEmpty(parameter.elementTextTrim("PID"))) {
				continue;
			}
			ParameterEntity entity = new ParameterEntity();
			entity.setPid(parameter.elementTextTrim("PID"));
			entity.setDataType(parameter.elementTextTrim("DataType"));
			entity.setDefaultValue(parameter.elementTextTrim("Defaultvalue"));
			entity.setDescription(parameter.elementTextTrim("Description"));
			entity.setDisplay(parameter.elementTextTrim("Display"));
			entity.setIndex(parameter.elementTextTrim("Index"));
			entity.setInputType(parameter.elementTextTrim("InputType"));
			entity.setLength(parameter.elementTextTrim("Length"));
			entity.setReadonly(parameter.elementTextTrim("Readonly"));
			entity.setRequired(parameter.elementTextTrim("Required"));
			entity.setRpsDisplay(parameter.elementTextTrim("RpsDisplay"));
			entity.setRpsReadonly(parameter.elementTextTrim("RpsReadonly"));
			entity.setSelect(parameter.elementTextTrim("Select"));
			entity.setSeparator(parameter.elementTextTrim("Separator"));
			entity.setTitle(parameter.elementTextTrim("Title"));
			entity.setType(parameter.elementTextTrim("Type"));
			entity.setGroupId(parameter.elementTextTrim("GroupID"));
			entity.setProgramFileId(parameter.elementTextTrim("FileID"));
			entity.setProgramAbbrName(programAbbrName);
			entity.setProgramId(programId);
			entity.init();
			paramsList.add(entity);
		}
		return paramsList;
	}
}
