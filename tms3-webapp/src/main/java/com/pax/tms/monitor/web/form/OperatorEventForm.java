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
package com.pax.tms.monitor.web.form;

import com.pax.common.web.form.BaseForm;

public class OperatorEventForm extends BaseForm {

	public static final String ADD_TERMINAL = "Add Terminal";
	public static final String EDIT_TERMINAL = "Edit Terminal";
	public static final String COPY_TERMINAL = "Copy Terminal";
	public static final String CLONE_TERMINAL = "Clone Terminal";
	public static final String DISMISS_TERMINAL = "Remove Terminal";
	public static final String DELETE_TERMINAL = "Delete Terminal";
	public static final String MOVE_TERMINAL = "Move Terminal";
	public static final String ACTIVATE_TERMINAL = "Activate terminal";
	public static final String DEACTIVATE_TERMINAL = "Deactivate terminal";
	public static final String ASSIGN_TERMINAL = "Assign terminal";
	public static final int TERMINAL_TYPE = 1;

	public static final String ADD_GROUP = "Add Group ";
	public static final String EDIT_GROUP = "Edit Group";
	public static final String MOVE_GROUP = "Move Group";
	public static final String DELETE_GROUP = "Delete Group";
	public static final String COPY_GROUP = "Copy Group";
	public static final String ADD_GLOBAL_SETTING = "Add GlobalSetting ";
	public static final String EDIT_GLOBAL_SETTING = "Edit Global Setting of";
	public static final int GROUP_TYPE = 0;
	public static final String ADD_CONDITION = "Add Condition ";
	public static final String EDIT_CONDITION = "Edit Condition ";
	public static final String ENABLE_CONDITION = "Enable Condition ";
	public static final String DISABLE_CONDITION = "Disable Condition ";
	public static final String ADD_ALERT_OFF = "Add Alert Off ";
	public static final String DELETE_ALERT_OFF = "Delete Alert Off ";
	public static final String EDIT_ALERT_SUBSCRIBE = "Edit Alert Subscribe ";

	public static final String TERMINAL = "Terminal";
	public static final String GROUP = "Group";

	private static final long serialVersionUID = 1L;

	private int type;
	private String message;
	private String additionMessage;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAdditionMessage() {
		return additionMessage;
	}

	public void setAdditionMessage(String additionMessage) {
		this.additionMessage = additionMessage;
	}

}
