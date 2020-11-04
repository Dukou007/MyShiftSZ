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
package com.pax.tms.deploy.dao.impl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.deploy.dao.DeployParaDao;
import com.pax.tms.deploy.domain.DeployParaInfo;
import com.pax.tms.deploy.model.DeployPara;
import com.pax.tms.user.security.UTCTime;

@Repository("deployParaDaoImpl")
public class DeployParaDaoImpl extends BaseHibernateDao<DeployPara, Long> implements DeployParaDao {

	@Override
	public List<Long> getDeployParaIds(Long groupId, String destModel, Long pkgId) {
		String hql = "select dp.id as id from DeployPara dp,Deploy d,GroupAncestor ga,GroupDeploy gd"
				+ " where gd.deploy.id=d.id and d.id=dp.deploy.id and gd.group.id=ga.group.id"
				+ " ga.ancestor.id=:groupId and d.model.id=:destModel and d.pkg.id=:pkgId)";

		return createQuery(hql, Long.class, false).getResultList();
	}

	@Override
	public void updateDeployParas(List<Long> deployParaIds, DeployParaInfo deployParaInfo, BaseForm form) {
		String sql = "update TMSTDEPLOY_PARA set FILE_SIZE=?,TEMPLATE_ID=?,FILE_VERSION=? ,FILE_PATH=? MODIFIER=?,"
				+ "  MODIFY_DATE=? where id=?";
		Timestamp timestamp = new Timestamp(form.getRequestTime().getTime());
		String username = form.getLoginUsername();

		doBatchExecute(sql, deployParaIds.iterator(), (st, deployParaId) -> {
			st.setLong(1, deployParaInfo.getFileSize());
			st.setLong(2, deployParaInfo.getTemplateId());
			st.setString(3, deployParaInfo.getFileVersion());
			st.setString(4, deployParaInfo.getFilePath());
			st.setLong(5, deployParaId);
			st.setString(6, username);
			st.setTimestamp(7, timestamp, UTCTime.UTC_CLENDAR);

		});

	}

	@Override
	public List<String> getDeployParaPaths(List<Long> deployIds) {
		if (CollectionUtils.isEmpty(deployIds)) {
			return Collections.emptyList();
		}
		String hql = "select dp.filePath from DeployPara dp where dp.deploy.id in (:deployIds)";
		return createQuery(hql, String.class).setParameter("deployIds", deployIds).getResultList();
	}

	@Override
	public void deleteDeployParas(List<Long> deployIds) {
		String sql = "delete from TMSTDEPLOY_PARA   " + " where DEPLOY_ID=?";
		doBatchExecute(sql, deployIds.iterator(), (st, deployId) -> st.setLong(1, deployId));
	}

}
