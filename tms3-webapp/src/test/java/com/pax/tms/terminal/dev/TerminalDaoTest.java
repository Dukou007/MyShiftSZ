package com.pax.tms.terminal.dev;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.tms.demo.ServiceJunitCase;
import com.pax.tms.terminal.dao.impl.TerminalDaoImpl;

public class TerminalDaoTest extends ServiceJunitCase {

	@Autowired
	private TerminalDaoImpl terminalDao;

	@Test
	public void testGetExistAndNotAcceptableTsns() {
		Set<String> tsns = new HashSet<String>();
		tsns.add("23491291");
		tsns.add("23491292");

		long userId = 1L;

		terminalDao.getExistedAndNotAcceptableTsns(tsns, userId);
	}

}
