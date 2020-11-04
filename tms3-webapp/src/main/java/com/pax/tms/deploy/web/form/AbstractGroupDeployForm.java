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
package com.pax.tms.deploy.web.form;

import java.util.Date;
import java.util.Map;

public interface AbstractGroupDeployForm {
	Long getPkgId();

	Long getPkgSchemaId();

	String getDestModel();

	Date getDwnlStartTime();

	Date getDwnlEndTime();

	Integer getDownReTryCount();

	Integer getActvReTryCount();

	Date getActvStartTime();

	Map<String, String> getParamMap();

	Long getGroupId();

	String getLoginUsername();

	Date getRequestTime();

	String getGroupName();

	String getTimeZone();

	boolean isDaylightSaving();
	
	boolean isDeletedWhenDone();

}
