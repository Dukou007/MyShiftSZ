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
	var hasData = false;
	var eventList = {
		init : function() {
			var self = this;
			self.bindEvent();
			self.$table = $('#event-table');
			this.activeTime = 'Last Week';
			this.activeEvent = 'All';
			this.fuzzyCondition = '';
			this.lastPaginationStatus = 1;

			// initialize the table,set the default param
			var o = self.switchTime('Last Week');
			self.renderTable(o.time, 'All');
		},
		switchTime : function(activeTime) {
			var time = '', self = this;
			switch (activeTime) {
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
				time = 3650;
				self.activeTime = 'All';
				break;
			default:
				time = 7;
			}
			return {
				time : time,
				activeTime : self.activeTime
			}

		},
		renderTable : function(defaultTime, defaultEvent) {
			var self = this;
			this.pageCurrentIndex = 1;
			window.initialTable=false;
			this.pageCount;
		
			TMS.bootTable('#event-table', {
				url : _WEB_SITE_ + '/events/service/allEvents/'
						+ _ACTIVE_GROUP_ + '/' + defaultEvent + '/'
						+ defaultTime,
				columns : [ {
					field : 'EVENTSOURCE',
					title : 'Source',
					formatter : detailFormatter,
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
				loadend : function(res) {
					window.initialTable=false;
					$('.event-export').removeClass('hide');
					$('.event-export .btn').removeClass("disabled");
					// 加载完执行code
                    if (res.totalCount && res.totalCount > 0) {
                    	hasData = true;
							$('.table-records').removeClass('hide').html(
									res.totalCount + ' records found');
					} else {
						hasData = false;
						$('.table-records').addClass('hide');
		                $('.event-export .btn').addClass("disabled");
					}
				},
				queryParams : function(params) { // 接口参数处理
					params.activeEvent = self.activeEvent;
					params.activeTime = self.switchTime(self.activeTime).time;
					params.fuzzyCondition = self.fuzzyCondition;
					self.pageCurrentIndex = params.pageIndex;
					self.pageCurrentIndex!=1?params.totalCount=self.totalCount:null;
					var pageIndex = params.pageIndex;
					var pageCount = self.pageCount;
					var lastPaginationStatus = self.lastPaginationStatus;
					var rowId, rowTime;
					if (pageIndex != pageCount && pageIndex != 1) {
						// after
						if (pageIndex > lastPaginationStatus) {
							var rowId = $(
									'#event-table tbody tr:last td:last span')
									.attr('data-rowId');
							var rowTime = $(
									'#event-table tbody tr:last td:last span')
									.attr('data-rowtime');
							params.eventId = rowId;
							params.eventTime = rowTime;
							params.paginationStatus = 'nextPage';
						} else if (pageIndex < lastPaginationStatus) {
							// before
							var rowId = $(
									'#event-table tbody tr:first td:last span')
									.attr('data-rowId');
							var rowTime = $(
									'#event-table tbody tr:first td:last span')
									.attr('data-rowtime');
							params.eventId = rowId;
							params.eventTime = rowTime;
							params.paginationStatus = 'previousPage';
						}

					}
					if (pageIndex == 1) {
						params.paginationStatus = 'firstPage';

					} else if (pageIndex == pageCount) {
						params.paginationStatus = 'lastPage';
					}
					pageIndex == pageCount&&pageIndex!=1?window.isLast=true:window.isLast=false;
					self.lastPaginationStatus = pageIndex;
					return params;

				},
				pagination : true,
				responseHandler : function(res) { // 接口数据处理
					res=TMS.dataEncodeOut(res);
					res.totalCount?self.totalCount=res.totalCount:null;
					self.pageCount = res.pageCount;
					window.isLast?self.lastPaginationStatus=res.pageCount:null;
					this.data = res.items;
					res.total = res.totalCount;
					self.pageCount = res.pageCount;
					return res;

				}
			});

			function detailFormatter(value, row, index) {
				if (row.TYPE == 'TERMINAL') {
					return [
							'<a  href="'
									+ _WEB_SITE_ + "/terminal/profile/"
									+ groupId + "/" + row.EVENTSOURCE
									+ '" data-eventid="',
							row.EVENTID + '" data-eventtime="' + row.EVENTTIME
									+ '">', value, '</a>' ].join('');
				} else {
					return [
							'<span title="' + row.NAMEPATH + '"  data-eventtime="' + row.EVENTTIME
									+ '">', value, '</span>' ].join('');
				}

			}

			function severityFormatter(value, row, index) {
				if (row.EVENTSEVERITY == 1) {
					return "Informational";
				} else if (row.EVENTSEVERITY == 2) {
					return "Warning";
				} else if (row.EVENTSEVERITY == 3) {
					return "Critical"
				}
			}

			function timeFormatter(value, row, index) {
				return TMS.changeTime(row.EVENTTIME, row.EVENTID);
			}
		},
		reSetTable : function(o) {
			var self = this;
			this.pageCount;
			self.pageCurrentIndex = 1;
			self.$table.bootstrapTable('refreshOptions', {
				url : _WEB_SITE_ + '/events/service/allEvents/'
						+ _ACTIVE_GROUP_ + '/' + o.activeEvent + '/'
						+ o.activeTime,
				queryParams : function(params) { // 接口参数处理
					self.pageCurrentIndex = params.pageIndex;
					params.fuzzyCondition = self.fuzzyCondition;
					params.activeEvent = o.activeEvent;
					params.activeTime = o.activeTime;
					self.pageCurrentIndex!=1?params.totalCount=self.totalCount:null;
					var pageIndex = params.pageIndex;
					var pageCount = self.pageCount;
					var lastPaginationStatus = self.lastPaginationStatus;
					var rowId, rowTime;
					if (pageIndex != pageCount && pageIndex != 1) {
						// after
						if (pageIndex > lastPaginationStatus) {
							var rowId = $(
									'#event-table tbody tr:last td:last span')
									.attr('data-rowId');
							var rowTime = $(
									'#event-table tbody tr:last td:last span')
									.attr('data-rowtime');
							params.eventId = rowId;
							params.eventTime = rowTime;
							params.paginationStatus = 'nextPage';
						} else if (pageIndex < lastPaginationStatus) {
							// before
							var rowId = $(
									'#event-table tbody tr:first td:last span')
									.attr('data-rowId');
							var rowTime = $(
									'#event-table tbody tr:first td:last span')
									.attr('data-rowtime');
							params.eventId = rowId;
							params.eventTime = rowTime;
							params.paginationStatus = 'previousPage';
						}

					}
					if (pageIndex == 1) {
						params.paginationStatus = 'firstPage';

					} else if (pageIndex == pageCount) {
						params.paginationStatus = 'lastPage';

					}
					pageIndex == pageCount&&pageIndex!=1?window.isLast=true:window.isLast=false;
					self.lastPaginationStatus = pageIndex;
					return params
				},
				responseHandler : function(res) { // 接口数据处理
					res=TMS.dataEncodeOut(res);
					res.totalCount?self.totalCount=res.totalCount:null;
					self.pageCount = res.pageCount;
					window.isLast?self.lastPaginationStatus=res.pageCount:null;
					
					this.data = res.items;
					res.total = res.totalCount;
					self.pageCount = res.pageCount;
					return res;

				},
				pageNumber : 1
			// 重置页码为1
			});

			function timeFormatter(value, row, index) {
				return TMS.changeTime(row.EVENTTIME, row.EVENTID);
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
		},

		bindEvent : function() {
			var $body = $(".g-container"), self = this, o = {};

			$body.on('click', '#timeTab li', function() {

				var targetTime = $(this).attr('data-type');

				if (self.activeTime != targetTime) {
					$('.g-searchInput').val('');
					var currentTimeObj = self.switchTime(targetTime);
					self.activeTime = targetTime;
					o.activeTime = currentTimeObj.time;
					o.activeEvent = self.activeEvent;
					self.fuzzyCondition = '';
					self.reSetTable(o);

					$('#timeTab').siblings('.dropdown-toggle').html(
							targetTime + '<span class="caret"></span>');

				}
			});
			$body.on('click', '#eventTab li', function() {
				self.pageCount = '';
				$('.g-searchInput').val('');
				if ($(this).hasClass('active'))
					return false;

				$('#eventTab li').removeClass('active');
				$(this).addClass('active');
				self.fuzzyCondition = '';
				var currentTimeObj = self.switchTime(self.activeTime);
				var targetEvent = $(this).attr('data-type');
				self.activeEvent = targetEvent;
				o.activeTime = currentTimeObj.time;
				o.activeEvent = self.activeEvent;
				o.showTime = self.activeTime;
				self.reSetTable(o);

			});
			$('.g-searchGo').click(function() {
				var keyWord = $('.g-searchInput').val();
			/*	if (keyWord == self.fuzzyCondition) {
					return false;
				}*/
				self.fuzzyCondition = keyWord;
				o.activeTime = self.switchTime(self.activeTime).time;
				o.activeEvent = self.activeEvent;
				o.showTime = self.activeTime;

				self.reSetTable(o);

			});
			$('.g-searchInput').keyup(function(e) {
				if (e.keyCode == 13) {
					var keyWord = $(this).val();
					if (keyWord == self.fuzzyCondition) {
						return false
					}
					self.fuzzyCondition = keyWord;
					o.activeTime = self.switchTime(self.activeTime).time;
					o.activeEvent = self.activeEvent;
					self.reSetTable(o);
				}
			});
			$('#exportExcel').click(function() {
				if (hasData) {
					TMS.getData({
	                    url: _WEB_SITE_ + '/report/eventlist/service/isexport/' + groupId,
	                    type: 'get',
	                    data: {
	                    	activeEvent: self.activeEvent,
	                    	activeTime: self.switchTime(self.activeTime).time,
	                    	fuzzyCondition: self.fuzzyCondition
	                    }
	                }, function(data) {
	                	if (data.statusCode != undefined && data.statusCode != '200') {
	    		            TMS.alert(data.message, 'danger');
	    		        } else {
	    		        	var downloadUrl = _WEB_SITE_ + '/report/eventlist/service/export/' + groupId + '?activeEvent=' + self.activeEvent
	    		        	+ '&activeTime=' + self.switchTime(self.activeTime).time + '&fuzzyCondition=' + self.fuzzyCondition;
	    		        	window.location.href = downloadUrl;
	    		        }
	                });
				}
            });
		}
	};

	$(function() {
		TMS.init();
		eventList.init();
	})
});
