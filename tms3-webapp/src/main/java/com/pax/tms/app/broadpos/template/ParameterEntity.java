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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class ParameterEntity {
	private String finalPid;
	private String pid;
	private String type;
	private String inputType;
	private String dataType;
	private String title;
	private String length;
	private String required;
	private String readonly;
	private String display;
	private String rpsReadonly;
	private String rpsDisplay;
	private String defaultValue;
	private String description;
	private String select;
	private String index;
	private String separator;
	private String groupId;
	private String programFileId;
	private String programId;
	private String programAbbrName;

	private boolean inherit;
	Set<String> parmFilter = new HashSet<String>();

	public ParameterEntity() {
	}

	public void init() {
		if ("combo".equalsIgnoreCase(type) && StringUtils.isNotEmpty(index)) {
			finalPid = pid + "_" + index;
		} else {
			finalPid = pid;
		}

		inherit = true;
		/*
		 * 只读与不显示的 不可继承
		 */
		if (readonly == null || "true".equalsIgnoreCase(readonly)) {
			inherit = false;
		}
		if (display == null || "false".equalsIgnoreCase(display)) {
			inherit = false;
		}
		if (rpsReadonly != null && "true".equalsIgnoreCase(rpsReadonly)) {
			inherit = false;
		}
		if (rpsDisplay != null && "false".equalsIgnoreCase(rpsDisplay)) {
			inherit = false;
		}
	}

	public void isImport() {
		/*
		 * 导入终端参数的逻辑
		 */
		parmFilter.add("sys.param.hrType");
		parmFilter.add("sys.param.hrTime");
		parmFilter.add("sys.param.hrInterval");

		if (parmFilter.contains(pid)) {
			inherit = false;
		}
	}

	public boolean isValid() {
		if (StringUtils.isEmpty(defaultValue) && "false".equalsIgnoreCase(required)) {
			return true;
		}
		return verifyPidValue(defaultValue);
	}

	public boolean verifyPidValue(String value) {
		if (StringUtils.isEmpty(value)) {
			return true;
		}
		/*
		 * 参数Schema中上传的文件名必须与原来的相同
		 */
		if ("upload".equalsIgnoreCase(inputType)) {
			if (StringUtils.isEmpty(defaultValue) && StringUtils.isEmpty(value)) {
				return true;
			} else if (StringUtils.isNotEmpty(defaultValue) && StringUtils.isNotEmpty(value)) {
				String filename = value.split("\\|")[0];
				if (filename.equals(defaultValue)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if ("select".equalsIgnoreCase(inputType)) {
			String[] options = select.replace("{", "").replace("}", "").replace("\"", "").split(",");
			for (String s : options) {
				if (s.split(":")[0].equals(value)) {
					return true;
				}
			}
			return false;
		} else if ("text".equalsIgnoreCase(inputType) || "password".equalsIgnoreCase(inputType)) {
			int temp = 0;
			if (StringUtils.isEmpty(dataType)) {
				return true;
			} else if ("keyIndex".equalsIgnoreCase(dataType)) {
				temp = Integer.parseInt(value);
				if (temp >= 1 && temp <= 10) {
					return true;
				}
				return false;
			} else if ("Port".equalsIgnoreCase(dataType)) {
				temp = Integer.parseInt(value);
				if (temp >= 1 && temp <= 65535) {
					return true;
				}
				return false;
			} else if ("Time_hhmm".equalsIgnoreCase(dataType)) {
				if (value.matches("^([0-1][0-9]|2[0-3])[0-5][0-9]$")) {
					return true;
				}
				return false;
			} else if ("IP".equalsIgnoreCase(dataType)) {
				if (value.matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$")) {
					return true;
				}
				return false;
			} else if ("Time".equalsIgnoreCase(dataType)) {
				if (value.matches("^([0-1][0-9]|2[0-3])[0-5][0-9][0-5][0-9]$")) {
					return true;
				}
				return false;
			} else if ("Datetime".equalsIgnoreCase(dataType)) {
				if (value.matches("^\\d{14}$")) {
					String _year = value.substring(0, 4);
					String _month = value.substring(4, 6);
					String _day = value.substring(6, 8);
					String _hour = value.substring(8, 10);
					String _minute = value.substring(10, 12);
					String _second = value.substring(12, 14);
					if (Integer.parseInt(_year) >= 1 && Integer.parseInt(_month) >= 1 && Integer.parseInt(_month) <= 12
							&& Integer.parseInt(_day) >= 1 && Integer.parseInt(_day) <= 31
							&& Integer.parseInt(_hour) <= 23 && Integer.parseInt(_minute) <= 59
							&& Integer.parseInt(_second) <= 59
							&& (_month.equals("02") && Integer.parseInt(_day) < 30)) {
						return true;
					}
				}
				return false;
			} else if ("Date".equalsIgnoreCase(dataType)) {
				if (value.matches("^\\d{8}$")) {
					String _year = value.substring(0, 4);
					String _month = value.substring(4, 6);
					String _day = value.substring(6, 8);
					if (Integer.parseInt(_year) >= 1 && Integer.parseInt(_month) >= 1 && Integer.parseInt(_month) <= 12
							&& Integer.parseInt(_day) >= 1 && Integer.parseInt(_day) <= 31
							&& (_month.equals("02") && Integer.parseInt(_day) < 30)) {
						return true;
					}
				}
				return false;
			} else if ("Amount".equalsIgnoreCase(dataType)) {
				// Amount类型, 导入原样存储
				if (value.length() <= Integer.parseInt(length)
						// && (defaultValue.indexOf(".") != -1)) {
						&& (value.matches("^[0-9]+$"))) {
					// if (defaultValue.matches("^(\\d+(?:\\.\\d{2})?|)$")) {
					return true;
					// }
				}
				return false;
			} else if ("Number_String".equalsIgnoreCase(dataType)) {
				if (value.length() <= Integer.parseInt(length) && (value.matches("^[a-zA-Z0-9]+$"))) {
					return true;
				}
				return false;
			} else if ("String".equalsIgnoreCase(dataType)) {
				if (value.length() <= Integer.parseInt(length) && (value.matches("^[\\x20-\\x7f]+$"))) {
					return true;
				}
				return false;
			} else if ("Number".equalsIgnoreCase(dataType)) {
				if (value.length() <= Integer.parseInt(length) && (value.matches("^[0-9]+$"))) {
					return true;
				}
				return false;
			} else if ("Float".equalsIgnoreCase(dataType)) {
				if (value.length() <= Integer.parseInt(length) && (value.matches("(^\\+?|^\\d?)\\d*\\.\\d+$"))) {
					return true;
				}
				return false;
			}
		}
		return false;
	}

	public String getFinalPid() {
		return finalPid;
	}

	public void setFinalPid(String finalPid) {
		this.finalPid = finalPid;
	}

	public boolean isInherit() {
		return inherit;
	}

	public void setInherit(boolean inherit) {
		this.inherit = inherit;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public String getReadonly() {
		return readonly;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getRpsReadonly() {
		return rpsReadonly;
	}

	public void setRpsReadonly(String rpsReadonly) {
		this.rpsReadonly = rpsReadonly;
	}

	public String getRpsDisplay() {
		return rpsDisplay;
	}

	public void setRpsDisplay(String rpsDisplay) {
		this.rpsDisplay = rpsDisplay;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getProgramFileId() {
		return programFileId;
	}

	public void setProgramFileId(String programFileId) {
		this.programFileId = programFileId;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getProgramAbbrName() {
		return programAbbrName;
	}

	public void setProgramAbbrName(String programAbbrName) {
		this.programAbbrName = programAbbrName;
	}
}