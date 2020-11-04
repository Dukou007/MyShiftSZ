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

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.exception.BusinessException;
import com.pax.common.model.AbstractModel;

/**
 * 
 * Entity represents the database table PUBTCOUNTRY.
 * 
 * @author Elliott.Z
 *
 */
@Entity
@Table(name = "PUBTCOUNTRY")
public class Country extends AbstractModel {

	private static final long serialVersionUID = -1524905619759633800L;

	public static final String US_ZIPCODE_REGEX = "[0-9]{5}";
	public static final String CANADA_ZIPCODE_REGEX = "[A-Za-z][0-9][A-Za-z]\\s{0,1}[0-9][A-Za-z][0-9]";

	@Id
	@GeneratedValue(generator = "PUBTCOUNTRY_ID_GEN")
	@GenericGenerator(name = "PUBTCOUNTRY_ID_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = "PUBTCOUNTRY_ID"),
			@Parameter(name = "increment_size", value = "1"), @Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "COUNTRY_ID")
	private Long id;

	@Column(name = "COUNTRY_CODE")
	private String code;

	@Column(name = "ABBR_NAME")
	private String abbrName;

	@OrderColumn
	@Column(name = "COUNTRY_NAME")
	private String name;

	@Column(name = "COUNTRY_DESC")
	private String description;

	@Column(name = "TRANS_CCY_CODE")
	private String transCurrencyCode;

	@Column(name = "TRANS_CCY_EXP")
	private Integer transCurrentExp;

	@Column(name = "TRANS_REFER_CCY_CODE")
	private String transReferCurrencyCode;

	@Column(name = "TRANS_REFER_CCY_EXP")
	private Integer transReferCurrencyExp;

	@Column(name = "creator")
	protected String creator;

	@Column(name = "create_date")
	protected Date createDate;

	@Column(name = "modifier")
	protected String modifier;

	@Column(name = "modify_date")
	protected Date modifyDate;

	@OneToMany(mappedBy = "country")
	private List<Province> provinces;

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

	public String getTransCurrencyCode() {
		return transCurrencyCode;
	}

	public void setTransCurrencyCode(String transCurrencyCode) {
		this.transCurrencyCode = transCurrencyCode;
	}

	public Integer getTransCurrentExp() {
		return transCurrentExp;
	}

	public void setTransCurrentExp(Integer transCurrentExp) {
		this.transCurrentExp = transCurrentExp;
	}

	public String getTransReferCurrencyCode() {
		return transReferCurrencyCode;
	}

	public void setTransReferCurrencyCode(String transReferCurrencyCode) {
		this.transReferCurrencyCode = transReferCurrencyCode;
	}

	public Integer getTransReferCurrencyExp() {
		return transReferCurrencyExp;
	}

	public void setTransReferCurrencyExp(Integer transReferCurrencyExp) {
		this.transReferCurrencyExp = transReferCurrencyExp;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public String getCreator() {
		return creator;
	}

	public String getModifier() {
		return modifier;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public List<Province> getProvinces() {
		return provinces;
	}

	public void setProvinces(List<Province> provinces) {
		this.provinces = provinces;
	}

	public static void validateZipCode(Long countryId, String zipCode) {
		if (!"".equals(zipCode) && zipCode != null) {
			if (countryId != null) {
				if (countryId == 1) {
					if (!Pattern.matches(Country.US_ZIPCODE_REGEX, zipCode)) {
						throw new BusinessException("msg.illegalZipCode");
					}
				} else {
					if (!Pattern.matches(Country.CANADA_ZIPCODE_REGEX, zipCode)) {
						throw new BusinessException("msg.illegalZipCode");
					}
				}

			} else {
				if (zipCode.length() > 7) {
					throw new BusinessException("msg.illegalZipCode");
				}
			}
		}
	}

}