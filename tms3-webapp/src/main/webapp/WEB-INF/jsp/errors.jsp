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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String contextPath = request.getContextPath();
%>
<c:set var='contextPath' value='<%=contextPath%>' />
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<title>PAX Portfolio Manager</title>
<link href="${contextPath}/static/css/other.css" rel="stylesheet">
<link rel="icon" href="<%=contextPath%>/static/images/favicon.ico" type="image/x-icon">
<!-- Bootstrap css-->
<!--[if lt IE 9]>
      <script src="static/js/libs/html5shiv.min.js"></script>
      <script src="static/js/libs/respond.min.js"></script>
      <![endif]-->
</head>

<body class="hint_body">
	<div class="hint_logo">
		<img class="" src="${contextPath}/static/images/logo_122806.png" alt="" width="340" height="39">
	</div>
	<div class="hint_container">
		<div class="hint_title warning_title">
			<p></p>
		</div>
		<div class="hint_msg">
			<c:choose>
				<c:when test="${empty exception.message}">
					<h2 class="hint-h2">Error</h2>
					<p>There was an error trying to complete your request. Please notify your support desk or try again.</p>
				</c:when>
				<c:otherwise>
					<h2>Error</h2>
					<p> <c:out value="${exception.message}" escapeXml="true" /></p>
				</c:otherwise>
			</c:choose>
			<p>
				<a href="javascript:void(0);" onclick="history.go(-1)" class="backBtn">Back</a>
			</p>
		</div>
	</div>
	<div class="hint_footer">
		Mailbox： <a>ppmsupport@pax.us</a> Copyright © 2020 PAX All Rights Reserved.
	</div>
	<!-- FOOTER -->
	<!--    <script type="text/javascript">
 seajs.use('index');
 </script> -->
	<!-- END -->
</body>

</html>