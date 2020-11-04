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
package com.pax.tms.user.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.pax.common.dao.hibernate.BaseHibernateDao;
import com.pax.tms.user.model.Authority;

@Repository("userPrivilegeDaoImpl")
public class UserPrivilegeDaoImpl extends BaseHibernateDao<Authority, Long> implements UserPrivilegeDao {
	private static final String USER_ID = "userId";
	@Value("${tms.terminal.log.ppm}")
	private String terminalLogPPM;

	@Override
	public List<Authority> getAssignedPrivileges(long userId) {
	    StringBuffer hql = new StringBuffer();
		hql.append("select auth from Authority auth where exists (from RoleAuthority roleAuth, UserRole userRole where auth.id=roleAuth.authority.id and roleAuth.role.id=userRole.role.id and userRole.user.id=:userId) "); 
		if(StringUtils.isEmpty(terminalLogPPM) || StringUtils.equalsIgnoreCase(terminalLogPPM, "false")){
		    hql.append(" and auth.code != 'tms:terminal log:view' ");
		}
		hql.append(" order by auth.name");
		return createQuery(hql.toString(), Authority.class).setParameter(USER_ID, userId).getResultList();
	}

	@Override
	public List<Long> getAssignedPrivilegeIds(long userId) {
	    StringBuffer hql = new StringBuffer();
        hql.append("select auth.id from Authority auth where exists (from RoleAuthority roleAuth, UserRole userRole where auth.id=roleAuth.authority.id and roleAuth.role.id=userRole.role.id and userRole.user.id=:userId) ");
        if(StringUtils.isEmpty(terminalLogPPM) || StringUtils.equalsIgnoreCase(terminalLogPPM, "false")){
            hql.append(" and auth.code != 'tms:terminal log:view' ");
        }
        hql.append(" order by auth.id");
        return createQuery(hql.toString(), Long.class).setParameter(USER_ID, userId).getResultList();
	}

	@Override
	public List<String> getAssignedPrivilegeCodes(long userId) {
	    StringBuffer hql = new StringBuffer();
	    hql.append("select distinct auth.code from Authority auth where exists (from RoleAuthority roleAuth, UserRole userRole where auth.id=roleAuth.authority.id and roleAuth.role.id=userRole.role.id and userRole.user.id=:userId) ");
	    if(StringUtils.isEmpty(terminalLogPPM) || StringUtils.equalsIgnoreCase(terminalLogPPM, "false")){
            hql.append(" and auth.code != 'tms:terminal log:view' ");
        }
        hql.append("order by auth.code");
		return createQuery(hql.toString(), String.class).setParameter(USER_ID, userId).getResultList();
	}

	@Override
	public boolean hasPermissionOfGroup(long operatorId, long groupId) {
		String hql = "select count(id) from Group g where g.id=:groupId"
				+ " and exists (select ug.group.id from UserGroup ug, GroupAncestor ga where"
				+ " ug.group.id=ga.ancestor.id and ug.user.id=:userId and ga.group.id=g.id)";

		return createQuery(hql, Long.class).setParameter(USER_ID, operatorId).setParameter("groupId", groupId)
				.getSingleResult() == 1;
	}

	@Override
	public boolean hasPermissionOfTerminal(long operatorId, String tid) {
		String hql = "select count(tid) from Terminal t where t.tid=:tsn"
				+ " and exists (select tg.id from TerminalGroup tg,UserGroup ug where"
				+ " tg.terminal.tid=t.tid and ug.group.id=tg.group.id and ug.user.id=:userId)";

		return createQuery(hql, Long.class).setParameter(USER_ID, operatorId).setParameter("tsn", tid)
				.getSingleResult() == 1;
	}

	@Override
	public boolean hasPermissionOfUsage(long operatorId, long uid) {
		String hql = "select count(u.id) from UsageThreshold u where u.id=:uid"
				+ " and exists (select ga.group.id from GroupAncestor ga, UserGroup ug where"
				+ " ga.group.id=u.group.id and ug.group.id=ga.ancestor.id and ug.user.id=:userId)";

		return createQuery(hql, Long.class).setParameter(USER_ID, operatorId).setParameter("uid", uid)
				.getSingleResult() == 1;
	}

	@Override
	public boolean hasPermissionOfUser(long operatorId, long userId) {
		String hql = "select count(user.id) from User user where user.id=:userId and not exists"
				+ " (select ug.group.id from UserGroup ug where ug.user.id=:userId"
				+ " and not exists (select ug2.group.id from UserGroup ug2 join GroupAncestor ga"
				+ " on ug2.group.id=ga.ancestor.id where ug2.user.id=:operatorId and ga.group.id=ug.group.id))";

		return createQuery(hql, Long.class).setParameter(USER_ID, userId).setParameter("operatorId", operatorId)
				.getSingleResult() == 1;
	}

	@Override
	public boolean hasPermissionOfPkg(Long loginUserId, Long pkgId) {
		String hql = "select count(pkg.id) from Pkg pkg where pkg.id=:pkgId and"
				+ " exists(select pkgGroup.id from PkgGroup pkgGroup, UserGroup ug"
				+ " where ug.group.id=pkgGroup.group.id and pkgGroup.pkg.id=pkg.id and ug.user.id=:userId)";
		return createQuery(hql, Long.class).setParameter(USER_ID, loginUserId).setParameter("pkgId", pkgId)
				.getSingleResult() == 1;
	}

	@Override
	public boolean hasPermissionOfPkgByGroup(Long groupId, Long pkgId) {
		String hql = "select count(pkg.id) from Pkg pkg where pkg.id=:pkgId and"
				+ " exists(select pkgGroup.id from PkgGroup pkgGroup"
				+ " where pkgGroup.pkg.id=pkg.id and pkgGroup.group.id=:groupId)";
		return createQuery(hql, Long.class).setParameter("groupId", groupId).setParameter("pkgId", pkgId)
				.getSingleResult() == 1;
	}

	@Override
	public boolean hasPermissionOfTerminalByGroup(Long groupId, String tid) {
		String hql = "select count(tid) from Terminal t where t.tid=:tsn"
				+ " and exists (select terminalGroup.id from TerminalGroup terminalGroup where"
				+ " terminalGroup.terminal.tid=t.tid and terminalGroup.group.id=:groupId)";

		return createQuery(hql, Long.class).setParameter("groupId", groupId).setParameter("tsn", tid)
				.getSingleResult() == 1;
	}

    @Override
    public boolean hasPermissionOfImportFileByGroup(Long groupId, Long fileId){
        String hql = "select count(imp.id) from ImportFile imp where imp.id=:fileId and imp.group.id=:groupId)";
        return createQuery(hql, Long.class).setParameter("groupId", groupId).setParameter("fileId", fileId)
                .getSingleResult() == 1;
    }
	

}
