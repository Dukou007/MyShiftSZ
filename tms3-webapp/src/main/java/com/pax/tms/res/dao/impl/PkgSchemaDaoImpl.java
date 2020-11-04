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
package com.pax.tms.res.dao.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.tms.app.broadpos.para.ApplicationModule;
import com.pax.tms.res.dao.PkgSchemaDao;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgSchema;
import com.pax.tms.res.model.ProgramFile;
import com.pax.tms.res.web.form.QueryPkgSchemaForm;

@Repository("templateDaoImpl")
public class PkgSchemaDaoImpl extends BaseHibernateDao<PkgSchema, Long> implements PkgSchemaDao {

	@Override
	protected <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {

		QueryPkgSchemaForm form = (QueryPkgSchemaForm) command;
		Assert.notNull(form.getGroupId());
		CriteriaWrapper wrapper = createCriteriaWrapper(PkgSchema.class, "t");
		wrapper.createAlias("t.pkg", "pk");
		wrapper.setProjection(Arrays.asList("id", "name", "pk.id", "pk.name", "pk.version", "createDate"));
		if (StringUtils.isNotEmpty(form.getFuzzyCondition())) {
			wrapper.fuzzy(form.getFuzzyCondition(), "name", "pk.name", "pk.version");
		}
		DetachedCriteria permissionCriteria = wrapper.subquery(Pkg.class, "p", "p.id", "pk.id");
		permissionCriteria.createAlias("p.pkgGroups", "pgs");
		permissionCriteria.createAlias("pgs.group", "g");
		permissionCriteria.add(Restrictions.eq("g.group.id", form.getGroupId()));
		permissionCriteria.setProjection(Projections.property("p.id"));
		wrapper.exists(permissionCriteria);
		if (ordered) {
			wrapper.addOrder(form, "createDate", ORDER_DESC);
			wrapper.addOrder("name", ORDER_ASC);
		}

		return wrapper;
	}

	@Override
	public List<PkgSchema> getPkgSchemaList(String pkgName, String pkgVersion) {
		String hql = "select ps from PkgSchema ps join Pkg p on ps.pkg.id=p.id  "
				+ "and p.name=:name and p.version=:version";
		return createQuery(hql, PkgSchema.class).setParameter("name", pkgName).setParameter("version", pkgVersion)
				.getResultList();
	}

	@Override
	public List<ApplicationModule> getAppModuleList(Long pkgId) {
		String sql = "SELECT PP.PGM_ID AS programId, PP.PGM_ABBR_NAME AS abbrName, PF.FILE_PATH AS pctFilePath"
				+ " FROM TMSTPACKAGE PKG " + " INNER JOIN TMSTPKG_PROGRAM PP ON PKG.PKG_ID=PP.PKG_ID "
				+ " INNER JOIN TMSTPROGRAM_FILE PF ON PP.PGM_ID=PF.PGM_ID AND PF.FILE_TYPE=:fileType "
				+ " WHERE PKG.PKG_ID=:pkgId";

		Map<String, Type> scalarMap = new HashMap<String, Type>(3);
		scalarMap.put("programId", StandardBasicTypes.LONG);
		scalarMap.put("abbrName", StandardBasicTypes.STRING);
		scalarMap.put("pctFilePath", StandardBasicTypes.STRING);

		NativeQuery<ApplicationModule> query = super.createNativeQuery(sql, ApplicationModule.class, scalarMap);
		query.setParameter("fileType", ProgramFile.CONF_FILE).setParameter("pkgId", pkgId);
		return query.getResultList();
	}

	@Override
	public Long getSysInitPkgSchema(Long pkgId) {
		String hql = "select ps.id from PkgSchema ps where ps.pkg.id=:pkgId and ps.isSys=:isSys";
		return uniqueResult(createQuery(hql, Long.class).setParameter("pkgId", pkgId).setParameter("isSys", true));
	}

	@Override
	public void deletePkgSchemas(List<Long> pkgIds) {

		String sql = "delete from TMSTPKG_SCHEMA where PKG_ID=?";

		doBatchExecute(sql, pkgIds.iterator(), (st, pkgId) -> 
			st.setLong(1, pkgId)
		);
	}

	@Override
	public List<String> getPkgSchemaIds(List<Long> pkgIds) {

		String hql = "select ps.paramSet from PkgSchema ps where ps.pkg.id in (:pkgIds)";

		return createQuery(hql, String.class).setParameterList("pkgIds", pkgIds).getResultList();
	}

	@Override
	public List<String> getPkgSchemaFilePaths(List<Long> pkgIds) {
		String hql = "select ps.filePath from PkgSchema ps where ps.pkg.id in (:pkgIds)";
		return createQuery(hql, String.class).setParameterList("pkgIds", pkgIds).getResultList();
	}

	@Override
	public boolean existNameAndPkgId(String name, Long pkgId) {
		PkgSchema pkgSchema = getPkgSchema(name, pkgId);
		return pkgSchema == null ? false : true;
	}

	private PkgSchema getPkgSchema(String name, Long pkgId) {
		String hql = "select ps from PkgSchema ps where ps.name=:name and ps.pkg.id=:pkgId";
		return uniqueResult(createQuery(hql, PkgSchema.class).setParameter("name", name).setParameter("pkgId", pkgId));
	}
}
