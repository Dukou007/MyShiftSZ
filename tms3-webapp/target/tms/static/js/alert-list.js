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
	var title = '<div class="g-panel-text">Alerts List</div>';
	var alertList = {
		init : function() {
			var self = this;
			self.renderTable();
			self.bindEvent();
		},
		renderTable : function() {
			TMS.bootTable('#alert-table', {
				url : _WEB_SITE_ + '/events/service/alertEvents/'
						+ _ACTIVE_GROUP_,
				title : title,
				columns : [ {
					field : 'GROUPNAME',
					title : 'Group',
					sortable : false,
					formatter : groupNameFormatter
				}, {
					field : 'ALERTSEVERITY',
					title : 'Severity',
					sortable : false,
					formatter : severityFormatter
				}, {
					field : 'ALERTMSG',
					title : 'Message',
					sortable : false
				}, {
					field : 'ALERTTIME',
					title : 'Time/Date',
					sortable : false,
					formatter : alertTimeFormatter
				} ],
				search : true,
				loadend : function(res) {
					// 加载完执行code
					if (res.totalCount && res.totalCount > 0) {
						$('.table-records').removeClass('hide').html(
								res.totalCount + ' records found');
					} else {
						$('.table-records').addClass('hide');
					}
				}

			/*
			 * searchType : { display : true, field : 'serachType', items : [{
			 * val : 'alertSeverity', title : 'Severity', status : 1 }, { val :
			 * 'eventId', title : 'SN/TID', status : 0 }] }
			 */
			});
			function groupNameFormatter(value, row, index) {
				return '<span title="' + row.NAMEPATH + '">' + value
						+ '</span>'
			}
			function severityFormatter(value, row, index) {
				if (row.ALERTSEVERITY == 1) {
					return "Info";
				} else if (row.ALERTSEVERITY == 2) {
					return "Warning";
				} else if (row.ALERTSEVERITY == 3) {
					return "Critical"
				}
			}
			function alertTimeFormatter(value, row, index) {
				return TMS.changeTime(row.ALERTTIME);
			}
		},
		bindEvent : function() {
			$('.search input').attr('placeholder', 'Group,Message');
		}
	}
	$(function() {
		TMS.init();
		alertList.init();

	})
});