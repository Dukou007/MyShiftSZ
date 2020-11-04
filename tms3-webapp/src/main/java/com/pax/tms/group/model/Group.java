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
package com.pax.tms.group.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.exception.BusinessException;
import com.pax.common.model.AbstractModel;
import com.pax.common.util.RegexMatchUtils;
import com.pax.tms.location.service.AddressInfo;
import com.pax.tms.terminal.model.TerminalGroup;
import com.pax.tms.user.model.UserGroup;

@Entity
@Table(name = "PUBTGROUP")
public class Group extends AbstractModel {

	private static final long serialVersionUID = 1756201650138846581L;

	public static final String ID_SEQUENCE_NAME = "PUBTGROUP_ID";
	public static final String INCREMENT_SIZE = "5";
	public static final String INVALID_GROUP_NAME_REGEX = "[/]+|[<>{}()=\"]|\\\\$";
	public static final String GROUP_NAME_SPACE = "\\s+";
	public static final String US_ZIPCODE_REGEX = "[0-9]{5}";
	public static final String CANADA_ZIPCODE_REGEX = "[A-Za-z][0-9][A-Za-z]\\s{0,1}[0-9][A-Za-z][0-9]";
	public static final String LIST_URL = "/group/list/";
	public static final String TITLE = "GROUP";
	public static final int GROUP_PATH_MAX_LENGTH = 2000;
	public static final String ADD_GROUP_PERMISSION = "tms:group:add";
	@Id
	@GeneratedValue(generator = ID_SEQUENCE_NAME + "_GEN")
	@GenericGenerator(name = ID_SEQUENCE_NAME + "_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "GROUP_ID")
	private Long id;

	@Column(name = "GROUP_CODE")
	private String code;

	@Column(name = "GROUP_NAME")
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ID")
	private Group parent;

	@OneToMany(mappedBy = "parent")
	private List<Group> childern;

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "PROVINCE")
	private String province;

	@Column(name = "CITY")
	private String city;

	@Column(name = "ZIP_CODE")
	private String zipCode;

	@Column(name = "TIME_ZONE")
	private String timeZone;

	@Column(name = "DAYLIGHT_SAVING")
	private boolean daylightSaving = false;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "TREE_DEPTH")
	private int treeDepth;

	@Column(name = "ID_PATH")
	private String idPath;

	@Column(name = "NAME_PATH")
	private String namePath;

	@Column(name = "SUB_COUNT")
	private int subCount;

	@Column(name = "CREATOR")
	private String creator;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIER")
	private String modifier;

	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	@OneToMany(mappedBy = "group")
	private List<GroupAncestor> ancestors;

	@OneToMany(mappedBy = "ancestor")
	private List<GroupAncestor> descendants;

	@OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
	private List<UserGroup> userGroupList;

	@OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
	private List<TerminalGroup> terminalGroups;

	@Transient
	private boolean handleTerminal = false;
	@Transient
	private Long parentId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Group getParent() {
		return parent;
	}

	public void setParent(Group parent) {
		this.parent = parent;
	}

	public String getIdPath() {
		return idPath;
	}

	public void setIdPath(String idPath) {
		this.idPath = idPath;
	}

	public String getNamePath() {
		return namePath;
	}

	public void setNamePath(String namePath) {
		this.namePath = namePath;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public List<GroupAncestor> getAncestors() {
		return ancestors;
	}

	public void setAncestors(List<GroupAncestor> ancestors) {
		this.ancestors = ancestors;
	}

	public List<GroupAncestor> getDescendants() {
		return descendants;
	}

	public void setDescendants(List<GroupAncestor> descendants) {
		this.descendants = descendants;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Group other = (Group) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public List<UserGroup> getUserGroupList() {
		return userGroupList;
	}

	public void setUserGroupList(List<UserGroup> userGroupList) {
		this.userGroupList = userGroupList;
	}

	public boolean isRoot() {
		return parent == null ? true : false;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public List<TerminalGroup> getTerminalGroups() {
		return terminalGroups;
	}

	public void setTerminalGroups(List<TerminalGroup> terminalGroups) {
		this.terminalGroups = terminalGroups;
	}

	public boolean isEnterPriceGroup() {

		if (this.treeDepth == 1 || this.treeDepth == 0) {
			return true;
		} else {
			return false;
		}
	}

	public int getTreeDepth() {
		return treeDepth;
	}

	public void setTreeDepth(int treeDepth) {
		this.treeDepth = treeDepth;
	}

	public List<Group> getChildern() {
		return childern;
	}

	public void setChildern(List<Group> childern) {
		this.childern = childern;
	}

	public List<Long> getIdPathArrayList() {
		if (idPath == null || idPath.length() == 0) {
			return Collections.emptyList();
		}
		String[] arr = idPath.split("/");
		List<Long> longArrList = new ArrayList<Long>(arr.length);
		for (String str : arr) {
			longArrList.add(Long.parseLong(str));
		}
		return longArrList;
	}

	public int getSubCount() {
		return subCount;
	}

	public void setSubCount(int subCount) {
		this.subCount = subCount;
	}

	public void setAddressInfo(AddressInfo addressInfo) {
		setCountry(addressInfo.getCountryName());
		setCity(addressInfo.getCityName());
		setProvince(addressInfo.getProvinceName());
		setZipCode(addressInfo.getZipCode().toUpperCase());
		setAddress(addressInfo.getAddress());
	}

	public static void validateGroupName(String groupName) {
		if (StringUtils.isEmpty(groupName)) {
			throw new BusinessException("msg.group.nameRequired");
		}

		if (RegexMatchUtils.contains(groupName, Group.INVALID_GROUP_NAME_REGEX)) {
			throw new BusinessException("msg.group.illegalName", new String[] { groupName });
		}
		if (RegexMatchUtils.isMatcher(groupName, Group.GROUP_NAME_SPACE)) {
			throw new BusinessException("msg.group.illegalNameSpace", new String[] { groupName });
		}

		if (groupName.length() > 60) {
			throw new BusinessException("msg.group.illegalNameLength", new String[] { groupName });
		}
	}

	public static void validateZipCode(String zipCode, String groupName, Long countryId) {
		if (StringUtils.isEmpty(zipCode)) {
			throw new BusinessException("msg.importFile.zipcodeRequired", new String[] { groupName });
		}
		if (zipCode.trim().length() > 7) {
			throw new BusinessException("msg.importFile.zipcodeOverLength", new String[] { groupName });
		}

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

	public boolean isHandleTerminal() {
		return handleTerminal;
	}

	public void setHandleTerminal(boolean handleTerminal) {
		this.handleTerminal = handleTerminal;
	}

    public Long getParentId() {
        if(null != parent){
            return parent.getId();
        }else{
            return 0l;
        }
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
