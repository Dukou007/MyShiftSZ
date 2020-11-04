/*		
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
 */
define(function(require, exports, module){
	var TMS = require('TMS');
	var validate = require('validate-methods');
	  $(function() {
		 TMS.init();
       
        $('#changePwdForm').validate({

        	submitHandler:function(form){
        		Encrypt();
        		TMS.getData({
        			url: _WEB_SITE_ + '/myProfile/service/changePassword',
	       			type: 'post',
	       			data: $("#changePwdForm").serialize(),
	       			followTo: _WEB_SITE_+'/myProfile/view/'+GROUP_ID
        		}, function(data){
					if (data.statusCode && data.statusCode != '200') {
						TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                        TMS.alert(data.message, 'danger');
						$("#oldPwd").val('');
					    $("#newPwd").val('');
					    $("#confirmNewPwd").val('');
						return false;
					}
				});
        		
        	}
        });

    })
	function Encrypt() {
		var oldPassword = $("#oldPwd").val();
		var newPassword = $("#newPwd").val();
		var confirmNewPwd = $("#confirmNewPwd").val();
	    var PUBLIC_KEY = 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCaDPx1NwZIPov0B28oNB9paXw6j4/+Co/dKz4r2+o323zb3QaMvwfhlNu6IJzhKesyqixxbeQYCZMcikd17SHdTThHYgDifxElFk3rd9i6iBhd+TQMuYgCvTc+orp+xH7gLVYka4oQK2NW9ytQVrOqd1QULKxf0mle1ADlr8uKNQIDAQAB';
	    var encrypt = new JSEncrypt();
	    encrypt.setPublicKey('-----BEGIN PUBLIC KEY-----' + PUBLIC_KEY + '-----END PUBLIC KEY-----');
	    var encryptedOldPwd = encrypt.encrypt(oldPassword);
	    var encryptedNewPwd = encrypt.encrypt(newPassword);
	    var encryptedConfirmNewPwd = encrypt.encrypt(confirmNewPwd);
	    $("#oldPwd").val(encryptedOldPwd);
	    $("#newPwd").val(encryptedNewPwd);
	    $("#confirmNewPwd").val(encryptedConfirmNewPwd);
	}
})

