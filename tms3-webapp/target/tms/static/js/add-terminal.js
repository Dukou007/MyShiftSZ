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
        TMS.bindEvent();
        TMS.init();
        TMS.setAreatimeZone('#countryId','#provinceId', '#timeZoneId');
        var groupId = $("#gid").val();
        $('.global-input1 input').blur(function() {
            $('.global-input2 input').val($(this).val());
        });
        $("#addTerminal").validate({

            submitHandler: function(form) {
                /*check SN/TID*/
                var start = $(form).find('.global-input1 input').val();
                var end = $(form).find('.global-input2 input').val();
                if (end.length != 0) {
                    if (start.length != end.length) {
                        $(form).find('.global-input2').removeClass('has-success').addClass('has-error');
                        $(form).find('.global-input2 .help-block').remove();
                        $(form).find('.global-input2').append('<span class="help-block">The latter shall be the same length of the former.</span>');
                        return false;
                    }
                    if (start > end) {
                        $(form).find('.global-input1').removeClass('has-success').addClass('has-error');
                        $(form).find('.global-input1 .help-block').remove();
                        $(form).find('.global-input1').append('<span class="help-block">The latter shall not be less than the former.</span>');
                        return false;
                    } else {
                        $(form).find('.global-input1 .help-block').remove();
                        $(form).find('.global-input1').removeClass('has-error').addClass('has-success');
                    }
                }


                TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                TMS.getData({
                    url: _WEB_SITE_ + '/terminal/service/add',
                    type: 'post',
                    data: $(form).serialize(),
                   /* followTo: _WEB_SITE_ + "/terminal/list/" + groupId*/
                }, function(data) {
                	//success
                    if (data.statusCode == 200 || data.statusCode == undefined) {
                    	
                    	var ignortsns = data.ignoreTsns;
                    	//其他组已经存在正在添加的组
                    	if(ignortsns){
                    		var str = '';
                    		var terminalList = '';
                    		
                    		terminalList = TMS.convertArrayDescribe(ignortsns);
                    		if(ignortsns.length <=3) {
                    			str = 'Terminal(s) '+terminalList+' exist(s) in other group(s), and the details will not be overwritten. Do you still want to add?';
                    		} else {
                    			str = 'The following '+ignortsns.length +' terminals including '+terminalList+' ect exist in other group(s),  and the details will not be overwritten. Do you still want to add?'
                    		}
                    		 $('#add-terminal-cover-modal .modal-body').html(str);	
                    		 $('#add-terminal-cover-modal').modal('show');
                    		 
                    		//确定覆盖已存在的terminal(s)
                    		 $('#add-terminal-cover-modal .J-confirm-btn').unbind().click(function(){
                    			 TMS.getData({
                                     url: _WEB_SITE_ + '/terminal/service/add',
                                     type: 'post',
                                     data: $(form).serialize()+'&override=1'
                                     //followTo: _WEB_SITE_ + "/terminal/list/" + groupId
                                 }, function(data) {
                                	
                                	 
                                	 if (data.statusCode == 200 || data.statusCode == undefined) {
                                		 $('#add-terminal-cover-modal').modal('hide');
                                			//其他组不存在
                                 		var str = '';
                                 		var ownByOtherParallerGroupTsnList = '';
                                 		var existTsnIgnoreToAssignGroupList = '';
                                 		
                                 		//权限外的terminal
                                 		var ownByOtherParallerGroupTsn = data.ownByOtherParallerGroupTsn;
                                 		//存在于当前组的terminal
                                 		var existTsnIgnoreToAssignGroup = data.existTsnIgnoreToAssignGroup;
                                 		//新添加的terminal
                                 		var newTsns = data.newTsns;
                                 		
                                 		var ownByOtherParallerGroupTsnList = TMS.convertArrayDescribe(ownByOtherParallerGroupTsn);
                             			var existTsnIgnoreToAssignGroupList = TMS.convertArrayDescribe(existTsnIgnoreToAssignGroup);
                             			
                                 		//1,存在于当前组的terminal与权限外的terminal
                                 		if(ownByOtherParallerGroupTsn.length>0&& existTsnIgnoreToAssignGroup.length>0){
                                 			if(ownByOtherParallerGroupTsn.length>3){
                                 				if(existTsnIgnoreToAssignGroup.length>3){
                                 					str ='Failed to add following '+ ownByOtherParallerGroupTsn.length +' terminals including ' + ownByOtherParallerGroupTsnList + ' etc which exist in other group(s) out of your authority,'+
                                         			 '<br/>and failed to add following '+ existTsnIgnoreToAssignGroup.length +' terminals including ' + existTsnIgnoreToAssignGroupList + ' etc which exist in this group.';
                                 				}else{
                                 					str ='Failed to add following '+ ownByOtherParallerGroupTsn.length +' terminals including ' + ownByOtherParallerGroupTsnList + ' etc which exist in other group(s) out of your authority,'+
                                         			'<br/>and failed to add terminal(s) including ' + existTsnIgnoreToAssignGroupList +' which exist(s) in this group.';
                                 				}
                                 			} else{
                                 				if(existTsnIgnoreToAssignGroup.length>3){
                                 					str ='Failed to add  terminal(s) including ' + ownByOtherParallerGroupTsnList + ' which exist(s) in other group(s) out of your authority,'+
                                         			 '<br/>and failed to add following ' + existTsnIgnoreToAssignGroup.length + ' terminals including' + existTsnIgnoreToAssignGroupList + ' etc which exist in this group.';
                                 				}else{
                                 					str ='Failed to add  terminal(s) including ' + ownByOtherParallerGroupTsnList + ' which exist(s) in other group(s) out of your authority,'+
                                         			'<br/>and failed to add terminal(s) including ' + existTsnIgnoreToAssignGroupList +' which exist(s) in this group.';
                                 				}
                                 			}
                                 			TMS.alert(str,'danger');
                                 			
                                 		}
                                 		//terminal都存在于权限外
                                 		if(ownByOtherParallerGroupTsn.length >0 && existTsnIgnoreToAssignGroup.length==0){
	                                 			if(ownByOtherParallerGroupTsn.length>3){
	                                 				str = 'Failed to add following '+ownByOtherParallerGroupTsn.length+' terminals including  ' + ownByOtherParallerGroupTsnList + '  etc which exist in other group(s) out of your authority';
	                                 			} else{
	                                 				str = 'Failed to add terminal(s) including  ' + ownByOtherParallerGroupTsnList + '  which exist(s) in other group(s) out of your authority';
	                                 			}
                                 			TMS.alert(str,'danger');
                                 		}
                                 		//都是权限内的组
                                 		if(ownByOtherParallerGroupTsn.length ==0 && existTsnIgnoreToAssignGroup.length>0){
	                                 			if(existTsnIgnoreToAssignGroup.length>3){
	                                 				str = 'failed to add following '+existTsnIgnoreToAssignGroup.length+ ' terminals including  ' +  existTsnIgnoreToAssignGroupList + '  etc which exist in this group.';
	                                 			} else{
	                                 				str = 'failed to add terminal(s) including  ' +  existTsnIgnoreToAssignGroupList + '  which exist(s) in this group.';
	                                 			}
                                 			TMS.alert(str,'danger');
                                 		}
                                 		if(ownByOtherParallerGroupTsn.length ==0 && existTsnIgnoreToAssignGroup.length==0){
                                 			TMS.alert('success','success');
                                 			setTimeout(function(){window.location.href = _WEB_SITE_ + "/terminal/list/" + groupId}, 2000);
                                 		}
                                 		 
                                 	}
                                	else{
                                		 TMS.alert(data.message,'danger');
                                	 }
                                 }); 
                    		 });
                    	}else{
                    		//当前组存在正在添加的terminal(s)
                    		var str = '';
                    		var ownByOtherParallerGroupTsnList = '';
                    		var existTsnIgnoreToAssignGroupList = '';
                    		
                    		//权限外的terminal
                    		var ownByOtherParallerGroupTsn = data.ownByOtherParallerGroupTsn;
                    		//存在于当前组的terminal
                    		var existTsnIgnoreToAssignGroup = data.existTsnIgnoreToAssignGroup;
                    		//新添加的terminal
                     		var newTsns = data.newTsns;
                    		
                    		var ownByOtherParallerGroupTsnList = TMS.convertArrayDescribe(ownByOtherParallerGroupTsn);
                			var existTsnIgnoreToAssignGroupList = TMS.convertArrayDescribe(existTsnIgnoreToAssignGroup);
                    		//1,存在于当前组的terminal与权限外的terminal
                    		if(ownByOtherParallerGroupTsn.length>0&& existTsnIgnoreToAssignGroup.length>0){
                    			if(ownByOtherParallerGroupTsn.length>3){
                    				if(existTsnIgnoreToAssignGroup.length>3){
                    					str ='Failed to add following  '+ ownByOtherParallerGroupTsn.length +'  terminals including ' + ownByOtherParallerGroupTsnList + ' etc which exist in other group(s) out of your authority,'+
                            			 '<br/>and failed to add following  '+ existTsnIgnoreToAssignGroup.length +'  terminals including ' + existTsnIgnoreToAssignGroupList + ' etc which exist in this group.';
                    				}else{
                    					str ='Failed to add following  '+ ownByOtherParallerGroupTsn.length +'  terminals including ' + ownByOtherParallerGroupTsnList + ' etc which exist in other group(s) out of your authority,'+
                            			'<br/>and failed to add  terminal(s) including ' + existTsnIgnoreToAssignGroupList +' which exist(s) in this group.';
                    				}
                    			} else{
                    				if(existTsnIgnoreToAssignGroup.length>3){
                    					str ='Failed to add  terminal(s) including ' + ownByOtherParallerGroupTsnList + ' which exist(s) in other group(s) out of your authority,'+
                            			 '<br/>and failed to add following  ' + existTsnIgnoreToAssignGroup.length + '  terminals including ' + existTsnIgnoreToAssignGroupList + ' etc which exist in this group.';
                    				}else{
                    					str ='Failed to add  terminal(s) including ' + ownByOtherParallerGroupTsnList + ' which exist(s) in other group(s) out of your authority,'+
                            			'<br/>and failed to add  terminal(s) including ' + existTsnIgnoreToAssignGroupList +' which exist(s) in this group.';
                    				}
                    			}
                    			TMS.alert(str,'danger');
                    			
                    		}
                    		//terminal都存在于权限外
                    		if(ownByOtherParallerGroupTsn.length >0 && existTsnIgnoreToAssignGroup.length==0){
                    			if(newTsns.length>0){
                         			if(ownByOtherParallerGroupTsn.length>3){
                         				str = 'Failed to add following '+ownByOtherParallerGroupTsn.length +' terminals including  ' + ownByOtherParallerGroupTsnList + '  etc which exist in other group(s) out of your authority';
                         			} else{
                         				str = 'Failed to add terminal(s) including  ' + ownByOtherParallerGroupTsnList + '  which exist(s) in other group(s) out of your authority';
                         			}
                     			}else{
                     				str = 'Failed! Terminal(s) exist(s) in other group(s) out of your authority.';
                     			}
                    			TMS.alert(str,'danger');
                    		}
                    		//都是权限内的组
                    		if(ownByOtherParallerGroupTsn.length ==0 && existTsnIgnoreToAssignGroup.length>0){
                    			if(newTsns.length>0){
                         			if(existTsnIgnoreToAssignGroup.length>3){
                         				str = 'failed to add following '+existTsnIgnoreToAssignGroup.length +' terminals including  ' +  existTsnIgnoreToAssignGroupList + '  etc which exist in this group.';
                         			} else{
                         				str = 'failed to add terminal(s) including  ' +  existTsnIgnoreToAssignGroupList + '  which exist(s) in this group.';
                         			}
                     			}else{
                     				str = 'Failed! Terminal(s) exist(s) in this group.';
                     			}
                    			TMS.alert(str,'danger');
                    		}
                    		if(ownByOtherParallerGroupTsn.length ==0 && existTsnIgnoreToAssignGroup.length==0){
                    			TMS.alert('success','success');
                    			setTimeout(function(){window.location.href = _WEB_SITE_ + "/terminal/list/" + groupId}, 2000);
                    		}
                    		
                    		 
                    	}
                    } else {
                    	//error
                        TMS.alert(data.message, 'danger');
                    }
                });
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