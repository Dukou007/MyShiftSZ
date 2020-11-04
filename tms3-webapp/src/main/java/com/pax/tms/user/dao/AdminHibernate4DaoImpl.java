package com.pax.tms.user.dao;

import java.util.List;

import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.CommonHibernateDao;

@Repository()
public class AdminHibernate4DaoImpl extends CommonHibernateDao implements AdminDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.paxsz.tms.admin.dao.AdminDao#executeSql(java.lang.String)
	 */
	@Override
	public void executeSql(String sql) {
		this.getSession().createNativeQuery(sql).executeUpdate();
	}

	@Override
	public List<String> selectSql(String sql) {
	
		@SuppressWarnings("unchecked")
		NativeQuery<String> query = this.getSession().createNativeQuery(sql);
		return query.getResultList();
	}

}