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
package com.pax.tms.cas.login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.webflow.execution.RequestContext;

import com.pax.common.dao.Sequence;
import com.pax.common.security.HashedCredentials;

@Component("tmsUserManager")
public class TmsUserManager {

	private static final String UTC0 = "Etc/UTC";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final Sequence userLogIdSequence = new Sequence("PUBTUSERLOG_ID", 20);
	private final Sequence auditLogIdSequence = new Sequence("PUBTAUDITLOG_ID", 20);

	private JdbcTemplate jdbcTemplate;

	/**
	 * The static/private salt.
	 */
	private String staticSalt;

	private String resetPasswordUrl;

	private String forgetPasswordUrl;

	/**
	 * Get user attributes from database
	 * 
	 * @param username
	 * @return
	 */
	public TmsUserAttributes getUserAttributes(String username) {
		String sql = "select USER_ID,USERNAME,EMAIL,PHONE,IS_SYS,"
				+ "IS_LOCKED,IS_ENABLED,ENABLED_DATE,IS_LDAP,ACC_EXPIRY_DATE,DISABLED_AFTER_DAYS,REMOVED_AFTER_DAYS,"
				+ "USER_PWD,ENCRYPT_SALT,ENCRYPT_ALG,ENCRYPT_ITERATION_COUNT,MAX_PWD_AGE,LAST_PWD_CHG_DATE,FAILED_LOGIN_COUNT,FORCE_CHG_PWD,"
				+ "LAST_LOGIN_DATE,CREATE_DATE from PUBTUSER where LOWER(USERNAME)=?";
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, username);
		if (results == null || results.isEmpty()) {
			return null;
		}

		Map<String, Object> attributes = results.get(0);
		TmsUserAttributes userAttributes = new TmsUserAttributes(attributes);

		loadRoles(userAttributes);
		loadGroups(userAttributes);

