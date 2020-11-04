/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: get UsageMessageInfo/TerminalRealStatusInfo/UsageStatusBar/TerminalRealStatus
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Frank           	    Modify 
 * ============================================================================		
 */
package com.pax.tms.monitor.dao;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.dao.hibernate.CriteriaWrapper;
import com.pax.tms.monitor.model.TerminalLog;
import com.pax.tms.monitor.web.form.TerminalLogFrom;
import com.pax.tms.terminal.model.TerminalGroup;

@Repository("terminalLogDaoImpl")
public class TerminalLogDaoImpl extends BaseHibernateDao<TerminalLog, Long> implements TerminalLogDao {

	
	@Override
    protected <S extends Serializable> CriteriaWrapper getCriteria(S command, boolean ordered) {
        TerminalLogFrom form = (TerminalLogFrom) command;
        CriteriaWrapper wrapper = createCriteriaWrapper(TerminalLog.class, "al");
        wrapper.setProjection(
                Arrays.asList("id", "trmId", "deviceName", "deviceType", "source", "sourceVersion", "sourceType", "severity", "message", "eventLocalTime", "commType","localIp","appInfo"));
        if (StringUtils.isNotEmpty(form.getFuzzyCondition())) {
            wrapper.fuzzy(form.getFuzzyCondition(), "trmId", "deviceName", "message");
        }
        if(null != form.getGroupId() && form.getGroupId() != 1){
            DetachedCriteria privilegeCriteria = wrapper.subquery(TerminalGroup.class, "tg", "tg.terminal.tid", "al.trmId");
            privilegeCriteria.add(Restrictions.eq("tg.group.id", form.getGroupId()));
            privilegeCriteria.setProjection(Projections.property("tg.id"));
            wrapper.exists(privilegeCriteria);
        }

        if (ordered) {
            wrapper.addOrder("id", ORDER_DESC);
        }
        return wrapper;
    }
	@Override
	public void deleteTerminalLogs(long groupId) {
	    String hql ;
	    if(groupId != 1){
	        hql = "delete terLog from TMSTTRM_LOG terLog ,TMSTTRM_GROUP terGroup where terGroup.TRM_ID=terLog.TRM_ID and terGroup.GROUP_ID =:groupId";
	        createNativeQuery(hql).setParameter("groupId", groupId).executeUpdate();
	    }else{
	        hql = "delete terLog from TMSTTRM_LOG terLog";
	        createNativeQuery(hql).executeUpdate();
	    }
        
    }
	
}
