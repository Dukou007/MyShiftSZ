package com.pax.tms.res.dao.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.res.dao.PkgOptLogDao;
import com.pax.tms.res.model.PkgOptLog;
import com.pax.tms.user.security.UTCTime;

@Repository("pkgOptLogDaoImpl")
public class PkgOptLogDaoImpl extends BaseHibernateDao<PkgOptLog, Long> implements PkgOptLogDao {

	@Override
	public void insertOptLogs(List<String> optLogFilePaths) {
		if (CollectionUtils.isEmpty(optLogFilePaths)) {
			return;
		}
		String sql = "insert into TMSTDELFILELOG (OPTLOG_ID,FILE_PATH,OPT_TIME,STATUS)" + "values (?,?,?,?)";
		doBatchInsert(sql, PkgOptLog.ID_SEQUENCE_NAME, optLogFilePaths, (st, filePath, relId) -> {
			st.setLong(1, relId);
			st.setString(2, filePath);
			st.setTimestamp(3, new Timestamp(new Date().getTime()), UTCTime.UTC_CLENDAR);
			st.setString(4, PkgOptLog.UN_PROCESS);

		});

	}

	@Override
	public List<String> getOptLogFilePaths() {

		String hql = "select pkgOptLog.filePath from PkgOptLog pkgOptLog ";
		return createQuery(hql, String.class).getResultList();
	}

	@Override
	public void deletePkgOptLog(String filePath) {
		String hql = "delete from PkgOptLog pkgOptLog where pkgOptLog.filePath=:filePath";
		createQuery(hql).setParameter("filePath", filePath).executeUpdate();

	}

}
