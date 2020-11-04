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
    require('fileupload');
    var groupId = $("#gId").val();
    var filePath = '';
    var fileName = '';
    var importTerminal = {
        init: function() {
            this.bindEvent();
            this.renderTable();
            this.$table = $('#table');
        },
        renderTable: function() {
            var self = this;
            TMS.bootTable('#table', {
                url: _WEB_SITE_ + "/importFile/service/ajaxList",
                method: 'post',
                contentType: 'application/x-www-form-urlencoded',
                columns: [{
                    field: 'fileName',
                    title: 'File Name'
                }, {
                    field: 'fileSize',
                    title: 'File Size',
                    formatter: sizeFormatter
                }, {
                    field: 'modifyDate',
                    title: 'Upload Time/Date',
                    formatter: dateFormatter
                }, {
                    field: 'status',
                    title: 'Import Status',
                }, {
                    field: '',
                    title: '',
                    formatter: tableAction
                }],
                queryParams: function(params) { // 接口参数处理
                    params.groupId = $("#gId").val();
                    params.fileType = 'TERMINAL_TYPE';
                    return params
                },
            });

            function statusOperate(value, row, index) {
                if (row.status == '1') {
                    return ['<span >Successful</span>'].join('');
                } else {
                    return ['<span style="color:#ff7068">Failed</span>'].join('');
                }
            }

            function dateFormatter(value, row, index) {
                return TMS.changeTime(row.modifyDate)
            }

            function sizeFormatter(value, row, index) {
                return (row.fileSize / 1024).toFixed(2) + "KB";
            }

            function tableAction(value, row, index) { // 操作
                return [
                    '<div class="g-action" data-id="' + row.id + '">',
                    '<div class="g-action-btn">',
                    '<i class="iconfont">&#xe60f;</i>',
                    '</div>',
                    '<ul class="g-action-menu">',
                    '<li id="delete_terminal_modal"><a  data-toggle="modal" style="color:#ff7068">',
                    'Delete', '</a></li>',
                    '</ul>',
                    '</div>'
                ].join('')
            }
        },
        ajaxBack: function(data, modal) { //请求回调
            var self = this;
            TMS.removeAlertdanger($('.bootstrap-growl .alert'));
            if (data.statusCode != undefined && data.statusCode != '200') {
                TMS.alert(data.message, 'danger');
            } else {
                TMS.alert(data.message, 'success');
                $(modal).modal('hide');
                self.$table.bootstrapTable('refresh');

            }
        },
        bindEvent: function() {
            var self = this,
                $body = $('body');
            $('.g-container').bind('drop dragover', function(e) { //阻止浏览器默认事件和传播
                e.preventDefault();
                e.stopPropagation();
            });
            $('#fileupload').fileupload({
                dataType: 'json',
                url: _WEB_SITE_ + "/tools/service/upload",
                type: 'put',
                autoUpload: false,
                recalculateProgress: false,
                dropZone: $('.g-container'),
                formData: function(form) {
                    return {};
                },
                add: function(e, data) {
                    var maxFileSize = 200 * 1024 * 1024; //允许的文件最大值200MB
                    TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                    $.each(data.files, function(index, file) {
                        var uploadErrors = [];
                        /*if(file.size >maxFileSize){
                            TMS.alert('Filesize is too big','danger');
                            uploadErrors.push('Filesize is too big');
                        }*/

                        if (uploadErrors.length == 0) {
                            var ff = new FileReader(); //check the drag and drop is a folder(firefox)
                            ff.readAsArrayBuffer(file);
                            ff.onload = function() {

                                data.submit();

                            };
                            ff.onerror = function() {
                                TMS.alert('Package file is a folder', 'danger-upload');
                            };
                        }

                    });

                },
                done: function(e, data) { //上传完成
                    var fileName = data.result.fileName;
                    var filePath = data.result.filePath;
                    TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                    //上传失败
                    if (data.result.statusCode != undefined && data.result.statusCode != '200') {
                        TMS.alert(data.result.message, 'danger');
                    } else {
                        //上传成功
                        TMS.getData({
                            url: _WEB_SITE_ + "/importFile/service/save",
                            type: 'post',
                            data: {
                                fileType: 'TERMINAL_TYPE',
                                parentId: $("#gId").val(),
                                fileName: fileName,
                                filePath: filePath
                            }

                        }, function(data) {
                            //error
                            if (data.statusCode != undefined && data.statusCode != '200') {
                                //Invalid file not end with .xml suffix
                                TMS.alert(data.message, 'danger-upload');
                            } else {
                                //success,导入文件保存成功
                                //返回值为空，导入类型为group
                                //其他组已经存在

                                if (data.existTsnNeedToAssignGroup && data.existTsnNeedToAssignGroup.length > 0) {
                                    var str = '';
                                    var terminalList = '';
                                    var ignortsns = data.existTsnNeedToAssignGroup;
                                    terminalList = TMS.convertArrayDescribe(ignortsns);
                                    if (ignortsns.length <= 3) {
                                        str = 'Terminal(s) ' + terminalList + ' exist(s) in other group(s), and the details will not be overwritten. Do you still want to add?';
                                    } else {
                                        str = 'The following ' + ignortsns.length + ' terminals including ' + terminalList + ' ect which exist in other group(s), and the details will not be overwritten. Do you still want to add?'
                                    }
                                    $('#modal_terminal_reconfirm .modal-body').html(str);
                                    $('#modal_terminal_reconfirm').modal('show');
                                    //确定覆盖已存在的terminal(s)
                                    $('#modal_terminal_reconfirm #confirm_btn').unbind().click(function() {
                                        TMS.getData({
                                            url: _WEB_SITE_ + '/importFile/service/save',
                                            type: 'post',
                                            data: {
                                                fileType: 'TERMINAL_TYPE',
                                                parentId: $("#gId").val(),
                                                fileName: fileName,
                                                filePath: filePath,
                                                override: 1
                                            }
                                        }, function(data) {

                                            if (data.statusCode == 200 || data.statusCode == undefined) {
                                                $('#modal_terminal_reconfirm').modal('hide');
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
                                                if (ownByOtherParallerGroupTsn.length > 0 && existTsnIgnoreToAssignGroup.length > 0) {
                                                    if (ownByOtherParallerGroupTsn.length > 3) {
                                                        if (existTsnIgnoreToAssignGroup.length > 3) {
                                                            str = 'Failed to add following ' + ownByOtherParallerGroupTsn.length + ' terminals including ' + ownByOtherParallerGroupTsnList + '  etc which exist in other group(s) out of your authority,' +
                                                                '<br/>and failed to add following ' + existTsnIgnoreToAssignGroup.length + ' terminals including  ' + existTsnIgnoreToAssignGroupList + ' etc which exist in this group.';
                                                        } else {
                                                            str = 'Failed to add following ' + ownByOtherParallerGroupTsn.length + ' terminals including  ' + ownByOtherParallerGroupTsnList + ' etc which exist in other group(s) out of your authority,' +
                                                                '<br/>and failed to add terminal(s) including  ' + existTsnIgnoreToAssignGroupList + ' which exist(s) in this group.';
                                                        }
                                                    } else {
                                                        if (existTsnIgnoreToAssignGroup.length > 3) {
                                                            str = 'Failed to add terminal(s) including ' + ownByOtherParallerGroupTsnList + '  which exist(s) in other group(s) out of your authority,' +
                                                                '<br/>and failed to add following ' + existTsnIgnoreToAssignGroup.length + ' terminals including ' + existTsnIgnoreToAssignGroupList + '  etc which exist in this group.';
                                                        } else {
                                                            str = 'Failed to add  terminal(s) including  ' + ownByOtherParallerGroupTsnList + '  which exist(s) in other group(s) out of your authority,' +
                                                                '<br/>and failed to add terminal(s) including  ' + existTsnIgnoreToAssignGroupList + '  which exist(s) in this group.';
                                                        }
                                                    }
                                                    TMS.alert(str, 'danger');

                                                }
                                                //terminal都存在于权限外
                                                if (ownByOtherParallerGroupTsn.length > 0 && existTsnIgnoreToAssignGroup.length == 0) {
                                                    
                                                        if (ownByOtherParallerGroupTsn.length > 3) {
                                                            str = 'Failed to add following '+ownByOtherParallerGroupTsn.length+' terminals including  ' + ownByOtherParallerGroupTsnList + '  etc which exist in other group(s) out of your authority';
                                                        } else {
                                                            str = 'Failed to add terminal(s) including  ' + ownByOtherParallerGroupTsnList + '  which exist(s) in other group(s) out of your authority';
                                                        }
                                                    
                                                    TMS.alert(str, 'danger');
                                                }
                                                //都是权限内的组
                                                if (ownByOtherParallerGroupTsn.length == 0 && existTsnIgnoreToAssignGroup.length > 0) {
                                                    
                                                        if (existTsnIgnoreToAssignGroup.length > 3) {
                                                            str = 'failed to add following '+existTsnIgnoreToAssignGroup.length+' terminals including  ' + existTsnIgnoreToAssignGroupList + '  etc which exist in this group.';
                                                        } else {
                                                            str = 'failed to add terminal(s) including  ' + existTsnIgnoreToAssignGroupList + '  which exist(s) in this group.';
                                                        }
                                                    
                                                    TMS.alert(str, 'danger');
                                                }
                                                if (ownByOtherParallerGroupTsn.length == 0 && existTsnIgnoreToAssignGroup.length == 0) {
                                                    TMS.alert('success', 'success');
                                                }

                                                $('#table').bootstrapTable('refresh');
                                                TMS.topLeftTree.refresh();

                                            } else {
                                                TMS.alert(data.message, 'danger');
                                            }
                                        });
                                    });
                                } else {
                                    //
                                    TMS.getData({
                                        url: _WEB_SITE_ + '/importFile/service/save',
                                        type: 'post',
                                        data: {
                                            fileType: 'TERMINAL_TYPE',
                                            parentId: $("#gId").val(),
                                            fileName: fileName,
                                            filePath: filePath,
                                            override: 1
                                        }
                                    }, function(data) {

                                        if (data.statusCode == 200 || data.statusCode == undefined) {
                                            $('#modal_terminal_reconfirm').modal('hide');
                                            //如果在terminal中导入group,返回对象为空，提示成功，刷新页面
                                          /*  console.log($.isEmptyObject(data)); return;*/
                                            if($.isEmptyObject(data)){
                                            	 TMS.alert('success', 'success');
                                            } else {
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
                                                
                                                if (ownByOtherParallerGroupTsn.length > 0 && existTsnIgnoreToAssignGroup.length > 0) {
                                                    if (ownByOtherParallerGroupTsn.length > 3) {
                                                        if (existTsnIgnoreToAssignGroup.length > 3) {
                                                            str = 'Failed to add following ' + ownByOtherParallerGroupTsn.length + ' terminals including ' + ownByOtherParallerGroupTsnList + '  etc which exist in other group(s) out of your authority,' +
                                                                '<br/>and failed to add following ' + existTsnIgnoreToAssignGroup.length + ' terminals including  ' + existTsnIgnoreToAssignGroupList + ' etc which exist in this group.';
                                                        } else {
                                                            str = 'Failed to add following ' + ownByOtherParallerGroupTsn.length + ' terminals including  ' + ownByOtherParallerGroupTsnList + ' etc which exist in other group(s) out of your authority,' +
                                                                '<br/>and failed to add  terminal(s) including  ' + existTsnIgnoreToAssignGroupList + ' which exist(s) in this group.';
                                                        }
                                                    } else {
                                                        if (existTsnIgnoreToAssignGroup.length > 3) {
                                                            str = 'Failed to add terminal(s) including ' + ownByOtherParallerGroupTsnList + '  which exist(s) in other group(s) out of your authority,' +
                                                                '<br/>and failed to add following ' + existTsnIgnoreToAssignGroup.length + ' terminals including ' + existTsnIgnoreToAssignGroupList + '  etc which exist in this group.';
                                                        } else {
                                                            str = 'Failed to add terminal(s) including  ' + ownByOtherParallerGroupTsnList + '  which exist(s) in other group(s) out of your authority,' +
                                                                '<br/>and failed to add terminal(s) including  ' + existTsnIgnoreToAssignGroupList + '  which exist(s) in this group.';
                                                        }
                                                    }
                                                    TMS.alert(str, 'danger');

                                                }
                                                //terminal都存在于权限外
                                                if (ownByOtherParallerGroupTsn.length > 0 && existTsnIgnoreToAssignGroup.length == 0) {
                                                    if (newTsns.length > 0) {
                                                        if (ownByOtherParallerGroupTsn.length > 3) {
                                                            str = 'Failed to add following '+ownByOtherParallerGroupTsn.length+' terminals including  ' + ownByOtherParallerGroupTsnList + '  etc which exist in other group(s) out of your authority';
                                                        } else {
                                                            str = 'Failed to add terminal(s) including  ' + ownByOtherParallerGroupTsnList + '  which exist(s) in other group(s) out of your authority';
                                                        }
                                                    } else {
                                                        str = 'Failed! Terminal(s) exist(s) in other group(s) out of your authority.';
                                                    }
                                                    TMS.alert(str, 'danger');
                                                }
                                                //都是权限内的组
                                                if (ownByOtherParallerGroupTsn.length == 0 && existTsnIgnoreToAssignGroup.length > 0) {
                                                    if (newTsns.length > 0) {
                                                        if (existTsnIgnoreToAssignGroup.length > 3) {
                                                            str = 'failed to add following '+existTsnIgnoreToAssignGroup.length+' terminals including  ' + existTsnIgnoreToAssignGroupList + '  etc which exist in this group.';
                                                        } else {
                                                            str = 'failed to add terminal(s) including  ' + existTsnIgnoreToAssignGroupList + '  which exist(s) in this group.';
                                                        }
                                                    } else {
                                                        str = 'Failed! Terminal(s) exist(s) in this group.';
                                                    }
                                                    TMS.alert(str, 'danger');
                                                }
                                                if (ownByOtherParallerGroupTsn.length == 0 && existTsnIgnoreToAssignGroup.length == 0) {
                                                    TMS.alert('success', 'success');
                                                }

                                                
                                            }
                                            $('#table').bootstrapTable('refresh');
                                            TMS.topLeftTree.refresh();
                             

                                        } else {
                                            TMS.alert(data.message, 'danger');
                                        }
                                    });
                                }
                                //else end
                            }
                        })


                    }
                },
                fail: function(e, data) { //上传失败
                    if (data.result && data.result.message) {
                        TMS.alert(data.result.message, 'danger-upload');
                    } else {
                        TMS.alert('Network instability', 'danger-upload');
                    }
                    $('#fileupload').val('');
                    filePath = '';
                },
                progress: function(e, data) {},
                progressall: function(e, data) { //进度条
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    $('.progress .progress-bar').css(
                        'width',
                        progress + '%'
                    );


                },
                start: function(e) { //开始时

                    $('.progress').removeClass('hide');
                },
                always: function(e, data) { //默认上传结束执行

                },
                stop: function() {

                    setTimeout(function() {
                        $('.progress').addClass('hide');
                        $('.progress .progress-bar').css(
                            'width', '0%'
                        );
                    }, 500)

                }
            }).prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled');
            //delete
            /*  var groupId = $('table').attr('data-id');*/
            $body.on('click', '#delete_terminal_modal', function() {
                fileId = $(this).parents('.g-action').attr('data-id');
                $('#modal_terminal_delete').modal('show');

            });
            $('#confirm_delete_terminal').click(function() {
                TMS.getData({
                    url: _WEB_SITE_ + '/importFile/service/delete/',
                    type: 'get',
                    data: {
                    	groupId: groupId,
                    	fileId: fileId
                    }
                }, function(data) {
                    self.ajaxBack(data, '#modal_terminal_delete');
                });
            });

            //import 
            $body.on('click', '#import_terminal_modal', function() {
                fileId = $(this).parents('.g-action').attr('data-id');
                $('#modal_terminal_import').modal('show');

            });
            $('#confirm_import_terminal').click(function() {
                TMS.getData({
                    url: _WEB_SITE_ + '/group/service/import/',
                    type: 'get',
                    data: {
                        fileId: fileId,
                        parentId: $("#gId").val()
                    }
                }, function(data) {
                    self.ajaxBack(data, '#modal_terminal_import');
                });
            });

        }

    };

    $(function() {
        TMS.init();
        TMS.actionMenuEvent();
        importTerminal.init();

    })
});