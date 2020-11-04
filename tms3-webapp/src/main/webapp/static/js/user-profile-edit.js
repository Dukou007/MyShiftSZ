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
		TMS.setUserArea('#countryId', '#provinceId');
		var countryDefault = $('#countryId').val();
		var provinceDefault = $('#viewProvinceName').text();
		var provinceId = $('#province_id').attr('data-province_id');
		if(countryDefault){
 			TMS.setUserProvince(countryDefault,provinceDefault,provinceId);
 		} 
		
		
		/* ldap y or n */
		if (IS_LDAP) {
			$('#userProfileEdit input').attr('readonly', 'true');
			$('#ldapPhone').removeClass('required');
			$('#ldapEmail').removeClass('required');
			$('#cityId').removeClass('citycheck');
			$('#zipcode').removeAttr('maxlength');
		
		};
		$('#EditButton').click(function() {
			$("#view").hide();
			$("#edit").show();
			$('.firstFocus').focus();
			return false;
		});
		$('#Back').click(function() {

			$("#edit").hide();
			$("#view").show();
			TMS.backReset($('#userProfileEdit'));
			return false;
		});

		/* show group path */
		$('.lispan').each(
			function(index, el) {
				var groupPath = $(el).text().split('/'),
					str = [];
				for (var i = 0; i < groupPath.length; i++) {
					if (i == groupPath.length - 1) {
						str.push('<span class="font14px black half-bold">' + groupPath[i] + '</span>');
					} else {
						str.push('<span class="font14px grey">' + groupPath[i] + ' > </span>');
					}

				}
				$(el).html(str.join(''));

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
		//profile zip format
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
		
		/* choose favorite */
		$('.user-profile-group-items input[type=radio]').change(
			function() {
				$(this).next().children('label span').removeClass(
					'glyphicon glyphicon-heart-empty').addClass(
					'glyphicon glyphicon-heart red');

				$(this).parent().siblings().children('label').children()
					.removeClass('glyphicon glyphicon-heart red').addClass(
						'glyphicon glyphicon-heart-empty');
			});
		

		$("#userProfileEdit").validate({
			debug: true,
			submitHandler: function(form) {
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				TMS.getData({
					url: _WEB_SITE_ + '/myProfile/service/edit',
					type: 'post',
					data: $("#userProfileEdit").serialize(),
				    followTo: _WEB_SITE_ + '/myProfile/view/' + GROUP_ID

				});
				return false;
			}
		});
	});
})