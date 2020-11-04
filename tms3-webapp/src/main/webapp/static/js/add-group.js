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
define(function(require, exports, module) {
    var TMS = require('TMS');
    var validate = require('validate-methods');
    $(function() {
    	TMS.init();
    	//添加country,province,timezone下拉监听
    	TMS.setAreatimeZone('#countryId','#provinceId', '#timeZoneId');
    	
    	var groupId = $("#gid").val();
   
        $("#addGroup").validate({
            submitHandler: function(form) {
                TMS.getData({
                    url: _WEB_SITE_+'/group/service/add',
                    type: 'post',
                    data: $(form).serialize(),
                    followTo:_WEB_SITE_+"/group/list/"+groupId
                });
                return false;
            }
        });
        /*$('#countryId').change(function() {
			var elem = $(this).val();
			if (elem == '1') {
				$('.zipcode').removeClass('canadaZipcode');
				$('.zipcode').removeClass('text-uppercase');
			    $('.zipcode').attr('maxlength', '5');
				$('.zipcode').attr('minlength', '5');
				$('.zipcode').addClass('usZipcode');
			}else if (elem == '2') {
				$('.zipcode').removeClass('usZipcode');
				$('.zipcode').attr('maxlength', '7');
				$('.zipcode').attr('minlength', '6');
				$('.zipcode').addClass('canadaZipcode');
				$('.zipcode').addClass('text-uppercase');
			}else{
				$('.zipcode').removeClass('usZipcode');
				$('.zipcode').removeClass('canadaZipcode');
				$('.zipcode').removeClass('text-uppercase');
				$('.zipcode').attr('maxlength', '7');
				$('.zipcode').attr('minlength', '1');
			}
			
		});*/
        
    });
})
