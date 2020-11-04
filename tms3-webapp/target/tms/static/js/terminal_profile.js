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
	var tsns = $("#tsn").val();
	
	$(function() {
		TMS.init();
		TMS.setAreatimeZone('#countryId','#provinceId', '#timeZoneId');
		TMS.groupPathSeeMore('.group-path-box');
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
		
		var countryDefault = $('#countryId').val();
 		var provinceDefault = $('#viewProvinceName').text();
 		var provinceId = $('#province_id').attr('data-province_id');
 		if(countryDefault){
 			TMS.setProvince(countryDefault,provinceDefault,provinceId);
 		} 
		
		var groupId = $("#gid").val();
		var tsns = $("#tsn").val();
		$('body').on('click', '#EditButton', function(){
			$("#view").hide();
			$("#edit").show();
			$('.firstFocus').focus();
			return false;
		});
		$('#Back').click(function() {
			$("#edit").hide();
			$("#view").show();
			TMS.backReset($('#edit-form'));
			return false;
		});
		$("#edit-form").validate({
			submitHandler : function(form) {
				TMS.getData({
					url : _WEB_SITE_ + '/terminal/service/edit',
					type : 'post',
					data : $(form).serialize(),
					followTo : _WEB_SITE_ + "/terminal/profile/" + groupId + "/" + tsns
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

		/* modal operate */
		/* remove terminal */
		$('#confirm_terminal_remove').click(function() {
			TMS.getData({
				url : _WEB_SITE_ + '/terminal/service/dismiss',
				type : 'get',
				dataType : 'json',
				data : {
					groupId : groupId,
					tsns : tsns
				},
				followTo : _WEB_SITE_ + "/terminal/list/" + groupId
			});
		});
		
		var treeDepth = $('#treeDepth').val();
		var deletePer = $('#permission-delete').val();
		var removePer = $('#permission-remove').val();
		var editPer = $('#permission-edit').val();
		var removeBtn='', deleteBtn='', editBtn='', btnList=[];
		//control edit btn hide or show
		if(editPer == '1'){
			editBtn = '<button type="button" class="btn btn-primary view-button-style" id="EditButton">Edit</button>';
			btnList.push(editBtn);
		}
		//control delete btn hide or show
		if(deletePer == '1'){
			deleteBtn = '<button type="button" class="btn btn-delete view-button-style" id="" data-toggle="modal" data-target="#modal_terminal_delete">Delete</button>';
			btnList.push(deleteBtn);
		} 
		//control remove btn hide or show
		if((treeDepth != '1'&& treeDepth !=0) && removePer == '1'){
			removeBtn = '<button type="button" class="btn btn-danger view-button-style" id="" data-toggle="modal" data-target="#modal_terminal_remove">Remove</button>';
			btnList.push(removeBtn);
		}
		
		if(btnList.length>0) {
			$('#terminal-view-btn-list').addClass('view-button');
			$('#terminal-view-btn-list').html(btnList.join(''));
		}
		
		$('#modal_terminal_delete .J-confirm-btn').click(function() {
			TMS.removeAlertdanger($('.bootstrap-growl .alert'));
			TMS.getData({
				url: _WEB_SITE_ + '/terminal/service/delete',
				type: 'post',
				dataType: 'json',
				data: {
					tsns: tsns,
					groupId: groupId
				},
				followTo: _WEB_SITE_ + "/terminal/list/" + groupId
			}, function(data) {
				if (data.statusCode == 200 || data.statusCode == undefined) {
					TMS.alert(data.message, 'success');
					$('#delete-terminal-modal').modal('hide');
				} else {
					TMS.alert(data.message, 'danger');
				}
			});
		});
		
		if(terminalInstalls==''){
			var tr='<tr class="no-records-found"><td colspan="4"><div class="alert alert-info paxinfonext"><strong>Hint:</strong>(NOT REPORTED)</div></td></tr>'
			// $('.terminal-profile').remove()
			$('.terminal-detail-indetall-tbody').append($(tr));			

        }else{
            var terminalInstallsArr=JSON.parse(terminalInstalls);
            var $div=$('<div></div>');
            terminalInstallsArr.map(function(item){
            	var tr='<tr><td>'+item.pkgName+'</td><td>'+item.pkgVersion+'</td> <td>'+item.pkgType+'</td></tr>';
                $div.append($(tr));
			});
			$('.terminal-detail-indetall-tbody').append($div.html());
		}
        $('.terminal-profile').removeClass('hide')


		

	});
});
