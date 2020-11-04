<!-- 		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) YYYY-? PAX Technology, Inc. All rights reserved.		
 * Description: // Detail description about the function of this module,		
 *             // interfaces with the other modules, and dependencies. 	
 * Revision History:		
 * Date	                 Author	                Action
 * 2017-1-10 	         TMS_HZ           	Create/Add/Modify/Delete
 * ============================================================================		
-->
<!DOCTYPE html>

<%@ page pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page
	import="org.apache.shiro.SecurityUtils, org.pac4j.core.profile.CommonProfile"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@ taglib prefix="tms" uri="http://www.pax.com/tms"%>
<%@page import="java.util.ResourceBundle"%>
<%
	String contextPath = request.getContextPath();
	String loginUsername = null;
	Object loginUserId = null;
	String loginUserRoles = null;
	Boolean isLdap = false;
	String displayName = null;
	ResourceBundle tmsResource = ResourceBundle.getBundle("tms");

	Object principal = SecurityUtils.getSubject().getPrincipal();
	if (principal instanceof io.buji.pac4j.subject.Pac4jPrincipal) {
		CommonProfile profile = ((io.buji.pac4j.subject.Pac4jPrincipal) principal).getProfile();
		loginUsername = profile.getId();
		loginUserId = profile.getAttribute("userId");
		loginUserRoles = (String) profile.getAttribute("userRoles");
		isLdap = (Boolean) profile.getAttribute("isLdap");
		displayName = (String) profile.getAttribute("displayName");
	} else if (principal instanceof com.pax.login.TmsPac4jPrincipal) {
		CommonProfile profile = ((com.pax.login.TmsPac4jPrincipal) principal).getProfile();
		loginUsername = profile.getId();
		loginUserId = profile.getAttribute("userId");
		loginUserRoles = (String) profile.getAttribute("userRoles");
		isLdap = (Boolean) profile.getAttribute("isLdap");
		displayName = (String) profile.getAttribute("displayName");
	}
	if (isLdap == null) {
		isLdap = false;
	}

	Cookie cookies[] = request.getCookies();
	for (int i = 0; i < cookies.length; i++) {
		Cookie cookie = cookies[i];
		if (cookie != null && "isSmall".equals(cookie.getName())) {
			pageContext.setAttribute("layoutSmall", cookie.getValue());
		}
	}
%>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<meta name="description" content="" />
<meta name="keywords" content="" />
<meta name="author" content="" />
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />
<title>PAX Portfolio Manager</title>
<link rel="shortcut icon" href="static/images/favicon.ico" type="image/x-icon">
<link rel="icon" href="<%=contextPath%>/static/images/favicon.ico" type="image/x-icon">
<link href="<%=contextPath%>/static/css/style.css" rel="stylesheet">
<!--[if lt IE 9]>
	<script src="static/js/libs/html5shiv.min.js"></script>
	<script src="static/js/libs/respond.min.js"></script>
	<![endif]-->
<script type="text/javascript">
	var _TOKEN_= document.getElementsByName('_csrf')[0].getAttribute('content');
	var _TOKEN_HEADER_NAME_ ="X-CSRF-TOKEN";
	var _WEB_SITE_ = '<%=contextPath%>';
	var _ACTIVE_GROUP_ = "${group.id}";
	var _URL_= "${activeUrl}";
	var _ACTIVE_URL_=_WEB_SITE_+"${activeUrl}";
	var _NAME_PATH_ = "${group.namePath}" ; 
    var _ACTIVE_TSN_ = "${terminal.tsn}";
    var _USER_ID_='<%=loginUserId%>';
</script>

