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

import java.io.Serializable;
import java.util.Date;

public class Pie implements Serializable{
	private static final long serialVersionUID = -2916020027650922039L;
	private String name;
	private int isshow;
	private int total = 0;
	private String alertValue = "0";
	private int threshold = 0;
	/*
	 * 1 - info 2 - warn 3 - critial
	 */
	private int alertLevel = 0;
	private int redCount = 0;
	private int greenCount = 0;
	private int greyCount = 0;
	private String redTitle;
	private String greenTitle;
	private String greyTitle;
	private Date pieDate;
	boolean alertFlag;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsshow() {
		return isshow;
	}

	public void setIsshow(int isshow) {
		this.isshow = isshow;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getAlertValue() {
		return alertValue;
	}

	public void setAlertValue(String alertValue) {
		this.alertValue = alertValue;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public int getAlertLevel() {
		return alertLevel;
	}

	public void setAlertLevel(int alertLevel) {
		this.alertLevel = alertLevel;
	}

	public int getRedCount() {
		return redCount;
	}

	public void setRedCount(int redCount) {
		this.redCount = redCount;
	}

	public int getGreenCount() {
		return greenCount;
	}

	public void setGreenCount(int greenCount) {
		this.greenCount = greenCount;
	}

	public int getGreyCount() {
		return greyCount;
	}

	public void setGreyCount(int greyCount) {
		this.greyCount = greyCount;
	}

	public String getRedTitle() {
		return redTitle;
	}

	public void setRedTitle(String redTitle) {
		this.redTitle = redTitle;
	}

	public String getGreenTitle() {
		return greenTitle;
	}

	public void setGreenTitle(String greenTitle) {
		this.greenTitle = greenTitle;
	}

	public String getGreyTitle() {
		return greyTitle;
	}

	public void setGreyTitle(String greyTitle) {
		this.greyTitle = greyTitle;
	}

	public Date getPieDate() {
		return pieDate;
	}

	public void setPieDate(Date pieDate) {
		this.pieDate = pieDate;
	}

	public boolean isAlertFlag() {
		return alertFlag;
	}

	public void setAlertFlag(boolean alertFlag) {
		this.alertFlag = alertFlag;
	}

}
