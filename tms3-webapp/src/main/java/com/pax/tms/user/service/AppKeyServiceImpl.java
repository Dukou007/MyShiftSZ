package com.pax.tms.user.service;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.exception.BusinessException;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.open.api.model.AppClient;
import com.pax.tms.user.dao.AppKeyDao;
import com.pax.tms.user.dao.UserDao;
import com.pax.tms.user.model.User;

@Service("appKeyServiceImpl")
public class AppKeyServiceImpl extends BaseService<AppClient, Long> implements AppKeyService {

	@Autowired
	private AppKeyDao appKeyDao;
	@Autowired
	private UserDao userDao;

	public AppKeyDao getAppKeyDao() {
		return appKeyDao;
	}

	public void setAppKeyDao(AppKeyDao appKeyDao) {
		this.appKeyDao = appKeyDao;
	}

	@Override
	public IBaseDao<AppClient, Long> getBaseDao() {
		return appKeyDao;
	}

	@Override
	public AppClient getAppClient(String appKey) {
		if (StringUtils.isEmpty(appKey)) {
			return null;
		}
		return appKeyDao.getAppClient(appKey);
	}

	@Override
	public String getAppKey(String userName) {
		validateInput(userName);
		User user = userDao.findByUsername(userName);
		checkUserStatus(user);

		AppClient oldAppClient = appKeyDao.getAppkey(userName);
		if (oldAppClient != null) {
			return oldAppClient.getAppKey();
		}

		AppClient appClient = createAppClient(user);
		appKeyDao.save(appClient);
		return appClient.getAppKey();
	}

	@Override
	public String getUserAppKey(String userName) {
		validateInput(userName);
		AppClient oldAppClient = appKeyDao.getAppkey(userName);
		return oldAppClient == null ? null : oldAppClient.getAppKey();
	}

	private void checkUserStatus(User user) {
		if (user == null) {
			throw new BusinessException("msg.user.userNameNotFound");
		}
		if (user.isLocked()) {
			throw new BusinessException("msg.user.userLocked");
		}
		if (!user.isAccountNonExpired()) {
			throw new BusinessException("msg.user.userExpired");
		}
		if (!user.isEnabled()) {
			throw new BusinessException("msg.user.userDecative");
		}

	}

	private AppClient createAppClient(User user) {
		AppClient appClient = new AppClient();
		appClient.setUserId(user.getId());
		appClient.setUpdatedOn(new Date());
		appClient.setUserName(user.getUsername());
		appClient.setAppKey(UUID.randomUUID().toString());
		return appClient;
	}

	@Override
	public String saveOrUpdateAppKey(String userName) {
		validateInput(userName);
		User user = userDao.findByUsername(userName);
		checkUserStatus(user);
		AppClient oldAppClient = appKeyDao.getAppkey(userName);
		if (oldAppClient == null) {
			AppClient appClient = createAppClient(user);
			appKeyDao.save(appClient);
			return appClient.getAppKey();
		} else {
			oldAppClient.setAppKey(UUID.randomUUID().toString());
			oldAppClient.setUpdatedOn(new Date());
			appKeyDao.update(oldAppClient);
			return oldAppClient.getAppKey();
		}

	}

	@Override
	public void removeAppKey(String userName) {
		validateInput(userName);
		appKeyDao.removeAppClient(userName);

	}

	private void validateInput(String userName) {
		if (StringUtils.isEmpty(userName)) {
			throw new BusinessException("msg.user.userNameEmpty");
		}
	}

	@Override
	public void deleteAppClient(Long userId) {
		appKeyDao.deleteAppClient(userId);
	}

}
