package com.pax.tms.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.service.impl.CommonService;
import com.pax.tms.user.dao.AdminDao;

@Service()
public class AdminServiceImpl extends CommonService implements AdminService {

    @Autowired
    private AdminDao adminDao;

    /*
     * (non-Javadoc)
     * 
     * @see com.paxsz.tms.admin.service.AdminService#executeSql(java.lang.String)
     */
    @Override
    public void executeSql(String sql) {
        adminDao.executeSql(sql);

    }

	@Override
	public List<String> selectSql(String sql) {
		 return adminDao.selectSql(sql);
		
	}

}
