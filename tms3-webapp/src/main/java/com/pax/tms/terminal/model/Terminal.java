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
package com.pax.tms.terminal.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.apache.commons.lang3.StringUtils;

import com.pax.common.exception.BusinessException;
import com.pax.common.model.AbstractModel;
import com.pax.tms.deploy.model.TerminalDeploy;
import com.pax.tms.location.service.AddressInfo;
import com.pax.tms.res.model.Model;

@Entity
@Table(name = "TMSTTERMINAL")
public class Terminal extends AbstractModel {

	private static final long serialVersionUID = 2931234910594563321L;

	public static final String TSN_REGEX = "^[0-9 A-Z]{8}|^[0-9 A-Z]{10}";
	public static final String US_ZIPCODE_REGEX = "[0-9]{5}";
	public static final String CANADA_ZIPCODE_REGEX = "[A-Za-z][0-9][A-Za-z]\\s{0,1}[0-9][A-Za-z][0-9]";

	public static final int STATUS_DISABLE = 0;

	public static final int STATUS_ENABLE = 1;

	public static final String LIST_URL = "/terminal/list/";

	public static final String ADD_URL = "/terminal/toAdd/";

	public static final String TITLE = "TERMINAL";

	@Id
	@Column(name = "TRM_ID")
	private String tid;

	@Column(name = "TRM_SN")
	private String tsn;

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "PROVINCE")
	private String province;

	@Column(name = "CITY")
	private String city;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	private Model model;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "TRM_STATUS")
	private boolean status = true;

	@Column(name = "ZIP_CODE")
	private String zipCode;

	@Column(name = "TIME_ZONE")
	private String timeZone;

	@Column(name = "DAYLIGHT_SAVING")
	private boolean daylightSaving = false;

	@Column(name = "SYNC_TO_SERVER_TIME")
	private boolean syncToServerTime = false;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "REPORT_TIME")
	private Date reportTime;
	
	@Column(name = "LOCAL_TIME")
	private String localTime;

	@Column(name = "INSTALL_APPS")
	private String installApps;
	
	@Column(name = "SYSMETRIC_KEYS")
    private String sysmetricKeys;
	
	@Column(name = "KEY_REPORT_TIME")
    private Date keyReportTime;

	@Column(name = "CREATOR")
	private String creator;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIER")
	private String modifier;

	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	@OneToMany(mappedBy = "terminal", fetch = FetchType.LAZY)
	private List<TerminalGroup> terminalGroups;
	@OneToMany(mappedBy = "terminal", fetch = FetchType.LAZY)
	private List<TerminalDeploy> terminalDeploys;
	@OneToOne(mappedBy = "terminal", fetch = FetchType.LAZY)
	private TerminalStatus terminalStatus;

	public TerminalStatus getTerminalStatus() {
		return terminalStatus;
	}

	public void setTerminalStatus(TerminalStatus terminalStatus) {
		this.terminalStatus = terminalStatus;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public boolean isDaylightSaving() {
		return daylightSaving;
	}

	public void setDaylightSaving(boolean daylightSaving) {
		this.daylightSaving = daylightSaving;
	}

	public boolean isSyncToServerTime() {
		return syncToServerTime;
	}

	public void setSyncToServerTime(boolean syncToServerTime) {
		this.syncToServerTime = syncToServerTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getReportTime() {
		return reportTime;
	}

	public void setReportTime(Date reportTime) {
		this.reportTime = reportTime;
	}

	public String getLocalTime() {
		return localTime;
	}

	public void setLocalTime(String localTime) {
		this.localTime = localTime;
	}

	public String getInstallApps() {
		return installApps;
	}

	public void setInstallApps(String installApps) {
		this.installApps = installApps;
	}
	
	public String getSysmetricKeys() {
        return sysmetricKeys;
    }

    public void setSysmetricKeys(String sysmetricKeys) {
        this.sysmetricKeys = sysmetricKeys;
    }

    public Date getKeyReportTime() {
        return keyReportTime;
    }

    public void setKeyReportTime(Date keyReportTime) {
        this.keyReportTime = keyReportTime;
    }

    public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getTsn() {
		return tsn;
	}

	public void setTsn(String tsn) {
		this.tsn = tsn;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tsn == null) ? 0 : tsn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Terminal other = (Terminal) obj;
		if (tsn == null) {
			if (other.tsn != null)
				return false;
		} else if (!tsn.equals(other.tsn))
			return false;
		return true;
	}

	public List<TerminalGroup> getTerminalGroups() {
		return terminalGroups;
	}

	public void setTerminalGroups(List<TerminalGroup> terminalGroups) {
		this.terminalGroups = terminalGroups;
	}

	public List<TerminalDeploy> getTerminalDeploys() {
		return terminalDeploys;
	}

	public void setTerminalDeploys(List<TerminalDeploy> terminalDeploys) {
		this.terminalDeploys = terminalDeploys;
	}

	public void setAddressInfo(AddressInfo addressInfo) {
		setCountry(addressInfo.getCountryName());
		setProvince(addressInfo.getProvinceName());
		setCity(addressInfo.getCityName());
		setAddress(addressInfo.getAddress());
		setZipCode(addressInfo.getZipCode().toUpperCase());
	}

	public static void validateZipCode(String zipCode, String tid, Long countryId) {
		if (StringUtils.isEmpty(zipCode)) {
			throw new BusinessException("msg.importFile.tsn.zipcodeRequired", new String[] { tid });
		}
		if (zipCode.trim().length() > 7) {
			throw new BusinessException("msg.importFile.tsn.zipcodeOverLength", new String[] { tid });
		}

	}
	/**
	 * 算法去除父组，只留tree deep最深的组名
	 * symbol是规定好了组名分隔符，组名不允许存在"/"
	 * @param params 需要去除父组的集合组名
	 * @return 返回不同组的treeDeep最深的所有组名
	 */
	public static List<String> removeParentGroupName(List<String> params){
		//算法改良
		List<String> lists = new ArrayList<>();
		List<String> result = new ArrayList<>();
		if(params.isEmpty())
		{
    		return result;
    	}
		String symbol="/";
		//这里将一级根组的名后也加上"/"，方便后续统一处理
		for(String s:params)
		{
			if(null != s)
			{
				if(-1 == s.indexOf(symbol))
				{
					lists.add(s+"/");
	    		}
				else
				{
					lists.add(s);
				}
			}
		}
		//这个临时数组是必须的，不然会导致lists中的index混乱
		List<String> tempLists = new ArrayList<>();
		for(String s:lists)
		{
			tempLists.add(s);
		}
		for(int i=0; i<lists.size(); i++)
		{
			for(int j=0; j<lists.size(); j++)
			{
				if(!lists.get(i).equals(lists.get(j)))
				{
					if(lists.get(i).contains(lists.get(j)) && 
					   (lists.get(i).substring(0, lists.get(i).indexOf(symbol))).equals(lists.get(j).substring(0, lists.get(j).indexOf(symbol)))){
						//这里使用迭代器去除数组元素
						Iterator<String> it = tempLists.iterator();
				        while(it.hasNext()){
				            String str = (String)it.next();
				            if(lists.get(j).equals(str)){
				                it.remove();
				            }        
				        }
					}
				}
			}
		}
		for(String s:tempLists)
		{
			if(symbol.equals((s.substring(s.length()-1, s.length()))))
			{
				result.add(s.replace(symbol, ""));
			}
			else
			{
				result.add(s);
			}
		}
		return result;
	}
	
	public static List<Map<String, Object>> removeMapParentGroupName(List<Map<String, Object>> trmGroupList){
		return null;
	}

}
