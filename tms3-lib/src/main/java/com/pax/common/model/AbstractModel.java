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
package com.pax.common.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.pax.common.service.ICommonService;
import com.pax.common.util.SpringContextUtil;

public abstract class AbstractModel implements java.io.Serializable {

	private static final long serialVersionUID = 2035013017939483936L;

	private static final String COMMON_SERVICE = "CommonService";

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public void save() {
		ICommonService commonService = SpringContextUtil.getBean(COMMON_SERVICE);
		commonService.save(this);
	}

	public void delete() {
		ICommonService commonService = SpringContextUtil.getBean(COMMON_SERVICE);
		commonService.deleteObject(this);
	}

	public void update() {
		ICommonService commonService = SpringContextUtil.getBean(COMMON_SERVICE);
		commonService.update(this);
	}

}
