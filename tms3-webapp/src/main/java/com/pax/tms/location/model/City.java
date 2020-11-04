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
package com.pax.tms.location.model;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.exception.BusinessException;
import com.pax.common.model.AbstractModel;

/**
 * Entity for database table PUBTCITY.
 * 
 * @author Elliott.Z
 *
 */
@Entity
@Table(name = "PUBTCITY")
public class City extends AbstractModel implements Serializable {

	private static final long serialVersionUID = -1734260471675168729L;

	public static final String CITY_NAME_REGEX = "[/]+|[<>\"]|\\\\$";

	@Id
	@GeneratedValue(generator = "PUBTCITY_ID_GEN")
	@GenericGenerator(name = "PUBTCITY_ID_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = "PUBTCITY_ID"), @Parameter(name = "increment_size", value = "1"),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "CITY_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PROVINCE_ID")
	private Province province;

	@Column(name = "ABBR_NAME")
	private String abbrName;

	@Column(name = "CITY_NAME")
	private String name;

	@Column(name = "CITY_DESC")
	private String description;

	@Column(name = "creator")
	protected String creator;

	@Column(name = "create_date")
	protected Date createDate;

	@Column(name = "modifier")
	protected String modifier;

	@Column(name = "modify_date")
	protected Date modifyDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAbbrName() {
		return abbrName;
	}

	public void setAbbrName(String abbrName) {
		this.abbrName = abbrName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
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

	public static void validateCityName(String cityName) {
		if (checkCityNameIsNull(cityName) && checkCityNameValidate(cityName)) {
			throw new BusinessException("msg.illegalCityName");
		}
	}

	private static boolean checkCityNameValidate(String cityName) {
		return !(!Pattern.matches(City.CITY_NAME_REGEX, cityName) && cityName.length() <= 35);
	}

	private static boolean checkCityNameIsNull(String cityName) {
		return !"".equals(cityName) && cityName != null;
	}

}