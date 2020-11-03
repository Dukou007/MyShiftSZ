package com.pax.tms.res.dao.impl;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.res.dao.ManufactureDao;
import com.pax.tms.res.model.Manufacture;

@Repository("manufactureDaoImpl")
public class ManufactureDaoImpl extends BaseHibernateDao<Manufacture, String> implements ManufactureDao {

}
