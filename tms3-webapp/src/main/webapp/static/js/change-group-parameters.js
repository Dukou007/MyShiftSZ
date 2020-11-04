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
    var changeParameters = {
        init: function() {
            this.isRenderTable = false; //是否生成table
            this.bindEvent();
            this.initChangeParameters();
        },
        initChangeParameters: function() {
            this.bindEventChangeParameters();
        },
        ajaxParams: function(obj, callback) {
            var self = this,
                url = _WEB_SITE_ + '/pkgSchema/service/getSchemaByPkgSchemaId/' + obj.pkgSchemaId;
            if (obj.paramSet != null) {
                url = _WEB_SITE_ + '/groupDeploy/service/getSchemaByPkgSchemaIdAndParamSet/' + obj.pkgSchemaId + '/' + obj.paramSet;
            }
            TMS.removeAlertdanger($('.bootstrap-growl .alert'));
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
        ajaxParamsByPkgId: function(obj, callback) {
            var self = this;
            TMS.removeAlertdanger($('.bootstrap-growl .alert'));
            TMS.getData({
                    url: _WEB_SITE_ + '/pkgSchema/service/getSysInitPkgSchema/' + obj.pkgId,
                    type: 'get'
                },
                function(data) {
                    if (data.statusCode == 200 || data.statusCode == undefined) {
                        $('.pkgSchema-id').val(data.pkgSchemaId);
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
                choose: function(d, e) {
                    $(e).closest('.form-edit-group').removeClass('has-error').removeClass('has-success');
                    $(e).closest('.form-edit-group').find('.help-block').remove();
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
                    var maxFileSize = 200 * 1024 * 1024; //允许的文件最大值200MB
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
                                TMS.alert('Package file is a folder','danger-upload');
                            };
                        } else {
                            TMS.alert(uploadErrors.join(''), 'danger-upload');
                        }

                    });

                },
                done: function(e, data) { //上传完成

                    $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.filename').attr('data-file', data.result.filePath);
                    TMS.alert(data.result.message, 'success');
                },
                fail: function(e, data) { //上传失败

                    //$(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.filename').attr('data-file', '');
                    if (data.result && data.result.message) {
                        TMS.alert(data.result.message, 'danger-upload');
                    } else {
                        TMS.alert('Network instability', 'danger-upload');
                    }


                },
                progress: function(e, data) {
                    //console.log(data.files[0].name, data.loaded, data.total)
                },
                progressall: function(e, data) { //进度条
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    //console.log('progressall', data.loaded, data.total)
                    $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.progress .progress-bar').css(
                        'width',
                        progress + '%'
                    );


                },
                start: function(e) { //开始时
                    //console.log('开始')
                    $(e.target).closest('.parameters-file-btn').siblings('.form-edit-group').find('.progress').removeClass('hide');
                },
                always: function(e, data) { //默认上传结束执行
                    //console.log('结束')
                },
                stop: function(e) {
                    console.log('finished')
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
        renderSelect2: function(pkgName, pkgVersion, callback) { //联动
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
                    if (!data) {
                        return false
                    }
                    $('#pkg-id').val(data.pkgId);
                    callback && callback()

                });
            }

        },
        addSelect: function(el) {
            $(el).find('.form-edit-group .edit-name').each(function(index, el) {
                $(el).parents('.form-edit-group').find('.form-control').addClass('ignore');
                $(el).html('<div class="checkbox block-inline checkbox-primary"><input data-index="0" name="selectItem" type="checkbox"><label></label></div>' + $(el).html());
            });
        },
        disabled: function(el) {
            $(el).find('input,select,.filename').prop('disabled', true);
            $(el).find('.parameters-file-btn .btn').addClass('disabled');
        },
        removeParameters: function() {
            $('.parameters-box').addClass('hide');
            $('.parameters-box .tab-content').html('');
        },
        getChangeParametersTable: function() {
            var data = [];
            $('.parameters-box .form-edit-group').each(function(index, el) {
                var o = {};
                if (!$(el).find('.form-control').hasClass('ignore')) {
                    o.title = $(el).find('.edit-name').text().replace('*', '');
                    o.value = $(el).find('.edit-value .form-control').val();
                    o.pid = $(el).find('.edit-value .form-control').attr('name');
                    o.description = $(el).find('.edit-name').attr('title');
                    data.push(o);
                }


            });
            this.renderParamsTable(data);
        },
        renderParamsTable: function(data) {
            var self = this;
            if (!this.isRenderTable) {
                this.isRenderTable = true;
                TMS.bootTable('#change-parameters-table', {
                    data: data,
                    columns: [{
                        field: 'pid',
                        title: 'PID'
                    }, {
                        field: 'title',
                        title: 'Title(UI Label)'
                    }, {
                        field: 'description',
                        title: 'Description(Mouse Over Hint)'
                    }, {
                        field: 'value',
                        title: 'value'
                    }]
                });
            } else {
                $('#change-parameters-table').bootstrapTable('refreshOptions', {
                    data: data
                })
            }

        },
        bindEvent: function() {
            var self = this;
            $('.first-terminaltype').on('change', function(event) {
                var val = $(this).val();
                self.renderSelect({
                    'data': {
                        'groupId': deployGroupId,
                        'destModel': val,
                        'type': 0
                    },
                    'el': '.first-packagename',
                    'url': _WEB_SITE_ + '/groupDeploy/service/getPkgNamesByGroupIdAndDestmodel'
                });
            });
            $('.first-packagename').on('change', function(event) {
                var val = $(this).val();
                self.renderSelect({
                    'data': {
                        'name': val
                    },
                    'el': '.first-packageversion',
                    'url': _WEB_SITE_ + '/pkg/service/getVersions'
                });
            });
            $('.first-packageversion').on('change', function(event) {
                var pkgName = $(".first-packagename").val();
                var pkgVersion = $(this).val();
                self.renderSelect2(pkgName, pkgVersion, function() {
                    self.ajaxParamsByPkgId({
                        pkgId: $('#pkg-id').val(),
                        el: '#change-group-parameters'
                    }, function() {
                        self.addSelect('#change-group-parameters .parameters-box');
                    })
                });
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
            var $parametersBox = $('.parameters-box'),
                $parametersBoxUl = $('.parameters-box .parameters-ul'),
                $ulposition = $('.ulposition'),
                $step1 = $('#step1');
            gTopContentHeight = $('.g-top-content').height();
            $('.g-middle-content').scroll(function() {
                if ($parametersBox.hasClass('hide') || $step1.hasClass('hide')) {
                    return false;
                }
                var height = $parametersBox.offset().top - gTopContentHeight;
                if (height < 0) {
                    $ulposition.removeClass('hide');
                    $parametersBoxUl.addClass('hide');
                } else {
                    $ulposition.addClass('hide');
                    $parametersBoxUl.removeClass('hide');
                }

            });
            self.initTimeSelect(".form_datetime");
        },
        getSerialize: function(el) {
            var str = '';
            $(el).find('.form-control').each(function(index, el) {
                if (!$(el).hasClass('ignore')) {
                    str = str + $(el).attr('name') + '=' + $(el).val() + '&'
                }
            });
            $(el).find('input[type="hidden"]').each(function(index, el) {
                str = str + $(el).attr('name') + '=' + $(el).val() + '&'
            });
            if (str) {
                str = str.substring(0, str.length - 1);
            }
            return str;
        },
        bindEventChangeParameters: function() {
            var self = this;
            $('#change-group-parameters').validate({
                ignore: '.ignore',
                onfocusout:false,
                showErrors: function(errorMap, errorList) {
                    var temp = [];
                    $("#change-group-parameters .form-edit-group").removeClass('has-error').removeClass('has-success');
                    for (var i = 0; i < errorList.length; i++) {
                        var name = $(errorList[i].element).parent().siblings('.edit-name').text().replace('*', '');
                        var id = $(errorList[i].element).closest('.tab-pane').attr('id');
                        var currentGroup = $('a[href="#' + id + '"]:eq(0)').text();
                        if (currentGroup) {
                            currentGroup = '(' + currentGroup + ')';
                        }
                        temp.push('<li>');
                        temp.push(name + currentGroup + ':' + errorList[i].message);
                        temp.push('</li>');
                        $(errorList[i].element).closest('.form-edit-group').removeClass('has-success').addClass('has-error');

                    }
                    temp = temp.join('');
                    $('#messageBox ul').html(temp);
                },
                submitHandler: function(form) {
                    $('#step1').addClass('hide');
                    $('#step2').removeClass('hide');
                    $('.view-terminaltype').text($('.first-terminaltype option:selected').html());
                    $('.view-packagename').text($('.first-packagename option:selected').html());
                    $('.view-packageversion').text($('.first-packageversion option:selected').html());
                    $('.view-dwnlstarttime').text($('#first-dwnlStartTime').val());
                    $('.view-actvstarttime').text($('#first-actvStartTime').val());
                    $('.ulposition').addClass('hide');
                    self.getChangeParametersTable();
                }
            });
            $('#step2-confirm').click(function(event) {
            	TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                self.changeValueSubmit('#change-group-parameters');
                var data = self.getSerialize('#change-group-parameters');
                self.changeValue('#change-group-parameters');
                TMS.getData({
                    url: _WEB_SITE_ + '/groupDeploy/service/changeDeployParas',
                    type: 'post',
                    data: data,
                    followTo: _WEB_SITE_ + "/groupDeploy/list/" + _ACTIVE_GROUP_ + '/' + deployGroupId
                }, function(data) {
                    if (data.statusCode == 200 || data.statusCode == undefined) {
                        TMS.alert(data.message, 'success');
                    } else {
                        TMS.alert(data.message, 'danger');
                    }

                });
            });
            $("body").on('change', '#change-group-parameters .checkbox input', function() {
                if ($(this).prop('checked')) {
                    $(this).parents('.form-edit-group').find('.form-control').removeClass('ignore');
                } else {
                    $(this).parents('.form-edit-group').find('.form-control').addClass('ignore');
                }
            });
            $('#step2-back').click(function(event) {
                $('#step1').removeClass('hide');
                $('#step2').addClass('hide');
            });
        }
    }
    $(function() {
        TMS.init();

        changeParameters.init();



    });
});