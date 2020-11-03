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
	var assignPer = $("#permission-assign").val();
	var checkedItems = [], formatItems = '';//用于ajax传参
	var terminalNotRegist = {
		init: function() {
			var self = this;
			var columns = [{
				field: 'trmSn',
				title: 'Terminal SN',
				sortable: false
			}, {
				field: 'terminalType',
				title: 'Terminal Type',
				sortable: false
			}, {
				field: 'lastDate',
				title: 'Last Accessed',
				sortable: false,
				formatter: self.dateFormatter
			}, {
				field: 'sourceIp',
				title: 'Source IP',
				sortable: false
			}];
			this.params = {};
			this.params.groupIds = groupId;
			var checkboxBtn = '';
			if (assignPer == '1') {
				columns.unshift({
					field: 'checkStatus',
					checkbox: true //开启全选
				});
				checkboxBtn = {
					html: [
						'<button class="btn btn-default J-select-assign" >',
						'Assign',
						'</button>'
					].join('')
				};
			};
			TMS.bootTable('#table', {
				url: _WEB_SITE_ + '/report/service/terminalNotRegisteredList/'+groupId,
				method: 'post',
				contentType: 'application/x-www-form-urlencoded',
				columns: columns,
				checkboxBtn: checkboxBtn,
				loadend: function(res) {
					self.renderExport(res);
					self.$table.bootstrapTable('uncheckAll');
				},
				queryParams: function(params) { // 接口参数处理
					for (key in self.params) {
						params[key] = self.params[key]
					}
					return params
				}
			});
			this.bindEvent();
			this.$table = $('#table');
			this.assigntree = new TMS.groupTree('#assign-terminal-modal', {
				nowGroup: false,
				multiselect: false,
				selectUp:true,
				isassign:true,
				loadAll: false,
				confirm: function(activeId) {
					var idArr = '';
					
					for (var i = 0; i < activeId.length; i++) {
						idArr += activeId[i].id + ',';
					}
					if (idArr) {
						idArr = idArr.substring(0, idArr.length - 1)
					} else {
						//no group selected, idArr set null
						idArr = '';
					}
					targetGroupId = idArr;
					TMS.getData({
						url: _WEB_SITE_ + '/terminal/service/noreg/assign',
						type: 'post',
						dataType: 'json',
						data: {
							tsns: formatItems,
							groupId: targetGroupId,
						},
					}, function(data) {
						self.ajaxBack(data, '#assign-terminal-modal');
					});
				}
			});
		},
		deleteGroup: function(id) {
			var self = this,
				items = this.selectGroup;
			for (var i = 0; i < items.length; i++) {
				if (id == items[i].id) {
					items.splice(i, 1);
					break;
				}
			}
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
			self.$table.bootstrapTable('uncheckAll');
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
			$('#exportUser input[name="terminalType"]').val(this.params.terminalType);
			$('#exportUser input[name="fuzzyCondition"]').val(this.params.fuzzyCondition);
		},
		dateFormatter: function(value, row, index) {
			return TMS.changeTime(row.lastDate)
		},
		bindEvent: function() {
			var $body = $(".g-container");
			var self = this;
			$('.group_chooseG_bottom').click(function() {
				self.Tree.init();
			});
			$('.g-container').on('click', '.group_note_delete', function(event) {
				var id = $(this).parents('.group_note_items').attr('data-id');
				$(this).parents('.group_note_items').remove();
				self.deleteGroup(id);
			});
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
					$('.startTime').focus();
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
			$('.datetime-icon').click(function(){
				if($(this).hasClass('start-datetime-icon')){
					self.initTimeSelect('#start-time');
				}else{
					self.initTimeSelect('#end-time');
				}
			});
			$('#report-btn').click(function() {
				var startTime = $('#start-time').val(),
					endTime = $('#end-time').val(),
					terminalType = $("#terminalType-select").val();
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				if (startTime && endTime) {
					var startArr = startTime.split(' '),
						startArr1 = startArr[0].split('-'),
						startArr2 = startArr[1].split(':'),
						endArr = endTime.split(' '),
						endArr1 = endArr[0].split('-'),
						endArr2 = endArr[1].split(':'),
						isError = false;
					var start = new Date(startArr1[0], startArr1[1], startArr1[2],startArr2[0], startArr2[1], startArr2[2]);
					var end = new Date(endArr1[0], endArr1[1], endArr1[2],endArr2[0], endArr2[1], endArr2[2]);
					if (start > end) {
						isError = true;
					}
				}
				var alertArr = $('.alert-danger');
				if (isError) {
					
					TMS.alert('End date/time must be later than start date/time', 'danger');
					$('.report-datetime').addClass('has-error');
					return false;
				}
				if(startTime){
					startTime = TMS.transferTimestamp(startTime);
				} 
				if(endTime){
					endTime = TMS.transferTimestamp(endTime)
				}
				self.params.startTime = startTime;
				self.params.endTime = endTime;
				self.params.terminalType = terminalType;
				self.showTable();

			});
			$body.on('change', '.bs-checkbox input', function(event) {
				//get all the checkbox checked
				checkedItems = self.$table.bootstrapTable('getAllSelections');
				//判断是否有选中的checkbox
				if(checkedItems.length){
					$('.select-box .btn').removeClass('disabled');
				}
			});
			//assign
			$body.on('click', '.J-select-assign', function() {
				if($(this).hasClass('disabled')) {
					return false;
				} 
				formatItems = TMS.setCheckedItems(checkedItems, 'trmSn');
				self.assigntree.init();
				$('#assign-terminal-modal').modal('show');
			});
		},
		ajaxBack: function(data, modal) { //请求回调
			var self = this;

			TMS.removeAlertdanger($('.bootstrap-growl .alert'));

			if (data.statusCode != undefined && data.statusCode != '200') {
				TMS.alert(data.message, 'danger');
			} else {
				TMS.alert(data.message, 'success');
				self.$table.bootstrapTable('refresh');
				$('.select-box .btn').addClass('disabled');
				$(modal).modal('hide');
			}

		}
	}
	$(function() {
		TMS.init();
		terminalNotRegist.init();

	})
});