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
 * 20170209              Aaron                  Modify
 * ============================================================================		
 */
package com.pax.tms.res.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.res.dao.ModelDao;
import com.pax.tms.res.model.Model;

@Repository("modelDaoImpl")
public class ModelDaoImpl extends BaseHibernateDao<Model, String> implements ModelDao {

	@Override
	public List<Model> getListByGroupId(Long groupId) {
		String hql = "select m from Model m where m.id in( select t.model.id from Terminal t "
				+ " where t.id in( select tg.terminal.id from TerminalGroup tg where tg.group.id "
				+ " in(select ga.group.id from GroupAncestor ga where ga.ancestor.id=:groupId)))";
		return createQuery(hql, Model.class).setParameter("groupId", groupId).getResultList();
	}

	@Override
	public List<Model> getList() {
		String hql = "select m from Model m order by m.name asc";
		return createQuery(hql, Model.class).getResultList();
	}

}
