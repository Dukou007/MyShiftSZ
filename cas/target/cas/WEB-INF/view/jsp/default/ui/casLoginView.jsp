<!DOCTYPE html>

<%@ page pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.net.URLEncoder"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
	String contextPath = request.getContextPath();
%>
<c:set var='contextPath' value='<%=contextPath%>' />
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<meta name="_csrf" content="${_csrf.token}" />
<meta name="_csrf_header" content="${_csrf.headerName}" />

<title>PAX Portfolio Manager &#8211; Login</title>
<link rel="icon" href="<%=contextPath%>/static/images/favicon.ico"
	type="image/x-icon">
<!-- Bootstrap css-->
<link href="${contextPath}/static/css/style.css" rel="stylesheet">
<!-- bootstrap-table -->
<script src="${contextPath}/static/js/libs/jquery-3.1.0.min.js"></script>
<!--[if lt IE 9]>
  <script src="${contextPath}/static/js/libs/html5shiv.min.js"></script>
  <script src="${contextPath}/static/js/libs/respond.min.js"></script>
  <![endif]-->
<style type="text/css">
input:focus {
  border-color: #66afe9;
  outline: 0;
  -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(102, 175, 233, 0.6);
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075), 0 0 8px rgba(102, 175, 233, 0.6);
}
</style>
<script type="text/javascript">
	var _WEB_SITE_ = '${contextPath}/';
	$(function(){
		$('#username').focus();
	})
</script>
<script src="${contextPath}/static/js/libs/sea.js"></script>
<script>
	seajs.config({
		debug : false,
		/*      map: [
		 [ '.js', '.min.js' ]
		 ],*/
		alias : {
			"jquery" : "libs/jquery-3.1.0.js",
			"jquery-ui" : "libs/jquery-ui.js",
			"bootstrap" : "libs/bootstrap.js",
			"validate" : "modules/jquery.validate.js",
			"iframe-transport" : "modules/jquery.iframe-transport.js",
			"validate-methods" : "modules/additional-methods.js",
			"table" : 'modules/bootstrap-table.js',
			'datetimepicker' : 'modules/bootstrap-datetimepicker.js',
			'fileupload' : 'modules/jquery.fileupload.js',
			'alert' : 'modules/jquery.bootstrap-growl.js',
			'highcharts' : 'modules/highcharts.js',
			'TMS' : 'modules/TMS.js',
			'xscroll' : 'modules/xscroll.js'
		},
		preload : [ 'jquery', 'bootstrap' ],
		base : '${contextPath}/static/js/'
	});
</script>
<script type="text/javascript" src="${contextPath}/static/js/modules/jsencrypt.js"></script>
</head>
<script type="text/javascript">
function handleSubmit() {
	Encrypt();
	$("#fm1").submit();
}
function Encrypt() {
	var password = $("#password").val();
    var PUBLIC_KEY = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCaDPx1NwZIPov0B28oNB9paXw6j4/+Co/dKz4r2+o323zb3QaMvwfhlNu6IJzhKesyqixxbeQYCZMcikd17SHdTThHYgDifxElFk3rd9i6iBhd+TQMuYgCvTc+orp+xH7gLVYka4oQK2NW9ytQVrOqd1QULKxf0mle1ADlr8uKNQIDAQAB';
    var encrypt = new JSEncrypt();
    encrypt.setPublicKey('-----BEGIN PUBLIC KEY-----' + PUBLIC_KEY + '-----END PUBLIC KEY-----');
    var encryptedPwd = encrypt.encrypt(password);
    $("#password").val(encryptedPwd);
}
</script>
<body class="login-body ">
	<c:if test="${not empty registeredService}">
		<c:set var="registeredServiceLogo" value="images/webapp.png" />
		<c:set var="registeredServiceName" value="${registeredService.name}" />
		<c:set var="registeredServiceDescription" value="${registeredService.description}" />

		<c:choose>
			<c:when test="${not empty mduiContext}">
				<c:if test="${not empty mduiContext.logoUrl}">
					<c:set var="registeredServiceLogo" value="${mduiContext.logoUrl}" />
				</c:if>
				<c:set var="registeredServiceName" value="${mduiContext.displayName}" />
				<c:set var="registeredServiceDescription" value="${mduiContext.description}" />
			</c:when>
			<c:when test="${not empty registeredService.logo}">
				<c:set var="registeredServiceLogo" value="${registeredService.logo}" />
			</c:when>
		</c:choose>
	</c:if>
	<!-- 主内容 -->
	<form:form method="post" class="login-panel" id="fm1" commandName="${commandName}" htmlEscape="true">
		<!--title disney-->
		<div class="subTopic" style="margin-bottom:10px;">
			<img class="" src="${contextPath}/static/images/logo_122806.png" alt='' width="340" height="39">
		</div>

		<!--middle box-->
		<div class="boxBaby ">
			<div class="login-title-box">Log in</div>

			<div class="login-form-panel">
				<!-- 错误信息 -->
				<form:errors path="*" id="msg" cssClass="login-error" element="div" htmlEscape="false" />
				<div class="form-input-login amount">
					<form:input cssClass="loginInput" cssErrorClass="loginInput error" id="username" size="25" tabindex="1"
						accesskey="${userNameAccessKey}" path="username" autocomplete="off" htmlEscape="true" placeholder="Username" 
						onkeydown="if(event.keyCode==13) handleSubmit();"/>
				</div>
				<div class="form-input-login password">
					<form:password cssClass="loginInput" cssErrorClass="loginInput error" id="password" size="25" tabindex="2"
						path="password" accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" placeholder="Password" 
						onkeydown="if(event.keyCode==13) handleSubmit();"/>
				</div>
				<div class="loginText">
					<!--div> class="checkbox checkbox-primary pull-left login-checkbox">
						<input type="checkbox" name="rememberMe" id="rememberMe" value="true" tabindex="5">
						<label for="rememberMe">Remember me</label>

					</div-->
					<a class="loginPass pull-right" href="${forgetPasswordUrl}">Forgot password?</a>
				</div>
				<div class="text-center">
					<input type="hidden" name="lt" value="${loginTicket}" />
					<input type="hidden" name="execution" value="${flowExecutionKey}" />
					<input type="hidden" name="_eventId" value="submit" />
					<button type='button' id='login' class="login-btn btn " tabindex="6" accesskey="l" onclick="handleSubmit()">Log in</button>
				</div>
			</div>
		</div>
	</form:form>
	<div class="login-footer">
		<span> Mailbox： <a>ppmsupport@pax.us</a>
		</span> <span>Copyright © 2018 PAX All Rights Reserved.</span>
	</div>
	<!-- 主内容end -->
</body>

</html>