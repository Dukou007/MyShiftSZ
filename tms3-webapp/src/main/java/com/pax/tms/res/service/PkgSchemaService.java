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

import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import com.pax.common.service.IBaseService;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.report.domain.ParamHistoryInfo;
import com.pax.tms.res.model.PkgSchema;
import com.pax.tms.res.web.form.AddPkgSchemaForm;
import com.pax.tms.res.web.form.EditPkgSchemaForm;
import io.vertx.core.json.JsonArray;

public interface PkgSchemaService extends IBaseService<PkgSchema, Long> {

	List<PkgSchema> getPkgSchemaList(String pkgName, String pkgVersion);

	PkgSchema validatePkgSchema(Long pkgSchemaId);

	Long getSysInitPkgSchema(Long pkgId);

	void save(AddPkgSchemaForm command);

	Document getPkgSchemaTemplate(Long pkgId);

	Map<String, String> getParamSetFromMongoDB(String paramSetId);

	Document getPkgSchemaBySchemaPath(String filepath);

	void edit(EditPkgSchemaForm command);

	JsonArray getSchemaHtmlByPkgSchemaId(Long pkgSchemaId, boolean isTemp);

	void deletePkgSchemas(List<Long> pkgIds);

	List<String> getPkgSchemaIds(List<Long> pkgList);

	void delete(Long pkgId, Long pkgSchemaId, BaseForm command);

	Map<String, String> setParamValueForSchema(Document doc, Map<String, String> defaultParams,
			Map<String, String> newParams, Map<String, String> oldParams, List<ParamHistoryInfo> paramHisList);

	Map<String, String> setParamValueForSchema(Document doc, Map<String, String> defaultParams,
			Map<String, String> newParams);
}
