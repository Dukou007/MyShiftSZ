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
    var laydate = require('date-picker');
    require('fileupload');
    var deployGroupId = $("#deployGroupId").val();
    var flag = true;
    var nowConfirmData=null;
    var addDepoly = {
        init: function() {
            this.bindEvent();
            this.initDeployPackage();
        },
        initDeployPackage: function() {
            this.bindEventDeployPackage();
        },
        ajaxParams: function(obj, callback) {
            var self = this,
                url = _WEB_SITE_ + '/pkgSchema/service/getSchemaByPkgSchemaId/' + obj.pkgSchemaId;
            if (obj.paramSet != null) {
                url = _WEB_SITE_ + '/groupDeploy/service/getSchemaByPkgSchemaIdAndParamSet/' + obj.pkgSchemaId + '/' + obj.paramSet;
            }
            TMS.getData({
                    url: url,
                    type: 'get'
                },
                function(data) {
                    if (data.statusCode == 200 || data.statusCode == undefined) {
                        self.renderParams(obj.el, JSON.parse(data.Group));
                        $('.parameters-box').removeClass('hide');
                        callback && callback();

                    } else {
                        TMS.alert(data.message, 'danger');
                    }

                });
        },
        initTimeSelect: function(el) {
            laydate({
                elem: el,
                format: 'YYYY-MM-DD hh:mm:ss',
                istime: true,
              /*  min: laydate.now(),*/
                choose: function(d, e) {
                	$(e).closest('.form-edit-group').removeClass('has-error').removeClass('has-success');
                    $(e).closest('.form-edit-group').find('.help-block').remove();
                    if($(e).attr('name') == 'dwnlStartTime'){
                		var downloadTime = $('input[name="dwnlStartTime"]').val();
                		var activeTime = $('input[name="actvStartTime"]').val();
                		if(downloadTime!=""&&activeTime!=""){
                			downloadTime = new Date(downloadTime.replace(/\-/g, "\/"));
                			activeTime = new Date(activeTime.replace(/\-/g, "\/"));
                		
                			if(downloadTime >= activeTime){
                				{  
                					$('input[name="actvStartTime"]').next('.error-block').remove();
                        			$('input[name="actvStartTime"]').after('<span id="group-error" style="display: block;margin-top: 2px;margin-bottom: 2px;color: #a94442;font-size: 13px;" class="error-block">Activation date/time should be later than the download date/time.</span>');
                        			$('input[name="actvStartTime"]').closest('.form-edit-group').addClass('has-error').removeClass('has-success');
                        			flag = false;
                        		 }
                			} else {
                				{
                        			$('input[name="actvStartTime"]').closest('.form-edit-group').removeClass('has-error').removeClass('has-success');
                        			$('input[name="actvStartTime"]').next('.error-block').remove();
                        			flag = true;
                        		}
                			}
                		} 
                		 
                	}
                	if($(e).attr('name') == 'actvStartTime'){
                		var downloadTime = $('input[name="dwnlStartTime"]').val();
                		var activeTime = $('input[name="actvStartTime"]').val();
                		downloadTime = new Date(downloadTime.replace(/\-/g, "\/"));
                		activeTime = new Date(activeTime.replace(/\-/g, "\/"));
                		if(downloadTime!=""&&activeTime!=""&&downloadTime >= activeTime)  
                		 {  
                			$(e).next('.error-block').remove();
                			$(e).after('<span id="group-error" style="display: block;margin-top: 2px;margin-bottom: 2px;color: #a94442;font-size: 13px;" class="error-block">Activation date/time should be later than the download date/time.</span>');
                    		$(e).closest('.form-edit-group').addClass('has-error').removeClass('has-success');
                    		flag = false;
                		 }
                		else{
                			$(e).closest('.form-edit-group').removeClass('has-error').removeClass('has-success');
                            $(e).next('.error-block').remove();
                            flag = true;
                		}
                		
                	}
                  
                }
            });
        },
        renderParams: function(el, data) {
            if (!data) {
                return false
            }

            temp = [],
                tempContent = [];
            for (var i = 0; i < data.length; i++) {
                if (i == 0) {
                    temp.push('<li role="presentation" class="active">');
                    tempContent.push('<div role="tabpanel" class="tab-pane clearfix fade in active" id="' + data[i].ID + '">');
                    tempContent.push(data[i].Data);
                    tempContent.push('</div>');
                } else {
                    temp.push('<li role="presentation">');
                    tempContent.push('<div role="tabpanel" class="tab-pane clearfix fade in" id="' + data[i].ID + '">');
                    tempContent.push(data[i].Data);
                    tempContent.push('</div>');
                }
                temp.push('<a href="#' + data[i].ID + '" role="tab" data-toggle="tab">' + data[i].Title + '</a>');
                temp.push('</li>');

            }
            $('.parameters-ul').html(temp.join(''));
            $(el + ' .parameters-box .tab-content').html(tempContent.join(''));
            $(el + ' .parameters-box .tab-content .tab-pane').each(function(index, el) {
                if ($(el).find('.parameters-panel').length == 0) {
                    $(el).html('<div class="parameters-panel" style="padding-top:20px;"><div class="parameters-body"><div class="row">' + $(el).html() + '</div></div></div>')

                }
            });
            this.changeValue(el);
            this.fileUpLoad(el);
        },
        changeValue: function(el) {
            $(el).find('.regex_Amount').each(function(index, el) {
                var val = $(el).val();
                if (val) {
                    val = val / 100;
                    val = val.toFixed(2);
                    $(el).val(val);
                }
            });
            $(el).find('.regex_Date').each(function(index, el) {
                var val = $(el).val(),
                    y, m, d;
                if (val) {
                    y = val.substring(0, 4);
                    m = val.substring(4, 6);
                    d = val.substring(6, 8);
                    $(el).val(m + '/' + d + '/' + y);
                }
            });
            $(el).find('.regex_DateTime').each(function(index, el) {
                var val = $(el).val(),
                    Y, M, D, h, m, s;
                if (val) {
                    Y = val.substring(0, 4);
                    M = val.substring(4, 6);
                    D = val.substring(6, 8);
                    h = val.substring(8, 10);
                    m = val.substring(10, 12);
                    s = val.substring(12, 14);
                    $(el).val(M + '/' + D + '/' + Y + ' ' + h + ':' + m + ':' + s);
                }
            });
            $(el).find('.regex_Time').each(function(index, el) {
                var val = $(el).val(),
                    h, m, s;
                if (val) {
                    h = val.substring(0, 2);
                    m = val.substring(2, 4);
                    s = val.substring(4, 6);
                    $(el).val(h + ':' + m + ':' + s);
                }
            });
            $(el).find('.regex_Time_hhmm').each(function(index, el) {
                var val = $(el).val(),
                    h, m;
                if (val) {
                    h = val.substring(0, 2);
                    m = val.substring(2, 4);
                    $(el).val(h + ':' + m);
                }
            });
            $(el).find('.filename').each(function(index, el) {
                var val = $(el).val();
                if (val.indexOf('|') != -1) {
                    $(el).attr('data-oldfile', val);
                    val = val.split('|')[0];
                    $(el).val(val);
                }
            });
        },
        changeValueSubmit: function(el) {

            $(el).find('.regex_Amount').each(function(index, el) {
                var val = $(el).val();
                if (val) {
                    val = val * 100
                    $(el).val(val);
                }
            });
            $(el).find('.regex_Date').each(function(index, el) {
                var val = $(el).val();
                if (val) {
                    val = val.split('/');
                    $(el).val(val[2] + val[0] + val[1]);
                }
            });
            $(el).find('.regex_DateTime').each(function(index, el) {
                var val = $(el).val(),
                    f, s;
                if (val) {
                    val = val.split(' ');
                    f = val[0].split('/');
                    s = val[1].split(':');
                    $(el).val(f[2] + f[0] + f[1] + s[0] + s[1] + s[2]);
                }
            });
            $(el).find('.regex_Time').each(function(index, el) {
                var val = $(el).val();
                if (val) {
                    val = val.split(':');
                    $(el).val(val[0] + val[1] + val[2]);
                }
            });
            $(el).find('.regex_Time_hhmm').each(function(index, el) {
                var val = $(el).val();
                if (val) {
                    val = val.split(':');
                    $(el).val(val[0] + val[1]);
                }
            });
            $(el).find('.filename').each(function(index, el) {
                var filename = $(el).attr('data-file');
                if (filename) {
                    $(el).val($(el).val() + '|' + filename);
                } else {
                    if ($(el).attr('data-oldfile')) {
                        $(el).val($(el).attr('data-oldfile'));
                    }
                }
            });

        },
        fileUpLoad: function(el) {
            $(el).find('.fileupload').fileupload({
                dataType: 'json',
                url: _WEB_SITE_ + '/pkgSchema/service/upload',
                type: 'put',
                autoUpload: false,
                recalculateProgress: false,
                formData: function(form) {
                    return {};
                },
                add: function(e, data) {
                	TMS.removeAlertdanger($('.alert-danger-upload'));
                    $.each(data.files, function(index, file) {
                        var uploadErrors = [];
                        var targetName = $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.filename').val();
                        if (targetName != file.name) {
                            uploadErrors.push('The filename should be the same');
                        }

                        if (uploadErrors.length == 0) {
                            var ff = new FileReader(); //check the drag and drop is a folder(firefox)
                            ff.readAsArrayBuffer(file);
                            ff.onload = function() {

                                data.submit();

                            };
                            ff.onerror = function() {
                                TMS.alert('Package file is a folder', 'danger-upload');
                            };
                        } else {
                            TMS.alert(uploadErrors.join(''), 'danger');
                        }

                    });

                },
                done: function(e, data) { //上传完成

                    $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.filename').attr('data-file', data.result.filePath);
                    TMS.alert(data.result.message, 'success');
                },
                fail: function(e, data) { //上传失败
                    if (data.result && data.result.message) {
                        TMS.alert(data.result.message, 'danger-upload');
                    } else {
                        TMS.alert('Network instability', 'danger-upload');
                    }


                },
                progress: function(e, data) {},
                progressall: function(e, data) { //进度条
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.progress .progress-bar').css(
                        'width',
                        progress + '%'
                    );


                },
                start: function(e) { //开始时
                    $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.progress').removeClass('hide');
                },
                always: function(e, data) { //默认上传结束执行
                },
                stop: function(e) {
                    
                    setTimeout(function() {
                        $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.progress').addClass('hide');
                        $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.progress .progress-bar').css(
                            'width', '0%'
                        );
                    }, 500)

                }
            })
        },
        renderSelect: function(o) { //联动
            var $select = $(o.el);

            TMS.getData({
                url: o.url,
                type: 'post',
                data: o.data
            }, function(data) {
                var temp = [];
                temp.push('<option value="">--Please Select--</option>');
                for (var i = 0; i < data.length; i++) {
                    temp.push('<option value="' + data[i] + '">' + data[i] + '</option>');
                }
                $select.html(temp.join(''));
            });

        },
        renderSelect2: function(pkgName, pkgVersion, el) { //联动
            var $select = $(el),
                self = this,
                $closestGroup = $select.closest('.form-edit-group'),
                $icon = $select.parent().siblings('.edit-name').find('.icon-required');
            if (!pkgVersion) {
                $select.html('<option value="">--Please Select--</option>');
                self.removeParameters();
            } else {
                TMS.getData({
                    url: _WEB_SITE_ + '/pkgSchema/service/getPkgAndPkgSchemaList',
                    type: 'post',
                    data: {
                        pkgName: pkgName,
                        pkgVersion: pkgVersion
                    }
                }, function(data) {
                    var temp = [];
                    data = data[0];
                    temp.push('<option value="">--Please Select--</option>');
                    if (!data) {
                        return false
                    }
                    var type = data.pkgType;
                    $('#pkg-id').val(data.pkgId);
                    for (var i = 0; i < data.pkgSchemaInfoList.length; i++) {
                        temp.push('<option value="' + data.pkgSchemaInfoList[i].pkgSchemaId + '">' + data.pkgSchemaInfoList[i].pkgSchemaName + '</option>');
                    }
                    if (type == 'BroadPos') {
                        $select.addClass('required');
                        $closestGroup.removeClass('hide');
                        $icon.removeClass('hide');
                    } else {
                        $closestGroup.addClass('hide');
                        $icon.addClass('hide');
                        $select.removeClass('required');
                    }
                    $select.html(temp.join('')).attr('data-type', type);
                });
            }

        },
        disabled: function(el) {
            $(el).find('input,select,.filename').prop('disabled', true);
            $(el).find('.parameters-file-btn .btn').addClass('disabled');
        },
        removeParameters: function() {
            $('.parameters-box').addClass('hide');
            $('.parameters-box .tab-content').html('');
        },
        bindEvent: function() {
            var self = this;
            self.initTimeSelect(".form_datetime");
            $('.firstFocus').focus();
            $('.first-packagename').on('change', function(event) {
                var val = $(this).val();
                self.renderSelect({
                    'data': {
                        'name': val,
                        'groupId': deployGroupId
                    },
                    'el': '.first-packageversion',
                    'url': _WEB_SITE_ + '/pkg/service/getVersions'
                });
            });
            $('.first-packageversion').on('change', function(event) {
                var modal = '#' + $(this).parents('form').attr('id');
                var pkgName = $(".first-packagename").val();
                var pkgVersion = $(this).val();
                self.renderSelect2(pkgName, pkgVersion, '.first-templatename');
            });
            $('.first-templatename').on('change', function(event) {
                var val = $(this).val(),
                    type = $(this).attr('data-type');
                if (!val) {
                    self.removeParameters();
                    return false;
                }
                if (type == 'BroadPos') {
                    self.ajaxParams({
                        pkgSchemaId: val,
                        el: '#add-group-deploy'
                    });
                }

            });
            $('.g-container').on('click', '.parameters-ul li', function() {
                var index = $(this).parent().find('li').index(this);
                var target = $(this).find('a').attr('href');
                $('.parameters-ul li').removeClass('active');
                $('.parameters-ul.ulposition li').eq(index).addClass('active');
                $('.parameters-ul.ulrelative li').eq(index).addClass('active');
                $('.parameters-box .tab-content .tab-pane.active').removeClass('in').one('bsTransitionEnd', function() {
                    $(this).removeClass('active');
                    $(target).addClass('active').addClass('in');
                })
            });
            $(document).on('click', '#deploy-confirm-modal .btn-primary', function() {
                self.confirmAjaxSubmit();
            });
            $('#installNow').on('click', function() {
            	var ele = $('#add-group-deploy').find('input[name="actvStartTime"]')
            	if($('#installNow').prop('checked')) {
            		ele.prop('disabled', true)
            		ele.css({'background':'none', 'cursor':'not-allowed'});
				} else {
					ele.prop('disabled', false);
					ele.css({'background':'#fff', 'cursor':'pointer'});
				}
            });
        },
        bindEventDeployPackage: function() {
            var self = this;
            $("#add-group-deploy").validate({
                ignore: '',
                onfocusout: false,
                submitHandler: function(form) {
                    var type = $('.first-templatename').attr('data-type');
                   
                    if (type != 'BroadPos') {
                       /* var data = $(form).serialize();*/
                    	var data = getString();
                    } else {
                        self.changeValueSubmit('#add-group-deploy');
                       /* var data = $(form).serialize();*/
                        var data = getString();
                        self.changeValue('#add-group-deploy');
                    }
                    if(!flag){
                    	return false;
                    }
                    nowConfirmData=self.serializeObject(data);
                    self.confirmRenderData();
                    // $('#deploy-confirm-modal').modal('show');
                }
            });
            //转换表单数据
    		function getString() {
    			var o = {}, str='';
    				$target = $('#add-group-deploy');
    				o.groupId = $target.find('input[name="groupId"]').val();
    				o.pkgId = $target.find('input[name="pkgId"]').val();
    				o.groupName = $target.find('input[name="groupName"]').val();
    				o.destModel = $target.find('select[name="destModel"]').val();
    				o.packagename = $target.find('select[name="packagename"]').val();
    				o.packageversion = $target.find('select[name="packageversion"]').val();
    				o.dwnlStartTime = $target.find('input[name="dwnlStartTime"]').val();
    				o.timeZone = $target.find('select[name="timeZone"]').val();
    				o.actvStartTime = $target.find('input[name="actvStartTime"]').val();
    				o.dwnlEndTime = $target.find('input[name="dwnlEndTime"]').val();
    				if($('#installNow').prop('checked')) {
    					o.actvStartTime = 'Immediately';
    				}
    				if($target.find('input[name="deletedWhenDone"]:checked').val() == '1') {
    					o.deletedWhenDone = 1;
    				} else {
    					o.deletedWhenDone = 0;
    				}
    				str = 'groupId='+o.groupId+'&pkgId='+o.pkgId+'&groupName='+o.groupName+'&destModel='+o.destModel+'&packagename='+o.packagename+'&packageversion='+o.packageversion+'&dwnlStartTime='+o.dwnlStartTime+'&timeZone='+o.timeZone+'&actvStartTime='+o.actvStartTime+'&dwnlEndTime='+o.dwnlEndTime+'&deletedWhenDone='+o.deletedWhenDone;
    			return str;
    		}

        },
        confirmAjaxSubmit:function(){
            var data=nowConfirmData;
            TMS.getData({
                url: _WEB_SITE_ + '/groupDeploy/service/deploy',
                type: 'post',
                data: data,
                followTo: _WEB_SITE_ + "/groupDeploy/list/" + deployGroupId
            }, function(data) {
                if (data.statusCode == 200 || data.statusCode == undefined) {
                    nowConfirmData=null;
                    TMS.alert(data.message, 'success');
                    $('#deploy-confirm-modal').modal('hide');
                } else {
                    TMS.alert(data.message, 'danger');
                }
            });
        },
        confirmRenderData:function(){
            var data=nowConfirmData;
            console.log(data);
            $('#confirm-groupName').text(data.groupName)
            $('#confirm-destModel').text(data.destModel)
            $('#confirm-packagename').text(data.packagename)
            $('#confirm-packageversion').text(data.packageversion)
            $('#confirm-timeZone').text(data.timeZone)
            $('#confirm-dwnlStartTime').text(data.dwnlStartTime)
            $('#confirm-actvStartTime').text(data.actvStartTime)
            $('#confirm-dwnlEndTime').text(data.dwnlEndTime)
            $('#confirm-currentDelete').text(data.deletedWhenDone=='1'?'YES':'NO')
            

            TMS.getData({
                url: _WEB_SITE_ + '/groupDeploy/service/deployConfirm',
                type: 'get',
                data: {
                    groupId:data.groupId,
                    modelId:data.destModel
                },
            }, function(data) {
                console.log(data);
                $('#confirm-currentCount').text(data.Count)
                $('#confirm-currentDetail').empty();
                if(data && data.Count){
                    var items=data.items;
                    var $contain=$('<div class="confirm-terminal-detail"></div>')
                    for(var key in data){
                        if(key!='Count'){
                            var $item=$('<span class="confirm-terminal-detail-item"></span>')
                            $item.text(key+' : '+data[key]);
                            $contain.append($item)
                        }
                    }
                    $('#confirm-currentDetail').append($contain)
                    
                }
                $('#deploy-confirm-modal').modal('show');
            });
        },
        serializeObject:function(str){
            var data={};
            var dataArr=str.split('&');
            for(var i in dataArr){
                var dataItem=dataArr[i].split('=');
                data[dataItem[0]]=dataItem[1];
            }
            return data;
        }
    }
    $(function() {
        TMS.init();

        addDepoly.init();



    });
});