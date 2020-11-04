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
package com.pax.tms.user.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;
import com.pax.tms.location.service.AddressInfo;

@Entity
@Table(name = "PUBTUSER")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends AbstractModel {

	private static final long serialVersionUID = 6630347964418345884L;

	public static final String DIRECTORY_LDAP = "LDAP";
	public static final String DIRECTORY_TMS = "Local";
	public static final String USER_LIST_URL = "/user/list/";
	public static final String MY_PROFILE_URL = "/myProfile/view/";
	public static final String CHG_PWD_URL = "/myProfile/toChangePassword/";
	public static final String USER_TITLE = "USER MANAGEMENT";
	public static final String MY_PROFILE_TITLE = "USER PROFILE";

	@Id
	@GeneratedValue(generator = "PUBTUSER_ID_GEN")
	@GenericGenerator(name = "PUBTUSER_ID_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = "PUBTUSER_ID"), @Parameter(name = "increment_size", value = "5"),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "USER_ID")
	private Long id;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "FULLNAME")
	private String fullname;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PHONE")
	private String phone;

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "PROVINCE")
	private String province;

	@Column(name = "CITY")
	private String city;

	@Column(name = "ZIP_CODE")
	private String zipCode;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "USER_DESC")
	private String description;

	@Column(name = "DIRECTORY")
	private String directory;

	@Column(name = "IS_SYS")
	private boolean sys = false;

	@Column(name = "IS_LDAP")
	private boolean ldap = false;

	@Column(name = "IS_LOCKED")
	private boolean locked = false;

	@Column(name = "LOCKED_DATE")
	private Date lockedDate;

	@Column(name = "IS_ENABLED")
	private boolean enabled = true;

	@Column(name = "ENABLED_DATE")
	private Date enableDate;

	@Column(name = "DISABLED_AFTER_DAYS")
	private Integer disabledAfterDays = 0;

	@Column(name = "REMOVED_AFTER_DAYS")
	private Integer removeAfterDays = 0;

	@Column(name = "LAST_LOGIN_DATE")
	private Date lastLoginDate;

	@Column(name = "ACC_EXPIRY_DATE")
	private Date accExpDate;

	@Column(name = "USER_PWD")
	private String password;

	@Column(name = "ENCRYPT_SALT")
	private String pwdEncSalt;

	@Column(name = "ENCRYPT_ALG")
	private String pwdEncAlg;

	@Column(name = "ENCRYPT_ITERATION_COUNT")
	private Integer pwdEncIt;

	@Column(name = "LAST_PWD_CHG_DATE")
	private Date pwdChgDate;

	@Column(name = "MAX_PWD_AGE")
	private Integer pwdChgIv;

	@Column(name = "FORCE_CHG_PWD")
	private boolean forceChgPwd = false;

	@Column(name = "FAILED_LOGIN_COUNT")
	private int passwordErrorCount = 0;

	@Column(name = "PREF_DASHBOARD_REAL")
	private String realTime;

	@Column(name = "PREF_DASHBOARD_USAGE")
	private String usage;

	@Column(name = "CREATOR")
	private String creator;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIER")
	private String modifier;

	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	@OneToMany(mappedBy = "user")
	private List<UserRole> userRoleList;

	@OneToMany(mappedBy = "user")
	private List<UserGroup> userGroupList;

	@Transient
	private String appKey;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Date getLockedDate() {
		return lockedDate;
	}

	public void setLockedDate(Date lockedDate) {
		this.lockedDate = lockedDate;
	}

	public Date getEnableDate() {
		return enableDate;
	}

	public void setEnableDate(Date enableDate) {
		this.enableDate = enableDate;
	}

	public List<UserRole> getUserRoleList() {
		return userRoleList;
	}

	public void setUserRoleList(List<UserRole> userRoleList) {
		this.userRoleList = userRoleList;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isSys() {
		return sys;
	}

	public void setSys(boolean sys) {
		this.sys = sys;
	}

	public Integer getDisabledAfterDays() {
		return disabledAfterDays;
	}

	public void setDisabledAfterDays(Integer disabledAfterDays) {
		this.disabledAfterDays = disabledAfterDays;
	}

	public Integer getRemoveAfterDays() {
		return removeAfterDays;
	}

	public void setRemoveAfterDays(Integer removeAfterDays) {
		this.removeAfterDays = removeAfterDays;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public int getPasswordErrorCount() {
		return passwordErrorCount;
	}

	public void setPasswordErrorCount(int passwordErrorCount) {
		this.passwordErrorCount = passwordErrorCount;
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

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public boolean isLdap() {
		return ldap;
	}

	public void setLdap(boolean ldap) {
		this.ldap = ldap;
	}

	public Date getAccExpDate() {
		return accExpDate;
	}

	public void setAccExpDate(Date accExpDate) {
		this.accExpDate = accExpDate;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getPwdEncSalt() {
		return pwdEncSalt;
	}

	public void setPwdEncSalt(String pwdEncSalt) {
		this.pwdEncSalt = pwdEncSalt;
	}

	public String getPwdEncAlg() {
		return pwdEncAlg;

	}

	public void setPwdEncAlg(String pwdEncAlg) {
		this.pwdEncAlg = pwdEncAlg;
	}

	public Integer getPwdEncIt() {
		return pwdEncIt;
	}

	public void setPwdEncIt(Integer pwdEncIt) {
		this.pwdEncIt = pwdEncIt;
	}

	public Integer getPwdChgIv() {
		return pwdChgIv;
	}

	public void setPwdChgIv(Integer pwdChgIv) {
		this.pwdChgIv = pwdChgIv;
	}

	public Date getPwdChgDate() {
		return pwdChgDate;
	}

	public void setPwdChgDate(Date pwdChgDate) {
		this.pwdChgDate = pwdChgDate;
	}

	public String getRealTime() {
		return realTime;
	}

	public void setRealTime(String realTime) {
		this.realTime = realTime;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public List<UserGroup> getUserGroupList() {
		return userGroupList;
	}

	public void setUserGroupList(List<UserGroup> userGroupList) {
		this.userGroupList = userGroupList;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (username == null) {
			return false;
		}
		if (obj instanceof User) {
			return username.equalsIgnoreCase(((User) obj).username);
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (username == null) {
			return 520;
		}
		return username.toUpperCase().hashCode();
	}

	public boolean isAccountNonExpired() {
		if (accExpDate == null) {
			return true;
		}
		if (accExpDate.after(new Date())) {
			return true;
		}
		return false;
	}

	public boolean isAccountNonLocked() {
		return !(locked || (passwordErrorCount > 5));
	}

	public boolean isCredentialsNonExpired() {
		if (this.forceChgPwd) {
			return false;
		}
		if (this.pwdChgDate != null && this.pwdChgIv != null && this.pwdChgIv > 0) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, this.pwdChgIv);
			if (calendar.before(Calendar.getInstance())) {
				return false;
			}
		}
		return true;
	}

	public String getStatus() {
		if (!isAccountNonLocked()) {
			return "Locked";
		} else {
			return "Normal";
		}
	}

	public void addGroup(UserGroup userGroup) {
		if (userGroupList == null) {
			userGroupList = new ArrayList<UserGroup>();
		}
		userGroup.setUser(this);
		userGroupList.add(userGroup);
	}

	public void addRole(UserRole userRole) {
		if (userRoleList == null) {
			userRoleList = new ArrayList<UserRole>();
		}
		userRole.setUser(this);
		userRoleList.add(userRole);
	}

	public void setAddressInfo(AddressInfo addressInfo) {
		setCountry(addressInfo.getCountryName());
		setProvince(addressInfo.getProvinceName());
		setCity(addressInfo.getCityName());
		setZipCode(addressInfo.getZipCode());
		setAddress(addressInfo.getAddress());
	}

	public boolean isForceChgPwd() {
		return forceChgPwd;
	}

	public void setForceChgPwd(boolean forceChgPwd) {
		this.forceChgPwd = forceChgPwd;
	}

	public boolean isSiteAdmin(Long userId) {

		return userId.equals(1L);

	}

}
