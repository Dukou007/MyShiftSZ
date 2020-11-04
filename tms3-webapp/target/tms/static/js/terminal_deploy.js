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
	var tsns = $("#tsn").val();
	
	$(function() {
		TMS.init();
		TMS.setArea('#countryId', '#provinceId');
		var groupId = $("#gid").val();
		var tsns = $("#tsn").val();
	
		TMS.actionMenuEvent(); // action操作
		/* table current */
		TMS.bootTable('#table_current', {
			// url: '../json/terminal_current.json',
			loadend : function() {
				$('.monitor-panel').show();
			},
			columns : [{
				field : 'source',
				title : 'SOURCE'
			}, {
				field : 'operator',
				title : 'OPERATOR'
			}, {
				field : 'packageName',
				title : 'PACKAGE NAME'
			}, {
				field : 'packageVersion',
				title : 'PACKAGE VERSION'
			}, {
				field : 'parameterTimestamp',
				title : 'PARAMETER TIMESTAMP'

			}, {
				field : 'downloadPlan',
				title : 'DOWNLOAD PLAN'

			}, {
				field : 'downloadStatus',
				title : 'DOWNLOAD STATUS',
				formatter : operateDownloadStatus

			}, {
				field : 'downloadTime',
				title : 'DOWNLOAD TIME'
			}, {
				field : 'activationPlan',
				title : 'ACTIVATION PLAN',
			}, {
				field : 'activationStatus',
				title : 'ACTIVATION STATUS',
				formatter : operateActivationStatus
			}, {
				field : 'activationTime',
				title : 'ACTIVATION TIME',

			}, {
				field : 'scheduleStatus',
				title : 'SCHEDULE STATUS',
				formatter : operateScheduleStatus
			}, {
				field : 'updateTime',
				title : 'UPDATE TIME',
			}, {
				field : '',
				title : '',
				formatter : tableAction
			}]
		});

		function tableAction(value, row, index) { // 操作
			return [
					'<div class="g-action" data-id="' + row.id + '">',
					'<div class="g-action-btn">',
					'<i class="iconfont">&#xe60f;</i>',
					'</div>',
					'<ul class="g-action-menu ">',
					'<li><a id="" data-toggle="modal" data-target="#modal_terminal_cancel" style="color:#ff7068">',
					'Cancel',
					'</a></li>',
					'<li><a  id="" data-toggle="modal" data-target="#modal_terminal_delete" style="color:#ff7068">',
					'Delete', '</a></li>', '</ul>', '</div>'].join('')
		}

		function operateDownloadStatus(value, row, index) {
			if (row.downloadStatus) {
				return ['<span >Successful</span>'].join('');
			} else {
				return ['<span style="color:#ff7068">Failed</span>'].join('');
			}
		}

		function operateActivationStatus(value, row, index) {
			if (row.activationStatus) {
				return ['<span >Successful</span>'].join('');
			} else {
				return ['<span style="color:#ff7068">Failed</span>'].join('');
			}
		}

		function operateScheduleStatus(value, row, index) {
			if (row.scheduleStatus) {
				return ['<span >Successful</span>'].join('');
			} else {
				return ['<span style="color:#ff7068">Failed</span>'].join('');
			}
		}

		/* table history */
		TMS.bootTable('#table_history', {
			// url: '../json/terminal_history.json',
			// url: '../json/terminal_history.json',
			/*
			 * loadend: function() { $('.monitor-panel').show(); },
			 */
			columns : [{
				field : 'source',
				title : 'SOURCE'
			}, {
				field : 'operator',
				title : 'OPERATOR'
			}, {
				field : 'packageName',
				title : 'PACKAGE NAME'
			}, {
				field : 'packageVersion',
				title : 'PACKAGE VERSION'
			}, {
				field : 'parameterTimestamp',
				title : 'PARAMETER TIMESTAMP'

			}, {
				field : 'downloadPlan',
				title : 'DOWNLOAD PLAN'

			}, {
				field : 'downloadStatus',
				title : 'DOWNLOAD STATUS',
				formatter : operateDownloadStatus

			}, {
				field : 'downloadTime',
				title : 'DOWNLOAD TIME'
			}, {
				field : 'activationPlan',
				title : 'ACTIVATION PLAN',
			}, {
				field : 'activationStatus',
				title : 'ACTIVATION STATUS',
				formatter : operateActivationStatus
			}, {
				field : 'activationTime',
				title : 'ACTIVATION TIME',

			}, {
				field : 'scheduleStatus',
				title : 'SCHEDULE STATUS',
				formatter : operateScheduleStatus
			}, {
				field : 'updateTime',
				title : 'UPDATE TIME',
			}]
		});

	

		/* modal operate */
		/* remove terminal */
		$('#confirm_terminal_remove').click(function() {
			TMS.getData({
				url : _WEB_SITE_ + '/terminal/service/dismiss',
				type : 'get',
				dataType : 'json',
				data : {
					groupId : groupId,
					tsns : tsns
				},
				followTo : _WEB_SITE_ + "/terminal/list/" + groupId
			});
		});

		/* cancel task */
		$('.confirm_terminal_cancel').click(function() {

		});

		/* delete task */
		$('.confirm_terminal_delete').click(function() {

		});

	});
});
