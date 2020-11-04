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
	$(function() {
		TMS.init();
	
		var editPer = $("#permission-edit").val();
		TMS.bootTable('#table', {
			url : _WEB_SITE_ + '/alert/service/listCondition/' + _ACTIVE_GROUP_,
			columns : [ {
				field : 'alertItem',
				title : 'Rules'

			}, {
				field : '',
				title : 'Severity',
				formatter : severityOperate,
				width : '150px'

			}, {
				field : 'alertThreshold',
				title : 'Threshold',
				formatter : threshholdOperate,
				width : '120px'

			}, {
				field : 'alertMessage',
				title : 'Message',
				formatter : messageOperate

			}, {
				field : 'notification',
				title : 'Notification Mode',
				formatter : notificationOperate,
				width : '200px'
			} ],
			loadend : function() {
				$('table select').prop('disabled', 'true');
				$('table input').prop('disabled', 'true');
				$('table .SE').click(function() {
					return false;
				})
				if(_ACTIVE_GROUP_ == 1){
					$('.ac-block1').addClass('hidden');
				}else{
					$('.ac-block1').removeClass('hidden');
				}
			
				
			}

		});
		function severityOperate(value, row, index) {
			var cri = "Critical";
			var warn = "Warning";
			var info = "Info";
			var k = "/";
			if (row.alertSeverity == "0") {
				return [ '<select style=\'width:132px\' class=\'form-control severitySelect\' name=\'severity\'><option selected value=\'0\'>'
						+ k
						+ '</option><option value=\'1\'>Info</option><option value=\'2\'>Warning</option><option value=\'3\'>Critical</option><select>' ]
						.join('');
			}
			if (row.alertSeverity == "1") {
				return [ '<select style=\'width:132px\' class=\'form-control severitySelect\' name=\'severity\'><option value=\'0\'>/</option><option selected value=\'1\'>'
						+ info
						+ '</option><option value=\'2\'>Warning</option><option value=\'3\'>Critical</option><select>' ]
						.join('');

			}
			if (row.alertSeverity == "2") {
				return [ '<select style=\'width:132px\' class=\'form-control severitySelect\' name=\'severity\'><option value=\'0\'>/</option><option value=\'1\'>Info</option><option selected value=\'2\'>'
						+ warn
						+ '</option><option value=\'3\'>Critical</option><select>' ]
						.join('');

			}
			if (row.alertSeverity == "3") {
				return [ '<select style=\'width:132px\' class=\'form-control severitySelect\' name=\'severity\'><option value=\'0\'>/</option><option value=\'1\'>Info</option><option value=\'2\'>Warning</option><option selected value=\'3\'>'
						+ cri + '</option><select>' ].join('');

			}
		}

		function threshholdOperate(value, row, index) {
			if (row.alertSeverity == "0") {
				return [ '<div class="threshold form-edit-group"><span class="alert-condition-more">></span><span class="alert-condition-percent">%</span>'
						+ '<input type=\'text\' name=\'threshold'
						+ row.condId
						+ '\' class="thresholding form-control required percentegeonly" data-thres="'
						+ row.condId
						+ '" value="'
						+ row.alertThreshold
						+ '" disabled>'
						+ '<input type=\'text\' value="'
						+ row.condId
						+ '" name=\'condId\' hidden>'
						+ '<input type=\'text\' value="'
						+ row.alertItem
						+ '" name=\'alertItem\'hidden>'
						+ '<input type=\'text\' name=\'message\' value="'
						+ row.alertMessage + '" hidden>' + '</div>' ].join('');

			} else {
				return [ '<div class="threshold form-edit-group"><span class="alert-condition-more">></span><span class="alert-condition-percent">%</span>'
						+ '<input type=\'text\' name=\'threshold'
						+ row.condId
						+ '\'  class="thresholding form-control required percentegeonly" data-thres="'
						+ row.condId
						+ '" value="'
						+ row.alertThreshold
						+ '">'
						+ '<input type=\'text\' value="'
						+ row.condId
						+ '" name=\'condId\' hidden>'
						+ '<input type=\'text\' value="'
						+ row.alertItem
						+ '" name=\'alertItem\' hidden>'
						+ '<input type=\'text\' name=\'message\'  value="'
						+ row.alertMessage + '" hidden>' + '</div>' ].join('');
			}

		}
		function messageOperate(value, row, index) {
			return [ '<span class="alertCondition-message' + row.condId + ' ">'
					+ row.alertMessage + '</span>' ].join('');
		}
		function notificationOperate(value, row, index) {
			var scbSms = 'SMS';
			var scbEmail = 'Email';
			var sms = row.scbSms;
			var email = row.scbEmail;

			if (row.alertSeverity == "0") {
				return [ '<div class="SE sms"><label class="alertDisabled alertDisabled-init">'
						+ scbSms
						+ '</label></div><div class="SE email"><label class="alertDisabled alertDisabled-init">'
						+ scbEmail + '</label></div>' ].join('');
			} else {
				if (row.scbSms != '0' && row.scbEmail == '0') {
					return [ '<div class="SE sms"><label class="alertConNotify alertConactive alertDisabled-init">'
							+ scbSms
							+ '</label></div><div class="SE email"><label class="alertConNotify alertDisabled-init">'
							+ scbEmail + '</label></div>' ].join('');
				}
				if (row.scbEmail != '0' && row.scbSms == '0') {
					return [ '<div class="SE sms"><label class="alertConNotify alertDisabled-init">'
							+ scbSms
							+ '</label></div><div class="SE email"><label class="alertConNotify alertConactive alertDisabled-init">'
							+ scbEmail + '</label></div>' ].join('');
				}
				if (row.scbEmail == '1' && row.scbSms == '1') {
					return [ '<div class="SE sms"><label class="alertConNotify alertConactive alertDisabled-init">'
							+ scbSms
							+ '</label></div><div class="SE email"><label class="alertConNotify alertConactive alertDisabled-init">'
							+ scbEmail + '</label></div>' ].join('');
				}
				return [ '<div class="SE sms"><label class="alertConNotify alertDisabled-init">'
						+ scbSms
						+ '</label></div><div class="SE email"><label class="alertConNotify alertDisabled-init">'
						+ scbEmail + '</label></div>' ].join('');

			}

		}
		var userRolename = $('.user-rolename').text();
		$(function() {
			// set disabled
			
			$('.btn1-edit').click(
					function() {
						$('.ac-block2').show();
						$('.ac-block1').hide();
						//add cursor:pointer style to select file
						$('select').css('cursor','pointer');
						if (editPer!='1') {
							$('table .SE').unbind('click');
							$('table .SE label').removeClass(
									'alertDisabled-init');
							return false;
						}

						$('table select').removeAttr('disabled');

						var inputArr = $('table input');
						for (var i = 0; i < inputArr.length; i++) {
							var elem = $(inputArr[i]).parents('td').prev('td')
									.find('select option:selected');
							var elemVal = elem.val();

							if (elemVal != 0) {
								$(inputArr[i]).removeAttr('disabled');
							}
						}

						$('table .SE').unbind('click');
						$('table .SE label').removeClass('alertDisabled-init');
						return false;
					});

			$('.btn2-back').click(function() {
				$('.ac-block1').show();
				$('.ac-block2').hide();
				$('table').bootstrapTable('refresh');

			});
		})

		$("table").on(
				'change',
				'.severitySelect',
				function() {
					var elem = $(this).find('option:selected').val();
					if (elem == 0) {
						$(this).parent().next()
								.find('.threshold .thresholding').attr(
										'disabled', 'true');
						$(this).parent().next().next().next().find('.SE label')
								.removeClass('alertConactive alertConNotify')
								.addClass('alertDisabled');

					} else {
						$(this).parent().next()
								.find('.threshold .thresholding').removeAttr(
										'disabled');
						$(this).parent().next().next().next().find('.SE label')
								.removeClass('alertDisabled').addClass(
										'alertConNotify');
					}

				})
		$("table").on('click', '.alertConNotify', function() {
			$(this).toggleClass('alertConactive');

		});
		// threshold input blur
		$("table").on(
				'blur',
				'.thresholding',
				function() {
					var str = $(this).val().trim(), thres = $(this).attr(
							'data-thres');
					if (str.length > 0) {
						var n = Number(str);

						if (!isNaN(n) && n !== 0) {
							var mes = $('.alertCondition-message' + thres)
									.text().replace(/[0-9]+(?=[^0-9]*$)/, n);
							$('.alertCondition-message' + thres).text(mes);
							$(this).siblings('input:last').val(mes);

						}
					}

				});

		// submit table form

		$("#editCondForm").validate(
				{

					submitHandler : function(form) {
						TMS.getData({
							url : _WEB_SITE_ + '/alert/service/editCondition/'
									+ _ACTIVE_GROUP_,
							type : 'post',
							contentType : 'application/json',
							data : getJson(),
							followTo : _WEB_SITE_ + '/alert/alertCondition/' + _ACTIVE_GROUP_,
						})/*
							 * , function(data) { if (data != 'fail') { if
							 * (!data.statusCode || data.statusCode &&
							 * data.statusCode == 200) { TMS.alert(data.message,
							 * "success"); $('table').bootstrapTable('refresh');
							 * $('.ac-block1').show(); $('.ac-block2').hide(); }
							 * else { TMS.alert(data.message, 'danger'); } } );
							 * return false;
							 */
					}
				});

		function getJson() {
			var jsonArr = [];
			$('#table tbody tr').each(
					function(index, el) {
						var o = {};
						o.condId = $(el).find('input[name="condId"]').val();
						o.alertSeverity = $(el).find('select[name="severity"]')
								.val();
						o.alertThreshold = $(el).find('.thresholding').val();
						o.scbEmail = $(el).find('.email>label').hasClass(
								'alertConactive');
						if (o.scbEmail) {
							o.scbEmail = '1';
						} else {
							o.scbEmail = '0';
						}
						o.scbSms = $(el).find('.sms>label').hasClass(
								'alertConactive');
						if (o.scbSms) {
							o.scbSms = '1';
						} else {
							o.scbSms = '0';
						}
						jsonArr.push(o);
					});
			return JSON.stringify(jsonArr);
		}
	})

})
