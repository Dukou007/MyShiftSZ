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
package com.pax.tms.monitor.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;

@Entity
@Table(name = "TMSTEVENT_TRM")
public class EventTrm extends AbstractModel {

	private static final long serialVersionUID = -4212085014751059630L;

	public static final String ID_SEQUENCE_NAME = "TMSTEVENT_ID";
	public static final String TITLE = "EVENT";
	public static final String LIST_URL = "/events/allEvents/";
	private static final String INCREMENT_SIZE = "20";

	public static final int INFO = 1;
	public static final int WARNNING = 2;
	public static final int CRITICAL = 3;

	public static final int GROUP = 0;
	public static final int TERMINAL = 1;

	/*
	 * event id
	 */
	@Id
	@GeneratedValue(generator = ID_SEQUENCE_NAME + "_GEN")
	@GenericGenerator(name = ID_SEQUENCE_NAME + "_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "EVENT_ID")
	private Long id;

	/*
	 * event date & time
	 */
	@Column(name = "EVENT_TIME")
	private Date time;

	/*
	 * event source. e.g. terminal id, group id etc.
	 */
	@Column(name = "EVENT_SOURCE")
	private String source;

	/*
	 * event severity, 1-information, 2-warning, 3-critical
	 */
	@Column(name = "EVENT_SEVERITY")
	private int serverity;

	/*
	 * event message
	 */
	@Column(name = "EVENT_MSG")
	private String message;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	

	public int getServerity() {
		return serverity;
	}

	public void setServerity(int serverity) {
		this.serverity = serverity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
