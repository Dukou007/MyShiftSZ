/*		
 * ============================================================================		
 *  COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.	
 * Description:search terminal download data	
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Alan Jiang           	Modify
 * ============================================================================		
 */
package com.pax.tms.report.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TemporalType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.tms.deploy.model.DownOrActvStatus;
import com.pax.tms.report.web.form.QueryTerminalDownloadForm;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.terminal.model.TerminalDownload;

@Repository("terminalDownloadDaoImpl")
public class TerminalDownloadDaoImpl extends BaseHibernateDao<TerminalDownload, Long> implements TerminalDownloadDao {

	@Override
	protected <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {
		QueryTerminalDownloadForm form = (QueryTerminalDownloadForm) command;

		CriteriaWrapper wrapper = createCriteriaWrapper(TerminalDownload.class, "td");
		wrapper.createAlias("td.tid", "t");
		wrapper.createAlias("t.model", "m");

		checkTerminalOrGroup(wrapper, form);
		searchByDownloadSchedule(wrapper, form);
		searchByDownloadTime(wrapper, form);
		searchByActivationSchedule(wrapper, form);
		searchByActivationTime(wrapper, form);

		if (StringUtils.isNotEmpty(form.getPkgType())) {
			wrapper.eq("pkgType", form.getPkgType());
		}
		if (form.getTerminalType() != null) {
			wrapper.eq("m.id", form.getTerminalType());
		}
		if (StringUtils.isNotEmpty(form.getDownStatusType())) {
			if ("Expired".equals(form.getDownStatusType())) {
				Conjunction con = Restrictions.conjunction();
				con.add(Restrictions.le("expireDate", new Date()));
				con.add(Restrictions.eq("downStatus", DownOrActvStatus.parse("Pending")));
				wrapper.addCriterion(
						Restrictions.or(Restrictions.eq("downStatus", DownOrActvStatus.parse("Expired")), con));
			} else if ("Pending".equals(form.getDownStatusType())) {
				Conjunction con1 = Restrictions.conjunction();
				con1.add(Restrictions.eq("downStatus", DownOrActvStatus.parse("Pending")));
				con1.add(Restrictions.ge("expireDate", new Date()));
				Conjunction con2 = Restrictions.conjunction();
				con2.add(Restrictions.eq("downStatus", DownOrActvStatus.parse("Pending")));
				con2.add(Restrictions.isNull("expireDate"));
				wrapper.addCriterion(Restrictions
						.or(Restrictions.eq("downStatus", DownOrActvStatus.parse("Downloading")), con1, con2));
			} else {
				wrapper.eq("downStatus", DownOrActvStatus.parse(form.getDownStatusType()));
			}
		}
		if (StringUtils.isNotEmpty(form.getActiStatusType())) {
			if ("-".equals(form.getActiStatusType())) {
				Conjunction con = Restrictions.conjunction();
				con.add(Restrictions.le("expireDate", new Date()));
				con.add(Restrictions.eq("actvStatus", DownOrActvStatus.parse("Pending")));
				con.add(Restrictions.ne("downStatus", DownOrActvStatus.parse("Downloading")));
				con.add(Restrictions.ne("downStatus", DownOrActvStatus.parse("Success")));
				wrapper.addCriterion(Restrictions
						.or(Restrictions.eq("actvStatus", DownOrActvStatus.parse("Not need activition")), con));
			} else if ("Pending".equals(form.getActiStatusType())) {
				Conjunction con = Restrictions.conjunction();
				con.add(Restrictions.ge("expireDate", new Date()));
				wrapper.addCriterion((Restrictions.eq("actvStatus", DownOrActvStatus.parse("Pending"))));
				wrapper.addCriterion(Restrictions.or(con, Restrictions.isNull("expireDate"),
						Restrictions.eq("downStatus", DownOrActvStatus.parse("Downloading")),
						Restrictions.eq("downStatus", DownOrActvStatus.parse("Success"))));
			} else {
				wrapper.eq("actvStatus", DownOrActvStatus.parse(form.getActiStatusType()));
			}
		}

		/**
		 * condition from dashboard
		 */
		if (form.getDayStart() != null) {
			searchByDashboard(wrapper, form);
		}

		wrapper.fuzzy(form.getFuzzyCondition(), "t.tid", "m.name");

		Map<String, String> columns = new HashMap<String, String>();
		columns.put("tid.tid", "tid");
		columns.put("m.name", "terminalType");
		columns.put("pkgName", "pkgName");
		columns.put("pkgType", "pkgType");
		columns.put("pkgVersion", "pkgVersion");
		columns.put("downSchedule", "downSchedule");
		columns.put("actvSchedule", "actvSchedule");
		columns.put("downStatus", "downStatus");
		columns.put("actvStatus", "actvStatus");
		columns.put("actvTime", "actvTime");
		columns.put("downStartTime", "downStartTime");
		columns.put("downEndTime", "downEndTime");
		columns.put("expireDate", "expireDate");
		wrapper.setProjection(columns);

		if (ordered) {
			wrapper.addOrder(form, "modifyDate", ORDER_DESC);
			wrapper.addOrder("tid.tid", ORDER_ASC);
		}
		return wrapper;
	}

