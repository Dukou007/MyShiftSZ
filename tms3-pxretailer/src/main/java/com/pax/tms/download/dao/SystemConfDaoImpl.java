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
package com.pax.tms.download.dao;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.download.model.SystemConf;

@Repository("systemConfDaoImpl")
public class SystemConfDaoImpl extends BaseHibernateDao<SystemConf, String> implements SystemConfDao {

    @Override
    public SystemConf findByParaKey(String paraKey) {
        String hql = "from SystemConf syspara where LOWER(syspara.key)=?";
        return uniqueResult(hql, paraKey.toLowerCase());
    }
}
