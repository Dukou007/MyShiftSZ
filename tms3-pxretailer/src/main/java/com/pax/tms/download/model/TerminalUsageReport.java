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
package com.pax.tms.download.model;

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
public class TerminalUsageReport extends AbstractModel implements Cloneable {

	private static final long serialVersionUID = -5052215535415071107L;

	public static final String TABLE_NAME = "TMSTTRM_REPORT_MSG";
	public static final String INCREMENT_SIZE = "1";
	public static final String ID_SEQUENCE_NAME = TABLE_NAME + "_SEQ";

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
	
	@Column(name = "GROUP_ID")
    private Long groupId;

	@Column(name = "REPORT_TM")
	private String reportTime;

	/**
	 * MSR Failure Read Times
	 */
	@Column(name = "MSR_ERRS")
	private Integer msrErrs;

	/**
	 * MSR Total Read Times
	 */
	@Column(name = "MSR_TOTS")
	private Integer msrTots;

	/**
	 * Contact IC Read Failures
	 */
	@Column(name = "ICR_ERRS")
	private Integer icrErrs;

	/**
	 * Contact IC Read Total
	 */
	@Column(name = "ICR_TOTS")
	private Integer icrTots;

	/**
	 * PIN Encryption Failures
	 */
	@Column(name = "PIN_FAILS")
	private Integer pinFails;

	/**
	 * PIN Encryption Totals
	 */
	@Column(name = "PIN_TOTS")
	private Integer pinTots;

	/**
	 * Signature Read Failures
	 */
	@Column(name = "SIGN_ERRS")
	private Integer signErr;

	/**
	 * Signature Read Total
	 */
	@Column(name = "SIGN_TOTS")
	private Integer signTots;

	/**
	 * Download Failures
	 */
	@Column(name = "DOWN_FAILS")
	private Integer downFails;
	
	/**
     * Download Pending 
     */
    @Column(name = "DOWN_PENDING")
    private Integer downPending;

	/**
	 * Download Total
	 */
	@Column(name = "DOWN_TOTS")
	private Integer downTots;

	/**
	 * Activation Failures
	 */
	@Column(name = "ACTV_FAILS")
	private Integer actvErrs;

	/**
     * Activation Failures
     */
    @Column(name = "ACTV_PENDING")
    private Integer actvPending;
	/**
	 * Activation Total
	 */
	@Column(name = "ACTV_TOTS")
	private Integer actvTots;

	/**
	 * Contactless IC Read Failures
	 */
	@Column(name = "CL_ICR_ERRS")
	private Integer clIcrErrs;

	/**
	 * Contactless IC Read Total
	 */
	@Column(name = "CL_ICR_TOTS")
	private Integer clIcrTots;

	/**
	 * Transaction Failures
	 */
	@Column(name = "TXN_ERRS")
	private Integer txnErrs;

	/**
	 * Transaction Total
	 */
	@Column(name = "TXN_TOTS")
	private Integer txnTots;

	/**
	 * Power Times
	 */
	@Column(name = "POWER_NO")
	private Integer powers;

	public boolean hasValue() {
		if (hasMsrReport() || hasIcrReport()) {
			return true;
		}

		if (hasPinReport() || hasSignReport()) {
			return true;
		}

		if (hasDownReport() || hasActvReport()) {
			return true;
		}

		if (hasClIcrReport() || hasTxnReport()) {
			return true;
		}

		return hasPowersReport();
	}

	private boolean hasMsrReport() {
		return (msrErrs != null && msrErrs > 0) || (msrTots != null && msrTots > 0);
	}

	public boolean hasIcrReport() {
		return (icrErrs != null && icrErrs > 0) || (icrTots != null && icrTots > 0);
	}

	public boolean hasPinReport() {
		return (pinFails != null && pinFails > 0) || (pinTots != null && pinTots > 0);
	}

	public boolean hasSignReport() {
		return (signErr != null && signErr > 0) || (signTots != null && signTots > 0);
	}

	public boolean hasDownReport() {
		return (downFails != null && downFails > 0) || (downTots != null && downTots > 0);
	}

	public boolean hasActvReport() {
		return (actvErrs != null && actvErrs > 0) || (actvTots != null && actvTots > 0);
	}

	public boolean hasClIcrReport() {
		return (clIcrErrs != null && clIcrErrs > 0) || (clIcrTots != null && clIcrTots > 0);
	}

	public boolean hasTxnReport() {
		return (txnErrs != null && txnErrs > 0) || (txnTots != null && txnTots > 0);

	}

	public boolean hasPowersReport() {
		return (powers != null && powers > 0);
	}

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

