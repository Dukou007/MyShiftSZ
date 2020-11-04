package com.pax.tms.report.dao;


import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.monitor.model.EventTrm;

@Repository("EventListDaoImpl")
public class EventListDaoImpl extends BaseHibernateDao<EventTrm, Long> implements EventListDao {

}
