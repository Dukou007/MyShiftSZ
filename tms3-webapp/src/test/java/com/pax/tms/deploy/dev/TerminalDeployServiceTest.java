package com.pax.tms.deploy.dev;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.tms.demo.AbstractShiroTest;
import com.pax.tms.deploy.dao.TerminalDeployDao;
import com.pax.tms.deploy.domain.TerminalDeployInfo;

import io.vertx.core.json.Json;

public class TerminalDeployServiceTest extends AbstractShiroTest {

	@Autowired
	private TerminalDeployDao terminalDeployDao;
	
	@Test
	public void getTerminalLastedDeploy() {
		String terminalId = "11110065";
		TerminalDeployInfo tdInfo = terminalDeployDao.getTerminalLastedDeploy(terminalId);
		System.out.println(Json.encodePrettily(tdInfo));
	}
}
