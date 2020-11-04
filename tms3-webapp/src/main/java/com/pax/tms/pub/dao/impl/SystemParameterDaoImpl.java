/*
 * ============================================================================
 * = COPYRIGHT				
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.		
 * ============================================================================		
 */
package com.pax.tms.pub.dao.impl;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.pub.dao.SystemParameterDao;
import com.pax.tms.pub.model.SystemParameter;

@Repository("systemParameterDaoImpl")
public class SystemParameterDaoImpl extends BaseHibernateDao<SystemParameter, String> implements SystemParameterDao {

    @Override
    public SystemParameter findByParaKey(String paraKey) {
        String hql = "from SystemParameter syspara where LOWER(syspara.key)=?";
        return uniqueResult(hql, paraKey.toLowerCase());
    }
}
