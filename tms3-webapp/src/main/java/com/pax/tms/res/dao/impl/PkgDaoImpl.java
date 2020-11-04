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
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.sql.JoinType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.common.web.form.BaseForm;
import com.pax.tms.app.broadpos.para.ApplicationModule;
import com.pax.tms.res.dao.PkgDao;
import com.pax.tms.res.model.MetaInfo;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgGroup;
import com.pax.tms.res.model.PkgProgram;
import com.pax.tms.res.model.PkgType;
import com.pax.tms.res.model.ProgramFile;
import com.pax.tms.res.web.form.QueryPkgForm;
import com.pax.tms.user.security.UTCTime;

@Repository("pkgDaoImpl")
public class PkgDaoImpl extends BaseHibernateDao<Pkg, Long> implements PkgDao {

	private static final String GROUP_ID = "groupId";

	@SuppressWarnings("unchecked")
	@Override
	public <T, S extends Serializable> List<T> page(S command, int start, int length) {
		QueryPkgForm form = (QueryPkgForm) command;
		String filter = form.getFuzzyCondition();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT pg.PKG_ID AS \"id\", pg.PKG_NAME AS \"name\", pg.PKG_VERSION AS \"version\",pg.TRM_SN AS \"sn\","
				+ "pg.MODEL_ID AS \"terminalType\", pg.PGM_TYPE AS \"pgmType\", pg.PKG_STATUS AS \"status\", pg.FILE_SIZE AS \"fileSize\", "
				+ " pg.MODIFY_DATE AS \"modifyDate\", T.ver_count AS \"verCount\", pg.PKG_SIGNED AS \"signed\" ");
		sql.append(" FROM ");

		sql.append(" ( SELECT MAX(P.PKG_ID) PKG_ID, COUNT(P.PKG_VERSION) VER_COUNT "
				+ " FROM tmstpackage P INNER JOIN tmstpkg_group TG ON TG.PKG_ID = P .PKG_ID "
				+ " WHERE	TG.GROUP_ID = :groupId ");
		if(0 == form.getQueryType()){
		    sql.append(" AND  p.PKG_TYPE = :pkgType ");
		}else{
		    sql.append(" AND  p.PKG_TYPE != :pkgType ");
		}
		String sn = form.getSn();
		if(StringUtils.isNotEmpty(sn)){
		    sql.append(" AND  p.TRM_SN = :sn ");
		}
		if (StringUtils.isNotEmpty(filter)) {
			sql.append(" AND  ( LOWER(P.PKG_NAME) LIKE :pkgName ESCAPE '!' "
					+ " OR LOWER(P.PKG_VERSION) LIKE :pkgVersion ESCAPE '!' "
					+ " OR LOWER(P.PGM_TYPE) LIKE :pgmType ESCAPE '!' ) ");
		}

		sql.append(" GROUP BY P.PKG_NAME ) T ");
		sql.append(" LEFT JOIN tmstpackage pg ON T .PKG_ID = pg.PKG_ID ORDER BY PG.MODIFY_DATE DESC ");

