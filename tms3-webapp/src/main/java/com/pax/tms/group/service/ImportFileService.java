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
package com.pax.tms.group.service;

import java.util.Map;

import com.pax.common.service.IBaseService;
import com.pax.tms.group.GroupInfo;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.model.ImportFile;
import com.pax.tms.group.web.form.ImportFileForm;

public interface ImportFileService extends IBaseService<ImportFile, Long> {

	GroupInfo getGroupInfo(ImportFile importFile, boolean isPermissionCreateGroup, ImportFileForm command);

	void saveImport(GroupInfo rootGroupInfo, Group parent, ImportFileForm command, Map<String, Object> resultMap,
			boolean isForbideImportGroup);

	Map<String, Object> save(ImportFileForm command);

}
