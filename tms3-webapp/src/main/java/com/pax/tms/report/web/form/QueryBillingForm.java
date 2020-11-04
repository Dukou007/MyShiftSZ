package com.pax.tms.report.web.form;

import com.pax.common.web.form.QueryForm;

public class QueryBillingForm extends QueryForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String month;
	private String localTimezone;

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getLocalTimezone() {
		return localTimezone;
	}

	public void setLocalTimezone(String localTimezone) {
		this.localTimezone = localTimezone;
	}

}
