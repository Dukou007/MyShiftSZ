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
package com.pax.tms.group.dao.impl;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.tms.group.dao.ImportFileDao;
import com.pax.tms.group.model.ImportFile;
import com.pax.tms.group.web.form.QueryFileForm;

@Repository("importFileDaoImpl")
public class ImportFileDaoImpl extends BaseHibernateDao<ImportFile, Long> implements ImportFileDao {

	@Override
	protected <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {

		QueryFileForm form = (QueryFileForm) command;

		Assert.notNull(form.getGroupId());
		CriteriaWrapper wrapper = createCriteriaWrapper(ImportFile.class, "file");
		wrapper.setProjection(Arrays.asList("id", "fileName", "fileSize", "modifyDate", "status"));

		wrapper.eq("file.group.id", form.getGroupId());
		if (StringUtils.equals(form.getFileType(), ImportFile.GROUP_TYPE)) {
			wrapper.eq("file.fileType", ImportFile.GROUP_TYPE);
		}else if (StringUtils.equals(form.getFileType(), ImportFile.KEY_TYPE)) {
		    wrapper.eq("file.fileType", ImportFile.KEY_TYPE);
        }else {
			wrapper.eq("file.fileType", ImportFile.TERMINAL_TYPE);
		}
		String sn = form.getSn();
		if (StringUtils.isNotEmpty(sn)) {
		    wrapper.eq("file.sn", sn);
        }

		if (ordered) {
			wrapper.addOrder(form, "createDate", ORDER_DESC);
		}

		return wrapper;
	}

	@Override
	public ImportFile getImportFile(String fileName, Long groupId, String fileType) {
		String hql = "select importFile from ImportFile importFile where importFile.fileName=:fileName and "
				+ " importFile.group.id=:groupId and importFile.fileType=:fileType";
		return uniqueResult(createQuery(hql, ImportFile.class).setParameter("fileName", fileName)
				.setParameter("groupId", groupId).setParameter("fileType", fileType));
	}

}