<script src="<%=contextPath%>/static/js/libs/sea.js"></script>
<script>
	seajs.config({
		debug : true,
		// map: [
		//   [ '.js', '.min.js' ]
		// ],
		alias : {
			"jquery" : "libs/jquery-3.1.0.js",
			"jquery-ui" : "libs/jquery-ui.js",
			"bootstrap" : "libs/bootstrap.js",
			"gridly" : "libs/jquery.gridly.js",
			"validate" : "modules/jquery.validate.js",
			"iframe-transport" : "modules/jquery.iframe-transport.js",
			"validate-methods" : "modules/additional-methods.js",
			"table" : 'modules/bootstrap-table.js',
			'datetimepicker' : 'modules/bootstrap-datetimepicker.js',
			'date-picker' : 'modules/laydate.js',
			'fileupload' : 'modules/jquery.fileupload.js',
			'alert' : 'modules/jquery.bootstrap-growl.js',
			'chart' : 'modules/chart.js',
			'TMS' : 'modules/TMS.js',
			'xscroll' : 'modules/xscroll.js',
			'btselect' : 'modules/bootstrap-mutiselect.js'
		},
		preload : [ 'jquery', 'bootstrap'],
		base : '<%=contextPath%>/static/js/'
	});
</script>
<script type="text/javascript" src="<%=contextPath%>/static/js/modules/jsencrypt.js"></script>
</head>
<body class="g-body">
	<input type="hidden" value="${title}" class="g-top-title">
	<div class="g-container">
		<!-- 导航 -->
		<div class="g-left-content">
			<a class="g-logo-box" href="<%=contextPath%>/index/${group.id}" title="PAX Porfolio Manager
			   ">
			</a>
			<ul class="g-left-menu" id="top-left-tree">
			</ul>
			<a class="hide-group-btn iconfont"> &#xe616; </a>
		</div>

		<!-- 导航end -->
		<div class="g-body-content">
			<!-- 吊顶 -->
			<div class="g-top-content">
				<c:if test="${not empty terminal}">
					<div class="g-terminal-bj"></div>
				</c:if>
				<c:if test="${empty terminal}">
					<div class="g-group-bj" ></div>
				</c:if>
				<div class="g-top-search">

					<i class="g-search-btn iconfont" title="search group or terminal">&#xe66a;</i>
					<ul class="g-path ">
					</ul>
					<div class="g-search-show hide">
						<i class="g-search-showbtn iconfont">&#xe66a;</i> <input
							type="text" class="g-search-input form-control"
							placeholder="Search group or terminal here">
						<ul class="g-search-list hide">
						</ul>
					</div>
				</div>
				<!-- 手机端切换按钮 -->
				<!-- version info -->
				<button type="button"
					class="navbar-toggle about-pxmaster-navbar-toggle iconfont" type="button"
					data-toggle="collapse" data-target=".about-pxmaster-collapse">&#xe6a8;</button> 
				<!-- setting -->
				<button type="button"
					class="navbar-toggle setting-navbar-toggle iconfont" type="button"
					data-toggle="collapse" data-target=".bs-js-setting-collapse">&#xe60f;</button>
				<!-- first -->
				<button type="button"
					class="navbar-toggle menu-navbar-toggle iconfont" type="button"
					data-toggle="collapse" data-target=".bs-js-navbar-collapse">&#xe611;</button>
				
				<!-- 手机端全局菜单 -->
				<shiro:hasPermission name="tms:package:view">
					<div class="navbar-collapse bs-js-setting-collapse collapse">
						<div class="g-menu">
							<ul class="g-menu-nav">
								<shiro:hasPermission name="tms:user:view">
									<li role="presentation"><a role="menuitem" tabindex="-1"
										href="<%=contextPath%>/user/list/${group.id}"> <i
											class="iconfont">&#xe66f;</i> User Management
									</a></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:package:view">
									<li role="presentation"><a role="menuitem" tabindex="-1"
										href="<%=contextPath%>/pkg/manageList/${group.id}"> <i
											class="iconfont">&#xe66f;</i> Manage Packages
									</a></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:group:view">
									<li role="presentation"><a role="menuitem" tabindex="-1"
										href="<%=contextPath%>/group/list/${group.id}"> <i
											class="iconfont">&#xe66f;</i> Manage Groups
									</a></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:audit log:view">
									<li role="presentation"><a role="menuitem" tabindex="-1"
										href="<%=contextPath%>/auditLog/list/${group.id}"> <i
											class="iconfont">&#xe66f;</i> Audit Log
									</a></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:report:terminal not registered">
									<li role="presentation"><a role="menuitem" tabindex="-1"
										href="<%=contextPath%>/report/terminalNotRegistered/${group.id}">
											<i class="iconfont">&#xe66f;</i> Terminal Not Registered
									</a></li>
								</shiro:hasPermission>
							</ul>
						</div>
					</div>
				    <div class="navbar-collapse about-pxmaster-collapse collapse">
						<div class="g-menu">
							<ul class="g-menu-nav">
								<li role="presentation" title="PAX Portfolio Manager version"><a role="menuitem" tabindex="-1"
									data-toggle="modal" data-target="#about-pxmaster" >
									 <i class="iconfont">&#xe66f;</i>About PAX Portfolio Manager</a></li>
									<shiro:hasPermission name="tms:system config:view">
										<li role="presentation" title="system infomation" class="sysinfoBtn">
											 <a><i class="iconfont">&#xe66f;</i>System Information</a>
										</li>
								    </shiro:hasPermission>
							</ul>
						</div>
					</div>
				</shiro:hasPermission>
				<!-- 手机端全局菜单end -->
				<div class="navbar-collapse bs-js-navbar-collapse collapse">
					<c:if test="${empty terminal}">
						<div class="g-menu">
							<ul class="g-menu-nav">
								<shiro:hasPermission name="tms:dashboard:view">
									<li title="Dashboards" class="nochild"><a
										href="<%=contextPath%>/index/${group.id}"> <i
											class="iconfont">&#xe600;</i>Dashboards
									</a></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:alert:list:view">
									<li title="Alerts"><a href="#" data-toggle="dropdown">
											<i class="iconfont">&#xe670;</i> Alerts
									</a>
										<ul class="dropdown-menu" role="menu">
											<li role="presentation" title="Alerts List"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/events/alertEvents/${group.id}">Alerts
													List</a></li>
											<li role="presentation" title="Alert Off"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/alert/alertOff/${group.id}">Alert
													Off</a></li>
											<li role="presentation" title="Manage Conditions"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/alert/alertCondition/${group.id}">Manage
													Conditions</a></li>
										</ul></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:event:view">
									<li title="Events"><a href="#" data-toggle="dropdown">
											<i class="iconfont">&#xe66d;</i> Events
									</a>
										<ul class="dropdown-menu" role="menu">
											<li role="presentation"  title="Events List"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/events/allEvents/${group.id}">Events List</a></li>
											<li role="presentation"  title="Statistics List"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/dashboard/statistics/${group.id}">Statistics List</a></li>
										</ul></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:terminal:view">
									<li title="Terminals"><a href="" data-toggle="dropdown">
											<i class="iconfont">&#xe603;</i> Terminals
									</a>
										<ul class="dropdown-menu" role="menu">
											<li role="presentation" title="Terminals List"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/terminal/list/${group.id}">Terminals List</a></li>
											<shiro:hasPermission name="tms:terminal:add">
												<li role="presentation" title="Add Terminal(s)"><a class="icon-add"
													role="menuitem" tabindex="-1"
													href="<%=contextPath%>/terminal/toAdd/${group.id}">Add Terminal(s)</a></li>
											</shiro:hasPermission>
											<shiro:hasPermission name="tms:terminal:import">
												<li role="presentation" title="Import Terminals"><a role="menuitem"
													tabindex="-1"
													href="<%=contextPath%>/terminal/toImport/${group.id}">Import Terminals</a></li>
											</shiro:hasPermission>
										</ul></li>
								</shiro:hasPermission>

								<shiro:hasPermission name="tms:package:view">
									<li title="Packages"><a href="#" data-toggle="dropdown">
											<i class="iconfont">&#xe604;</i> Packages
									</a>
										<ul class="dropdown-menu" role="menu">
											<li role="presentation" title="Packages List"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/pkg/list/${group.id}">Packages
													List</a></li>
											<shiro:hasPermission name="tms:group:deployments:add">
												<li role="presentation" title="Deploy Package"><a role="menuitem"
													tabindex="-1"
													href="<%=contextPath%>/groupDeploy/toDeploy/${group.id}">Deploy
														Package</a></li>
											</shiro:hasPermission>
											<shiro:hasPermission name="tms:group:deployments:view">
												<li role="presentation" title="Package Deployments"><a role="menuitem"
													tabindex="-1"
													href="<%=contextPath%>/groupDeploy/list/${group.id}">Deployments</a></li>
											</shiro:hasPermission>
										</ul></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:offlinekey:view">
									<li title="Keys"><a href="#" data-toggle="dropdown">
											<i class="iconfont">&#xe673;</i> Keys
									</a>
										<ul class="dropdown-menu" role="menu">
											<li role="presentation" title="Keys List"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/offlinekey/list/${group.id}">Keys
													List</a></li>
											<shiro:hasPermission name="tms:offlinekey:import">
												<li role="presentation" title="Import Keys"><a role="menuitem" tabindex="-1"
													href="<%=contextPath%>/offlinekey/toImport/${group.id}">Import
														Keys</a></li>
											</shiro:hasPermission>
											<shiro:hasPermission name="tms:group:deployments:add">
												<li role="presentation" title="Deploy Keys"><a role="menuitem"
													tabindex="-1"
													href="<%=contextPath%>/groupDeploykey/toDeploy/${group.id}">Deploy
														Key</a></li>
											</shiro:hasPermission>
											<shiro:hasPermission name="tms:group:deployments:view">
												<li role="presentation" title="Key Deployments"><a role="menuitem"
													tabindex="-1"
													href="<%=contextPath%>/groupDeploykey/list/${group.id}">Deployments</a></li>
											</shiro:hasPermission>
										</ul></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:report:user maintenance">
									<li title="Reports"><a href="#" data-toggle="dropdown">
											<i class="iconfont">&#xe601;</i> Reports
									</a>
										<ul class="dropdown-menu dropdown-menu-right" role="menu">
											<li role="presentation" title="User Maintenance Report"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/report/userMaintenance/${group.id}">User
													Maintenance Report</a></li>
											<li role="presentation" title="Terminal Download Report"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/report/terminalDownload/${group.id}">Terminal
													Download Report</a></li>
											<shiro:hasPermission name="tms:report:billing">
												<li role="presentation" title="Billing Report"><a role="menuitem" tabindex="-1"
													href="<%=contextPath%>/report/billingReport/${group.id}">
														Billing Report</a></li>
											</shiro:hasPermission>
										</ul></li>
								</shiro:hasPermission>
							</ul>
						</div>
					</c:if>

					<c:if test="${not empty terminal}">
						<div class="g-menu">
							<ul class="g-menu-nav">
								<shiro:hasPermission name="tms:dashboard:view">
									<li title="Dashboards" class="nochild"><a
										href="<%=contextPath%>/terminal/monitor/${group.id}/${terminal.tsn}">
											<i class="iconfont">&#xe600;</i> Dashboards
									</a></li>
								</shiro:hasPermission>

								<shiro:hasPermission name="tms:event:view">
									<li title="Events"><a href="#" data-toggle="dropdown">
											<i class="iconfont">&#xe66d;</i> Events
									</a>
										<ul class="dropdown-menu" role="menu">
											<li role="presentation" title="Events List"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/events/terminalEvents/${group.id}/${terminal.tsn}">Events List</a></li>
											<li role="presentation" title="Statistics List"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/dashboard/terminalStatistics/${group.id}/${terminal.tsn}">Statistics List</a></li>
										</ul></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:terminal:view">
									<li title="Terminals" class="nochild"><a
										href="<%=contextPath%>/terminal/list/${group.id}/${terminal.tsn}">
											<i class="iconfont">&#xe603;</i> Terminals
									</a></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:package:view">
									<li title="Packages"><a href="#" data-toggle="dropdown">
											<i class="iconfont">&#xe604;</i> Packages
									</a>
										<ul class="dropdown-menu" role="menu">
											<li role="presentation" title="Packages List"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/pkg/list/${group.id}/${terminal.tsn}">Packages
													List</a></li>
											<shiro:hasPermission name="tms:terminal:deployments:add">
												<li role="presentation" title="Deploy Package"><a role="menuitem"
													tabindex="-1"
													href="<%=contextPath%>/terminalDeploy/toDeploy/${group.id}/${terminal.tsn}">Deploy
														Package</a></li>
											</shiro:hasPermission>
											<shiro:hasPermission name="tms:terminal:deployments:view">
												<li role="presentation" title="Package Deployments"><a role="menuitem"
													tabindex="-1"
													href="<%=contextPath%>/terminalDeploy/list/${group.id}/${terminal.tsn}">Deployments</a></li>
											</shiro:hasPermission>
										</ul></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:offlinekey:view">
									<li title="Keys"><a href="#" data-toggle="dropdown">
											<i class="iconfont">&#xe673;</i> Keys
									</a>
										<ul class="dropdown-menu" role="menu">
											<li role="presentation" title="Keys List"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/offlinekey/list/${group.id}/${terminal.tsn}">Keys
													List</a></li>
											<shiro:hasPermission name="tms:terminal:deployments:add">
												<li role="presentation" title="Deploy Keys"><a role="menuitem"
													tabindex="-1"
													href="<%=contextPath%>/terminalDeploykey/toDeploy/${group.id}/${terminal.tsn}">Deploy
														Key</a></li>
											</shiro:hasPermission>
											<shiro:hasPermission name="tms:terminal:deployments:view">
												<li role="presentation" title="Key Deployments"><a role="menuitem"
													tabindex="-1"
													href="<%=contextPath%>/terminalDeploykey/list/${group.id}/${terminal.tsn}">Deployments</a></li>
											</shiro:hasPermission>
										</ul></li>
								</shiro:hasPermission>
								<shiro:hasPermission name="tms:report:user maintenance">
									<li title="Reports"><a href="#" data-toggle="dropdown">
											<i class="iconfont">&#xe601;</i> Reports
									</a>
										<ul class="dropdown-menu" role="menu">
											<li role="presentation" title="Terminal Download Report"><a role="menuitem" tabindex="-1"
												href="<%=contextPath%>/report/terminalDownload/${group.id}/${terminal.tsn}">Terminal
													Download Report</a></li>
										</ul></li>
								</shiro:hasPermission>
							</ul>
						</div>
					</c:if>

					<div class="g-top-right">
						<div class="g-user-box">
							<a href="#" class="dropdown-toggle g-user-name"
								data-toggle="dropdown" title="<%=displayName%>"> <%=displayName%><span class="caret"></span>
							</a>
							<c:if test="${empty terminal}">
								<ul class="dropdown-menu dropdown-menu-right"
									style="right: -40px;" role="menu">
									<li role="presentation" title="My Profile"><a role="menuitem" tabindex="-1"
										href="<%=contextPath%>/myProfile/view/${group.id}">My Profile</a>
									</li>
									<%
										if (isLdap == false) {
									%>
									<li role="presentation" title="Change Password"><a role="menuitem" tabindex="-1"
										href="<%=contextPath%>/myProfile/toChangePassword/${group.id}">Change
											Password</a></li>
									<%
										}
									%>

									<li id="logout-btn" role="presentation" title="Log out"><a role="menuitem" tabindex="-1">Log out</a></li>
								</ul>
							</c:if>
							<c:if test="${not empty terminal}">
								<ul class="dropdown-menu dropdown-menu-right"
									style="right: -40px;" role="menu">
									<li role="presentation" title="Profile"><a role="menuitem" tabindex="-1"
										href="<%=contextPath%>/myProfile/view/${group.id}/${tsn}">Profile</a>
									</li>
									<%
										if (isLdap == false) {
									%>
									<li role="presentation" title="Change Password"><a role="menuitem" tabindex="-1"
										href="<%=contextPath%>/myProfile/toChangePassword/${group.id}/${tsn}">Change
											Password</a></li>
									<%
										}
									%>
									<li id="logout-btn" role="presentation" title="Log out"><a role="menuitem" tabindex="-1">Log out</a></li>
								</ul>
							</c:if>
						</div>
						<span class="g-user-sprit" style="color:transparent">|</span>
						<div class="g-setting">
							<a href="#" class="iconfont g-setting-icon"
								data-toggle="dropdown">&#xe70b;</a>
							<ul class="dropdown-menu dropdown-menu-right" role="menu">
								<li role="presentation"><a role="menuitem" tabindex="-1"
									href="<%=tmsResource.getString("pxdesigner.app.link")%>" target="_blank">PXDesigner</a>
								</li>
							</ul>
						</div>
						<span class="g-user-sprit">|</span>
						<shiro:hasPermission name="tms:package:view">
							<div class="g-setting">
								<a href="#" class="iconfont g-setting-icon"
									data-toggle="dropdown" title="Settings">&#xe60f;</a>
								<ul class="dropdown-menu dropdown-menu-right" role="menu">
									<shiro:hasPermission name="tms:user:view">
										<li role="presentation" title="User Management"><a role="menuitem" tabindex="-1"
											href="<%=contextPath%>/user/list/${group.id}">User
												Management</a></li>
									</shiro:hasPermission>
									<shiro:hasPermission name="tms:package:view">
										<li role="presentation" title="Manage Packages"><a role="menuitem" tabindex="-1"
											href="<%=contextPath%>/pkg/manageList/${group.id}">Manage
												Packages</a></li>
									</shiro:hasPermission>
									<shiro:hasPermission name="tms:group:view">
										<li role="presentation" title="Manage Groups"><a role="menuitem" tabindex="-1"
											href="<%=contextPath%>/group/list/${group.id}">Manage
												Groups</a></li>
									</shiro:hasPermission>
									<shiro:hasPermission name="tms:audit log:view">
										<li role="presentation" title="Audit Log"><a role="menuitem" tabindex="-1"
											href="<%=contextPath%>/auditLog/list/${group.id}">Audit
												Logs</a></li>
									</shiro:hasPermission>
									<shiro:hasPermission name="tms:terminal log:view">
										<li role="presentation" title="Terminal Log"><a role="menuitem" tabindex="-1"
											href="<%=contextPath%>/terminal/log/${group.id}">Terminal
												Logs</a></li>
									</shiro:hasPermission>
									<shiro:hasPermission name="tms:report:terminal not registered">
										<li role="presentation" title="Terminal Not Registered"><a role="menuitem" tabindex="-1"
											href="<%=contextPath%>/report/terminalNotRegistered/${group.id}">Terminal
												Not Registered</a></li>
									</shiro:hasPermission>
								</ul>
							</div>
						</shiro:hasPermission>
						<span class="g-user-sprit">|</span>
						<div class="g-setting">
								<a href="#" class="iconfont g-setting-icon"
									data-toggle="dropdown" title="PAX Portfolio Manager Version">&#xe6a8;</a>
								<ul class="dropdown-menu dropdown-menu-right" role="menu">
										<li role="presentation" title="About PAX Portfolio Manager"><a role="menuitem" tabindex="-1"
											data-toggle="modal" data-target="#about-pxmaster" >About PAX Portfolio Manager</a></li>
										<shiro:hasPermission name="tms:system config:view">
										<li role="presentation" title="system infomation" class="sysinfoBtn">
											 <a>System Information</a>
										</li>
										</shiro:hasPermission>
								</ul>
							</div>
						
					</div>
				</div>

			</div>
			<a class="show-group-btn iconfont"> &#xe610; </a>
			<!-- 吊顶end -->