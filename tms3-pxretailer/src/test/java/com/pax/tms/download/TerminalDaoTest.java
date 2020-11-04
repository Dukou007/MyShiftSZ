package com.pax.tms.download;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pax.common.util.DateTimeUtils;
import com.pax.tms.download.dao.TerminalDownloadDaoImpl;
import com.pax.tms.download.model.Event;
import com.pax.tms.download.model.Terminal;
import com.pax.tms.download.model.TerminalStatus;
import com.pax.tms.download.model.TerminalUsageReport;

@ContextConfiguration(locations = { "classpath:pxretailer/propertyFileConfigurer.xml",
		"classpath:pxretailer/spring-config.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional(transactionManager = "txManager", propagation = Propagation.SUPPORTS)
@Rollback(false)
// @Commit
public class TerminalDaoTest {

	@Autowired
	TerminalDownloadDaoImpl trmDao;

	@Test
	public void testGetTerminalBySn() {
		String deviceType = "S80";
		String deviceSerialNumber = "12345678";
		Terminal t = trmDao.getTerminalBySn(deviceType, deviceSerialNumber);
		System.out.println(t);
	}

	@Test
	public void testAddUnregisteredTerminal() {
		String deviceType = "S80";
		String deviceSerialNumber = "77771111";
		trmDao.addUnregisteredTerminal(deviceType, deviceSerialNumber, "127.0.0.1", new Date());
	}

	@Test
	public void testUpdateUnregisteredTerminal() {
		String deviceType = "S80";
		String deviceSerialNumber = "77771111";
		trmDao.updateUnregisteredTerminal(deviceType, deviceSerialNumber, "127.1.0.1", new Date());
	}

	@Test
	public void testGetTerminalStatus() {
		TerminalStatus ts = trmDao.getTerminalStatus("12345678");
		System.out.println(ts);
	}

	@Test
	public void testInsertTerminalEventList() {
		TerminalStatus ts = trmDao.getTerminalStatus("12345678");
		List<Event> list = new ArrayList<>();
		Date now = new Date();

		list.add(new Event(now, ts.getTrmId(), Event.INFO, "Terminal online"));

		list.add(new Event(now, ts.getTrmId(), Event.CRITICAL, "Terminal offline"));

		list.add(new Event(now, ts.getTrmId(), Event.CRITICAL, "Terminal tampered " + ts.getTamper()));

		list.add(new Event(now, ts.getTrmId(), Event.CRITICAL, "Privacy shield removed"));

		list.add(new Event(now, ts.getTrmId(), Event.WARNNING, "Stylus removed"));

		list.add(new Event(now, ts.getTrmId(), Event.INFO, "Stylus attached"));

		list.add(new Event(now, ts.getTrmId(), Event.INFO, "Source IP changed " + ts.getLastSourceIp()));

		trmDao.saveEventList(list);
	}

	@Test
	public void testInsertTerminalStatusReport3() {
		TerminalUsageReport reportMessage = new TerminalUsageReport();
		reportMessage.setReportTime(DateTimeUtils.format(new Date(), DateTimeUtils.PATTERN_STANDARD));
		reportMessage.setTerminalId("12345678");
		trmDao.saveUsageReport(reportMessage);
	}

}
