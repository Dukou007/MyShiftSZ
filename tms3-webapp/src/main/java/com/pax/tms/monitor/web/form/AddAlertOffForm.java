/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description:  AlertOffForm
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.web.form;

import com.pax.common.web.form.BaseForm;

public class AddAlertOffForm extends BaseForm {

	private static final long serialVersionUID = -4430516486507605627L;

	private Long groupId;
	private Long offId;
	private int repeatType;
	private String offDate;
	private String offStartTime;
	private String offEndTime;


	@Override
	public Long getGroupId() {
		return groupId;
	}

	@Override
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getOffId() {
		return offId;
	}

	public void setOffId(Long offId) {
		this.offId = offId;
	}

	public int getRepeatType() {
		return repeatType;
	}

	public void setRepeatType(int repeatType) {
		this.repeatType = repeatType;
	}

	public String getOffDate() {
		return offDate;
	}

	public void setOffDate(String offDate) {
		this.offDate = offDate;
	}

	public String getOffStartTime() {
		return offStartTime;
	}

	public void setOffStartTime(String offStartTime) {
		this.offStartTime = offStartTime;
	}

	public String getOffEndTime() {
		return offEndTime;
	}

	public void setOffEndTime(String offEndTime) {
		this.offEndTime = offEndTime;
	}

}