	private void searchByActivationTime(CriteriaWrapper wrapper, QueryTerminalDownloadForm form) {
		if ("Activation Time".equals(form.getTimeType())) {
			if (form.getStartTime() != null && form.getEndTime() != null) {
				wrapper.ge("actvTime", form.getStartTime());
				wrapper.le("actvTime", form.getEndTime());
			} else if (form.getStartTime() != null) {
				wrapper.ge("actvTime", form.getStartTime());
			} else {
				wrapper.le("actvTime", form.getEndTime());
			}
		}
	}

	private void searchByActivationSchedule(CriteriaWrapper wrapper, QueryTerminalDownloadForm form) {
		if ("Activation Schedule".equals(form.getTimeType())) {
			if (form.getStartTime() != null && form.getEndTime() != null) {
				wrapper.ge("actvSchedule", form.getStartTime());
				wrapper.le("actvSchedule", form.getEndTime());
			} else if (form.getStartTime() != null) {
				wrapper.ge("actvSchedule", form.getStartTime());
			} else {
				wrapper.le("actvSchedule", form.getEndTime());
			}
		}
	}

	private void searchByDownloadTime(CriteriaWrapper wrapper, QueryTerminalDownloadForm form) {
		if ("Download Time".equals(form.getTimeType())) {
			if (form.getStartTime() != null && form.getEndTime() != null) {
				wrapper.ge("downEndTime", form.getStartTime());
				wrapper.le("downEndTime", form.getEndTime());
			} else if (form.getStartTime() != null) {
				wrapper.ge("downEndTime", form.getStartTime());
			} else {
				wrapper.le("downEndTime", form.getEndTime());
			}
		}
	}

	/**
	 * condition from dashboard
	 * 
	 * @param wrapper
	 * @param form
	 */
	private void searchByDashboard(CriteriaWrapper wrapper, QueryTerminalDownloadForm form) {
		String itemStatus = "";
		if (StringUtils.isNotEmpty(form.getDownStatusType())) {
			itemStatus = form.getDownStatusType();
			if ("Pending".equals(itemStatus)) {
				Disjunction dis = Restrictions.disjunction();
				dis.add(Restrictions.ge("expireDate", new Date()));
				dis.add(Restrictions.isNull("expireDate"));
				wrapper.addCriterion(
						Restrictions.or(Restrictions.eq("downStatus", DownOrActvStatus.parse("Downloading")), dis));
			} else {
				wrapper.ge("downEndTime", form.getDayStart());
			}
		} else if (StringUtils.isNotEmpty(form.getActiStatusType())) {
			itemStatus = form.getActiStatusType();
			if (!"Pending".equals(itemStatus)) {
				wrapper.ge("actvTime", form.getDayStart());
			}
		}
	}

	private void searchByDownloadSchedule(CriteriaWrapper wrapper, QueryTerminalDownloadForm form) {
		if ("Download Schedule".equals(form.getTimeType())) {
			if (form.getStartTime() != null && form.getEndTime() != null) {
				wrapper.ge("downSchedule", form.getStartTime());
				wrapper.le("downSchedule", form.getEndTime());
			} else if (form.getStartTime() != null) {
				wrapper.ge("downSchedule", form.getStartTime());
			} else {
				wrapper.le("downSchedule", form.getEndTime());
			}
		}
	}

	private void checkTerminalOrGroup(CriteriaWrapper wrapper, QueryTerminalDownloadForm form) {
		if (StringUtils.isEmpty(form.getTsn())) {
			DetachedCriteria criteria = wrapper.subquery(Terminal.class, "ter", "ter.tid", "td.tid.tid");
			criteria.createAlias("ter.terminalGroups", "tg");
			criteria.createAlias("tg.group", "g");
			criteria.add(Restrictions.in("g.id", form.getGroupId()));
			criteria.setProjection(Projections.property("ter.tid"));
			wrapper.exists(criteria);
		} else {
			wrapper.eq("tid.tid", form.getTsn());
		}
	}

