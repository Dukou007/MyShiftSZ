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
    var groupId = $("#gid").val();
    var offlinekeyid = $('#keyid').val();
    $(function() {
        TMS.init();
        TMS.bindEvent();
        TMS.groupPathSeeMore('.group-path-box');
        TMS.groupPathSeeMore('.group-path-box1');
        TMS.groupPathSeeMore('.group-path-box2');
        $('#modal_key_remove .J-confirm-btn').click(function() {
        	TMS.removeAlertdanger($('.bootstrap-growl .alert'));
            TMS.getData({
                url: _WEB_SITE_ + '/offlinekey/service/dismiss',
                data: {
                	offlineKeyId: offlinekeyid,
                    groupId: groupId
                },
                type: 'get',
                followTo: _WEB_SITE_ + "/offlinekey/list/" + groupId
            }, function(data) {
                if (data.statusCode == 200 || data.statusCode == undefined) {
                    TMS.alert(data.message, 'success');
                } else {
                    TMS.alert(data.message, 'danger');
                }
            });
        });
       
        $('body').on('click','#EditButton', function(){
        	 $(".J-view").hide();
             $(".J-edit").show();
             return false;
        });
        $('#Back').click(function() {
            $(".J-edit").hide();
            $(".J-view").show();
            TMS.backReset($("#key_edit-form"));
            return false;
        });


        $("#key_edit-form").validate({
            submitHandler: function(form) {
                var keyId = $("#keyId").val();
                TMS.getData({
                    url: _WEB_SITE_ + '/offlinekey/service/edit/' + keyId,
                    type: 'post',
                    data: $(form).serialize(),
                    followTo: _WEB_SITE_ + "/offlinekey/profileView/" + groupId + "/" + keyId
                });
                return false;
            }
        });
        //group permission
        var treeDepth = $('#treeDepth').val();
        //role permission
		var removePer = $('#permission-remove').val();
		var deletePer = $('#permission-delete').val();
		var editPer = $('#permission-edit').val();
		var removeBtn='', deleteBtn='', editBtn='', btnList=[];
		//control edit btn hide or show
		if(editPer == '1'){
			editBtn = '<button type="button" class="btn btn-primary view-button-style" id="EditButton">Edit</button>';
			btnList.push(editBtn);
		}
		//control delete btn hide or show
		if(deletePer == '1'){
			deleteBtn = '<button type="button" class="btn btn-delete view-button-style" id="" data-toggle="modal" data-target="#modal_key_delete">Delete</button>';
			btnList.push(deleteBtn);
		} 
		//control remove btn hide or show
		if((treeDepth != '1'&& treeDepth !=0) && removePer == '1'){
			removeBtn = '<button type="button" class="btn btn-danger view-button-style" data-toggle="modal" data-target="#modal_key_remove">Remove</button>';
			btnList.push(removeBtn);
		}
		
		if(btnList.length>0) {
			$('#key-view-btn-list').addClass('view-button');
			$('#key-view-btn-list').html(btnList.join(''));
		}
		
	
		$('#modal_key_delete .J-confirm-btn').click(function() {
			TMS.removeAlertdanger($('.bootstrap-growl .alert'));
			TMS.getData({
				url: _WEB_SITE_ + '/offlinekey/service/delete',
				type: 'post',
				dataType: 'json',
				data: {
					 offlineKeyId: offlinekeyid,
                     groupId: groupId
				},
				followTo: _WEB_SITE_ + "/offlinekey/list/" + groupId
			}, function(data) {
				if (data.statusCode == 200 || data.statusCode == undefined) {
					TMS.alert(data.message, 'success');
					$('#delete-package-modal').modal('hide');
				} else {
					TMS.alert(data.message, 'danger');
				}
			});
		});
  

    });
});