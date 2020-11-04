/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: // Detail description about the function of this module,		
 *             // interfaces with the other modules, and dependencies. 		
 * Revision History:		
 * Date	                 Author	                Action
 * 20170112	             Jaden           	    Modify
 * ============================================================================		
 */
package com.pax.tms.res.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.pax.common.service.IBaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.group.model.Group;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.web.form.AddOfflineKeyForm;
import com.pax.tms.res.web.form.AssignPkgForm;
import com.pax.tms.res.web.form.EditPkgForm;

public interface OfflineKeyService extends IBaseService<Pkg, Long> {

    Map<String, Object> save(AddOfflineKeyForm command);

	void edit(Long keyId, EditPkgForm command);

	void dismiss(List<Long> offlineKeyList, BaseForm command);

	void delete(List<Long> offlineKeyList, BaseForm command);

	void assign(Long[] keyId, AssignPkgForm command);

	List<String> getGroupNames(Long keyId);

	List<String> getOfflineKeyNamesByGroupId(Long groupId, String type);

	List<String> getOfflineKeyNamesByGroupIdAndDestmodel(Long groupId, String destModel, String type);

	List<String> getOfflineKeyVersionsByName(String name, Long groupId);

	Pkg getOfflineKeyByNameAndVersion(String name, String version);

	Pkg validateOfflineKey(Long keyId);

	void dismissByGroup(Group group, BaseForm command);

	List<Map<String, Object>> getOfflineKeyStatusChangedMessage(Collection<Long> keyIds, String op);

	void sendOfflineKeyStatusChangedMessage(List<Map<String, Object>> msgList);

	List<Map<String, Object>> getOfflineKeyListByName(String name, Long groupId);

}