	public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getReportTime() {
		return reportTime;
	}

	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}

	public Integer getMsrErrs() {
	    if(null == msrErrs){
	        return 0;
	    }
		return msrErrs;
	}

	public void setMsrErrs(Integer msrErrs) {
		this.msrErrs = msrErrs;
	}

	public Integer getMsrTots() {
	    if(null == msrTots){
            return 0;
        }
		return msrTots;
	}

	public void setMsrTots(Integer msrTots) {
		this.msrTots = msrTots;
	}

	public Integer getIcrErrs() {
	    if(null == icrErrs){
            return 0;
        }
		return icrErrs;
	}

	public void setIcrErrs(Integer icrErrs) {
		this.icrErrs = icrErrs;
	}

	public Integer getIcrTots() {
	    if(null == icrTots){
            return 0;
        }
		return icrTots;
	}

	public void setIcrTots(Integer icrTots) {
		this.icrTots = icrTots;
	}

	public Integer getPinFails() {
	    if(null == pinFails){
            return 0;
        }
		return pinFails;
	}

	public void setPinFails(Integer pinFails) {
		this.pinFails = pinFails;
	}

	public Integer getPinTots() {
	    if(null == pinTots){
            return 0;
        }
		return pinTots;
	}

	public void setPinTots(Integer pinTots) {
		this.pinTots = pinTots;
	}

	public Integer getSignErr() {
	    if(null == signErr){
            return 0;
        }
		return signErr;
	}

	public void setSignErr(Integer signErr) {
		this.signErr = signErr;
	}

	public Integer getSignTots() {
	    if(null == signTots){
            return 0;
        }
		return signTots;
	}

	public void setSignTots(Integer signTots) {
		this.signTots = signTots;
	}

	public Integer getDownFails() {
	    if(null == downFails){
            return 0;
        }
		return downFails;
	}

	public void setDownFails(Integer downFails) {
		this.downFails = downFails;
	}
	
	public Integer getDownPending() {
	    if(null == downPending){
            return 0;
        }
        return downPending;
    }

    public void setDownPending(Integer downPending) {
        this.downPending = downPending;
    }

    public Integer getDownTots() {
        if(null == downTots){
            return 0;
        }
		return downTots;
	}

	public void setDownTots(Integer downTots) {
		this.downTots = downTots;
	}

	public Integer getActvErrs() {
	    if(null == actvErrs){
            return 0;
        }
		return actvErrs;
	}

	public void setActvErrs(Integer actvErrs) {
		this.actvErrs = actvErrs;
	}
	
	public Integer getActvPending() {
	    if(null == actvPending){
            return 0;
        }
        return actvPending;
    }

    public void setActvPending(Integer actvPending) {
        this.actvPending = actvPending;
    }

    public Integer getActvTots() {
        if(null == actvTots){
            return 0;
        }
		return actvTots;
	}

	public void setActvTots(Integer actvTots) {
		this.actvTots = actvTots;
	}

	public Integer getClIcrErrs() {
	    if(null == clIcrErrs){
            return 0;
        }
		return clIcrErrs;
	}

	public void setClIcrErrs(Integer clIcrErrs) {
		this.clIcrErrs = clIcrErrs;
	}

	public Integer getClIcrTots() {
	    if(null == clIcrTots){
            return 0;
        }
		return clIcrTots;
	}

	public void setClIcrTots(Integer clIcrTots) {
		this.clIcrTots = clIcrTots;
	}

	public Integer getTxnErrs() {
	    if(null == txnErrs){
            return 0;
        }
		return txnErrs;
	}

	public void setTxnErrs(Integer txnErrs) {
		this.txnErrs = txnErrs;
	}

	public Integer getTxnTots() {
	    if(null == txnTots){
            return 0;
        }
		return txnTots;
	}

	public void setTxnTots(Integer txnTots) {
		this.txnTots = txnTots;
	}

	public Integer getPowers() {
	    if(null == powers){
            return 0;
        }
		return powers;
	}

	public void setPowers(Integer powers) {
		this.powers = powers;
	}

	@Override
	public String toString() {
		return "TerminalUsageReport [reportId=" + reportId + ", terminalId=" + terminalId + ", reportTime=" + reportTime
				+ ", msrErrs=" + msrErrs + ", msrTots=" + msrTots + ", icrErrs=" + icrErrs + ", icrTots=" + icrTots
				+ ", pinFails=" + pinFails + ", pinTots=" + pinTots + ", signErr=" + signErr + ", signTots=" + signTots
				+ ", downFails=" + downFails + ", downTots=" + downTots + ", actvErrs=" + actvErrs + ", actvTots="
				+ actvTots + ", clIcrErrs=" + clIcrErrs + ", clIcrTots=" + clIcrTots + ", txnErrs=" + txnErrs
				+ ", txnTots=" + txnTots + ", powers=" + powers + "]";
	}
	
	@Override  
    public Object clone() {  
	    TerminalUsageReport terminalUsageReport = null;  
        try{  
            terminalUsageReport = (TerminalUsageReport)super.clone();  
        }catch(CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return terminalUsageReport;  
    }  
	
	

}
