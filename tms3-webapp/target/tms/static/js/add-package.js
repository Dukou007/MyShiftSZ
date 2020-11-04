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
	TMS.init();
	require('fileupload');
	var filePath = '';
	var fileName = '';
	var groupId = $("#gid").val();
	var addPackage = {
		init: function() {
			this.creatTree();
			this.bindEvent();
		},
		creatTree: function() {
			var self = this;
			this.Tree = new TMS.groupTree('#add-package-tree', {
				
				nowGroup: false,
				multiselect: true,
				selectUp:true,
				isModal: true,
				loadAll: false,
				isassign:true,
				groupPanel: { //group 选择框
					display: true
				}
			});
		},
		bindEvent: function() {
			var self = this;
			

			$('.g-container').bind('drop dragover', function(e) { //阻止浏览器默认事件和传播
				e.preventDefault();
				e.stopPropagation();
			});
			$('#fileupload').fileupload({
				dataType: 'json',
				url: _WEB_SITE_ + '/tools/service/upload',
				type: 'put',
				autoUpload: false,
				recalculateProgress: false,
				dropZone: $('.g-container'),
				formData: function(form) {
					return {};
				},
				add: function(e, data) {
					TMS.removeAlertdanger($('.alert-danger-upload'));
					$.each(data.files, function(index, file) {
						var uploadErrors = [];
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
					filePath = data.result.filePath;
					fileName = data.result.fileName;
					$('.upload-filename').text(fileName);
					TMS.alert(data.result.message, 'success');
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
			$('#addPkg').submit(function(event) {
				var items = self.Tree.option.activeGroup.items;
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				//group is null,file is null
				if (items.length == 0 && !filePath) {
					TMS.alert('Please select a group', 'danger');
					TMS.alert('Please select a file', 'danger-upload');
					return false
				}
				//group is null
				if (items.length == 0 && filePath) {
				
					TMS.alert('Please select a group', 'danger');
					return false
				}
				//file is null
				if (items.length != 0 && !filePath) {
					
					TMS.alert('Please select a file', 'danger-upload');
					return false
				}				
				var ids = '';
				for (var i = 0; i < items.length; i++) {
					ids += items[i].id + ',';
				}
				if (ids) {
					ids = ids.substring(0, ids.length - 1)
				}
				var packageType = $('select[name="packageType"]').val();
				var destModel = $('select[name="destModel"]').val();
				var notes = $('#notesArea').val();
				TMS.getData({ //提交
					url: _WEB_SITE_ + '/pkg/service/add',
					type: 'post',
					dataType: 'json',
					data: {
						groupIds: ids,
						filePath: filePath,
						type: $("#packageType").val(),
						fileName: fileName,
						notes: notes
					},
					followTo: _WEB_SITE_ + "/pkg/manageList/" + groupId
				});
				return false
			});
		}
	}
	$(function() {
		TMS.init();
		addPackage.init();
	})
});