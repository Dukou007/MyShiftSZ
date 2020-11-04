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
	var tsn = $("#tsn").val();
	var groupId = _ACTIVE_GROUP_;
	var hasData = false;
	var terminalList = {
		init : function() {
			var self = this;
			self.activeTime = 'Last Week';
			self.$table = $('#alert-table');
			self.fuzzyCondition = '';
			self.bindEvent();
			self.renderTable(self.switchTime(self.activeTime));

		},
		switchTime : function(currentTime) {
			var time = '', self = this;
			switch (currentTime) {
			case 'Last 1 Day':
				time = 1;
				self.activeTime = 'Last 1 Day';
				break;
			case 'Last 3 Days':
				time = 3;
				self.activeTime = 'Last 3 Days';
				break;
			case 'Last Week':
				time = 7;
				self.activeTime = 'Last Week';
				break;
			case 'Last Month':
				time = 30;
				self.activeTime = 'Last Month';
				break;
			case 'All':
				time = 36500;
				self.activeTime = 'All';
				break;
			default:
				time = 7;
				self.activeTime = 'Last Week';
			}
			return time;
		},
		renderTable : function(defaultTime) {
			TMS.bootTable('#alert-table', {
				url : _WEB_SITE_ + '/events/service/terminalEvents/' + tsn
						+ '/' + defaultTime,
				columns : [ {
					field : 'EVENTSOURCE',
					title : 'Source',
					formatter: detailFormatter,
					sortable : false
				}, {
					field : 'EVENTSEVERITY',
					title : 'Severity',
					sortable : false,
					formatter : severityFormatter
				}, {
					field : 'EVENTMSG',
					title : 'Message',
					sortable : false
				}, {
					field : 'EVENTTIME',
					title : 'Event Time/Date',
					sortable : false,
					formatter : timeFormatter
				} ],
				pagination : true,
				loadend: function(res) {
					$('.event-export').removeClass('hide');
					$('.event-export .btn').removeClass("disabled");
					//加载完执行code
					if (res.totalCount && res.totalCount > 0) {
						hasData = true;
			                $('.table-records').removeClass('hide').html(res.totalCount + ' records found');
		            }
		            else{
		            	hasData = false;
		                $('.table-records').addClass('hide');
		                $('.event-export .btn').addClass("disabled");
		            }
				}

			/*
			 * searchType : { display : true, field : 'serachType', items : [{
			 * val : 'alertSeverity', title : 'Severity', status : 1 }, { val :
			 * 'eventId', title : 'SN/TID', status : 0 }] }
			 */
			});
			function detailFormatter(value, row, index) {
				return [
					'<a href="' + _WEB_SITE_ + "/terminal/profile/" + groupId + "/" + row.EVENTSOURCE + '">',
					value,
					'</a>'
				].join('');
			}
			
			function severityFormatter(value, row, index) {
				if (row.EVENTSEVERITY == 1) {
					return "Information";
				} else if (row.EVENTSEVERITY == 2) {
					return "Warning";
				} else if (row.EVENTSEVERITY == 3) {
					return "Critical"
				}
			}

			function timeFormatter(value, row, index) {
				return TMS.changeTime(row.EVENTTIME);
			}
		},
		reSetTable : function(currentTime) {
			var self = this;
			self.$table.bootstrapTable('refreshOptions', {
				url : _WEB_SITE_ + '/events/service/terminalEvents/' + tsn
						+ '/' + currentTime,
				queryParams : function(params) { // 接口参数处理
					params.fuzzyCondition = self.fuzzyCondition;
					params.activeTime = currentTime;
					return params
				},
				pageNumber : 1
			// 重置页码为1
			});

			function timeFormatter(value, row, index) {
				return TMS.changeTime(row.eventTime);
			}

			function severityFormatter(value, row, index) {
				if (row.eventSeverity == 1) {
					return "Information";
				} else if (row.eventSeverity == 2) {
					return "Warning";
				} else if (row.eventSeverity == 3) {
					return "Critical"
				}
			}
		},
		bindEvent : function() {
			var $body = $(".g-container"), self = this;
			$body.on('click', '#timeTab li', function() {
				$('.g-searchInput').val('');
				var targetTime = $(this).attr('data-type');
				if (self.activeTime != targetTime) {
					self.activeTime = targetTime;
					var currentTime = self.switchTime(targetTime);
					self.fuzzyCondition = '';
					self.reSetTable(currentTime);
					$('#timeTab').siblings('.dropdown-toggle').html(
							targetTime + '<span class="caret"></span>');

				}
			});
			$body.on('click', '.g-searchGo', function() {
				var targetVal = $('.g-searchInput').val();
				/*if (targetVal != self.fuzzyCondition) {
					self.fuzzyCondition = targetVal;
					var currentTime = self.switchTime(self.activeTime);
					self.reSetTable(currentTime);
					return false;
				}*/
				
					self.fuzzyCondition = targetVal;
					var currentTime = self.switchTime(self.activeTime);
					self.reSetTable(currentTime);
					return false;
				
			});
			$('.g-searchInput').keyup(function(e) {
				if (e.keyCode == 13) {
					var keyWord = $(this).val();
					if (keyWord == self.fuzzyCondition) {
						return false
					}
					self.fuzzyCondition = keyWord;
					activeTime = self.switchTime(self.activeTime);

					self.reSetTable(activeTime);
				}
			});
			$('#exportExcel').click(function() {
				if (hasData) {
					TMS.getData({
	                    url: _WEB_SITE_ + '/report/eventlist/service/isexport/' + groupId,
	                    type: 'get',
	                    data: {
	                    	terminalId: tsn,
	                    	activeTime: self.switchTime(self.activeTime),
	                    	fuzzyCondition: self.fuzzyCondition
	                    }
	                }, function(data) {
	                	if (data.statusCode != undefined && data.statusCode != '200') {
	    		            TMS.alert(data.message, 'danger');
	    		        } else {
	    		        	var downloadUrl = _WEB_SITE_ + '/report/eventlist/service/export/' + groupId + '?terminalId=' + tsn
	    		        	+ '&activeTime=' + self.switchTime(self.activeTime) + '&fuzzyCondition=' + self.fuzzyCondition;
	    		        	window.location.href = downloadUrl;
	    		        }
	                });
				}
            });
		}
	}

	$(function() {
		TMS.init();
		terminalList.init();

	})
});