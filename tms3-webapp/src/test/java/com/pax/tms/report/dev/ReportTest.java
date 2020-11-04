package com.pax.tms.report.dev;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.common.util.ExcelWritter;
import com.pax.tms.report.dao.UserMaintenanceDao;
import com.pax.tms.user.model.UserLog;

public class ReportTest extends BaseHibernateDao<UserLog, Long> {

	@Autowired
	private UserMaintenanceDao userMainDao;

	@Test
	public void testUserExportExcel() throws FileNotFoundException {
		ExcelWritter ew = new ExcelWritter(new FileOutputStream(new File("d:/temp/userMain.xlsx")));

		ew.open();
		ew.writeCaption("User Report", 3);
		ew.writeTitle(new String[] { "User Name", "Role", "Action" });
		ew.setWidths(new int[] { 40, 40, 40 });
		userMainDao.stream(new Consumer<UserLog>() {
			@Override
			public void accept(UserLog t) {
				try {
					ew.writeContent(t.getUsername(), t.getRole(), t.getEventAction());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		try {
			ew.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
