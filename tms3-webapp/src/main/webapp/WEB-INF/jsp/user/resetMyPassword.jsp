<!DOCTYPE html>
<%@ page pageEncoding="UTF-8"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
<title>Reset password</title>
<link rel="icon" href="<%=contextPath%>/static/images/favicon.ico"
	type="image/x-icon">
<link href="${contextPath}/static/css/style.css" rel="stylesheet">

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
	var _TOKEN_HEADER_NAME_ ="X-CSRF-TOKEN";
	var _TOKEN_= document.getElementsByName('_csrf')[0].getAttribute('content');
	var _WEB_SITE_ = '${contextPath}';
	var _followTo = _WEB_SITE_ + "?renew=true";
</script>
<script src="${contextPath}/static/js/libs/sea.js"></script>
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
			"validate" : "modules/jquery.validate.js",
			"iframe-transport" : "modules/jquery.iframe-transport.js",
			"validate-methods" : "modules/additional-methods.js",
			"table" : 'modules/bootstrap-table.js',
			'datetimepicker' : 'modules/bootstrap-datetimepicker.js',
			'fileupload' : 'modules/jquery.fileupload.js',
			'alert' : 'modules/jquery.bootstrap-growl.js',
			'chart' : 'modules/chart.js',
			'TMS' : 'modules/TMS.js',
			'xscroll' : 'modules/xscroll.js',
			'btselect' : 'modules/bootstrap-mutiselect.js'
		},
		preload : [ 'jquery', 'bootstrap' ],
		base : '${contextPath}/static/js/'
	});
</script>
<script type="text/javascript" src="${contextPath}/static/js/modules/jsencrypt.js"></script>
</head>
<body class="login-body ">
  <!--title pax-->
  
  <!-- 主内容 -->
  <div class="login-panel">

    <!--title disney-->
    <div class="subTopic">
      <img class="" data-token="${_csrf.token}" src="${contextPath}/static/images/logo_122806.png" alt='' width="340" height="39"></div>
    <!--middle box-->
    <div class="boxBaby ">

      <div class="login-title-box">Set User Password</div>
      <form role="form "  id="reset-mypassword-form" autocomplete="off">
        <input type="hidden" name="username" value="<c:out value="${param.username}" escapeXml="true" />" />
		<input type="hidden" name="token" value="<c:out value="${param.token}" escapeXml="true" />"  />
        <div class="login-form-panel">
          <!-- 错误信息 -->
		  <div class="login-error" style="display:none" >
		  </div>
		  <div class="login-success" style="display:none" >
		  </div>
          <div  class="loginText text-left" >
          <!-- <span style="font-size:14px;color:#9da4b6;padding-left:15px;"></span> --><span style="font-size:20px;font-weight:500;color:#4a647e"> <c:out value="${param.username}" escapeXml="true" /></span>
          </div>
          <div class="form-input-login password">
            <input class="loginInput required passwordCheck firstFocus" type="password"  id="newPassword" name='newPassword' minlength="7" maxlength="16" id="newPwd" placeholder="New password" autocomplete="off"></div>
          <div class="form-input-login password">
            <input class="loginInput required" type="password" id="confirmNewPwd" value="" placeholder="Confirm password" autocomplete="off" equalTo="#newPassword"></div>
          <div class="text-center clearfix">
            <button type='submit' id='login' data-loading-text="login" class="login-btn btn ">Confirm</button>
          </div>
        </div>
      </form>
    </div>
  </div>
  <div class="login-footer">
    <span>
      Mailbox：
      <a>ppmsupport@pax.us</a>
    </span>
    <span>Copyright © 2020 PAX All Rights Reserved.</span>
  </div>
  <!-- /.modal -->
<!-- reset password -->
<div class="modal in" id="choose-login-modal">
<div class="modal-dialog">
	<div class="modal-content">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal">
				<span aria-hidden="true">&times;</span>
				<span class="sr-only">Close</span>
			</button>
			<h4 class="modal-title">PLEASE SELECT A SYSTEM</h4>
		</div>
		<div class="modal-body text-center">
			<div class="choose-login-panel clearfix">
				<a href="${tmsAddress}" class="role-style pull-left ppm">
					<i class="iconfont icon-choose-login">&#xe641;</i>
					<span class="choose-login-des">PAX Portfolio Manager</span>
				</a>
				<a href="${pxAddress}" data-url="${pxAddress}" class="role-style pull-right pxdesigner">
					<i class="icon iconfont icon-choose-login">&#xe640;</i>
					<span class="choose-login-des">PX Designer</span>
				</a>
				
			</div>
			
		</div>
	</div>
	<!-- /.modal-content -->
</div>
<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
	<script type="text/javascript">
		seajs.use([ 'TMS', 'validate-methods' ], function(TMS) {
			var pxaddress=$('.pxdesigner').attr('data-url'); 
			$('#newPassword').focus();
			$('#reset-mypassword-form').validate({
				onfocusout: false,
				onkeyup:false,
				onclick:false,

                showErrors: function(errorMap, errorList) {
                    var temp = [];
                    if(errorList.length==0){
                    	return false;
                    }
                    for (var i = 0; i < errorList.length; i++) {
                        var name = $(errorList[i].element).attr('placeholder');
                        temp.push(name+' : ' + errorList[i].message+'<br>');
                    }
                    temp = temp.join('');
                    $('.login-error').html(temp).show();
                },
				submitHandler : function(form) {
					var token = {};
				    token[_TOKEN_HEADER_NAME_]=_TOKEN_;
				    Encrypt();
					TMS.getData({
							url : _WEB_SITE_ + '/user/service/resetMyPassword',
							type : 'POST',
							data : $(form).serialize(),
							headers: token
						 } ,function(data){
							if (data.statusCode && data.statusCode != '200') {
								$('.login-error').html(data.message).show();
								$('.login-success').hide();
								$("#newPassword").val('');
							    $("#confirmNewPwd").val('');
								return false;		
							} else {
								$('.login-success').html('Success').show();
								$('.login-error').hide();
								if("${pxAddress}") {
									$('.login-success').html('Success').show();
									setTimeout(function () {
										$('#choose-login-modal').modal('show');
									  }, 1000);
								} else {
									setTimeout(
											function() {
												var tmsAddress="${tmsAddress}";
												window.location.href = tmsAddress;
											}, 2000);
								}
							}
						
					} );
					return false;
				}
			});
		});
		function Encrypt() {
			var newPassword = $("#newPassword").val();
			var confirmNewPwd = $("#confirmNewPwd").val();
		    var PUBLIC_KEY = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCaDPx1NwZIPov0B28oNB9paXw6j4/+Co/dKz4r2+o323zb3QaMvwfhlNu6IJzhKesyqixxbeQYCZMcikd17SHdTThHYgDifxElFk3rd9i6iBhd+TQMuYgCvTc+orp+xH7gLVYka4oQK2NW9ytQVrOqd1QULKxf0mle1ADlr8uKNQIDAQAB';
		    var encrypt = new JSEncrypt();
		    encrypt.setPublicKey('-----BEGIN PUBLIC KEY-----' + PUBLIC_KEY + '-----END PUBLIC KEY-----');
		    var encryptedNewPwd = encrypt.encrypt(newPassword);
		    var encryptedConfirmNewPwd = encrypt.encrypt(confirmNewPwd);
		    $("#newPassword").val(encryptedNewPwd);
		    $("#confirmNewPwd").val(encryptedConfirmNewPwd);
		}
		
	</script>
</body>

</html>
