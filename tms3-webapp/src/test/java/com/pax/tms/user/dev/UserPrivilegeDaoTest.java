package com.pax.tms.user.dev;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.tms.demo.ServiceJunitCase;
import com.pax.tms.user.dao.UserPrivilegeDaoImpl;

public class UserPrivilegeDaoTest extends ServiceJunitCase {

	@Autowired
	private UserPrivilegeDaoImpl userPrivilegeDao;

	private Long operatorId = 1L;

	@Test
	public void testHasPermissionOfUser() {
		boolean b = userPrivilegeDao.hasPermissionOfUser(operatorId, 1L);
		assertTrue(b);
	}

	@Test
	public void testHasPermissionOfGroup() {
		boolean b = userPrivilegeDao.hasPermissionOfGroup(operatorId, 1L);
		assertTrue(b);
	}

	@Test
	public void testHasPermissionOfTerminal() {
		boolean b = userPrivilegeDao.hasPermissionOfTerminal(operatorId, "3#$%S322");
		assertFalse(b);
	}

}
