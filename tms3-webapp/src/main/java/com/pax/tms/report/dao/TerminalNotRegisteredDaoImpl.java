/*		
 * ============================================================================		
 *  COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.	
 * Description:search terminal not registered data
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Alan Jiang           	Modify
 * ============================================================================		
 */
package com.pax.tms.report.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.tms.monitor.model.UnregisteredTerminal;
import com.pax.tms.report.web.form.QueryTerminalNotRegisteredForm;

@Repository("terminalNotRegisteredDaoImpl")
public class TerminalNotRegisteredDaoImpl extends BaseHibernateDao<UnregisteredTerminal, Long>
		implements TerminalNotRegisteredDao {

	@Override
	protected <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {
		QueryTerminalNotRegisteredForm form = (QueryTerminalNotRegisteredForm) command;
		CriteriaWrapper wrapper = createCriteriaWrapper(UnregisteredTerminal.class, "nrt");
		wrapper.createAlias("nrt.model", "m");

		if (form.getStartTime() != null && form.getEndTime() != null) {
			wrapper.ge("lastDate", form.getStartTime());
			wrapper.le("lastDate", form.getEndTime());
		} else if (form.getStartTime() != null) {
			wrapper.ge("lastDate", form.getStartTime());
		} else if (form.getEndTime() != null) {
			wrapper.le("lastDate", form.getEndTime());
		}

		if (form.getTerminalType() != null) {
			wrapper.eq("m.id", form.getTerminalType());
		}

		Map<String, String> columns = new HashMap<String, String>();
		columns.put("trmSn", "trmSn");
		columns.put("m.name", "terminalType");
		columns.put("sourceIp", "sourceIp");
		columns.put("lastDate", "lastDate");
		wrapper.setProjection(columns);

		wrapper.fuzzy(form.getFuzzyCondition(), "trmSn", "m.name");

		if (ordered) {
			wrapper.addOrder(form, "lastDate", ORDER_DESC);
			wrapper.addOrder("trmSn", ORDER_ASC);
		}
		return wrapper;
	}

	@Override
	public void deleteTerminalNotRegisters(Collection<String> newTsns) {
		String sql = "delete from TMSTTRM_UNREG where TRM_SN=?";
		doBatchExecute(sql, newTsns.iterator(), (st, tsn) -> st.setString(1, tsn));

	}

	@Override
	public UnregisteredTerminal getUnregisteredTerminalBySN(String tsn) {
		String hsql = "select ut from UnregisteredTerminal ut where ut.trmSn=:tsn";
		return createQuery(hsql,UnregisteredTerminal.class).setParameter("tsn", tsn).uniqueResult();
	}

}
