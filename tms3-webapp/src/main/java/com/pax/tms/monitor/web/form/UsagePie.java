/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: UsagePie
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.web.form;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.pax.tms.group.model.UsageThreshold;

public class UsagePie extends Pie {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7723780573213758313L;

	private Long groupId;

	private String groupName;

	private String reportCycle;

	private Date startTime;

	private Date endTime;

	private String xvalue;

	public UsagePie() {
		super();
	}

	public String getXvalue() {
		if (StringUtils.isNotEmpty(reportCycle)) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(endTime);
			switch (reportCycle) {
			case UsageThreshold.CYCLE_PER_DAY:
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM");
				xvalue = sdf.format(endTime);
				break;
			case UsageThreshold.CYCLE_PER_WEEK:
				xvalue = cal.get(Calendar.WEEK_OF_YEAR) + " week";
				break;
			case UsageThreshold.CYCLE_PER_MONTH:
				SimpleDateFormat sdf2 = new SimpleDateFormat("MMM");
				xvalue = sdf2.format(endTime);
				break;
			default:
				xvalue = " ";
				break;
			}
		}
		return xvalue;
	}

	public void setXvalue(String xvalue) {
		this.xvalue = xvalue;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getReportCycle() {
		return reportCycle;
	}

	public void setReportCycle(String reportCycle) {
		this.reportCycle = reportCycle;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
