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
	var offset = new Date().getTimezoneOffset() / 60;
	var localTimezone = offset <= 0 ? ('UTC+' + Math.abs(offset)):('UTC-' + offset);
	var billingReport = {
		init: function() {
			var self = this;
			this.params = {};
			this.params.groupIds = groupId;
			self.renderTable();
			this.bindEvent();
			this.$table = $('#table');
		},
		renderTable: function() {
			var self = this;
			TMS.bootTable('#table', {
				url: _WEB_SITE_ + '/report/service/billingList/'+groupId,
				method: 'post',
				contentType: 'application/x-www-form-urlencoded',
				columns: [{
					field: 'Month',
					title: 'Month',
					sortable: false,
					cellStyle:{
                        css: {width: '30%'}
                    }
				}, {
					field: 'Connected Devices',
					title: 'Connected Devices',
					sortable: false,
					cellStyle:{
                        css: {width: '30%'}
                    }
				}, {
					field: 'Statement',
					title: 'Statement',
					sortable: false,
					formatter: self.downloadFormatter
				}],
				loadend: function(res) {
					if (res.totalCount && res.totalCount > 0) {
		                $('.table-records').removeClass('hide').html(res.totalCount + ' records found');
		            } else {
		                $('.table-records').addClass('hide');
		            }
				},
				queryParams: function(params) { // 接口参数处理
					for (key in self.params) {
						params[key] = self.params[key]
					}
					return params
				}
			});
		},
		resetTable: function() {
			var self = this;
			self.$table.bootstrapTable('refreshOptions', {
				queryParams: function(params) { // 接口参数处理
					
					for (key in self.params) {
						params[key] = self.params[key]
					}
					return params
				},
				pageNumber: 1 //重置页码为1
			})
		},
		downloadFormatter: function(value, row, index) {
			return [
				'<form action="' + _WEB_SITE_ + "/report/billingList/export/" + groupId  + '" method="post">',
				'<input type="hidden" name="month" value="' + row.Month +'">',
				'<input type="hidden" name="localTimezone" value="' + localTimezone +'">',
				'<button class="btn-download" type="submit">' + value + '</span>',
				'</form>'
			].join('');
		},
		bindEvent: function() {
			var self = this;
		}
	}
	$(function() {
		TMS.init();
		billingReport.init();
	})
});