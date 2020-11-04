package com.pax.tms.terminal.dao.impl;

import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.terminal.dao.TerminalRealStatusDao;
import com.pax.tms.terminal.model.TerminalStatus;
/**
 * tmsttrmstatus表 Dao层实现
 * @author zengpeng
 *
 */
@Repository("terminalRealStatusDaoImpl")
public class TerminalRealStatusDaoImpl extends BaseHibernateDao<TerminalStatus, String> implements TerminalRealStatusDao {

}
