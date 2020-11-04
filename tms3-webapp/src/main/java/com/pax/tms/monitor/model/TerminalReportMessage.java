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
@Table(name = "TMSTTRM_REPORT_MSG")
public class TerminalReportMessage extends AbstractModel {

	private static final long serialVersionUID = -5052215535415071107L;

	public static final String TABLE_NAME = "TMSTTRM_REPORT_MSG";
	public static final String INCREMENT_SIZE = "5";

	@Id
	@GeneratedValue(generator = TABLE_NAME + "_ID_GEN")
	@GenericGenerator(name = TABLE_NAME + "_ID_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = TABLE_NAME + "_SEQ"),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "RPT_ID")
	private Long reportId;

	@Column(name = "TRM_ID")
	private String terminalId;

	@Column(name = "REPORT_TM")
	private Date reportTime;
	
	@Column(name = "TAMPER")
	private String tamper;
	
	@Column(name = "ON_OFF_LINE")
	private String online;
	
	@Column(name = "SHIELD")
	private String shield;
	
	@Column(name = "STYLUS")
	private String stylus;
	
	@Column(name = "DOWN_STS")
	private String downStatus;
	
	@Column(name = "ACTV_STS")
	private String acivStatus;

	@Column(name = "MSR_ERRS")
	private int msrErrs;

	@Column(name = "MSR_TOTS")
	private int msrTots;

	@Column(name = "ICR_ERRS")
	private int icrErrs;

	@Column(name = "ICR_TOTS")
	private int icrTots;

	@Column(name = "PIN_FAILS")
	private int pinFails;

	@Column(name = "PIN_TOTS")
	private int pinTots;

	@Column(name = "SIGN_ERRS")
	private int signErr;

	@Column(name = "SIGN_TOTS")
	private int signTots;

	@Column(name = "DOWN_FAILS")
	private int downFails;

	@Column(name = "DOWN_TOTS")
	private int downTots;

	@Column(name = "ACTV_FAILS")
	private int actvErrs;

	@Column(name = "ACTV_TOTS")
	private int actvTots;

	@Column(name = "CL_ICR_ERRS")
	private int clICRErrs;

	@Column(name = "CL_ICR_TOTS")
	private int clICRTots;

	@Column(name = "TXN_ERRS")
	private int txnErrs;

	@Column(name = "TXN_TOTS")
	private int txnTots;

	@Column(name = "POWER_NO")
	private int powers;

	public Long getReportId() {
		return reportId;
	}

	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}

	public String getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	public Date getReportTime() {
		return reportTime;
	}

	public void setReportTime(Date reportTime) {
		this.reportTime = reportTime;
	}

	public int getMsrErrs() {
		return msrErrs;
	}

	public void setMsrErrs(int msrErrs) {
		this.msrErrs = msrErrs;
	}

	public int getMsrTots() {
		return msrTots;
	}

	public void setMsrTots(int msrTots) {
		this.msrTots = msrTots;
	}

	public int getIcrErrs() {
		return icrErrs;
	}

	public void setIcrErrs(int icrErrs) {
		this.icrErrs = icrErrs;
	}

	public int getIcrTots() {
		return icrTots;
	}

	public void setIcrTots(int icrTots) {
		this.icrTots = icrTots;
	}

	public int getPinFails() {
		return pinFails;
	}

	public void setPinFails(int pinFails) {
		this.pinFails = pinFails;
	}

	public int getPinTots() {
		return pinTots;
	}

	public void setPinTots(int pinTots) {
		this.pinTots = pinTots;
	}

	public int getSignErr() {
		return signErr;
	}

	public void setSignErr(int signErr) {
		this.signErr = signErr;
	}

	public int getSignTots() {
		return signTots;
	}

	public void setSignTots(int signTots) {
		this.signTots = signTots;
	}

	public int getDownFails() {
		return downFails;
	}

	public void setDownFails(int downFails) {
		this.downFails = downFails;
	}

	public int getDownTots() {
		return downTots;
	}

	public void setDownTots(int downTots) {
		this.downTots = downTots;
	}

	public int getActvErrs() {
		return actvErrs;
	}

	public void setActvErrs(int actvErrs) {
		this.actvErrs = actvErrs;
	}

	public int getActvTots() {
		return actvTots;
	}

	public void setActvTots(int actvTots) {
		this.actvTots = actvTots;
	}

	public int getClICRErrs() {
		return clICRErrs;
	}

	public void setClICRErrs(int clICRErrs) {
		this.clICRErrs = clICRErrs;
	}

	public int getClICRTots() {
		return clICRTots;
	}

	public void setClICRTots(int clICRTots) {
		this.clICRTots = clICRTots;
	}

	public int getTxnErrs() {
		return txnErrs;
	}

	public void setTxnErrs(int txnErrs) {
		this.txnErrs = txnErrs;
	}

	public int getTxnTots() {
		return txnTots;
	}

	public void setTxnTots(int txnTots) {
		this.txnTots = txnTots;
	}

	public int getPowers() {
		return powers;
	}

	public void setPowers(int powers) {
		this.powers = powers;
	}

	public String getTamper() {
		return tamper;
	}

	public void setTamper(String tamper) {
		this.tamper = tamper;
	}

	public String getOnline() {
		return online;
	}

	public void setOnline(String online) {
		this.online = online;
	}

	public String getShield() {
		return shield;
	}

	public void setShield(String shield) {
		this.shield = shield;
	}

	public String getStylus() {
		return stylus;
	}

	public void setStylus(String stylus) {
		this.stylus = stylus;
	}

	public String getDownStatus() {
		return downStatus;
	}

	public void setDownStatus(String downStatus) {
		this.downStatus = downStatus;
	}

	public String getAcivStatus() {
		return acivStatus;
	}

	public void setAcivStatus(String acivStatus) {
		this.acivStatus = acivStatus;
	}
	
}