		return userAttributes;
	}

	private void loadRoles(TmsUserAttributes userAttributes) {
		final String sql = "select r.APP_NAME, r.ROLE_NAME from PUBTUSER_ROLE u "
				+ "inner join PUBTROLE r on u.ROLE_ID=r.ROLE_ID where u.USER_ID=? ";
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userAttributes.getUserId());

		Object appName;
		Object roleName;
		// List<String> allRoles = new ArrayList<>(results.size());
		Map<String, List<String>> appRolesMap = new HashMap<>();
		for (Map<String, Object> record : results) {
			appName = record.get("APP_NAME");
			roleName = record.get("ROLE_NAME");
			if (appName == null) {
				appName = "";
			}
			if (roleName == null) {
				continue;
			}
			String appNameStr = StringUtils.trimToEmpty(appName.toString()).toUpperCase();
			String roleNameStr = StringUtils.trim(roleName.toString());
			// allRoles.add(roleNameStr);
			addAppRole(appNameStr, roleNameStr, appRolesMap);
		}

		userAttributes.setAllRoles(appRolesMap.get("TMS"));
		userAttributes.setAppRoles(appRolesMap);
	}

	private void addAppRole(String appName, String roleName, Map<String, List<String>> appRolesMap) {
		List<String> appRoles = appRolesMap.get(appName);
		if (appRoles == null) {
			appRoles = new ArrayList<>();
			appRolesMap.put(appName, appRoles);
		}
		appRoles.add(roleName);
	}

	private void loadGroups(TmsUserAttributes userAttributes) {
		final String sql = "select ug.GROUP_ID, ug.IS_DEFAULT, g.GROUP_NAME from PUBTUSER_GROUP ug inner join PUBTGROUP g on ug.GROUP_ID = g.GROUP_ID where ug.USER_ID=?";
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, userAttributes.getUserId());

		Long id;
		Object groupId;
		Object defaultFlag;
		String groupName;
		Long defaultGroup = null;
		List<Long> groups = new ArrayList<>(results.size());
		JsonArray jsonArray = new JsonArray();
		JsonObject jsonObject;
		for (Map<String, Object> record : results) {
			groupId = record.get("GROUP_ID");
			defaultFlag = record.get("IS_DEFAULT");
			if (groupId != null) {
				id = Long.parseLong(groupId.toString());
				groupName = (String) record.get("GROUP_NAME");
				groups.add(id);
				jsonObject = new JsonObject();
				jsonObject.set("id", id);
				jsonObject.set("name", groupName);
				jsonArray.add(jsonObject);
				if (defaultFlag != null && "1".equals(defaultFlag.toString())) {
					defaultGroup = id;
				}
				loadSubGroups(id, jsonObject);
			}
		}

		if (defaultGroup == null && !groups.isEmpty()) {
			defaultGroup = groups.get(0);
		}

		userAttributes.setGroups(jsonArray);
		userAttributes.setDefaultGroup(defaultGroup);
	}

	private void loadSubGroups(long groupId, JsonObject root) {
		final String sql = "select g.GROUP_ID, g.GROUP_NAME, g.PARENT_ID from PUBTGROUP_PARENTS gp inner join PUBTGROUP g  on g.GROUP_ID=gp.GROUp_ID where gp.PARENT_ID=? and gp.GROUP_ID<>?";
		List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, groupId, groupId);
		Long id;
		JsonObject jsonObject, parentJsonObject;
		Map<Long, JsonObject> map = new HashMap<>();
		map.put(groupId, root);

		Map<Long, Long> parentIdMap = new HashMap<>();
		for (Map<String, Object> record : results) {
			id = Long.parseLong(record.get("GROUP_ID").toString());
			jsonObject = new JsonObject();
			jsonObject.set("id", id);
			jsonObject.set("name", (String) record.get("GROUP_NAME"));
			map.put(id, jsonObject);
			parentIdMap.put(id, Long.parseLong(record.get("PARENT_ID").toString()));
		}

		for (Map.Entry<Long, Long> entry : parentIdMap.entrySet()) {
			jsonObject = map.get(entry.getKey());
			parentJsonObject = map.get(entry.getValue());
			if (parentJsonObject != null) {
				JsonArray jsonArray = (JsonArray) parentJsonObject.get("subgroups");
				if (jsonArray == null) {
					jsonArray = new JsonArray();
					parentJsonObject.add("subgroups", jsonArray);
				}
				jsonArray.add(jsonObject);
			}
		}
	}

	/**
	 * verify password at login
	 * 
	 * @param userAttributes
	 * @param inputtedPassword
	 * @return
	 */
	public boolean verifyPassowrd(TmsUserAttributes userAttributes, String inputtedPassword) {
		String encodedPassword = userAttributes.getPassword();
		if (encodedPassword != null) {
			String digestedPasswordEntered = encodedPassword(userAttributes, inputtedPassword);
			return digestedPasswordEntered.equals(encodedPassword);
		}
		return true;
	}

	private String encodedPassword(TmsUserAttributes userAttributes, String inputtedPassword) {
		String algorithmName = userAttributes.getPasswordEncodeAlgorithm();
		String dynaSalt = userAttributes.getPasswordEncodeSalt();
		int numOfIterations = userAttributes.getPasswordEncodeIterations();
		return HashedCredentials.encodedPassword(inputtedPassword, algorithmName, staticSalt, dynaSalt,
				numOfIterations);
	}

	/**
	 * add failed login count at login failed
	 * 
	 * @param userAttributes
	 * @return
	 */
	public int addFailedLoginCount(TmsUserAttributes userAttributes) {
		int failedLoginCount = userAttributes.getFailedLoginCount() + 1;
		updateFailedLoginCount(userAttributes, failedLoginCount);
		return failedLoginCount;
	}

	/**
	 * reset failed login count at login successfully
	 * 
	 * @param userAttributes
	 */
	public void resetFailedLoginCount(TmsUserAttributes userAttributes) {
		updateFailedLoginCount(userAttributes, 0);
	}

	private void updateFailedLoginCount(TmsUserAttributes userAttributes, int currentErrorCount) {
		String sql = "update PUBTUSER set FAILED_LOGIN_COUNT=? where USER_ID=?";
		jdbcTemplate.update(sql, currentErrorCount, userAttributes.getUserId());
	}

	/**
	 * lock user
	 * 
	 * @param userAttributes
	 */
	public void lockUser(TmsUserAttributes userAttributes) {
		String sql = "update PUBTUSER set IS_LOCKED='1',IS_ENABLED='0', LOCKED_DATE=? where USER_ID=?";
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(UTC0));
		jdbcTemplate.update(sql, cal, userAttributes.getUserId());
	}

	/**
	 * add lock user log
	 * 
	 * @param transformedCredential
	 * @param userAttributes
	 */
	public void addLockUserLog(UsernamePasswordCredential transformedCredential, TmsUserAttributes userAttributes) {
		addLog(transformedCredential, userAttributes,
				"Locked by system due to inputting incorrect password exceeds the limit times");
	}

	/**
	 * update last login date
	 * 
	 * @param userAttributes
	 */
	public void updateLastLoginDate(TmsUserAttributes userAttributes) {
		String sql = "update PUBTUSER set LAST_LOGIN_DATE=? where USER_ID=?";
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(UTC0));
		jdbcTemplate.update(sql, cal, userAttributes.getUserId());
	}

	/**
	 * update LDAP user profile
	 * 
	 * @param userAttributes
	 * @param ldapAttributes
	 */
	public void updateLdapUserProfile(TmsUserAttributes userAttributes, Map<String, Object> ldapAttributes) {
		if (ldapAttributes == null || ldapAttributes.isEmpty()) {
			return;
		}
		String username = (String) ldapAttributes.get("username");
		if (username == null || username.isEmpty()) {
			return;
		}
		String fullname = (String) ldapAttributes.get("fullname");
		String email = (String) ldapAttributes.get("email");
		String phone = (String) ldapAttributes.get("phone");
		String countryName = (String) ldapAttributes.get("countryName");
		String provinceName = (String) ldapAttributes.get("provinceName");
		String cityName = (String) ldapAttributes.get("cityName");
		String zipCode = (String) ldapAttributes.get("zipCode");
		String address = (String) ldapAttributes.get("address");

		Map<String, Object> tmsAttributes = userAttributes.getAttributes();
		tmsAttributes.put("EMAIL", email);
		tmsAttributes.put("PHONE", phone);

		String sql = "update PUBTUSER set FULLNAME=?, EMAIL=?, PHONE=?, COUNTRY=?, PROVINCE=?, CITY=?, ZIP_CODE=?, ADDRESS=? where USER_ID=?";
		jdbcTemplate.update(sql, fullname, email, phone, countryName, provinceName, cityName, zipCode, address,
				userAttributes.getUserId());
	}

	/**
	 * add login log
	 * 
	 * @param transformedCredential
	 * @param userAttributes
	 * @param successful
	 */
	public void addLoginLog(UsernamePasswordCredential transformedCredential, TmsUserAttributes userAttributes,
			boolean successful) {
		addLog(transformedCredential, userAttributes, successful ? "Login Successful" : "Login Failed");
	}

	private void addLog(UsernamePasswordCredential transformedCredential, TmsUserAttributes userAttributes,
			String action) {

		Long userId = userAttributes.getUserId();
		String userName = userAttributes.getUserName();

		String roles = userAttributes.getAppRoles("TMS");
		if (roles == null || roles.isEmpty()) {
			return;
		}
		if (roles.length() > 600) {
			roles = roles.substring(0, 596) + " ...";
		}

		String clientIp = null;
		if (transformedCredential instanceof TmsUserCredential) {
			clientIp = ((TmsUserCredential) transformedCredential).getIp();
		}

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC0));

		String userLogSql = "insert into PUBTUSERLOG (LOG_ID, USERNAME, ROLE, CILENT_IP, EVENT_TIME, EVENT_ACTION)"
				+ " values (?,?,?,?,?,?)";
		String auditLogSql = "insert into PUBTAUDITLOG (LOG_ID, USERNAME, ROLE, USER_ID, CILENT_IP, ACTION_NAME, ACTION_DATE)"
				+ "VALUES(?,?,?,?,?,?,?)";

		jdbcTemplate.update(userLogSql, userLogIdSequence.getId(), userName, roles, clientIp, calendar, action);
		jdbcTemplate.update(auditLogSql, auditLogIdSequence.getId(), userName, roles, userId, clientIp, action,
				calendar);
	}

	/**
	 * Get reset password url
	 * 
	 * @param username
	 * @param context
	 * @return
	 */
	public String getResetPasswordUrl(String username, RequestContext context) {
		String url = resetPasswordUrl;
		if (resetPasswordUrl == null) {
			return "";
		}

		String resetToken = gernateResetToken(username);
		if (url.contains("?")) {
			url += "&token=" + resetToken;
		} else {
			url += "?token=" + resetToken;
		}
		try {
			url += "&username=" + URLEncoder.encode(username, "UTF-8");
			if (context != null) {
				url += "&from=" + getFromUrl(WebUtils.getHttpServletRequest(context));
			}
		} catch (UnsupportedEncodingException e) {
			// never happen
			logger.error("unsupported encoding - UTF-8", e);
		}
		return url;
	}

	private String gernateResetToken(String username) {
		String resetToken = UUID.randomUUID().toString();
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(UTC0));
		String sql = "update PUBTPWDRESETTOKEN set RESET_PWD_TOKEN=?, TOKEN_ISSUE_DATE=? where USER_ID=(select USER_ID from PUBTUSER where LOWER(USERNAME)=?)";
		jdbcTemplate.update(sql, resetToken, cal, StringUtils.lowerCase(username));
		return resetToken;
	}

	private String getFromUrl(HttpServletRequest request) {
		StringBuilder fullRequestUrlBuffer = new StringBuilder(request.getRequestURL());
		String queryString = request.getQueryString();
		if (queryString != null && !queryString.isEmpty()) {
			fullRequestUrlBuffer.append("?");
			fullRequestUrlBuffer.append(queryString);
		}
		try {
			return URLEncoder.encode(fullRequestUrlBuffer.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// never happen
			logger.error("unsupported encoding - UTF-8", e);
		}
		return "";
	}

	/**
	 * Sets static/private salt to be combined with the dynamic salt retrieved
	 * from the database. Optional.
	 *
	 * <p>
	 * If using this implementation as part of a password hashing strategy, it
	 * might be desirable to configure a private salt. A hash and the salt used
	 * to compute it are often stored together. If an attacker is ever able to
	 * access the hash (e.g. during password cracking) and it has the full salt
	 * value, the attacker has all of the input necessary to try to brute-force
	 * crack the hash (source + complete salt).
	 * </p>
	 *
	 * <p>
	 * However, if part of the salt is not available to the attacker (because it
	 * is not stored with the hash), it is much harder to crack the hash value
	 * since the attacker does not have the complete inputs necessary. The
	 * privateSalt property exists to satisfy this private-and-not-shared part
	 * of the salt.
	 * </p>
	 * <p>
	 * If you configure this attribute, you can obtain this additional very
	 * important safety feature.
	 * </p>
	 * 
	 * @param staticSalt
	 *            the static salt
	 */
	@Autowired
	public final void setStaticSalt(@Value("${tms.user.password.encode.staticSalt:}") String staticSalt) {
		this.staticSalt = staticSalt;
	}

	@Autowired
	public void setDataSource(@Qualifier("tmsUserDatabaseDataSource") DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Autowired
	public void setResetPasswordUrl(@Value("${tms.user.resetPasswordUrl:}") String resetPasswordUrl) {
		this.resetPasswordUrl = resetPasswordUrl;
	}

	public String getResetPasswordUrl() {
		return resetPasswordUrl;
	}

	@Autowired
	public void setForgetPasswordUrl(@Value("${tms.user.forgetPasswordUrl:}") String forgetPasswordUrl) {
		this.forgetPasswordUrl = forgetPasswordUrl;
	}

	public String getForgetPasswordUrl() {
		return forgetPasswordUrl;
	}

}