		NativeQuery<T> query = super.createNativeQuery(new String(sql));
		query.setFirstResult(start).setMaxResults(length).setParameter(GROUP_ID, form.getGroupId());
		query.setParameter("pkgType", PkgType.OFFLINEKEY.getPkgName());
		if (StringUtils.isNotEmpty(sn)) {
		    query.setParameter("sn", sn);
        }
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter("pkgName", toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter("pkgVersion", toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter("pgmType", toMatchString(filter, MatchMode.ANYWHERE, true));
		}
		return super.mapResult(query).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S extends Serializable> long count(S command) {
		QueryPkgForm form = (QueryPkgForm) command;
		String filter = form.getFuzzyCondition();

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(pg.PKG_ID) totals  ");
		sql.append(" FROM ");

		sql.append(" (SELECT MAX(P.PKG_ID) PKG_ID "
				+ " FROM tmstpackage P INNER JOIN tmstpkg_group TG ON TG.PKG_ID = P .PKG_ID "
				+ " WHERE	TG.GROUP_ID = :groupId ");

		if(0 == form.getQueryType()){
            sql.append(" AND  p.PKG_TYPE = :pkgType ");
        }else{
            sql.append(" AND  p.PKG_TYPE != :pkgType ");
        }
		
		String sn = form.getSn();
        if(StringUtils.isNotEmpty(sn)){
            sql.append(" AND p.TRM_SN = :sn ");
        }
		if (StringUtils.isNotEmpty(filter)) {
			sql.append(" AND  ( LOWER(P.PKG_NAME) LIKE :pkgName ESCAPE '!' "
					+ " OR LOWER(P.PKG_VERSION) LIKE :pkgVersion ESCAPE '!' "
					+ " OR LOWER(P.PGM_TYPE) LIKE :pgmType ESCAPE '!' ) ");
		}

		sql.append(" GROUP BY P .PKG_NAME ) T ");
		sql.append(" LEFT JOIN tmstpackage pg ON T .PKG_ID = pg.PKG_ID ");

		NativeQuery<Number> query = super.createNativeQuery(new String(sql));
		query.setParameter(GROUP_ID, form.getGroupId());
		query.setParameter("pkgType", PkgType.OFFLINEKEY.getPkgName());
		if (StringUtils.isNotEmpty(sn)) {
            query.setParameter("sn", sn);
        }
		if (StringUtils.isNotEmpty(filter)) {
			query.setParameter("pkgName", toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter("pkgVersion", toMatchString(filter, MatchMode.ANYWHERE, true));
			query.setParameter("pgmType", toMatchString(filter, MatchMode.ANYWHERE, true));
		}
		Number obj = query.getSingleResult();
		return obj.longValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getPkgListByName(String name, Long groupId) {
		String hql = "select p.id as id, p.name as name, p.version as version, p.sn as sn, p.pgmType as pgmType, "
				+ " p.status as status, p.fileSize as fileSize, p.modifyDate as modifyDate , p.signed as signed "
				+ " from Pkg p where exists (select pkgGroup.id from PkgGroup pkgGroup "
				+ " where pkgGroup.pkg.id=p.id and pkgGroup.group.id=:groupId) "
				+ " and p.name=:name order by p.id desc";
		Query<Map<String, Object>> query = createQuery(hql).setParameter("name", name).setParameter("groupId", groupId);
		return super.mapResult(query).getResultList();
	}

	@Override
	protected <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {

		QueryPkgForm form = (QueryPkgForm) command;
		Assert.notNull(form.getGroupId());
		CriteriaWrapper wrapper = createCriteriaWrapper(Pkg.class, "p");
		wrapper.createAlias("p.model", "model", JoinType.LEFT_OUTER_JOIN);
		wrapper.setProjection(
				Arrays.asList("id", "name", "version", "pgmType", "model.name", "sn", "fileSize", "status", "modifyDate","signed"));
		if (StringUtils.isNotEmpty(form.getFuzzyCondition())) {
			wrapper.fuzzy(form.getFuzzyCondition(), "name", "version", "pgmType", "model.name");

		}
		if(0 == form.getQueryType()){
		    wrapper.eq("p.type", PkgType.OFFLINEKEY.getPkgName());
        }else{
            wrapper.ne("p.type", PkgType.OFFLINEKEY.getPkgName());
        }
		DetachedCriteria permissionCriteria = wrapper.subquery(PkgGroup.class, "pg", "p.id", "pg.pkg.id");
		permissionCriteria.add(Restrictions.eq("pg.group.id", form.getGroupId()));
		permissionCriteria.setProjection(Projections.property("pg.id"));

		wrapper.exists(permissionCriteria);
		if (ordered) {
			wrapper.addOrder(form, "modifyDate", ORDER_DESC);
			wrapper.addOrder("name", ORDER_ASC);
		}

		return wrapper;
	}

	@Override
	public List<Long> getNotAcceptancePkgIds(List<Long> pkgIds, Long userId) {
		String hql = "select p.id from Pkg p where p.id in(:pkgIds)"
				+ " and not exists (select pkgGroup.id from  PkgGroup pkgGroup ,UserGroup ug"
				+ " where ug.group.id=pkgGroup.group.id and pkgGroup.pkg.id=p.id and ug.user.id=:userId)";
		return createQuery(hql, Long.class).setParameter("userId", userId).setParameterList("pkgIds", pkgIds)
				.getResultList();

	}

	@Override
	public void deactivate(Collection<Long> pkgList, BaseForm command) {

		updatePkgStatus(pkgList, command, Pkg.DISABLE);

	}

	private void updatePkgStatus(Collection<Long> pkgList, BaseForm command, long status) {
		String sql = "update TMSTPACKAGE  set PKG_STATUS=?,CREATOR=?,CREATE_DATE=?,MODIFIER=?,MODIFY_DATE=? where PKG_ID=? ";
		Timestamp timestamp = new Timestamp(command.getRequestTime().getTime());
		String username = command.getLoginUsername();

		doBatchExecute(sql, pkgList.iterator(), (st, pkgId) -> {
			st.setLong(1, status);
			st.setString(2, username);
			st.setTimestamp(3, timestamp, UTCTime.UTC_CLENDAR);
			st.setString(4, username);
			st.setTimestamp(5, timestamp, UTCTime.UTC_CLENDAR);
			st.setLong(6, pkgId);

		});

	}

	@Override
	public void activate(Collection<Long> pkgList, BaseForm command) {
		updatePkgStatus(pkgList, command, Pkg.ENABLE);

	}

	@Override
	public void delete(Collection<Long> pkgIds) {
		String sql = "delete from TMSTPACKAGE where PKG_ID=?";
		String sql1 = "delete from TMSTPKG_PROGRAM where PKG_ID=?";
		String sql2 = "delete from  TMSTPROGRAM_FILE where PKG_ID=?";

		doBatchExecute(sql, pkgIds.iterator(), (st, pkgId) -> st.setLong(1, pkgId)

		);
		doBatchExecute(sql1, pkgIds.iterator(), (st, pkgId) -> st.setLong(1, pkgId)

		);
		doBatchExecute(sql2, pkgIds.iterator(), (st, pkgId) -> st.setLong(1, pkgId));

	}

	@Override
	public boolean existNameAndVersion(String name, String version) {
		Pkg pkg = getPkg(name, version);
		return pkg == null ? false : true;

	}

	private Pkg getPkg(String name, String version) {
		String hql = "select pkg from Pkg pkg where pkg.name=:name and pkg.version=:version";
		return uniqueResult(createQuery(hql, Pkg.class).setParameter("name", name).setParameter("version", version));

	}

	@Override
	public List<String> getPkgVersionsByName(String name, Long groupId) {
		String hql = "select p.version from Pkg p where exists"
				+ " (select pkgGroup.id from PkgGroup pkgGroup where pkgGroup.pkg.id=p.id"
				+ " and pkgGroup.group.id=:groupId) and p.name=:name and p.status=:status";
		return createQuery(hql, String.class).setParameter("name", name).setParameter("groupId", groupId)
				.setParameter("status", true).getResultList();
	}

	@Override
	public Pkg getPkgByNameAndVersion(String name, String version) {

		return getPkg(name, version);
	}

	@Override
	public List<MetaInfo> getMetaInfo(Long pkgId) {
		String hql = "select pf.pkgProgram.id as metaId,pf.filePath as filePath,pem.attrValue as apmName "
				+ "from ProgramFile pf,PkgEntryMetadata pem where pf.pkg.id=:pkgId and pf.type=:type and "
				+ "pf.pkgProgram.id=pem.pkgEntryId and pf.pkg.id=pem.pkg.id and pem.attrKey=:attrKey";
		return createQuery(hql, MetaInfo.class, false).setParameter("pkgId", pkgId)
				.setParameter("type", ProgramFile.PROGRAM_FILE_KEY).setParameter("attrKey", "APM_NAME").getResultList();
	}

	@Override
	public List<PkgProgram> getPkgProgramInfo(Long pkgId) {
		String hql = "select pg from PkgProgram pg where pg.pkg.id=:pkgId and pg.version is not null and pg.version <>' '";
		Query<PkgProgram> query = createQuery(hql, PkgProgram.class).setParameter("pkgId", pkgId);

		return query.getResultList();
	}

	@Override
	public List<Pkg> getExistDeployPkgs(List<Long> pkgIds) {

		String hql = "select p from Pkg p  where p.id in(:pkgIds) and exists("
				+ " select d.id from Deploy d where p.id=d.pkg.id) ";
		return createQuery(hql, Pkg.class).setParameterList("pkgIds", pkgIds).getResultList();
	}

	@Override
	public List<String> programFilePaths(List<Long> pkgIds) {

		String hql = "select pf.filePath from ProgramFile pf where pf.pkg.id in (:pkgIds)";
		return createQuery(hql, String.class).setParameterList("pkgIds", pkgIds).getResultList();
	}

	@Override
	public List<String> getPkgFilePaths(List<Long> pkgIds) {
		String hql = "select p.filePath from Pkg p where p.id in (:pkgIds)";
		return createQuery(hql, String.class).setParameterList("pkgIds", pkgIds).getResultList();
	}

	@Override
	public List<Pkg> getNamesById(List<Long> pkgIds) {
		String hql = "select p from Pkg p where p.id in (:pkgIds)";
		return createQuery(hql, Pkg.class).setParameter("pkgIds", pkgIds).getResultList();
	}

	@Override
	public List<Long> getpkgListByGroupId(Long groupId, Integer queryType) {
		String hql = "select p.id from Pkg p where exists (select pg.id from PkgGroup pg"
				+ " where pg.pkg.id=p.id and pg.group.id=:groupId)";
		if(0 == queryType){
		    hql += " and p.type = :pkgType ";
		}else {
		    hql += " and p.type != :pkgType ";
        }
		return createQuery(hql, Long.class).setParameter("groupId", groupId).setParameter("pkgType", PkgType.OFFLINEKEY.getPkgName()).getResultList();
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
	public List<Long> getPackageIds(String pkgName, String pkgVersion, String pgmType, PkgType pkgType) {
		String hql = "select pkg.id from Pkg pkg where pkg.name=:name and pkg.version=:version and pkg.pgmType=:pgmType and pkg.type=:pkgType";
		return createQuery(hql, Long.class).setParameter("name", pkgName).setParameter("version", pkgVersion)
				.setParameter("pgmType", pgmType).setParameter("pkgType", pkgType.getPkgName()).getResultList();
	}

	@Override
	public List<Pkg> getPackages(String pkgName, String pkgVersion, String pgmType, PkgType pkgType) {
		String hql = "from Pkg pkg where pkg.name=:name and pkg.version=:version and pkg.pgmType=:pgmType and pkg.type=:pkgType";
		return createQuery(hql, Pkg.class).setParameter("name", pkgName).setParameter("version", pkgVersion)
				.setParameter("pgmType", pgmType).setParameter("pkgType", pkgType.getPkgName()).getResultList();
	}

	@Override
	public List<Pkg> getPackages(String name, PkgType pkgType) {
		String hql = "from Pkg pkg where pkg.name=:name and pkg.type=:pkgType";
		return createQuery(hql, Pkg.class).setParameter("name", name).setParameter("pkgType", pkgType.getPkgName())
				.getResultList();
	}

	@Override
	public List<Long> getGrantedGroups(long pkgId) {
		String hql = "select pg.group.id from PkgGroup pg where pg.pkg.id=:pkgId";
		return createQuery(hql, Long.class).setParameter("pkgId", pkgId).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getPkgIdsAndTime() {
		String hql = "select pkg.id as pkgId ,pkg.modifyDate as usageTime from Pkg pkg ";
		return mapResult(createQuery(hql)).getResultList();
	}
	
	@Override
    public List<Pkg> getPkgByName(String name, Long groupId) {
        String hql = "select p from Pkg p where exists"
                + " (select pkgGroup.id from PkgGroup pkgGroup where pkgGroup.pkg.id=p.id"
                + " and pkgGroup.group.id=:groupId) and p.name=:name and p.status=:status order by p.version desc ";
        return createQuery(hql, Pkg.class).setParameter("name", name).setParameter("groupId", groupId)
                .setParameter("status", true).getResultList();
    }
	
	public List<Pkg> getLatestPkgByGroup(Long groupId, Integer queryType){
        StringBuilder hql = new StringBuilder();
        hql.append(" SELECT pg.PKG_ID AS \"id\", pg.PKG_NAME AS \"name\", pg.PKG_VERSION AS \"version\",pg.TRM_SN AS \"sn\","
                + " pg.PGM_TYPE AS \"pgmType\", pg.PKG_TYPE AS \"type\", pg.FILE_SIZE AS \"fileSize\", "
                + " pg.MODIFY_DATE AS \"modifyDate\" FROM ");
        hql.append(" ( SELECT MAX(p.PKG_ID) pkgid FROM tmstpackage p INNER JOIN tmstpkg_group TG ON TG.PKG_ID = p.PKG_ID  WHERE TG.GROUP_ID = :groupId AND p.PKG_STATUS = :status ");
        if(0 == queryType){
            hql.append(" AND p.PKG_TYPE = :pkgType ");
        }else{
            hql.append(" AND p.PKG_TYPE != :pkgType ");
        }

        hql.append(" GROUP BY p.PKG_NAME ) T ");
        hql.append(" LEFT JOIN tmstpackage pg ON T.pkgid = pg.PKG_ID ORDER BY pg.MODIFY_DATE DESC ");
        
        Map<String, Type> scalarMap = new HashMap<>(3);
        scalarMap.put("id", StandardBasicTypes.LONG);
        scalarMap.put("name", StandardBasicTypes.STRING);
        scalarMap.put("version", StandardBasicTypes.STRING);
        scalarMap.put("sn", StandardBasicTypes.STRING);
        scalarMap.put("pgmType", StandardBasicTypes.STRING);
        scalarMap.put("type", StandardBasicTypes.STRING);
        scalarMap.put("fileSize", StandardBasicTypes.LONG);
        scalarMap.put("modifyDate", StandardBasicTypes.TIMESTAMP);
        
        NativeQuery<Pkg> query = super.createNativeQuery(hql.toString(), Pkg.class, scalarMap);
        query.setParameter("groupId", groupId).setParameter("status", true).setParameter("pkgType", PkgType.OFFLINEKEY.getPkgName());
        return query.getResultList();
        
    }
	
	@Override
    public List<Pkg> getLatestPkgByGroupAndModel(Long groupId,String model, Integer latestType){
	    StringBuilder hql = new StringBuilder();
	    // latestType  1：查最高版本的  0 : 查最新上传的
	    hql.append(" SELECT pg.PKG_ID AS \"id\", pg.PKG_NAME AS \"name\", pg.PKG_VERSION AS \"version\",pg.TRM_SN AS \"sn\","
                + " pg.PGM_TYPE AS \"pgmType\", pg.PKG_TYPE AS \"type\", pg.FILE_SIZE AS \"fileSize\", "
                + " pg.MODIFY_DATE AS \"modifyDate\" FROM tmstpackage pg INNER JOIN ");
	    if (latestType == 1) {
            hql.append(" ( SELECT MAX(p.PKG_VERSION + 0) AS PKG_VERSION,p.PKG_NAME AS PKG_NAME FROM tmstpackage p INNER JOIN tmstpkg_group TG ON TG.PKG_ID = p.PKG_ID  WHERE TG.GROUP_ID = :groupId ");
            hql.append(" AND p.PKG_TYPE = :pkgType ");
            if(StringUtils.isNotBlank(model)){
                hql.append(" AND p.MODEL_ID = :model ");
            }
            hql.append(" GROUP BY p.PKG_NAME ) T ");
            hql.append(" ON pg.PKG_NAME = T.PKG_NAME AND pg.PKG_VERSION = T.PKG_VERSION ORDER BY pg.PKG_ID ");
        }else{
            hql.append(" ( SELECT MAX(p.PKG_ID) pkgid FROM tmstpackage p INNER JOIN tmstpkg_group TG ON TG.PKG_ID = p.PKG_ID  WHERE TG.GROUP_ID = :groupId ");
            hql.append(" AND p.PKG_TYPE = :pkgType ");
            if(StringUtils.isNotBlank(model)){
                hql.append(" AND p.MODEL_ID= :model ");
            }
            hql.append(" GROUP BY p.PKG_NAME ) T ");
            hql.append(" ON T.pkgid = pg.PKG_ID ORDER BY pg.MODIFY_DATE DESC ");
        }
	    Map<String, Type> scalarMap = new HashMap<>(3);
        scalarMap.put("id", StandardBasicTypes.LONG);
        scalarMap.put("name", StandardBasicTypes.STRING);
        scalarMap.put("version", StandardBasicTypes.STRING);
        scalarMap.put("sn", StandardBasicTypes.STRING);
        scalarMap.put("pgmType", StandardBasicTypes.STRING);
        scalarMap.put("type", StandardBasicTypes.STRING);
        scalarMap.put("fileSize", StandardBasicTypes.LONG);
        scalarMap.put("modifyDate", StandardBasicTypes.TIMESTAMP);
        
        NativeQuery<Pkg> query = super.createNativeQuery(hql.toString(), Pkg.class, scalarMap);
        query.setParameter("groupId", groupId);
        query.setParameter("pkgType", PkgType.OFFLINEKEY.getPkgName());
        if(StringUtils.isNotBlank(model)){
            query.setParameter("model", model);
        }
        return query.getResultList();
	}

	@Override
    public List<Pkg> getPackageUnCheck() {
        String hql = "select p from Pkg p where p.signed is null";
        return createQuery(hql, Pkg.class).getResultList();
    }
}
