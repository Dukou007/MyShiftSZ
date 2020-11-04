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
    var selectGroup = {
        init: function() {
            this.creatTree();
            this.bindEvent();
        },
        creatTree: function() {
            var self = this;
            this.Tree = new TMS.groupTree('#add-tree', {
                isModal: true,
                nowGroup: false,
                multiselect: true,
                filterChildGroup: true,
                allowSelectOtherEGroup: false,
                groupPanel: { //group 选择框
                    display: true,
                    titleShow: false
                }
            });
        },
        bindEvent: function() {
            var self = this;
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
       
        }
    }
    $(function() {
        TMS.init();
        selectGroup.init();
        TMS.setUserArea('#countryId', '#provinceId');
        $(".group_chooseG").on("click",'.group_note_delete',function(){
        	groupCheck()
        })
        $(".group-confirm-btn").on("click",function(){
        	groupCheck()
        })
        function groupCheck(){
        	if($("#group-error").length){
        		setTimeout(function(){
            		var groupCount = $('.group_note .group_note_items').length;
            		if(groupCount==0){
            			$("#group-error").show()
            			$(".group_chooseG").css({"border":"1px solid #a94442"})
            		}else{
            			$("#group-error").hide()
            			$(".group_chooseG").css({"border":"1px solid #d3e0ea"})
            		}
            	},20)
        	}
        }
        $('.role-privilege-checkbox input[type="checkbox"]').change(function(){
        	if($("#role-error").length){
        		var roleCount = $('.role-privilege-checkbox input[type="checkbox"]:checked').length;
        		roleCount == 0 ? $("#role-error").show() : $("#role-error").hide();
        	}
        })
        $("#um_addUser").on("submit",function(){
        	var roleCount = $('.role-privilege-checkbox input[type="checkbox"]:checked').length;
        	var groupCount = $('.group_note .group_note_items').length;
        	if(groupCount==0){
        		$(".group_chooseG").css({"border":"1px solid #a94442"})
        		$("#group-error").length?$("#group-error").show():$(".group_chooseG").after('<span id="group-error" style="display: block;margin-top: 2px;margin-bottom: 2px;color: #a94442;font-size: 13px;" class="error-block">This filed is required.</span>');	
        	}
        	if(roleCount == 0){
        		$("#role-error").length?$("#role-error").show():$(".role-box").after('<span id="role-error" style="display: block;margin-top: 2px;margin-bottom: 2px;color: #a94442;font-size: 13px;" class="error-block">This filed is required.</span>');	
        	}
        })
        $("#um_addUser").validate({
            submitHandler: function(form) {
            	TMS.removeAlertdanger($('.bootstrap-growl .alert'));
            	var roleCount = $('.role-privilege-checkbox input[type="checkbox"]:checked').length;
            	var groupCount = $('.group_note .group_note_items').length;
            	if (roleCount == 0 || groupCount == 0) {
                    return false;
                }
            	
                TMS.getData({
                    url: _WEB_SITE_ + '/user/service/addUser',
                    type: 'post',
                    data: $(um_addUser).serialize(),
                    followTo: _WEB_SITE_ + "/user/list/" + GROUP_ID
                });
                return false;
            }
        });



    });
})