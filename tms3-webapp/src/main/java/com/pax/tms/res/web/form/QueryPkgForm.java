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
package com.pax.tms.res.web.form;

import com.pax.common.web.form.QueryForm;

public class QueryPkgForm extends QueryForm {

	private static final long serialVersionUID = 1L;
	
	private String sn;
	private Integer queryType ; // 1:query package 0:query offlineKey

	
    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Integer getQueryType() {
        return queryType;
    }

    public void setQueryType(Integer queryType) {
        this.queryType = queryType;
    }
	
}
