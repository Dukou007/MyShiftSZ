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
package com.pax.tms.user.web.form;

import com.pax.common.web.form.BaseForm;

public class OperatorLogForm extends BaseForm {

	public static final String ADD_TERMINAL = "Add Terminal ";
	public static final String EDIT_TERMINAL = "Edit Terminal ";
	public static final String COPY_TERMINAL = "Copy Terminal ";
	public static final String CLONE_TERMINAL = "Clone Terminal ";
	public static final String REMOVE_TERMINAL = "Remove Terminal ";
	public static final String DELETE_TERMINAL = "Delete Terminal ";
	public static final String MOVE_TERMINAL = "Move Terminal ";
	public static final String ACTIVATE_TERMINAL = "Activate terminal ";
	public static final String DEACTIVATE_TERMINAL = "Deactivate terminal ";
	public static final String ASSIGN_TERMINAL = "Assign Terminal ";

	public static final String ADD_PACKAGE = "Add Package ";
	public static final String EDIT_PACKAGE = "Edit Package ";
	public static final String COPY_PACKAGE = "Copy Package ";
	public static final String DISMISS_PACKAGE = "Dismiss Package ";
	public static final String DELETE_PACKAGE = "Delete Package ";
	public static final String REMOVE_PACKAGE = "Remove Package ";
	public static final String ACTIVATE_PACKAGE = "Activate Package ";
	public static final String DEACTIVATE_PACKAGE = "Deactivate Package ";
	public static final String ASSIGN_PACKAGE = "Assign Package ";
	
	public static final String IMPORT_OFFLINEKEY = "Import Key ";
    public static final String EDIT_OFFLINEKEY = "Edit Key ";
    public static final String COPY_OFFLINEKEY = "Copy Key ";
    public static final String DISMISS_OFFLINEKEY = "Dismiss Key ";
    public static final String DELETE_OFFLINEKEY = "Delete Key ";
    public static final String REMOVE_OFFLINEKEY = "Remove Key ";
    public static final String ACTIVATE_OFFLINEKEY = "Activate Key ";
    public static final String DEACTIVATE_OFFLINEKEY = "Deactivate Key ";
    public static final String ASSIGN_OFFLINEKEY = "Assign Key ";

	public static final String LOGOUT_USER = "Logout Successful";
	public static final String ADD_LOCAL_USER = "Add Local User ";
	public static final String ADD_LDAP_USER = "Add LDAP User ";
	public static final String EDIT_USER = "Edit User ";
	public static final String EDIT_USER_PROFILE = "Edit User ";
	public static final String ACTIVE_USER = "Activate User ";
	public static final String DEACTIVE_USER = "Deactivate User ";
	public static final String DELETE_USER = "Delete User ";
	public static final String CHG_USER_PWD = "Change ";
	public static final String RESET_USER_PWD = "Reset User ";
	public static final String FORGOT_USER_PWD = "Forgot password";
	public static final String Send_RESET_EMAIL = "Request to reset User ";
	public static final String GENERATE_USER_PWD = "Generate " ;

	public static final String ADD_GROUP = "Add Group ";
	public static final String EDIT_GROUP = "Edit Group ";
	public static final String MOVE_GROUP = "Move Group ";
	public static final String DELETE_GROUP = "Delete Group ";
	public static final String COPY_GROUP = "Copy Group ";
	public static final String EDIT_GLOBAL_SETTING = "Set global parameters in Group ";
	public static final String ADD_CONDITION = "Add Condition in Group ";
	public static final String EDIT_CONDITION = "Edit Condition ";
	public static final String DELETE_CONDITION = "Delete Condition in Group ";
	public static final String DISABLE_CONDITION = "Disable Condition ";
	public static final String ENABLE_CONDITION = "Enable Condition ";
	public static final String ADD_ALERT_OFF = "Add alert off ";
	public static final String DELETE_ALERT_OFF = "Delete alert off ";
	public static final String EDIT_ALERT_SUBSCRIBE = "Change ";

	public static final String TERMINAL = "Terminal ";
	public static final String DEPLOY_TERMINAL = "Deploy Package ";
	public static final String DEPLOY_TERMINAL_KEY = "Deploy OfflineKey ";
	public static final String DELETE_DEPLOY_TERMINAL = "Cancel the task of deploying Package ";
	public static final String DELETE_DEPLOY_TERMINAL_KEY = "Cancel the task of deploying OfflineKey ";
	public static final String GROUP = "Group ";
	public static final String ADD_DEPLOY_GROUP = "Deploy Package ";
	public static final String ADD_DEPLOY_GROUP_KEY = "Deploy OfflineKey ";
	public static final String DELETE_DEPLOY_GROUP = "Cancel the task of deploying Package ";
	public static final String DELETE_DEPLOY_GROUP_KEY = "Cancel the task of deploying OfflineKey ";
	public static final String IMPORT_TERMINALS = "Import Terminals ";
	public static final String EXPORT_TERMINALS = "Export Terminals ";
	public static final String IMPORT_GROUPS = "Import Groups  ";

	public static final String ADD_PKG_SCHEMA = "Add Template";
	public static final String EDIT_PKG_SCHEMA = "Edit Template";
	public static final String DELETE_PKG_SCHEMA = "Delete Template";

	public static final String TERMINA_DOWNLOAD_REPORT = "Export Terminal Download Report";
	public static final String USER_MAINTENANCE_REPORT = "Export User Maintenance Report";
	public static final String TERMINA_APPCONFIG_REPORT = "Export Terminal Application Configuration Report";
	public static final String PARAMETER_CHANGE_HISTORY_REPORT = "Export Parameter Change History Report";
	public static final String TERMINAL_NOT_REGISTERED_REPORT = "Export Terminal Not Registered Report";
	public static final String EVENT_LIST_REPORT = "Export Event List Report";
	
	private static final long serialVersionUID = 1L;
	private String username;
	private String roles;
	private String message;
	private Long userId;
	private String additionMessage;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getAdditionMessage() {
		return additionMessage;
	}

	public void setAdditionMessage(String additionMessage) {
		this.additionMessage = additionMessage;
	}

}
