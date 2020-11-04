/*
 * ============================================================================
 * = COPYRIGHT				
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.		
 * ============================================================================		
 */
package com.pax.common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pax.common.exception.AppException;
import com.pax.common.util.SpringContextUtil;

public class DbHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(DbHelper.class);

	private static DataSource dataSource;

	private DbHelper() {
	}

	public static Connection getConnection() {
		try {
			if (dataSource == null) {
				dataSource = SpringContextUtil.getBean("dataSource");
			}
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new AppException(e);
		}
	}

	public static final long generateId(String segmentName, int idCount) {
		Connection con = null;
		Exception exception = null;

		if (idCount <= 0) {
			throw new IllegalArgumentException("Id count should not be " + idCount);
		}

		try {
			con = getConnection();
			con.setAutoCommit(false);

			int lock = lockSegment(segmentName, con);
			if (lock == 0) {
				return lockFailed(segmentName, idCount, con);
			} else {
				return getAndUpdateSegmentValue(segmentName, idCount, con);
			}
		} catch (Exception e) {
			exception = e;
			throw new AppException(e);
		} finally {
			close(con, exception);
		}
	}

	private static long getAndUpdateSegmentValue(String segmentName, int idCount, Connection con) throws SQLException {
		long currentValue = getSegmentVaue(segmentName, con);
		long nextValue = currentValue + idCount;
		updateSegment(segmentName, nextValue, con);
		return currentValue;
	}

	private static long lockFailed(String segmentName, int idCount, Connection con) throws SQLException {
		try {
			return addSegment(segmentName, idCount, con);
		} catch (SQLException e) {
			int lock = lockSegment(segmentName, con);
			if (lock == 0) {
				throw e;
			}
			return getAndUpdateSegmentValue(segmentName, idCount, con);
		}
	}

	private static Long getSegmentVaue(String segmentName, Connection con) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "SELECT NEXT_VALUE FROM PUBTSEQUENCE WHERE SEQ_NAME=?";

		try {
			st = con.prepareStatement(sql);
			st.setString(1, segmentName);
			rs = st.executeQuery();
			if (rs.next()) {
				return rs.getLong(1);
			} else {
				return null;
			}
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
		}
	}

	private static int lockSegment(String segmentName, Connection con) throws SQLException {
		PreparedStatement st = null;
		String sql = "UPDATE PUBTSEQUENCE SET NEXT_VALUE=NEXT_VALUE WHERE SEQ_NAME=?";

		try {
			st = con.prepareStatement(sql);
			st.setString(1, segmentName);
			return st.executeUpdate();
		} finally {
			DbUtils.closeQuietly(st);
		}
	}

	private static int addSegment(String segmentName, int idCount, Connection con) throws SQLException {
		PreparedStatement st = null;
		String sql = "INSERT INTO PUBTSEQUENCE(SEQ_NAME,NEXT_VALUE) VALUES(?, ?)";

		try {
			st = con.prepareStatement(sql);
			st.setString(1, segmentName);
			st.setInt(2, idCount + 1);
			st.executeUpdate();
			return 1;
		} finally {
			DbUtils.closeQuietly(st);
		}
	}

	private static void updateSegment(String segmentName, long nextValue, Connection con) throws SQLException {
		PreparedStatement st = null;
		String sql = "UPDATE PUBTSEQUENCE SET NEXT_VALUE=? WHERE SEQ_NAME=?";

		try {
			st = con.prepareStatement(sql);
			st.setLong(1, nextValue);
			st.setString(2, segmentName);
			st.executeUpdate();
		} finally {
			DbUtils.closeQuietly(st);
		}
	}

	public static void close(PreparedStatement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				LOGGER.warn("close statement failed", e);
			}
		}
	}

	public static void close(Connection con, Exception exception) {
		if (con != null) {
			if (exception != null) {
				DbUtils.rollbackAndCloseQuietly(con);
			} else {
				DbUtils.commitAndCloseQuietly(con);
			}
		}
	}

}
