package com.pax.tms.res.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.res.dao.ManufactureDao;
import com.pax.tms.res.model.Manufacture;
import com.pax.tms.res.service.ManufactureService;

@Service("manufactureServiceImpl")
public class ManufactureServiceImpl extends BaseService<Manufacture, String> implements ManufactureService {

	@Autowired
	private ManufactureDao manufactureDao;

	@Override
	public IBaseDao<Manufacture, String> getBaseDao() {

		return manufactureDao;
	}

}
