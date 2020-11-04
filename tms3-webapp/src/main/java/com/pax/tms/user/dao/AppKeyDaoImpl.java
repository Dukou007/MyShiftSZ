package com.pax.tms.user.dao;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.open.api.model.AppClient;
import org.springframework.stereotype.Repository;

@Repository("appKeyDaoImpl")
public class AppKeyDaoImpl extends BaseHibernateDao<AppClient, Long> implements AppKeyDao {

	@Override
	public AppClient getAppkey(String userName) {
		String hql = "from  AppClient appClient where LOWER(appClient.userName)=:userName";
		return getSession().createQuery(hql, AppClient.class).setParameter("userName", userName.toLowerCase())
				.uniqueResult();
	}

	@Override
	public void removeAppClient(String userName) {
		String hql = "delete from AppClient appClient where LOWER(appClient.userName)=:userName ";
		createQuery(hql).setParameter("userName", userName.toLowerCase()).executeUpdate();

	}

	@Override
	public AppClient getAppClient(String appKey) {
		String hql = "from  AppClient appClient where LOWER(appClient.appKey)=:appKey";
		return getSession().createQuery(hql, AppClient.class).setParameter("appKey", appKey.toLowerCase())
				.uniqueResult();
	}

	@Override
	public void deleteAppClient(Long userId) {
		String hql = "delete from  AppClient appClient where appClient.userId=:userId";
		createQuery(hql).setParameter("userId", userId).executeUpdate();
	}
}
