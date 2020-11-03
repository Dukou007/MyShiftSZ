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
package com.pax.tms.pub.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pax.common.model.AbstractModel;

/**
 * Entity for database table PUBTSYSCONF
 * 
 * @author Elliott.Z
 *
 */
@Entity
@Table(name = "PUBTSYSCONF")
public class SystemParameter extends AbstractModel {

	private static final long serialVersionUID = 1L;

	@Column(name = "CATEGORY")
    private String category;
	
	@Id
	@Column(name = "PARA_KEY")
	private String key;

	@Column(name = "PARA_VALUE")
	private String value;
	

	public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
