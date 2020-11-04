/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: list/add/edit/delete AlertCondition list/add/delete AlertOff
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */	
package com.pax.tms.monitor.service;

import java.util.List;

import com.pax.common.service.IBaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.monitor.model.AlertCondition;
import com.pax.tms.monitor.model.AlertOff;
import com.pax.tms.monitor.model.AlertSetting;
import com.pax.tms.monitor.web.form.AddAlertOffForm;
import com.pax.tms.monitor.web.form.EditConditionForm;

public interface AlertConditionService extends IBaseService<AlertCondition, Long> {

	void addAlertCondition(Group group, BaseForm command);

	void editCondition(EditConditionForm[] command, Long groupId);

	void addAlertOff(AddAlertOffForm command);

	List<AlertOff> getAlertOffList(Long settingId);

	List<AlertOff> getAlertOffListByGroupId(Long groupId);

	void deleteAlertOff(Long offId, BaseForm command);

	void deleteCondition(Long groupId, BaseForm command);

	List<AlertCondition> getAlertConditionList(Long groupId, Long userId);

	List<AlertCondition> getAlertConditionListByGroupId(Long groupId);

	AlertSetting findSettingByGroupId(Long groupId);

	void deleteAlertOffInfo(Long userId, Long groupId);
	
	void deleteByGroupId(Long groupId);

}
