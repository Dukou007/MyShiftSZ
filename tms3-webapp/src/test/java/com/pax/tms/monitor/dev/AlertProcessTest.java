package com.pax.tms.monitor.dev;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.pax.tms.monitor.web.AlertConstants;
import com.pax.tms.user.security.UTCTime;

public class AlertProcessTest {
	static Connection conn;

	@BeforeClass
	public static void getConnection() {
		getConnOracle();
	}

	static void getConnMysql() {
		conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:mysql://ppm-database:3306/pax_ppm?useUnicode=true&characterEncoding=UTF-8&useCursorFetch=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC",
					"paxppm", "PaxPPM_123@hz");
			conn.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void getConnOracle() {
		conn = null;
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.6.103:1521:prdbps01", "pax_ppm",
					"PaxPPM#759HZ");
			conn.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void close() throws Exception {
		if (conn != null) {
			conn.close();
		}
	}

	@Test
	public void testConn() throws SQLException {
		String sql = "select 1 from dual";
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			System.out.println(rs.getObject(1));
		}
	}

	@Test
	public void generateRealData() throws Exception {
		while (true) {
			insertBatchTerminalStatus();
			Thread.sleep(10 * 1000);
			System.out.println("########generateRealData()#########");
		}
	}

	/**
	 * A:  11110001~11110020   NORMAL
	 * B:  22220001~22220020   NOTHING
	 * C:  33330001~33330020   Only ACTIVATIONS AND DOWNLOADS
	 */
	@Test
	public void insertReportMessage() throws Exception {
		String sql = "INSERT INTO TMSTTRM_REPORT_MSG"
				+ " VALUES (?,?,?,null,null,null,null,null,null,null,null,null,null,null,null,null,null,?,?,?,?,null,null,null,null,null)";

		Random rnd = new Random();
		PreparedStatement ps = conn.prepareStatement(sql);
		int idStart = 33000;
		int tidStart = 33330001;
		int day = 10 * 3600 * 1000;
		int ndays = day * 1;

		for (int i = 1; i <= 20; i++) {
			ps.setInt(1, idStart + i); // rpt_id
			ps.setInt(2, tidStart + rnd.nextInt(20)); // trm_id
			ps.setTimestamp(3, new Timestamp(System.currentTimeMillis() - ndays - rnd.nextInt(20 * 3500 * 1000))); // report
			// time
			//			ps.setInt(4, rnd.nextInt(3)); // tamper
			//			ps.setInt(5, rnd.nextInt(2)); // online
			//			ps.setInt(6, rnd.nextInt(3)); // shield
			//			ps.setInt(7, rnd.nextInt(3)); // STYLUS
			//			ps.setInt(8, rnd.nextInt(3)); // DOWN_STS
			//			ps.setInt(9, rnd.nextInt(3)); // ACTV_STS
			//			ps.setInt(10, rnd.nextInt(200)); // MSR_ERRS
			//			ps.setInt(11, getErrorsAndTots(200)); // MSR_TOTS
			//			ps.setInt(12, rnd.nextInt(300)); // ICR_ERRS
			//			ps.setInt(13, getErrorsAndTots(300)); // ICR_TOTS
			//			ps.setInt(14, rnd.nextInt(400)); // PIN_FAILS
			//			ps.setInt(15, getErrorsAndTots(400)); // PIN_TOTS
			//			ps.setInt(16, rnd.nextInt(500)); // SIGN_ERRS
			//			ps.setInt(17, getErrorsAndTots(500)); // SIGN_TOTS
			ps.setInt(4, rnd.nextInt(600)); // DOWN_FAILS
			ps.setInt(5, getErrorsAndTots(600)); // DOWN_TOTS
			ps.setInt(6, rnd.nextInt(700)); // ACTV_FAILS
			ps.setInt(7, getErrorsAndTots(700)); // ACTV_TOTS
			//			ps.setInt(22, rnd.nextInt(800)); // CL_ICR_ERRS
			//			ps.setInt(23, getErrorsAndTots(800)); // CL_ICR_TOTS
			//			ps.setInt(24, rnd.nextInt(900)); // TXN_ERRS
			//			ps.setInt(25, getErrorsAndTots(900)); // TXN_TOTS
			//			ps.setInt(26, getErrorsAndTots(200)); // POWER_NO
			ps.addBatch();
		}

		ps.executeBatch();
		conn.commit();
		ps.close();
	}

	@Test
	public void insertBatchTerminalStatus() throws Exception {
		//		cleanTrmStatus();

		String sql = "insert into tmsttrmstatus (TRM_ID, TRM_SN, MODEL_ID, LAST_CONN_TIME, "
				+ " LAST_DWNL_TIME, LAST_DWNL_STATUS, LAST_ACTV_TIME, LAST_ACTV_STATUS, "
				+ " LAST_SOURCE_IP, IS_ONLINE, TAMPER, PRIVACY_SHIELD, STYLUS, ONLINE_SINCE, OFFLINE_SINCE)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement ps = conn.prepareStatement(sql);
		Random rnd = new Random();
		Timestamp ts = new Timestamp(new Date().getTime());
		String modelId = "D210";
		int idStart = 20000001;
		String[] dwnlStatus = { "SUCCESS", "PENDING", "DOWNLOADING", "FAILED", "CANCELED" };
		String lastIp = "127.0.0.1";
		String[] tampers = { "0000", "Broken" };

		for (int i = 0; i <= 15; i++) {
			ps.setInt(1, idStart + i);
			ps.setInt(2, idStart + i);
			ps.setString(3, modelId);
			ps.setTimestamp(4, ts, UTCTime.UTC_CLENDAR);
			ps.setTimestamp(5, ts);
			ps.setString(6, dwnlStatus[rnd.nextInt(5)]);// last download status
			ps.setTimestamp(7, ts);
			ps.setString(8, dwnlStatus[rnd.nextInt(5)]);// last act status
			ps.setString(9, lastIp);// ip
			ps.setInt(10, rnd.nextInt(2) + 1);// online
			ps.setString(11, tampers[rnd.nextInt(2)]);// tampers
			ps.setInt(12, rnd.nextInt(2) + 1);
			ps.setInt(13, rnd.nextInt(2) + 1);
			ps.setTimestamp(14, ts);
			ps.setTimestamp(15, ts);

			ps.addBatch();

		}
		ps.executeBatch();
		conn.commit();
		ps.close();
	}

	@Test
	public void cleanTrmStatus() throws Exception {
		String sql = "delete from tmsttrmstatus";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.executeUpdate();
		conn.commit();
		ps.close();
	}

	@Test
	public void createTermianlStatus() throws Exception {
		// run cleanTrmStatus() to clean the table before
		String model = "PX7";
		int terminalId = 10000000;
		String[] dwnlStatus = { "SUCCESS", "PENDING", "DOWNLOADING", "FAILED", "CANCELED" };
		String lastIp = "127.0.0.1";
		String[] tampers = { "0000", "Broken" };
		insertTerminalStatus(model, terminalId, dwnlStatus[0], lastIp, tampers[0]);
	}

	public void insertTerminalStatus(String model, int terminalId, String dwnlStatus, String lastIp, String tampers)
			throws Exception {
		String sql = "insert into tmsttrmstatus (TRM_ID, TRM_SN, MODEL_ID, LAST_CONN_TIME, "
				+ " LAST_DWNL_TIME, LAST_DWNL_STATUS, LAST_ACTV_TIME, LAST_ACTV_STATUS, "
				+ " LAST_SOURCE_IP, IS_ONLINE, DOWNLOADING, TAMPER, PRIVACY_SHIELD, STYLUS)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		PreparedStatement ps = conn.prepareStatement(sql);
		Random rnd = new Random();
		Timestamp ts = new Timestamp(1492646399000L);
		Timestamp ts2 = new Timestamp(1492646399999L);
		ps.setInt(1, terminalId);
		ps.setInt(2, terminalId);
		ps.setString(3, model);
		ps.setTimestamp(4, ts, UTCTime.UTC_CLENDAR);
		ps.setTimestamp(5, ts2, UTCTime.UTC_CLENDAR);
		ps.setString(6, dwnlStatus);// last download status
		ps.setTimestamp(7, ts);
		ps.setString(8, dwnlStatus);// last act status
		ps.setString(9, lastIp);// ip
		ps.setInt(10, rnd.nextInt(2) + 1);// online
		ps.setInt(11, 1);// downloading
		ps.setString(12, tampers);// tampers
		ps.setInt(13, rnd.nextInt(2) + 1);
		ps.setInt(14, rnd.nextInt(2) + 1);
		ps.addBatch();
		ps.executeBatch();
		conn.commit();
		ps.close();
	}

	@Test
	public void testDate() {
		Timestamp ts2 = new Timestamp(1492646399999L);
		System.out.println(ts2);
	}

	@Test
	public void cleanUpGroupUsageStatus() throws Exception {
		String sql = "delete from tmstgroup_usage_sts";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.executeUpdate();
		conn.commit();
		ps.close();
	}

	@Test
	public void createGroupUsageStatus() throws Exception {
		// run cleanUpGroupUsageStatus() to clean the table before
		String[] cycle = { "per day", "per week", "per month" };// per day=0 per
																// week=1 per
																// month=2
		String[] itemName = { "MSR Read Rate", "Contact IC Read Rate", "PIN Encryption Failure", "Signature",
				"Download History", "Activation History", "Contactless IC Read Rate", "Transaction History",
				"Power-cycle History" };// MSR Read Rate=0
		int informationId = 204;// 1,2,3,4........unrepeatable
		long n = 4;// before n cycle days
		long ndays = 7;// 1 7 30 days
		int groupId = 1;
		String groupName = "PAX";
		oneGroupUsageStatus(informationId, groupId, n, ndays, groupName, cycle[1], itemName[0]);
	}

	@Test
	public void qwe21312323() {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.WEEK_OF_MONTH, -1);// 周数减一，即上周
		System.out.println(date.getTime());
		System.out.println(new Timestamp(System.currentTimeMillis()));
	}

	public void oneGroupUsageStatus(int i, int groupId, long n, long ndays, String groupName, String cycle,
			String itemName) throws Exception {
		String sql = "INSERT INTO tmstgroup_usage_sts VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?);";
		Random rnd = new Random();
		PreparedStatement ps = conn.prepareStatement(sql);
		long day = 24 * 3600 * 1000;
		int total = 100000;
		int normal = rnd.nextInt(total);
		int abnormal = rnd.nextInt(total - normal);
		int unknow = total - normal - abnormal;
		ps.setInt(1, i); // rpt_id
		ps.setInt(2, groupId); // group_id
		ps.setString(3, groupName); // group_name
		ps.setString(4, itemName); // item_name
		ps.setInt(5, total); // totals
		ps.setInt(6, abnormal); // abnormal
		ps.setInt(7, normal); // normal
		ps.setInt(8, unknow); // unknow
		ps.setInt(9, rnd.nextInt(3));// AlertSeverity
		ps.setString(10, String.valueOf(rnd.nextInt(100))); // alert_threshold
		ps.setString(11, String.valueOf(rnd.nextInt(100))); // alert value
		ps.setTimestamp(12, (new Timestamp(System.currentTimeMillis() - n * ndays * day))); // start
		// time
		ps.setTimestamp(13, (new Timestamp(System.currentTimeMillis() - n * ndays * day))); // end
		// time
		ps.setString(14, cycle); // { "per day", "per week", "per month"
									// };
		ps.setTimestamp(15, (new Timestamp(System.currentTimeMillis() - n * ndays * day))); // create
		// time
		ps.addBatch();
		ps.executeBatch();
		conn.commit();
		ps.close();
	}

	@Test
	public void insertGroupUsageStatus() throws Exception {
		String sql = "INSERT INTO tmstgroup_usage_sts VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?);";
		String[] cycle = { "per day", "per week", "per month" };
		Random rnd = new Random();
		PreparedStatement ps = conn.prepareStatement(sql);
		int groupId = 1101;
		for (int i = 701; i <= 745; i++) {
			int total = rnd.nextInt(100000) + 1000;
			int normal = rnd.nextInt(total);
			int abnormal = rnd.nextInt(total - normal);
			int unknow = rnd.nextInt(total - normal - abnormal);
			ps.setInt(1, i); // rpt_id
			ps.setInt(2, groupId); // group_id
			ps.setString(3, "groupName"); // group_name
			ps.setString(4, AlertConstants.getUsageItems()[i % 9]); // item_name
			ps.setInt(5, total); // totals
			ps.setInt(6, abnormal); // abnormal
			ps.setInt(7, normal); // normal
			ps.setInt(8, unknow); // unknow
			ps.setInt(9, rnd.nextInt(3));// AlertSeverity
			ps.setString(10, String.valueOf(rnd.nextInt(100))); // alert_threshold
			ps.setString(11, String.valueOf(rnd.nextInt(100))); // alert value
			ps.setTimestamp(12, new Timestamp(System.currentTimeMillis())); // start
																			// time
			ps.setTimestamp(13, new Timestamp(System.currentTimeMillis())); // end
																			// time
			ps.setString(14, cycle[i % 3]); // PIN_FAILS
			ps.setTimestamp(15, new Timestamp(System.currentTimeMillis())); // create
																			// time
			ps.addBatch();
		}

		ps.executeBatch();
		conn.commit();
		ps.close();
	}

	@Test
	public void insertTerminalUsageStatus() throws Exception {
		String sql = "INSERT INTO tmsttrm_usage_msg VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
		String[] cycle = { "per day", "per week", "per month" };
		Random rnd = new Random();
		PreparedStatement ps = conn.prepareStatement(sql);
		String terminalSN = "00000000";
		for (int i = 1; i < 45; i++) {
			int totals = 100;
			int itemErrs = rnd.nextInt(totals);
			ps.setInt(1, i); // rpt_id
			ps.setString(2, terminalSN); // terminalSN
			ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));// start
																			// time
			ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));// end
																			// time
			ps.setString(5, AlertConstants.getUsageItems()[i % 9]); // item_name
			ps.setInt(6, itemErrs); // errors
			ps.setInt(7, totals); // totals
			ps.setString(8, cycle[i % 3]); // cycle
			ps.setTimestamp(9, new Timestamp(System.currentTimeMillis()));// create
																			// time
			ps.addBatch();
		}
		ps.executeBatch();
		conn.commit();
		ps.close();
	}

	private int getErrorsAndTots(int errs) {
		Random rnd = new Random();
		int tots = errs + rnd.nextInt(errs * 10);
		return tots;
	}

}
