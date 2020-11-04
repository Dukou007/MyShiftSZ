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
 * ============================================================================		
 */
package com.pax.tms.deploy.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.pax.common.dao.IBaseDao;
import com.pax.common.fs.FileManagerUtils;
import com.pax.common.service.impl.BaseService;
import com.pax.tms.app.broadpos.para.SchemaPacker;
import com.pax.tms.deploy.dao.DeployDao;
import com.pax.tms.deploy.dao.DeployParaDao;
import com.pax.tms.deploy.model.Deploy;
import com.pax.tms.deploy.model.DeployPara;
import com.pax.tms.deploy.service.DeployParaService;
import com.pax.tms.res.service.PkgSchemaService;

@Service("deployParaServiceImpl")
public class DeployParaServiceImpl extends BaseService<DeployPara, Long> implements DeployParaService {

	@Autowired
	@Qualifier("deployParaDaoImpl")
	private DeployParaDao deployParaDao;

	@Override
	public IBaseDao<DeployPara, Long> getBaseDao() {
		return deployParaDao;
	}

	@Autowired
	private DeployDao deployDao;

	@Autowired
	private PkgSchemaService schemaService;

	/*
	 * timer task process
	 */
	@Override
	public void doProcessDeployList() {
		List<Deploy> list = deployDao.getUnDealDeployList();
		list.forEach(deploy -> processDeploy(deploy));
	}

	/*
	 * Terminal deploy call
	 */
	@Async
	@Override
	public void processDeploy(Deploy deploy) {
		int paramStatus = (deploy.getParamStatus() == null) ? 0 : deploy.getParamStatus();
		boolean result = this.saveDeployPara(deploy);
		if (result) {
			paramStatus = 1;
		} else {
			paramStatus--;
		}
		deploy.setParamStatus(paramStatus);
		deployDao.update(deploy);
	}

	private boolean saveDeployPara(Deploy deploy) {
		String schemaPath = deploy.getPkgSchema().getFilePath();
		Map<String, String> paraPathMap = null;
		try {
			paraPathMap = this.packParameter(schemaPath, deploy.getParamSet(), deploy.getId() + "",
					deploy.getParamVersion());
			for (Entry<String, String> entry : paraPathMap.entrySet()) {
				String localPath = entry.getValue();
				DeployPara deployPara = new DeployPara();
				deployPara.setDeploy(deploy);
				deployPara.setPkgProgramId(Long.valueOf(entry.getKey()));
				deployPara.setPkgId(deploy.getPkg().getId());
				deployPara.setFileName("conf");
				deployPara.setFileSize(new File(localPath).length());
				deployPara.setFileVersion(deploy.getParamVersion());
				deployPara.setFilePath(FileManagerUtils.saveLocalFileToFdfs(localPath, "zip"));
				deployPara.setCreator(deploy.getCreator());
				deployPara.setCreateDate(new Date());
				deployPara.setModifier(deploy.getModifier());
				deployPara.setModifyDate(new Date());
				deployParaDao.save(deployPara);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Map<String, String> packParameter(String schemaPath, String paraSetId, String deployIdStr,
			String paraVersion) throws IOException, DocumentException {
		Document doc = schemaService.getPkgSchemaBySchemaPath(schemaPath);
		Map<String, String> paraMap = schemaService.getParamSetFromMongoDB(paraSetId);
		try (SchemaPacker packer = new SchemaPacker(doc)) {
			return packer.pack(deployIdStr, paraMap, paraVersion);
		}

	}

	@Override
	public List<String> getDeployParaPaths(List<Long> deployIds) {

		return deployParaDao.getDeployParaPaths(deployIds);

	}

	@Override
	public void deleteDeployParas(List<Long> deployIds) {

		deployParaDao.deleteDeployParas(deployIds);
	}

}
