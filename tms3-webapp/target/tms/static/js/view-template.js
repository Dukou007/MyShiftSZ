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
    require('fileupload');
    var pkgId = $("#first-pkgid").val();
    var groupId = $("#gid").val();
    var viewTemplate = {
        init: function() {
            var self = this;
            this.bindEvent();
            this.ajaxParams(function() {
                self.disabled();
            });
        },
        disabled: function() {
            $('.view-btn-group').removeClass('hide');
            $('.edit-btn-group').addClass('hide');
            $('.edit-item-panel').addClass('hide').removeClass('has-error');
            $('.view-item-panel').removeClass('hide');
            $('.parameters-body input,.parameters-body select').prop('disabled', true);
            $('.parameters-body .filename').prop('disabled', true);
            $('.parameters-body .parameters-file-btn .btn').addClass('disabled');
            $('#messageBox ul').html('');
            $('#first-templatename').val($.trim($('.first-templatename').text()));
            $('.g-panel-text').html('<a class="g-back-button iconfont" href="javascript:history.go(-1)"></a>View Template');
        },
        enabled: function() {
            $('.view-btn-group').addClass('hide');
            $('.edit-btn-group').removeClass('hide');

            $('.edit-item-panel').removeClass('hide');
            $('.view-item-panel').addClass('hide');
            $('.g-panel-text').html('Edit Template');

        },
        renderParams: function(data) {
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
                    tempContent.push('<div role="tabpanel" class="tab-pane clearfix fade" id="' + data[i].ID + '">');
                    tempContent.push(data[i].Data);
                    tempContent.push('</div>');
                }
                temp.push('<a href="#' + data[i].ID + '" role="tab" data-toggle="tab">' + data[i].Title + '</a>');
                temp.push('</li>');

            }
            $('.parameters-ul').html(temp.join(''));
            $('.parameters-box .tab-content').html(tempContent.join(''));
            $('.parameters-box .tab-content .tab-pane').each(function(index, el) {
                if ($(el).find('.parameters-panel').length == 0) {
                    $(el).html('<div class="parameters-panel" style="padding-top:20px;"><div class="parameters-body"><div class="row">' + $(el).html() + '</div></div></div>')

                }
            });
            this.changeValue();
            this.fileUpLoad();
        },
        ajaxParams: function(callback) {
            var self = this;
            var pkgSchemaId = $('#pkgSchema-id').val();
            TMS.removeAlertdanger($('.bootstrap-growl .alert'));
            TMS.getData({
                    url: _WEB_SITE_ + '/pkgSchema/service/getSchemaByPkgSchemaId/' + pkgSchemaId,
                    type: 'get'
                },
                function(data) {
                    if (data.statusCode == 200 || data.statusCode == undefined) {
                        self.renderParams(JSON.parse(data.Group));
                        callback && callback();

                    } else {
                        TMS.alert(data.message, 'danger');
                    }

                });
        },
        changeValue: function() {
            $('.regex_Amount').each(function(index, el) {
                var val = $(el).val();
                if (val) {
                    val = val / 100;
                    val = val.toFixed(2);
                    $(el).val(val);
                }
            });
            $('.regex_Date').each(function(index, el) {
                var val = $(el).val(),
                    y, m, d;
                if (val) {
                    y = val.substring(0, 4);
                    m = val.substring(4, 6);
                    d = val.substring(6, 8);
                    $(el).val(m + '/' + d + '/' + y);
                }
            });
            $('.regex_DateTime').each(function(index, el) {
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
            $('.regex_Time').each(function(index, el) {
                var val = $(el).val(),
                    h, m, s;
                if (val) {
                    h = val.substring(0, 2);
                    m = val.substring(2, 4);
                    s = val.substring(4, 6);
                    $(el).val(h + ':' + m + ':' + s);
                }
            });
            $('.regex_Time_hhmm').each(function(index, el) {
                var val = $(el).val(),
                    h, m;
                if (val) {
                    h = val.substring(0, 2);
                    m = val.substring(2, 4);
                    $(el).val(h + ':' + m);
                }
            });
            $('.filename').each(function(index, el) {
                var val = $(el).val();
                if (val.indexOf('|') != -1) {
                    $(el).attr('data-oldfile', val);
                    val = val.split('|')[0];
                    $(el).val(val);
                    //$(el).attr('value', val);
                }
            });
        },
        changeValueSubmit: function() {

            $('.regex_Amount').each(function(index, el) {
                var val = $(el).val();
                if (val) {
                    val = val * 100
                    $(el).val(val);
                }
            });
            $('.regex_Date').each(function(index, el) {
                var val = $(el).val();
                if (val) {
                    val = val.split('/');
                    $(el).val(val[2] + val[0] + val[1]);
                }
            });
            $('.regex_DateTime').each(function(index, el) {
                var val = $(el).val(),
                    f, s;
                if (val) {
                    val = val.split(' ');
                    f = val[0].split('/');
                    s = val[1].split(':');
                    $(el).val(f[2] + f[0] + f[1] + s[0] + s[1] + s[2]);
                }
            });
            $('.regex_Time').each(function(index, el) {
                var val = $(el).val();
                if (val) {
                    val = val.split(':');
                    $(el).val(val[0] + val[1] + val[2]);
                }
            });
            $('.regex_Time_hhmm').each(function(index, el) {
                var val = $(el).val();
                if (val) {
                    val = val.split(':');
                    $(el).val(val[0] + val[1]);
                }
            });
            $('.filename').each(function(index, el) {
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
        fileUpLoad: function() {
            $('.fileupload').fileupload({
                dataType: 'json',
                url: _WEB_SITE_ + '/pkgSchema/service/upload',
                type: 'put',
                autoUpload: false,
                recalculateProgress: false,
                formData: function(form) {
                    return {};
                },
                add: function(e, data) {
                    var maxFileSize = 200 * 1024 * 1024; //允许的文件最大值200MB
                    TMS.removeAlertdanger($('.bootstrap-growl .alert'));
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
                },
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
        bindEvent: function() {
            var self = this;
            var $parametersBox = $('.parameters-box'),
                $parametersBoxUl = $('.parameters-box .parameters-ul'),
                $ulposition = $('.ulposition'),
                gTopContentHeight = $('.g-top-content').height();
            $('.g-middle-content').scroll(function() {
                if ($parametersBox.hasClass('hide')) {
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
            $('#remove-template-modal .J-confirm-btn').click(function() {
            	TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                TMS.getData({
                    url: _WEB_SITE_ + '/pkgSchema/service/delete',
                    type: 'post',
                    data: {
                        pkgSchemaId: $('#pkgSchema-id').val(),
                        pkgId: $('#first-pkgid').val()
                    },
                    followTo: _WEB_SITE_ + "/pkgSchema/list/" + groupId
                }, function(data) {
                    if (data.statusCode == 200 || data.statusCode == undefined) {
                        TMS.alert(data.message, 'success');
                        $('#remove-template-modal').modal('hide');
                    } else {
                        TMS.alert(data.message, 'danger');
                    }


                });

            })
            $('#go-edit').click(function() {
                self.ajaxParams(function() {
                    self.enabled();
                });
            });
            $('#back-view').click(function() {
                self.ajaxParams(function() {
                    self.disabled();
                });
            });
            $("#edit-template").validate({
                ignore: '',
                onfocusout: false,
                showErrors: function(errorMap, errorList) {
                    var temp = [];
                    $('.form-edit-group').removeClass('has-error').removeClass('has-success');
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
                    self.changeValueSubmit();
                    var data = $(form).serialize();
                    self.changeValue();
                    TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                    TMS.getData({
                        url: _WEB_SITE_ + '/pkgSchema/service/edit',
                        type: 'post',
                        data: data,
                        followTo: _WEB_SITE_ + "/pkgSchema/list/" + groupId
                    }, function(data) {
                        if (data.statusCode == 200 || data.statusCode == undefined) {
                            TMS.alert(data.message, 'success');
                        } else {
                            TMS.alert(data.message, 'danger');
                        }


                    });

                }
            });
        }
    }
    $(function() {
        TMS.init();
        viewTemplate.init();
    });
})