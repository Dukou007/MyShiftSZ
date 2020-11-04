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
	require('validate-methods');
	require('datetimepicker');
	require('fileupload');
	var mutiSelect = require('btselect');
	var TMS = require('TMS');
	$(function() {
		TMS.init();

		new mutiSelect({
			el:'.mult-select'
		})
		//pannel切换
		$('.toggle-button').click(function() {
			var target = $(this).attr('data-target');
			$(target).toggle();
			$(this).find('.toggle-icon').toggleClass('icondown');
			if ($(this).parents('.panel').hasClass('panel-primary')) {
				$(this).parents('.panel').removeClass('panel-primary').addClass('panel-default');
			} else {
				$(this).parents('.panel').removeClass('panel-default').addClass('panel-primary');
			}
		});
		//文件上传
		$('.file').change(function(e) {
			var target = $(this).attr('data-target');
			$(target).val(e.target.files[0].name)
		});

		//表单验证
		$("#form1").validate({
			debug: true,
			submitHandler: function(form) //验证成功后执行函数
				{
					
						//code
				}
		});
		//时间选择
		$(".form_datetime").datetimepicker({
			autoclose: true,
			minView: 2,
			container: '.body-content'
		});
		//table
		TMS.bootTable('#table', {
			url: '/static/js/data1.json',
			columns: [{
				field: 'userName',
				title: 'userName',
				sortable: false
			}, {
				field: 'userFullName',
				title: 'userFullName',
				sortable: false
			}, {
				field: 'email',
				title: 'email',
				sortable: false
			}, {
				field: 'organizationName',
				title: 'organizationName',
				sortable: false
			}, {
				field: '',
				title: '操作',
				formatter: operateFormatter
			}],
			loadend:function(){
			},
			sidePagination: 'server',
			queryParams: function(params) { //接口参数处理
				return params
			},
			detailView: true,
			onExpandRow:function(index, row, $detail){
			},
			detailFormatter: function(index, row) { //详情
				var html = [];
				html.push('121212121212');

				return html.join('');
			},
			responseHandler: function(res) { //接口数据处理
				this.data = res.items;
				res.total = res.totalCount;
				return res;
			},

			autocomplete:true,
			autocompleteUrl:'search.php',
			showRefresh:true,
			sortName: 'userName',
			sortOrder: 'asc',
			pagination: true,
			search: true,
			queryParamsType:'limit',
			searchOnEnterKey: true
		});

		function operateFormatter(value, row, index) {
			var trigger = 'hover';
			if (TMS.isPC()) {
				trigger = 'focus';
			}
				var str = '<ul class=\'\' style=\'margin-bottom:0px\'><li><i class=\'glyphicon glyphicon-wrench\' title=\'Delpoy\'></i></li><li class=\'modal-del\'><i class=\'glyphicon glyphicon-trash\' ></i></li></ul>';
			return [
				'<a class="set" data-id=' + row.id + ' href="javascript:void(0)" data-content="'+str+'" data-container=".body-content"  data-html="true" data-trigger="' + trigger + '"  data-toggle="popover" data-placement="bottom" >',
				'<i class="glyphicon glyphicon-cog"></i>',
				'</a>',
			].join('');
		}
		$('.body-content').on('click', '.set', function(event) {
			//$(this).popover('toggle');
		});
		$('#table').on('click-row.bs.table', function(row, $element) {
		});

		$('#fileupload').fileupload({
			dataType: 'json',
			autoUpload: false,
			recalculateProgress: false,
			dropZone: $('#none'),
			formData: function(form) {
				return {};
			},
			add: function(e, data) {
				var maxFileSize = 999 * 1024; //允许的文件最大值999kb
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				$.each(data.files, function(index, file) {
					var uploadErrors = [];
					$('#file').val(file.name);
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
			},
			fail: function(e, data) { //上传失败
				TMS.alert('fail', 'danger');
				$('#file').val('');
			},
			progress: function(e, data) {
			},
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
		$('#change-userpassword').submit(function() {
			var newPassword,confirmPassword;
			newPassword = $(this).find('#newpassword').val();
			confirmPassword = $(this).find('#confirmpassword').val();
			TMS.removeAlertdanger($('.bootstrap-growl .alert'));
			TMS.getData({
				url: $("#contextPath").val() + '/user/changeUserPassword/'+11,
				type: 'post',
				data:{
					newPassword:newPassword,
					confirmPassword:confirmPassword
				}
			},function(data){
				if(data == 'fail'){//404、500错误
					TMS.alert('Network instability','danger');
				}else{
					if (!data.statusCode || data.statusCode && data.statusCode == 200) {
						//success
						TMS.alert(data.message,'success');
					} else {
						//error
						TMS.alert(data.message,'danger');
					}
				}
			});
			return false;
			
		});
	});
})