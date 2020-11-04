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

            this.bindEvent();
            this.selectGroup = [];
            this.getGroup();
            this.creatTree();
        },

        getGroup: function() {
            var self = this;
            $('.group_note_items').each(function(index, el) {
                var o = {};
                o.id = $(el).attr('data-id');
                o.path = $(el).attr('title');
                o.name = $(el).attr('data-name');
                o.idPath = $(el).attr('data-idpath');
                self.selectGroup.push(o);
            })
        },
        creatTree: function() {
            var self = this;
            this.Tree = new TMS.groupTree('#add-tree', {
                isModal: true,
                nowGroup: false,
                multiselect: true,
                filterChildGroup: true,
                allowSelectOtherEGroup: false,
                activeGroup: { //group 当前选择高亮
                    items: self.selectGroup
                },
                groupPanel: { //group 选择框
                    display: true,
                    titleShow: false
                }
            });
        },
        ajaxBack: function(data, modal) { // 请求回调
            var self = this;
            TMS.removeAlertdanger($('.bootstrap-growl .alert'));
            if (data.statusCode != undefined && data.statusCode != '200') {
                TMS.alert(data.message, 'danger');
            } else {
                TMS.alert(data.message, 'success');
                /*self.$table.bootstrapTable('refresh');*/
                // $('.select-box .btn').addClass('disabled');
                $(modal).modal('hide');
            }

        },
        bindEvent: function() {
            var self = this;
           
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
     			TMS.setUserProvince(countryDefault,provinceDefault,provinceId);
     		} 
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
            $('#EditButton').click(function() {
                $("#view").hide();
    			$(".view").hide();
                
                $("#edit").show();
                $('.firstFocus').focus();
                return false;
            });
            $('#Back').click(function() {
                $("#edit").hide();
                $("#view").show();
    			$(".view").show();                
                //TMS.backReset($('#userProfileEdit'));
                TMS.backReset($('#userProfileEdit'));
                return false;
            });
            $('#GetAppKey').click(function() {
                var username=$('.user-profile input[name="username"]').val();
                TMS.getData({
                    url: _WEB_SITE_+'/client/get-app-key?userName='+username,
                    type: 'get',
                    dataType:'text'
                },
                function(data) {
                    if(data.indexOf('statusCode')!=-1){
                        var obj=JSON.parse(data);
                        if(typeof obj!='object'){
                            obj=JSON.parse(obj);                            
                        }
                        TMS.alert(obj.message, 'danger');                        
                    }else{
                        $('.view .btn-primary').removeClass('hide');
                        $('#GetAppKey').css('opacity','0.7');
                        $('.app-key .view-value').html(data);
                        $('.app-key,#RemoveAppKeyBtn,#RefreshAppKeyBtn').removeClass('hide');
                    }
                    
                });
                return false;
            });
            $('#RefreshAppKey').click(function() {
                console.log('inrefresh')
                var username=$('.user-profile input[name="username"]').val();
                TMS.getData({
                    url: _WEB_SITE_+'/client/refresh-app-key',
                    type: 'post',
                    dataType:'text',                    
                    data:{userName:username} 
                },
                function(data) { 
                    if(data.indexOf('statusCode')!=-1){
                        var obj=JSON.parse(data);
                        if(typeof obj!='object'){
                            obj=JSON.parse(obj);                            
                        }
                        TMS.alert(obj.message, 'danger');      
                    }else{
                        $('.app-key .view-value').html(data);       
                    }
                    $('#modal_refresh_key').modal('hide')             
                });
                return false;
            });
            $('#RemoveAppKey').click(function() {
                var username=$('.user-profile input[name="username"]').val();
                TMS.getData({
                    url: _WEB_SITE_+'/client/remove-app-key',
                    type: 'post',
                    dataType:'text',                    
                    data:{userName:username}
                },
                function(data) {
                    if(data.indexOf('statusCode')!=-1){
                        var obj=JSON.parse(data);
                        if(typeof obj!='object'){
                            obj=JSON.parse(obj);
                        }
                        TMS.alert(obj.message, 'danger');      
                    }else{           
                        $('#GetAppKey').css('opacity','1');                        
                        $('.app-key .view-value').html('');
                        $('.app-key,#RemoveAppKeyBtn,#RefreshAppKeyBtn').addClass('hide');      
                    }
                    $('#modal_remove_key').modal('hide')                                          
                    
                });
                return false;
            });
            /*ldap y or not*/
            if (IS_LDAP) {
                var part1 = $('.ldap-part1').html();
                $('.ldap-part1').children().hide();
                var part2 = $('.ldap-part2').html();
                $('.ldap-part2').children().hide();
                $('.ldap-part1').html(part2);
                $('.ldap-part2').html(part1);
                $('.ldap-part2').hide();
                $('#edit').removeAttr('class').attr('class', 'col-sm-12 col-md-6');
                $('#ldap-part3').removeAttr('class').attr('class', 'col-sm-12 col-md-12');
                $('#userProfileEdit input').attr('readonly', 'true');

            }
            /* delete user */
            $('#confirm_user_delete').click(function() {
                TMS.getData({
                    url: _WEB_SITE_ + '/user/service/delete/' + USER_ID +'/'+GROUP_ID,
                    type: 'get',
                    followTo: _WEB_SITE_ + '/user/list/' + GROUP_ID
                });
            });
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
            $("#userProfileEdit").on("submit",function(){
            	var roleCount = $('.role-privilege-checkbox input[type="checkbox"]:checked').length;
            	var groupCount = $('.group_note .group_note_items').length;
            	if(groupCount==0){
            		$(".group_chooseG").css({"border":"1px solid #a94442"})
            		$("#group-error").length?$("#group-error").show():$(".group_chooseG").after('<span id="group-error" style="display: block;margin-top: 2px;margin-bottom: 2px;color: #a94442;font-size: 13px;" class="error-block">This field is required.</span>');	
            	}
            	if(roleCount == 0){
            		$("#role-error").length?$("#role-error").show():$(".role-box").after('<span id="role-error" style="display: block;margin-top: 2px;margin-bottom: 2px;color: #a94442;font-size: 13px;" class="error-block">This field is required.</span>');	
            	}
            })
            $("#userProfileEdit").validate({
                submitHandler: function(form) {
                	TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                	var roleCount = $('.role-privilege-checkbox input[type="checkbox"]:checked').length;
                	var groupCount = $('.group_note .group_note_items').length;
                	var isSiteAdmin = $('.form-edit-group .edit-value .siteAdmin').length;

                    if (!isSiteAdmin && (roleCount == 0 || groupCount == 0)) {
                        return false;
                    }
                 
                    TMS.getData({
                        url: _WEB_SITE_ + '/user/service/edit' ,
                        type: 'post',
                        data: $("#userProfileEdit").serialize(),
                        followTo: _WEB_SITE_ + '/user/view/' + USER_ID + '/' + GROUP_ID

                    });
                    return false;
                }
            });


        }
    }
    $(function() {
        TMS.init();
        TMS.setUserArea('#countryId', '#provinceId');
        selectGroup.init();
        if(AppKey&&AppKey!=''){
            $('#GetAppKey').css('opacity','0.7');            
			$('.view .btn-primary').removeClass('hide');
            $('.app-key .view-value').html(AppKey);
            $('.app-key,#RemoveAppKeyBtn,#RefreshAppKeyBtn').removeClass('hide');
		}
    });
})