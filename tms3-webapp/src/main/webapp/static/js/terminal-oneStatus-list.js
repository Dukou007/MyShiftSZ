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
	var refreshTime = 180000;
	var groupId = $("#gid").val();
	var itemName = $("#itemName").val();
	var itemStatus = $("#itemStatus").val();
	var treeDepth = $("#treeDepth").val();
	var tsn = $("#tsn").val();
	require('validate-methods');
	var viewPer = $('#per-terminal-view').val();
	var terminalId;
	var terminalList = {
		init : function() {
			var self = this;
			if(itemStatus.indexOf('Online') != -1 || itemStatus.indexOf('Offline') != -1){
				var columnsline = {
					field : '',
					title : 'Since Time',
					formatter : self.sinceTimeFormatter,
					sortable : false
				}
				
			} else {
				var columnsline = {
						field : '',
						title : 'Online/Offline',
						formatter : self.onlineFormatter,
						sortable : false
					}
			}
			var columns = [ {
				field : 'tsn',
				title : 'Terminal SN',
				sortable : false,
				formatter : self.snFormatter
			}, {
				field : 'status',
				title : 'Status',
				sortable : false,
				formatter : self.statusFormatter
			}, {
				field : 'model.name',
				title : 'Terminal Type',
				sortable : false
			},columnsline ];
			var rightBtn = '';
			self.$table = $('#table');
			var title = itemName + ' ' + itemStatus.split(',')[1];
			if ((itemName == 'Tampers' && itemStatus.split(',')[0] != '3')) {
				title = itemStatus.split(',')[1];
			} else if (itemName == "Offline") {
				title = itemStatus.split(',')[1];
			}
			if(itemStatus.indexOf('Online') != -1 || itemStatus.indexOf('Offline') != -1){
				columns.push({
					field : 'ts.lastSourceIP',
					title : 'Source IP',
					sortable : false
				})
				
			}
			setInterval(function() {
				$('#table').bootstrapTable('refresh', _WEB_SITE_ + '/dashboard/service/oneStatus/' + itemName+ '/' + itemStatus)
			}, refreshTime);
			TMS.bootTable('#table', {
				url : _WEB_SITE_ + '/dashboard/service/oneStatus/' + itemName
						+ '/' + itemStatus,
				type : 'post',
				title : '<a class="g-back-button iconfont" href="javascript:history.go(-1)" title="Return to previous page"></a>Terminals ' + title,
				method : 'post',
				contentType : 'application/x-www-form-urlencoded',

				queryParams : function(params) { // 接口参数处理
					params.groupId = groupId;
					return params
				},

				columns : columns,
				search : true,
				rightBtn : rightBtn,
				loadend : function(res) {
					//加载完执行code
					if (res.totalCount && res.totalCount > 0) {
			                $('.table-records').removeClass('hide').html(res.totalCount + ' records found');
		            } else {
		                $('.table-records').addClass('hide');
		            }
				}
			});
	
		},
		snFormatter : function(value, row, index) {
			if (viewPer == '1') {
				return [
						'<a href="' + _WEB_SITE_ + "/terminal/profile/"
								+ groupId + "/" + row.tsn + '">', value, '</a>' ]
						.join('');
			} else {
				return [ value ].join('');
			}
		},
		sinceTimeFormatter: function(value, row, index) {
			var sinceTime;
			
			if (row['ts.isOnline'] == null || row['ts.isOnline'] == 2) {
				sinceTime=row['ts.offlineSince']
			}
			if (row['ts.isOnline'] == 1) {
				sinceTime=row['ts.onlineSince']
			}
			
			return TMS.changeTime(sinceTime);
		},
		onlineFormatter : function(value, row, index) {
			var online;
			if (row['ts.isOnline'] == null || row['ts.isOnline'] == 2) {
				online = 'Offline';
			}
			if (row['ts.isOnline'] == 1) {
				online = 'Online';
			}
			return online;
		},
		statusFormatter : function(value, row, index) {
			var status;
			if (!row.status) {
				status = 'Deactivated';
			} else {
				status = 'Active';
			}
			return status;
		}
	}

	$(function() {
		TMS.init();
		terminalList.init();
	})
});