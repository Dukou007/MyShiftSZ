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
	var groupId = _ACTIVE_GROUP_;
	var dashStaticList = {
		init : function() {
			var self = this;
			self.renderTable();

		},
		renderTable : function() {
			var title = '<div class="g-panel-text">Statistics List</div>'
			TMS.bootTable('#table', {
				url : _WEB_SITE_ + '/dashboard/service/statistics/'
						+ _ACTIVE_GROUP_,
				title : title,
				type : 'post',
				contentType : 'application/json',
				columns : [ {
					field : 'terminalId',
					title : 'Terminal SN',
					sortable : false,
					formatter : sourceOperate
				}, {
					field : 'USAGENAME',
					title : 'Usage Name',
					sortable : false
				}, {
					field : '',
					title : 'Period',
					sortable : false,
					formatter : periodOperate
				}, {
					field : 'TERMINALMODEL',
					title : 'Terminal Type',
					sortable : false
				}, {
					field : '',
					title : 'Error/Normal Counts',
					sortable : false,
					formatter : countsOperate
				} ],
				loadend : function(res) {
					$('.search input').attr('placeholder',
							'Terminal SN,Usage Name');
					
						//加载完执行code
						if (res.totalCount && res.totalCount > 0) {
				                $('.table-records').removeClass('hide').html(res.totalCount + ' records found');
			            } else {
			                $('.table-records').addClass('hide');
			            }
					
				},
				search : true,

			});
			function sourceOperate(value, row, index) {
				return [
					'<a href="' + _WEB_SITE_ + "/terminal/profile/" + groupId + "/" + row.TERMINALSN + '">',
					row.TERMINALSN,
					'</a>'
				].join('');
				
			}
			function periodOperate(value, row, index) {
				return TMS.changeTime(row.STARTTIME) + " - "
						+ TMS.changeTime(row.ENDTIME);
			}
			function countsOperate(value, row, index) {
				var normalCount = row.TOTALCOUNT - row.ERRORCOUNT;
				return row.ERRORCOUNT + " / " + normalCount;
			}
		}
	}
	$(function() {
		TMS.init();
		dashStaticList.init();

	})
});