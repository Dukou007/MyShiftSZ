package com.pax.tms.terminal.web.form;

import com.pax.common.web.form.BaseForm;

public class AssignTerminalForm extends BaseForm {

	private static final long serialVersionUID = 1L;

	private Long[] groupIds;
	
	public Long[] getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Long[] groupIds) {
		this.groupIds = groupIds;
	}

}
