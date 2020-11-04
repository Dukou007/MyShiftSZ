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
	require('validate-methods');
	$(function(){
		TMS.init();
		var groupIds = $("#groupIds").val()?$("#groupIds").val().replace(/\[|\]/gi,'').split(','):'';
		groupIds.indexOf($("#vid").val().toString())!=-1?$("#Delete").hide():$("#Delete").show();
		$("#EditButton").show();
		//取到profile页面的timezone值
		var timezoneVal = $('#profile-timezone').text().trim();
		TMS.setAreatimeZone('#countryId','#provinceId', '#timeZoneId');
		var countryName = $('#d-country-name').text();
		if(countryName == 'Canada'){
			var zipCode = $('#d-zipCode').text();
			if(zipCode.length == 6){
				var temp = [];
				for(var i=0;i<zipCode.length;i++){
					if(i == 3){
						temp.push(' '+zipCode[i]);
					}else{
						temp.push(zipCode[i]);
					}
				}
				$('#d-zipCode').text(temp.join(''));
			}
		}
		
		var countryDefault = $('#countryId').val();
 		var provinceDefault = $('#viewProvinceName').text();
 		var provinceId = $('#province_id').attr('data-province_id');
 		if(countryDefault){
 			TMS.setProvince(countryDefault,provinceDefault,provinceId);
 		} 
 		
		var groupId = $("#gid").val();
		var vId = $("#vid").val();
		var parentId = $("#parentId").val();
		var groupIds = $("#groupIds").val();
		if(groupId==vId){
			groupId = parentId;
		}
		
		$('#EditButton').click(function() {
			$("#view").hide();
			$("#edit").show();	
			$(".firstFocus").focus();
			return false;
		});	
		$('#Back').click(function() {
			$("#edit").hide();
			$("#view").show();
			TMS.backReset($('#edit-form'));
			return false;	
		});	
		$("#edit-form").validate({
	    	
            submitHandler: function(form) {
                TMS.getData({
                    url: _WEB_SITE_+'/group/service/edit',
                    type: 'post',
                    data: $(form).serialize(),
                    followTo:window.location.href
                });
                return false;
            }
        });
		
		$('#confirm_group_delete').click(function(){
			TMS.getData({
                url: _WEB_SITE_+'/group/service/delete',
                type: 'post',
                data: {
                	id:vId
                },
                followTo:_WEB_SITE_+"/group/list/"+groupId
            });
            return false;
        });
		$('#confirm_group_sync').click(function(){
			TMS.getData({
                url: _WEB_SITE_+'/group/service/syncProfileToTerminals',
                type: 'post',
                data: {
                	id:vId
                },
                followTo:_WEB_SITE_+"/group/list/"+groupId
            });
            return false;
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
});



