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
	var laydate = require('date-picker');
	require('validate-methods');
	$(function() {
		TMS.init();
		if( _ACTIVE_GROUP_ == 1){
			$('.addBtn').hide();
		}
		var deletePer = $("#permission-delete").val();
		$('.addAlertBtn').click(function(){
			$('#select-time').modal('show');
			
		});
		$("#alert-off li").click(function() {
			if ($(this).hasClass('active')) {
				return;
			}
			$(".alertoff-model li ").removeClass('active');
			$(".alert-time .g-panel").hide();
			var index = $(".alertoff-model li ").index(this);
			$('.alertoff-model li').eq(index).addClass("active");
			resitFunction();
			if (index == 0) {
				$('#yearly input').val("");
				$('#yearly').show();
				
				resitFunction();
			} else if (index == 1) {
				$('#monthly input').val("");
				$('#monthly').show();
			
				resitFunction();
			} else if (index == 2) {
				$(".weekly-ul li").removeClass('weekly-active');
				$(".weekly-ul li:eq(0)").addClass('weekly-active');

				$('.weekly-point').hide();
				$('.weekly-point:eq(0)').show();
				$('#weekly input').val("");
				$('#weekly').show();
				
				resitFunction();
			} else if (index == 3) {
				$('#daily input').val("");
				$('#daily').show();
				
				resitFunction();
			} else {
				$('#onetime input').val("");
				$('#onetime').show();
			
				resitFunction();
			}

		});
		$('.weekly-ul li').click(function() {
			var liIndex = $(".weekly-ul li ").index(this);
			var hasActive = $(this).hasClass('weekly-active');
			var activeLength = $('.weekly-ul li.weekly-active').length;

			if (hasActive && activeLength > 1) {
				$(this).removeClass('weekly-active');
				$(this).find('.weekly-point').hide();
			} else {
				$(this).addClass('weekly-active');
				$(this).find('.weekly-point').show();
			}
		});

		function resetInput(ev) {
			$(ev).closest('.form-edit-group').removeClass('has-error').find(
				'.help-block').remove();
		}

		function initTimeSelect(o) {
			var _o = {
				elem: '',
				format: 'YYYY-MM-DD',
				choose: function(d, e) {
					resetInput(e)
				}
			};
			o = $.extend({}, _o, o);
			laydate(o);
		};
		initTimeSelect({
			elem: ".to-time",
			format: "hh:mm",
			istime: true
		});
		initTimeSelect({
			elem: ".from-time",
			format: "hh:mm",
			istime: true
		});
		initTimeSelect({
			elem: ".form_onetime",
			format: "YYYY-MM-DD",
			min: laydate.now(),
			istime: false
		});
		initTimeSelect({
			elem: ".form_monthtime",
			format: "DD",
			istime: false
		});
		initTimeSelect({
			elem: ".form_datetime",
			format: "MM-DD",
			istime: false
		});

		$('#select-time').on('hidden.bs.modal', function(e) {
			$("#alert-off li ").removeClass('active');
			$("#alert-off li ").eq(0).addClass('active');
			resetModal(resitFunction);
		});
		$('.model-icon').on(
			'click',
			function(e) {
				$target = $(this).closest('.form-edit-group').find(
					'input.form-control');
				if ($target.hasClass('form_datetime')) {
					initTimeSelect({
						elem: ".form_datetime",
						format: "MM-DD",
						istime: false
					});
				} else if ($target.hasClass('form_monthtime')) {
					initTimeSelect({
						elem: ".form_monthtime",
						format: "DD",
						istime: false
					});
				} else if ($target.hasClass('form_onetime')) {
					initTimeSelect({
						elem: ".form_onetime",
						format: "YYYY-MM-DD",
						min: laydate.now(),
						istime: false
					});
				}
			})

		function resitFunction() {
			$('#form')[0].reset();
			$('#select-time .help-block').remove();
			$('.model-value div').removeClass('has-success');
			$('.model-value div').removeClass('has-error');
			$('.model-value').removeClass('has-success');
			$('.model-value').removeClass('has-error');
			$('.weekly-li').removeClass('weekly-active');
			$('.weekly-li .weekly-point').hide();
			$('.weekly-li').eq(1).addClass('weekly-active');
			$('.weekly-li .weekly-point').eq(1).show();
		}
		
		function resetModal(func){
			func();
			$('#yearly').show();
			$('#monthly').hide();
			$('#weekly').hide();
			$('#daily').hide();
			$('#onetime').hide();
		}
	
		// 表单提交
		$("#form")
			.validate({
				debug: true,
				submitHandler: function(form) // 验证成功后执行函数
					{
						var $targetPanel = $('#select-time .g-panel:visible');
						var flag = 0;
						var fromTime = $targetPanel.find('.from-time')
							.val();
						var toTime = $targetPanel.find('.to-time')
							.val();
						var fromTimeArr = fromTime.split(':');
						var toTimeArr = toTime.split(':');
						
						TMS.removeAlertdanger($('.bootstrap-growl .alert'));
						if (fromTimeArr[0] * 60 + Number(fromTimeArr[1]) >= toTimeArr[0] * 60 + Number(toTimeArr[1])) {
							$targetPanel.find('.model-input2,.model-input1').removeClass('has-success').addClass('has-error');
							
							TMS.alert('the latter time should be later than the former','danger');
							flag = 1;
						}
						
						if (flag)
							return false
						TMS.getData({
							url: _WEB_SITE_ + '/alert/service/doAddAlertOff/' + _ACTIVE_GROUP_,
							type: 'post',
							contentType: 'application/json',
							data: getJson(),

						}, function(data) {
							if (data.statusCode !== undefined && data.statusCode != '200') {
								TMS.alert(data.message, 'danger')
							} else {
								TMS.alert(data.message, 'success');
								$('#select-time').modal('hide');
								$('#table').bootstrapTable('refresh');
							}
						});
						return false;
					}
			});

		function getJson() {
			var o = {};
			if ($('#yearly:visible').length != 0) {
				$target = $('#yearly');
				o.repeatType = $target.find('input[name="repeatType"]').val();
				o.offDate = $target.find('input[name="offDate"]').val();
				o.offStartTime = $target.find('input[name="offStartTime"]')
					.val();
				o.offEndTime = $target.find('input[name="offEndTime"]').val();
			} else if ($('#weekly:visible').length != 0) {
				$target = $('#weekly');
				var dayType = '';
				$('#weekly .weekly-ul li').each(function(index, el) {
					if ($(el).hasClass('weekly-active')) {
						dayType += $(el).attr('data-value') + ','
					}

				});
				if (dayType) {
					dayType = dayType.substring(0, dayType.length - 1);
				}
				o.repeatType = $target.find('input[name="repeatType"]').val();
				o.offDate = dayType;
				o.offStartTime = $target.find('input[name="offStartTime"]')
					.val();
				o.offEndTime = $target.find('input[name="offEndTime"]').val();
			} else if ($('#monthly:visible').length != 0) {
				$target = $('#monthly');
				o.repeatType = $target.find('input[name="repeatType"]').val();
				o.offDate = $target.find('input[name="offDate"]').val();
				o.offStartTime = $target.find('input[name="offStartTime"]')
					.val();
				o.offEndTime = $target.find('input[name="offEndTime"]').val();
			} else if ($('#daily:visible').length != 0) {
				$target = $('#daily');
				o.repeatType = $target.find('input[name="repeatType"]').val();
				o.offDate = 'Everyday';
				o.offStartTime = $target.find('input[name="offStartTime"]')
					.val();
				o.offEndTime = $target.find('input[name="offEndTime"]').val();
			} else if ($('#onetime:visible').length != 0) {
				$target = $('#onetime');
				o.repeatType = $target.find('input[name="repeatType"]').val();
				o.offDate = $target.find('input[name="offDate"]').val();
				o.offStartTime = $target.find('input[name="offStartTime"]')
					.val();
				o.offEndTime = $target.find('input[name="offEndTime"]').val();
			}
			return JSON.stringify(o);
		}

		// table
		var columns = [{
			field: 'type',
			title: 'Type',
			formatter: typeFormatter,

		}, {
			field: 'DETAIL',
			title: 'Detail',
			formatter: detailFormatter,

		}, {
			field: 'CreateTime/Date',
			title: 'Create Time/Date',
			formatter: createDateFormatter
		}];
		if (deletePer == '1') {
			columns.push({
				field: '',
				title: '',
				width: '45px',
				formatter: tableAction,

			});
		}
		TMS.bootTable('#table', {
			url: _WEB_SITE_ + '/alert/service/listAlertOff/' + _ACTIVE_GROUP_,
			type: 'post',
			contentType: 'application/json',
			columns: columns,
			queryParamsType: '',
			sidePagination: 'server',
			queryParams: function(params) { // 接口参数处理
				return params;
			},
			loadend : function() {
				if(_ACTIVE_GROUP_ == 1){
					$('.button-active').addClass('hidden');
				}else{
					$('.button-active').removeClass('hidden');
				}
			}

		});

		function typeFormatter(value, row, index) {
			if (row.repeatType == 0) {
				return 'One Time';
			} else if (row.repeatType == 1) {
				return 'Daily';
			} else if (row.repeatType == 2) {
				return 'Weekly';
			} else if (row.repeatType == 3) {
				return 'Monthly';
			} else {
				return 'Yearly';
			}

		}

		function createDateFormatter(value, row, index) {
			return TMS.changeTime(row.createDate);

		}

		function detailFormatter(value, row, index) {
			var month = {
				'01': 'January',
				'02': 'February',
				'03': 'March',
				'04': 'April',
				'05': 'May',
				'06': 'June',
				'07': 'July',
				'08': 'August',
				'09': 'September',
				'10': 'October',
				'11': 'November',
				'12': 'December'
			}

			if (row.repeatType == 0) { // onetime
				var date = row.offDate.split('-');

				var day = Number(date[2]);
				var year = date[0];
				return month[date[1]] + ' ' + day + ', ' + year + '  ' + '<span style="color:#9da4b6;">' + row.offStartTime + ' - ' + row.offEndTime + '</span>';
			} else if (row.repeatType == 1) { // daily
				return '<span style="color:#9da4b6;">' + row.offStartTime + ' - ' + row.offEndTime + '</span>';

			} else if (row.repeatType == 2) { // weekly
				var str = "";
				var eachDate = new Array('Sunday','Monday', 'Tuesday', 'Wednesday',
					'Thursday', 'Friday', 'Saturday');
				var number = row.offDate.split(",");
				for (i = 0; i < number.length; i++) {
					var each = number[i] - 1;
					str = str + eachDate[each] + "  ";
				}
				return str + ' ' + '<span style="color:#9da4b6;">' + row.offStartTime + ' - ' + row.offEndTime + '</span>';
			} else if (row.repeatType == 3) { // monthly
				var rowDate = row.offDate % 10;

				if (rowDate == 1) {
					return Number(row.offDate) + 'st' + ' ' + '<span style="color:#9da4b6;">' + row.offStartTime + ' - ' + row.offEndTime + '</span>';
				} else if (rowDate == 2) {
					return Number(row.offDate) + 'nd' + ' ' + '<span style="color:#9da4b6;">' + row.offStartTime + ' - ' + row.offEndTime + '</span>';
				} else {
					return Number(row.offDate) + 'th' + ' ' + '<span style="color:#9da4b6;">' + row.offStartTime + ' - ' + row.offEndTime + '</span>';
				}
			} else { // yearly
				var date = row.offDate.split('-');

				var day = Number(date[1]);
				return month[date[0]] + ' ' + day + ' ' + '<span style="color:#9da4b6;">' + row.offStartTime + ' - ' + row.offEndTime + '</span>';

			}

		}

		function tableAction(value, row, index) {
			return ['<div class="g-action" data-id="' + row.offId + '">',
				'<div class="g-action-btn">',
				'<i class="iconfont">&#xe60f;</i>', '</div>',
				'<ul class="g-action-menu ">', '<li class="J-remove">',
				'<a style="color:#ff7068">Delete</a>', '</li>', '</ul>',
				'</div>'
			].join('')
		}

		var offId = TMS.actionMenuEvent(function(e) {
			offId = $(e).parents('.g-action').attr('data-id');
			$('#remove-modal').modal('show');
		});
		$('#remove-modal .J-confirm-btn').on(
			'click',
			function() {
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				
				TMS.getData({
					url: _WEB_SITE_ + '/alert/service/deleteAlertOff/' + offId + '/' + _ACTIVE_GROUP_,
					type: 'post'
				}, function(data) {
					if (data.statusCode !== undefined && data.statusCode != '200') {
						TMS.alert(data.message, 'danger')
					} else {
						TMS.alert(data.message, 'success');
						$('#remove-modal').modal('hide');
						$('#table').bootstrapTable('refresh');
					}

				});

			})

	});
});