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
	var userMaintenance = {
		init: function() {
			var self = this;
			this.params = {};
			this.params.groupIds = groupId;
			self.renderTable();
			this.bindEvent();
			this.$table = $('#table');
			this.creatTree();
			this.isFirst = true;
		},
		creatTree: function() {
			var self = this;
			this.Tree = new TMS.groupTree('#add-tree', {
				isModal: true,
				nowGroup: false,
				multiselect: true,
				groupPanel: { //group 选择框
					display: true,
					titleShow: true
				}
			});
		},
		renderTable: function() {
			var self = this;
			TMS.bootTable('#table', {
				url: _WEB_SITE_ + '/report/service/userMaintList/'+groupId,
				method: 'post',
				contentType: 'application/x-www-form-urlencoded',
				columns: [{
					field: 'username',
					title: 'User',
					sortable: false
				}, {
					field: 'role',
					title: 'Role',
					sortable: false
				}, {
					field: 'eventAction',
					title: 'Operation',
					sortable: false
				}, {
					field: 'clientIp',
					title: 'IP Address',
					sortable: false
				}, {
					field: 'eventTime',
					title: 'Time/Date',
					sortable: false,
					formatter: self.dateFormatter
				}],
				loadend: function(res) {
					self.renderExport(res);

				},
				queryParams: function(params) { // 接口参数处理
					for (key in self.params) {
						params[key] = self.params[key]
					}
					return params
				}
			});
		},
		renderExport: function(res) {
			var self = this;
			$('.report-export').removeClass('hide');
            if (res.totalCount && res.totalCount > 0) {
                $('.table-records').removeClass('hide').html(res.totalCount + ' records found');
                $('.report-export .btn').removeClass("disabled");
                $('.report-export .btn').attr('type', 'sumbit');
            } else {
                $('.table-records').addClass('hide');
                $('.report-export .btn').addClass("disabled");
                $('.report-export .btn').attr('type', 'button');
            }
		},
		resetTable: function() {
			var self = this;
			if ($('.advance-box').hasClass('hide')) {
				delete self.params.startTime;
				delete self.params.endTime;
				delete self.params.selectedStatus;
				delete self.params.groupIds;
			}
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
		showTable: function() {
			var self = this;
			self.resetTable();
			self.getExport();
		},
		getExport: function() {
			$('#exportUser input[name="startTime"]').val(this.params.startTime);
			$('#exportUser input[name="endTime"]').val(this.params.endTime);
			$('#exportUser input[name="fuzzyCondition"]').val(this.params.fuzzyCondition);
			$('#exportUser input[name="selectedStatus"]').val(this.params.selectedStatus);
			$('#exportUser input[name="groupIds"]').val(this.params.groupIds);
		},
		dateFormatter: function(value, row, index) {
			return TMS.changeTime(row.eventTime)
		},
		initTimeSelect: function(el) {
			laydate({
				elem: el,
				format: 'YYYY-MM-DD hh:mm:ss',
				istime: true,
				choose: function(d, e) {
					$('.report-datetime').removeClass('has-error');
				}
			});
		},
		bindEvent: function() {
			var self = this;
			$('#advance-btn').click(function() {
				var text = $(this).text().replace(/\s/g, "");
				if (text == 'Advanced') {
					$(this).empty();
					$(this).html('<i class="iconfont left-icon">&#xe66b;</i> Basic');
					
				} else {
					$(this).empty();
					$(this).html('<span class="glyphicon glyphicon-filter"></span>Advanced');

				}
				$('.advance-box').toggleClass('hide');
				if(!$('.advance-box').hasClass('hide')){
					$('.firstFocus').focus();
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
			$('.datetime-icon').click(function() {
				if ($(this).hasClass('start-datetime-icon')) {
					self.initTimeSelect('#start-time');
				} else {
					self.initTimeSelect('#end-time');

				}
			});
			self.initTimeSelect('#start-time,#end-time');
			$('#report-btn').click(function() {
				var startTime = $('#start-time').val(),
					endTime = $('#end-time').val(),
					selectedStatus = $('#role-select').val(),
					groupIds = '';
				
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				if (startTime && endTime) {
					var startArr = startTime.split(' '),
						startArr1 = startArr[0].split('-'),
						startArr2 = startArr[1].split(':'),
						endArr = endTime.split(' '),
						endArr1 = endArr[0].split('-'),
						endArr2 = endArr[1].split(':'),
						isError = false;
					var start = new Date(startArr1[0], startArr1[1], startArr1[2], startArr2[0], startArr2[1], startArr2[2]);
					var end = new Date(endArr1[0], endArr1[1], endArr1[2], endArr2[0], endArr2[1], endArr2[2]);
					if (start > end) {
						isError = true;
					}
				}
				
				if (isError) {
					
					TMS.alert('End date/time must be later than start date/time', 'danger');
					$('.report-datetime').addClass('has-error');
					return false;
				}
				var items = self.Tree.getActiveGroup();
				for (var i = 0; i < items.length; i++) {
					groupIds += items[i].id + ',';
				}
				if (groupIds) {
					groupIds = groupIds.substring(0, groupIds.length - 1);
				}
				if(startTime){
					startTime = TMS.transferTimestamp(startTime);
				} 
				if(endTime){
					endTime = TMS.transferTimestamp(endTime)
				}
				
				self.params.startTime = startTime;
				self.params.endTime = endTime;
				self.params.selectedStatus = selectedStatus;
				self.params.groupIds = groupIds;
				self.showTable();

			});
		}
	}
	$(function() {
		TMS.init();
		userMaintenance.init();

	})
});