	@Override
	public void deleteTerminalDownload(Long deployId) {
		String hql = "delete from TerminalDownload td where td.deploy.id=:deployId and (td.downStatus=:downStatus1 or td.downStatus=:downStatu2) ";
		createQuery(hql).setParameter("deployId", deployId).setParameter("downStatus1", DownOrActvStatus.PENDING)
				.setParameter("downStatu2", DownOrActvStatus.DOWNLOADING).executeUpdate();

	}

	@Override
	public void deleteTerminalDownloads(List<Long> deployIds) {
		if (CollectionUtils.isEmpty(deployIds)) {
			return;
		}
		String sql = "delete from TMSTTRMDWNL where DEPLOY_ID=? and ( DWNL_STATUS=? or DWNL_STATUS=?)";
		doBatchExecute(sql, deployIds.iterator(), (st, deployId) -> {
			st.setLong(1, deployId);
			st.setString(2, DownOrActvStatus.PENDING.name());
			st.setString(3, DownOrActvStatus.DOWNLOADING.name());
		});

	}

	@Override
	public void updateTerminalDownloadStatus(List<Long> overDueDeployIds) {
		if (CollectionUtils.isEmpty(overDueDeployIds)) {
			return;
		}
		String sql = "update TMSTTRMDWNL set DWNL_STATUS=?,ACTV_STATUS=? where DEPLOY_ID=? AND DWNL_STATUS IN (?,?)";
		doBatchExecute(sql, overDueDeployIds.iterator(), (st, deployId) -> {
			st.setString(1, DownOrActvStatus.EXPIRED.name());
			st.setString(2, DownOrActvStatus.NOACTIVITION.name());
			st.setLong(3, deployId);
			st.setString(4, DownOrActvStatus.PENDING.name()); // Set PENDING、DOWNLOADING to EXPIRED
			st.setString(5, DownOrActvStatus.DOWNLOADING.name());					
		});

	}

	@Override
	public Long getTerminalReportbyTSN(String tsn) {
		String hql = "select count(*) from TerminalDownload where tsn=:tsn";
		long count = createQuery(hql, Long.class).setParameter("tsn", tsn).getSingleResult();
		return count > 0 ? count : 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getDownloadPkgUsageInfo() {
		String hql = "select distinct td.downEndTime as usageTime , pkg.id as pkgId from TerminalDownload td , "
				+ " Pkg pkg where td.downEndTime = (select MAX(td1.downEndTime) from TerminalDownload td1 where "
				+ " td.pkgName = td1.pkgName and td.pkgVersion=td1.pkgVersion) and td.pkgName = pkg.name and td.pkgVersion=pkg.version";
		return mapResult(createQuery(hql)).getResultList();
	}
	
	@Override
	public Long getStatusCount(Long groupId, Date dayStart, String itemStatus) {
		StringBuilder sql = new StringBuilder();
		String dsql = "('PENDING','DOWNLOADING')";
		if(DownOrActvStatus.SUCCESS.getName().toLowerCase().equals(itemStatus.toLowerCase())){
			dsql = "('SUCCESS')";
		}
		// query table : TMSTTRMDWNL
		String fromPendSql = " FROM TMSTTRM_GROUP TG INNER JOIN TMSTTRMDWNL TD ON TG.TRM_ID=TD.TRM_ID ";
		String gSql = " AND TG.GROUP_ID=:groupId ";
		sql.append(" SELECT count(*) as ITEMCOUNT " + fromPendSql
				+ " WHERE ((TD.DWNL_END_TIME > :startTime OR "
				+ "(((TD.EXPIRE_DATE >:nowDate OR TD.EXPIRE_DATE is NULL) AND TD.DWNL_END_TIME is NULL))))"
				+ gSql + " AND TD.DWNL_STATUS IN"+ dsql);
		Long count = createNativeQuery(sql.toString(), BigDecimal.class)
								.setParameter("startTime", dayStart, TemporalType.TIMESTAMP)
								.setParameter("nowDate", new Date(), TemporalType.TIMESTAMP)
								.setParameter("groupId", groupId).getSingleResult().longValue();
										
		return count;
	}
}
