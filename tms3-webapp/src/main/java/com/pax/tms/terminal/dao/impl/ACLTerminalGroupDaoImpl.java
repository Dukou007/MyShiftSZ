package com.pax.tms.terminal.dao.impl;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.terminal.dao.ACLTerminalGroupDao;
import com.pax.tms.terminal.model.ACLTerminalGroup;

@Repository("aclTerminalGroupDaoImpl")
public class ACLTerminalGroupDaoImpl extends BaseHibernateDao<ACLTerminalGroup, Long> implements ACLTerminalGroupDao {

	@Override
	public void saveACLTerminalGroups(Collection<String> tsns, Long groupId) {
		if(CollectionUtils.isEmpty(tsns)){
			return;
		}
		String insertSql = " insert into ACLTTRM_GROUP(GROUP_ID,TRM_ID) select p.PARENT_ID, t.TRM_ID "
				+ "	from PUBTGROUP g inner join PUBTGROUP_PARENTS p on "
				+ " g.GROUP_ID=p.GROUP_ID, TMSTTERMINAL t  where g.GROUP_ID=:groupId and t.TRM_ID "
				+ " in (:tsns) and not exists (select TRM_ID from "
				+ "  ACLTTRM_GROUP acl where acl.GROUP_ID=p.PARENT_ID and acl.TRM_ID=t.TRM_ID) ";

		createNativeQuery(insertSql).setParameter("tsns", tsns).setParameter("groupId", groupId).executeUpdate();

	}


	@Override
	public void deleteACLTerminalGroups(Collection<String> tsns, Long groupId) {

		String sql = " delete aclTg from ACLTTRM_GROUP aclTg,PUBTGROUP g,PUBTGROUP_PARENTS gp"
				+ "	 where g.GROUP_ID=gp.GROUP_ID and g.GROUP_ID=aclTg.GROUP_ID"
				+ "	 and gp.PARENT_ID=? and aclTg.TRM_ID=? ";

		doBatchExecute(sql, tsns.iterator(), (st, tsn) -> {
			st.setLong(1, groupId);
			st.setString(2, tsn);

		});
	}

	@Override
	public void deleteACLTerminalGroupByUser(Collection<String> tsns, Long loginUserId) {
		if(CollectionUtils.isEmpty(tsns)){
			return;
		}

		String sql = " delete aclTg from ACLTTRM_GROUP aclTg,PUBTUSER_GROUP ug ,PUBTGROUP_PARENTS gp "
				+ " where ug.GROUP_ID=gp.PARENT_ID and gp.GROUP_ID=aclTg.GROUP_ID and ug.USER_ID=?"
				+ "	and aclTg.TRM_ID=?";

		doBatchExecute(sql, tsns.iterator(), (st, tsn) -> {
			st.setLong(1, loginUserId);
			st.setString(2, tsn);

		});

	}

}
