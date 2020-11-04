/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: Create/Modify usageThreshold
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Aaron           	    Modify UsageThresholdService
 * ============================================================================		
 */	
package com.pax.tms.group.service;

import java.util.List;

import com.pax.common.service.IBaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.model.UsageThreshold;
import com.pax.tms.group.web.form.EditUsageThresholdForm;

public interface UsageThresholdService extends IBaseService<UsageThreshold, Long>{

	void editUsageThreshold(EditUsageThresholdForm command);
	
	List<UsageThreshold> list(Long groupId);
	
	void saveUsageThreshold(BaseForm command,Group group);
}
