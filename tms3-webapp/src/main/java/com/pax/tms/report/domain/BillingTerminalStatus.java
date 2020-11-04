package com.pax.tms.report.domain;

import java.util.Date;

public class BillingTerminalStatus {
	private String TRM_SN;
	private String MODEL_ID;
	private Date LAST_CONN_TIME;
	private Date ONLINE_SINCE;
	private Date OFFLINE_SINCE;
	
	public String getTRM_SN() {
		return TRM_SN;
	}
	public void setTRM_SN(String tRM_SN) {
		TRM_SN = tRM_SN;
	}
	public String getMODEL_ID() {
		return MODEL_ID;
	}
	public void setMODEL_ID(String mODEL_ID) {
		MODEL_ID = mODEL_ID;
	}
	public Date getLAST_CONN_TIME() {
		return LAST_CONN_TIME;
	}
	public void setLAST_CONN_TIME(Date lAST_CONN_TIME) {
		LAST_CONN_TIME = lAST_CONN_TIME;
	}
    public Date getONLINE_SINCE() {
        return ONLINE_SINCE;
    }
    public void setONLINE_SINCE(Date oNLINE_SINCE) {
        ONLINE_SINCE = oNLINE_SINCE;
    }
    public Date getOFFLINE_SINCE() {
        return OFFLINE_SINCE;
    }
    public void setOFFLINE_SINCE(Date oFFLINE_SINCE) {
        OFFLINE_SINCE = oFFLINE_SINCE;
    }
	
}
