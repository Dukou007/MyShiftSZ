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
	var laydate = require('date-picker');
	var tsn = $("#tsn").val();
	var itemName = $("#itemName").val();
	var itemStatus = $("#itemStatus").val();
	var terminalDownload = {
		init : function() {
			var self = this;
			this.params = {};
			this.params.groupIds = groupId;
			self.renderTable();
			this.bindEvent();
			this.$table = $('#table');
			this.creatTree();
			this.isFirst = true;
		},

		initTimeSelect : function(el) {
			laydate({
				elem : el,
				format : 'YYYY-MM-DD hh:mm:ss',
				istime : true,
				choose : function(d, e) {
					$('.report-datetime').removeClass('has-error');
				}
			});
		},
		creatTree : function() {
			var self = this;
			this.Tree = new TMS.groupTree('#add-tree', {
				isModal : true,
				nowGroup : false,
				multiselect : true,
				groupPanel : { // group 选择框
					display : true,
					titleShow : true
				}
			});
		},
		renderTable : function() {
			var self = this;
			var URL = '';
			URL = _WEB_SITE_ + '/dashboard/service/terminalDownload/'
					+ itemName + '/' + itemStatus + '/' + groupId;
			setInterval(function() {
				$('#table').bootstrapTable(
						'refresh',
						_WEB_SITE_ + '/dashboard/service/terminalDownload/'
								+ itemName + '/' + itemStatus + '/' + groupId)
			}, refreshTime);
			TMS.bootTable('#table', {
				url : URL,
				method : 'post',
				contentType : 'application/x-www-form-urlencoded',
				columns : [ {
					field : 'tid',
					title : 'Terminal SN',
					formatter : self.snFormatter,
					sortable : false
				}, {
					field : 'terminalType',
					title : 'Terminal<br>Type',
					sortable : false
				}, {
					field : 'pkgName',
					title : 'Package<br>Name',
					sortable : false,
					formatter : self.resetTableFormatter
				}, {
					field : 'pkgVersion',
					title : 'Package<br>Version',
					sortable : false
				}, {
					field : 'pkgType',
					title : 'Package<br>Type',
					sortable : false
				}, {
					field : 'downSchedule',
					title : 'Download<br>Schedule',
					formatter : self.dateFormatter1
				}, {
					field : 'downStatus',
					title : 'Download<br>Status',
					sortable : false
				}, {
					field : 'downEndTime',
					title : 'Download<br>Time',
					formatter : self.dateFormatter2
				}, {
					field : 'actvSchedule',
					title : 'Activation<br>Schedule',
					formatter : self.dateFormatter3
				}, {
					field : 'actvStatus',
					title : 'Activation<br>Status',
					sortable : false,
					formatter : self.activationStatusFormatter
				}, {
					field : 'actvTime',
					title : 'Activation<br>Time',
					formatter : self.dateFormatter4
				} ],
				queryParams : function(params) { // 接口参数处理
					for (key in self.params) {
						params[key] = self.params[key]
					}
					;
					params.tsn = tsn;
					return params
				},
				loadend : function(res) {
					// 加载完执行code
					if (res.totalCount && res.totalCount > 0) {
						$('.table-records').removeClass('hide').html(
								res.totalCount + ' records found');
					} else {
						$('.table-records').addClass('hide');
					}
				},
			/*
			 * loadend : function(res) { self.renderExport(res); }
			 */
			});
		},
		renderExport : function(res) {
			var self = this;
			$('.report-export').removeClass('hide');
			if (res.totalCount && res.totalCount > 0) {
				$('.table-records').removeClass('hide').html(
						res.totalCount + ' records found');
				$('.report-export .btn').removeClass("disabled");
				$('.report-export .btn').attr('type', 'sumbit');
			} else {
				$('.table-records').addClass('hide');
				$('.report-export .btn').addClass("disabled");
				$('.report-export .btn').attr('type', 'button');
			}
		},
		resetTable : function() {
			var self = this;
			if ($('.advance-box').hasClass('hide')) {
				delete self.params.startTime;
				delete self.params.endTime;
				delete self.params.timeType;
				delete self.params.pkgType;
				delete self.params.terminalType;
				delete self.params.downStatusType;
				delete self.params.actiStatusType;
				delete self.params.groupIds;
			}
			self.$table.bootstrapTable('refreshOptions', {
				queryParams : function(params) { // 接口参数处理
					for (key in self.params) {
						params[key] = self.params[key]
					}
					return params
				},
				pageNumber : 1
			// 重置页码为1
			})
		},
		showTable : function() {
			var self = this;
			self.resetTable();
			self.getExport();
		},
		getExport : function() {
			$('#exportTDownload input[name="startTime"]').val(
					this.params.startTime);
			$('#exportTDownload input[name="endTime"]')
					.val(this.params.endTime);
			$('#exportTDownload input[name="timeType"]').val(
					this.params.timeType);
			$('#exportTDownload input[name="pkgType"]')
					.val(this.params.pkgType);
			$('#exportTDownload input[name="terminalType"]').val(
					this.params.terminalType);
			$('#exportTDownload input[name="downStatusType"]').val(
					this.params.downStatusType);
			$('#exportTDownload input[name="actiStatusType"]').val(
					this.params.actiStatusType);
			$('#exportTDownload input[name="groupIds"]').val(
					this.params.groupIds);
			$('#exportTDownload input[name="fuzzyCondition"]').val(
					this.params.fuzzyCondition);
		},
		snFormatter : function(value, row, index) {
			var permissionView = $('#permission-view').val();
			if (permissionView == '1') {
				return [
						'<a href="' + _WEB_SITE_ + "/terminal/profile/"
								+ groupId + "/" + row.tid + '">', value, '</a>' ]
						.join('');
			} else {
				return [ '<span>', value, '</span>' ].join('');
			}

		},
		dateFormatter1 : function(value, row, index) {
			return TMS.changeTime(row.downSchedule)
		},
		dateFormatter2 : function(value, row, index) {
			return TMS.changeTime(row.downEndTime)
		},
		dateFormatter3 : function(value, row, index) {
			if (!value) {
				return 'Immediately'
			} else {
				return TMS.changeTime(row.actvSchedule)
			}
		},
		activationStatusFormatter : function(value, row, index) {
			if (value == 'NOACTIVITION') {
				value = '-';
			}
			return value;
		},
		dateFormatter4 : function(value, row, index) {
			return TMS.changeTime(row.actvTime)
		},
		resetTableFormatter : function(value, row, index) {
			if (!value) {
				value = '-';
			}
			return '<div class="table-w">' + value + '</div>';
		},
		bindEvent : function() {
			var self = this;
			$('#advance-btn')
					.click(
							function() {
								var text = $(this).text().replace(/\s/g, "");
								if (text == 'Advanced') {
									$(this).empty();
									$(this)
											.html(
													'<i class="iconfont left-icon">&#xe66b;</i> Basic');
								} else {
									$(this).empty();
									$(this)
											.html(
													'<span class="glyphicon glyphicon-filter"></span>Advanced');

								}
								$('.advance-box').toggleClass('hide');
								if (!$('.advance-box').hasClass('hide')) {
									$('#varioustime-select').focus();
								}
							});
			$('#search-btn-group .btn').click(function() {
				var keyWord = $('#search-btn-group input').val();
				self.params.fuzzyCondition = keyWord;
				self.showTable();
			});
			$('#search-btn-group input').keyup(function(e) {

				if (e.keyCode == 13) {
					var keyWord = $(this).val();
					self.params.fuzzyCondition = keyWord;
					self.showTable();
				}
			});
			self.initTimeSelect('#start-time,#end-time');
			$('.datetime-icon').click(function() {
				if ($(this).hasClass('start-datetime-icon')) {
					self.initTimeSelect('#start-time');
				} else {
					self.initTimeSelect('#end-time');
				}
			});
			$('#report-btn')
					.click(
							function() {
								var startTime = $('#start-time').val(), endTime = $(
										'#end-time').val(), timeType = $(
										'#varioustime-select').val(), pkgType = $(
										'#pkgType-select').val(), terminalType = $(
										'#terminalType-select').val(), downStatusType = $(
										'#downloadStatus-select').val(), actiStatusType = $(
										'#activationStatus-select').val(), groupIds = '';
								TMS
										.removeAlertdanger($('.bootstrap-growl .alert'));
								if (startTime && endTime) {
									var startArr = startTime.split(' '), startArr1 = startArr[0]
											.split('-'), startArr2 = startArr[1]
											.split(':'), endArr = endTime
											.split(' '), endArr1 = endArr[0]
											.split('-'), endArr2 = endArr[1]
											.split(':'), isError = false;
									var start = new Date(startArr1[0],
											startArr1[1], startArr1[2],
											startArr2[0], startArr2[1],
											startArr2[2]);
									var end = new Date(endArr1[0], endArr1[1],
											endArr1[2], endArr2[0], endArr2[1],
											endArr2[2]);
									if (start > end) {
										isError = true;
									}
								}

								if (isError) {
									TMS
											.alert(
													'End date/time must be later than start date/time',
													'danger');
									$('.report-datetime').addClass('has-error');
									return false;
								}
								var items = self.Tree.getActiveGroup();
								for (var i = 0; i < items.length; i++) {
									groupIds += items[i].id + ',';
								}
								if (groupIds) {
									groupIds = groupIds.substring(0,
											groupIds.length - 1);
								}
								if (startTime) {
									startTime = TMS
											.transferTimestamp(startTime);
								}
								if (endTime) {
									endTime = TMS.transferTimestamp(endTime)
								}
								self.params.startTime = startTime;
								self.params.endTime = endTime;
								self.params.timeType = timeType;
								self.params.pkgType = pkgType;
								self.params.terminalType = terminalType;
								self.params.downStatusType = downStatusType;
								self.params.actiStatusType = actiStatusType;
								self.params.groupIds = groupIds;
								self.showTable();
							});
		}
	}
	$(function() {
		TMS.init();
		terminalDownload.init();
	})
});