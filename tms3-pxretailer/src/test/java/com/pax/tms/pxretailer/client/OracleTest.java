package com.pax.tms.pxretailer.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.Test;

public class OracleTest {

	@Test
	public void testOracleConnection() throws ClassNotFoundException, SQLException {
		/*
		 * database.driverClass=oracle.jdbc.OracleDriver
		 * database.url=jdbc:oracle:thin:@192.168.6.157:1521:prdbps01
		 * database.user=pax_ppm database.password=PaxPPM#759hz
		 * database.validationQuery=select 0 from dual
		 * hibernate.dialect=org.hibernate.dialect.Oracle10gDialect
		 * quartz.config=classpath:ppm_quartz_clustering.properties
		 * 
		 */

		Class.forName("oracle.jdbc.OracleDriver");
		Properties prop = new Properties();
		prop.put("user", "pax_ppm");
		prop.put("password", "PaxPPM#759hz");
		prop.put("BatchPerformanceWorkaround", false);
		prop.put("jdbc.BatchPerformanceWorkaround", false);
		prop.put("jdbc.use_scrollable_resultset", true);
		prop.put("use_scrollable_resultset", true);
		try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.6.157:1521:prdbps01", prop)) {
			System.out.println(conn.getAutoCommit());
			System.out.println(conn.getClass());
			ResultSet rs = conn.createStatement().executeQuery("select 0 from dual");
			rs.close();
			System.out.println("Connected");

			String sql = "update TMSTTRMSTATUS set IS_ONLINE=2 where TRM_ID=?";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, "12345678");
			st.addBatch();

			System.out.println("executeBatch");
			int[] results = st.executeBatch();
			System.out.println(results);
			for (int result : results) {
				System.out.println(result);
			}
			st.close();

			conn.commit();
		}

	}

}
