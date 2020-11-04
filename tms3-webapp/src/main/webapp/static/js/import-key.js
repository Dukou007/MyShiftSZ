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
    var tsn = $("#tsn").val();
    var filePath = '';
    var fileName = '';
    var importKey = {
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
                    params.groupId = groupId;
                    params.fileType = 'KEY_TYPE';
                    params.sn = tsn;
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
                    '<li id="delete_key_modal"><a  data-toggle="modal" style="color:#ff7068">',
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
                                TMS.alert('Key file is a folder', 'danger-upload');
                            };
                        }

                    });

                },
                done: function(e, data) { //上传完成
                    fileName = data.result.fileName;
                    filePath = data.result.filePath;
                    TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                    //上传失败
                    if (data.result.statusCode != undefined && data.result.statusCode != '200') {
                        TMS.alert(data.result.message, 'danger');
                    } else {
                    	$('.upload-filename').text(fileName);
                    	TMS.alert(data.result.message, 'success');
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

            $('#importKey').submit(function(event) {
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				//file is null
				if (!filePath) {
					TMS.alert('Please select a file', 'danger-upload');
					return false
				}				
				var notes = $('#notesArea').val();
				TMS.getData({ //提交
					url: _WEB_SITE_ + '/offlinekey/service/import',
					type: 'post',
					dataType: 'json',
					data: {
						groupId: groupId,
						filePath: filePath,
						fileName: fileName,
						notes: notes
					}
				}, function(data) {
					var keyTips = 'Total: ' + data.total + '\r\nImported: ' + data.imported + '\r\nSN not found: ' + data.notFoundSN
            			+ '\r\nAlready Exist: ' + data.existKey + '\r\nInvalid key file: ' + data.errorFile;
					if (data.statusCode != undefined && data.statusCode != '200') {
                        TMS.alert(data.message, 'danger');
                    } else {
                    	TMS.alert(keyTips, 'warning');
                    	TMS.getData({
                          url: _WEB_SITE_ + '/importFile/service/save',
                          type: 'post',
                          data: {
                              fileType: 'KEY_TYPE',
                              parentId: groupId,
                              fileName: fileName,
                              filePath: filePath,
                              override: 1
                          }
                      }, function(data) {
                    	  if (data.statusCode != undefined && data.statusCode != '200') {
                              TMS.alert(data.message, 'danger');
                          } else {
//                        	  TMS.alert('success', 'success');
                        	  filePath = '';
                        	  fileName = '';
                        	  $('.upload-filename').text('');
                        	  $('#notesArea').val('');
                        	  $('#table').bootstrapTable('refresh');
                          }
                      });
					}
				});
				return false
			});
            //delete
            /*  var groupId = $('table').attr('data-id');*/
            $body.on('click', '#delete_key_modal', function() {
                fileId = $(this).parents('.g-action').attr('data-id');
                $('#modal_key_delete').modal('show');

            });
            $('#confirm_delete_key').click(function() {
                TMS.getData({
                    url: _WEB_SITE_ + '/importFile/service/delete/',
                    type: 'get',
                    data: {
                    	groupId: groupId,
                    	fileId: fileId
                    }
                }, function(data) {
                    self.ajaxBack(data, '#modal_key_delete');
                });
            });
        }
    };

    $(function() {
        TMS.init();
        TMS.actionMenuEvent();
        importKey.init();
    })
});