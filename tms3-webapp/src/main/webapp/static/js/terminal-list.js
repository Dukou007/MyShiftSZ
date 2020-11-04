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
	require('validate-methods');
	var TMS = require('TMS');
	var groupId = $("#gid").val();
	var tsn = $("#tsn").val();
	var treeDepth = $("#treeDepth").val();
	//get all the permission value
	var query=null;
	var copyPer = $("#permission-copy").val();
	var deletePer = $('#permission-delete').val();
	var clonePer = $("#permission-clone").val();
	var removePer = $("#permission-remove").val();
	var checkedItems = [], formatItems = '';//用于ajax传参
	var terminalList = {
		init: function() {
			var self = this;
			var columns = [{
				field: 'tsn',
				title: 'Terminal SN',
				sortable: false,
				formatter: self.snFormatter
			},/* {
				field: 'status',
				title: 'Status',
				sortable: false,
				formatter: self.statusFormatter
			}, */{
				field: 'model.name',
				title: 'Terminal Type',
				sortable: false
			}, {
				field: 'ts.isOnline',
				title: 'Online/Offline',
				formatter: self.onlineFormatter,
				sortable: false
			}, {
				field: 'groupNames',
				title: 'Group',
				formatter: self.groupFormatter,
				
				sortable: false
			}, {
				field: 'ts.onlineSince',
				title: 'Last Accessed',
				formatter: self.timeFormatter,
				sortable: false
			},
			{
				field: 'ts.lastSourceIP',
				title: 'Source IP',
				sortable: false
			}, {
				field: 'localTime',
				title: 'Terminal Local Time',
				formatter: self.localTimeFormatter,
				sortable: false
			}];
			var checkboxBtn = '';
			var rightBtn = '';
			var removeBtn = '';
			var deleteBtn = '';
			if (copyPer == '1') {
				columns.unshift({
					field: 'checkStatus',
					checkbox: true //开启全选
				});
				if((treeDepth != '1' && treeDepth !=0) && removePer == '1' ) {
					removeBtn = '<button class="btn btn-default J-select-remove" style="color:#ff7068">Remove</button>';
				}
				if(deletePer == '1') {
					deleteBtn = '<button class="btn btn-default J-select-delete" style="color:#ff7068">Delete</button>';
				}
				// exportBtn = '<button class=" btn btn-default J-select-export" style="">Export</button>';
				
				checkboxBtn = {
					html: [
						'<button class="btn btn-default J-select-activate">',
						'Activate',
						'</button>',
						'<button class="btn btn-default J-select-deactivate">',
						'Deactivate',
						'</button>',
						'<button class="btn btn-default J-select-clone" >',//once name copy
						'Clone',
						'</button>',
						'<button class="btn btn-default J-select-copy" >',//once name assign
						'Copy',
						'</button>',
						'<button class="btn btn-default J-select-move">',
						'Move',
						'</button>',
						removeBtn,
						// exportBtn,
						deleteBtn
					].join('')
				};


			};
			self.bindEvent();
			self.$table = $('#table');
			var _search = false
			if (tsn == null || tsn == undefined) {
				_search = true
			}
			TMS.bootTable('#table', {
				url: _WEB_SITE_ + '/terminal/service/ajaxList',
				type: 'post',
				//title: 'Terminals List',
				method: 'post',
				contentType: 'application/x-www-form-urlencoded',
				queryParams: function(params) { // 接口参数处理
					params.groupId = groupId;
					params.tsn = tsn;
					query=params;
					return params
				},
				columns: columns,
				search: _search,

				rightBtn: rightBtn,
				checkboxBtn: checkboxBtn,
				loadend: function(res) {
					//加载完执行code
					$('.search input').attr('placeholder','Terminal SN,Terminal Type');
						//加载完执行code
						if (res.totalCount && res.totalCount > 0) {
				                $('.table-records').removeClass('hide').html(res.totalCount + ' records found');
			            } else {
			                $('.table-records').addClass('hide');
						}
						$('.select-box .J-select-export').removeClass('disabled');					
						$('.report-export').remove();
						var tsn=$("#tsn").val()||'';
						var fuzzyCondition='';
						if(query&& query.fuzzyCondition){
							fuzzyCondition=query.fuzzyCondition;							
						}
						// var url='tms/report/userMaintenance/export/1';
						var url=_WEB_SITE_ + '/terminal/service/export/'+groupId;
						var $export = null
						if (res.totalCount && res.totalCount > 0) {
							$export=$('<div class="report-export pull-left" style="margin-bottom:15px;"><form id="exportUser" action="'+url+'" method="post"><input type="hidden" name="tsn" value="'+tsn+'"><input type="hidden" value="'+fuzzyCondition+'" name="fuzzyCondition"><button class="btn btn-primary" type="submit">Export</button></form></div>')
						} else {
							$export=$('<div class="report-export pull-left" style="margin-bottom:15px;"><form id="exportUser" action="'+url+'" method="post"><input type="hidden" name="tsn" value="'+tsn+'"><input type="hidden" value="'+fuzzyCondition+'" name="fuzzyCondition"><button class="btn btn-primary disabled" type="button">Export</button></form></div>')
						}
						$('.fixed-table-toolbar').append($export).css('overflow','hidden')
					}
			});
			this.assigntree = new TMS.groupTree('#copy-terminal-modal', { //assign树,现页面改名copy
				nowGroup: false,
				multiselect: true,
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
						url: _WEB_SITE_ + '/terminal/service/copy',
						type: 'post',
						dataType: 'json',
						data: {
							tsns: formatItems,
							groupIds: targetGroupId,
							groupId:groupId
						},
					}, function(data) {
						//显示操作之后的消息，隐藏模态框
						if (data.statusCode != undefined && data.statusCode != '200') {
							TMS.alert(data.message, 'danger');
						} else {
							var existTsns = data.existTsns;
							var str = '';
							if(existTsns.length <= 0){
								str = 'success';
								TMS.alert(str, 'success');
							} else if (existTsns.length <= 3){
	                    		str = 'Terminal(s) ' + existTsns + ' exist(s) in the group(s)';
	                    		TMS.alert(str, 'danger');
							} else {
								str = 'The following '+existTsns.length+' terminal(s) ' + existTsns[0] + ','+ existTsns[1]+','+ existTsns[2] +' etc exist(s) in the group(s)';
								TMS.alert(str, 'danger');
							}
							selectionIds=[];
							
							self.$table.bootstrapTable('refresh');
							$('.select-box .btn').addClass('disabled');
							$('#copy-terminal-modal').modal('hide');
							
						}
						//self.ajaxBack(data, '#copy-terminal-modal');
					});
				}
			});
			
			
			this.tree = new TMS.groupTree('#clone-terminal-tree', { //copy树
				nowGroup: false,
				loadAll: false,
				selectUp:true,
				confirm: function(activeId) {
					$('#clone-terminal-tree').modal('hide');
					if (!activeId[0]) {
						return false
					}
				
					$('.J-show-cloneTree input').val(activeId[0].path);
					$('#group-id').val(activeId[0].id);
					$('.J-show-cloneTree').parents('.form-edit-group').removeClass('has-error');
					$('.J-show-cloneTree .help-block').remove();
				}
			});

			this.moveTree = new TMS.groupTree('#move-terminal-tree', { //move树
				nowGroup: false,
				loadAll: false,
				selectUp:true,
				confirm: function(activeId) {
					
					var _data = {
						url: _WEB_SITE_ + '/terminal/service/move',
						type: 'post',
						dataType: 'json',
						data: {
							tsns: formatItems,
							groupId: groupId,
							targetGroupId: activeId[0].id
						},
					}
					if (tsn) {
						_data.followTo = _WEB_SITE_ + "/terminal/list/" + groupId
					}
					TMS.getData(_data, function(data) {
						self.ajaxBack(data, '#move-terminal-tree');
					});
				}
			});
		},
		snFormatter: function(value, row, index) {
			if (!row.status) {
				return [
					'<a href="' + _WEB_SITE_ + "/terminal/profile/" + groupId + "/" + row.tsn + '">',
					'<span title="Deactivated" style="color:#4a647e">' + value + '</span>',
					'</a>'
				].join('');
			}else {
				return [
					'<a href="' + _WEB_SITE_ + "/terminal/profile/" + groupId + "/" + row.tsn + '">',
					'<span>' + value + '</span>',
					'</a>'
				].join('');
			}
		},
		onlineFormatter: function(value, row, index) {
			var online;
			if (row['ts.isOnline'] == null || row['ts.isOnline'] == 2) {
				online = 'Offline';
				return "<span>" + online + "</span>";
			}
			if (row['ts.isOnline'] == 1) {
				online = 'Online';
				return "<span style='color:#1AAA42'>" + online + "</span>";
			}
		},
		timeFormatter: function (value, row, index) {
			return TMS.changeTime(row['ts.onlineSince']);
		},
		localTimeFormatter: function (value, row, index) {
			return TMS.changeTime(row['localTime'], null, true);
		},
		/*statusFormatter: function(value, row, index) {
			var status;
			if (!row.status) {
				status = 'Deactivated';
			} else {
				status = 'Active';
			}
			return status;
		},*/
		groupFormatter: function(value, row, index) {
			
			value=value.replace('null//','');
			value=value.replace('//null','');
			value=value.replace('null','');
			if(value==''){
				return '-'
			}
			var text;
			var title=value.replace(/\/\//g,'\n');
			var nowGroups=value.split('//');
			if(nowGroups.length>1){
				text=nowGroups[0]+'...';
			}else{
				text=nowGroups[0];				
			}
			var span='<span title="'+title+'">'+text+'</span>'
			return String(span);
		},
		bindEvent: function() {
			var $body = $(".g-container"),
				self = this;
			
			//copy操作只支持单选
			$body.on('change', '.bs-checkbox input', function(event) {
				//get all the checkbox checked
				checkedItems = self.$table.bootstrapTable('getSelections');
				//判断是否有选中的checkbox
				var activeNum = 0;
				if(checkedItems.length){
					//当只选中一项
					if(checkedItems.length == 1) {
						//按钮取消禁用
						//根据status设置activate, deactivate按钮状态
						$('.select-box .btn').removeClass('disabled');
		          		checkedItems[0].status? $('.J-select-activate').addClass('disabled')
		          						:$('.J-select-deactivate').addClass('disabled');

					} else {
						//选中多项
						//计算active items个数
						for(var i in checkedItems){
				         	if(checkedItems[i].status){
				         		activeNum++
				         	}
				         }
						$('.select-box .btn').removeClass('disabled');
	          			$('.J-select-clone').addClass('disabled');

						activeNum==checkedItems.length? 
						$('.J-select-activate').addClass('disabled') : (activeNum == 0 ? 
						$('.J-select-deactivate').addClass('disabled'):null);

					}
				}
				$('.select-box .J-select-export').removeClass('disabled');					
				

			});
			//点击任意一个操作按钮，弹出对应的模态框，执行操作！
			//activate
			$body.on('click', '.J-select-activate', function() {
				if($(this).hasClass('disabled')) {
					return false;
				} 
				formatItems = TMS.setCheckedItems(checkedItems, 'tsn');
				TMS.changeModalText('#activate-terminal-modal', 'activate', 'terminal', checkedItems);
				$('#activate-terminal-modal').modal('show');
			});
			$('#activate-terminal-modal .J-confirm-btn').click(function() {
				TMS.getData({
					url: _WEB_SITE_ + '/terminal/service/activate',
					type: 'post',
					dataType: 'json',
					data: {
						tsns: formatItems,
						groupId:groupId
					},
				}, function(data) {
					self.ajaxBack(data, '#activate-terminal-modal');
				});
			});

			//deactive
			$body.on('click', '.J-select-deactivate', function() {
				if($(this).hasClass('disabled')) {
					return false;
				} 
				formatItems = TMS.setCheckedItems(checkedItems, 'tsn');
				TMS.changeModalText('#deactivate-terminal-modal', 'deactivate', 'terminal', checkedItems);
				$('#deactivate-terminal-modal').modal('show');
			});
			$('#deactivate-terminal-modal .J-confirm-btn').click(function() {
				TMS.getData({
					url: _WEB_SITE_ + '/terminal/service/deactivate',
					type: 'post',
					dataType: 'json',
					data: {
						tsns: formatItems,
						groupId:groupId
					},
				}, function(data) {
					self.ajaxBack(data, '#deactivate-terminal-modal');
				});
			});

			//clone
			$body.on('click', '.J-select-clone', function() {
				if($(this).hasClass('disabled')) {
					return false;
				} 
				formatItems = TMS.setCheckedItems(checkedItems, 'tsn');
				$('#source-tsn').val(formatItems);
				$('#clone-terminal-modal').modal('show');
			});
			$("#clone-terminal-modal form").validate({ //clone terminal
				submitHandler: function(form) {
					TMS.getData({
						url: _WEB_SITE_ + '/terminal/service/clone',
						type: 'post',
						data: $(form).serialize()
					}, function(data) {
						selectionIds=[];
						 //success
						 if (data.statusCode == 200 || data.statusCode == undefined) {
							
							 var ignortsns = data.ignoreTsns;
		                    	//其他组已经存在
		                    	if(ignortsns){
		                    		 var str = '';
		                    		 str = 'Terminal exist(s) in other group(s), are you sure to overwrite?';
		                    		 $('#reConfirm-clone-terminal-modal .modal-body').html(str);	
		                    		 $('#reConfirm-clone-terminal-modal').modal('show');
		                    		//确定覆盖已存在的terminal(s)
		                    		 $('#reConfirm-clone-terminal-modal .J-confirm-btn').click(function(){
		                    			 TMS.getData({
		                                     url: _WEB_SITE_ + '/terminal/service/clone',
		                                     type: 'post',
		                                     data: $(form).serialize()+'&override=1'
		                                     //followTo: _WEB_SITE_ + "/terminal/list/" + groupId
		                                 }, function(data) {
		                                	 if (data.statusCode == 200 || data.statusCode == undefined) {
		                                		TMS.alert('success','success');
		                                		 $('#reConfirm-clone-terminal-modal').modal('hide');
		                                		 $('#clone-terminal-modal').modal('hide');
		                                		 self.$table.bootstrapTable('refresh');
		                         				 $('.select-box .btn').addClass('disabled');
		                                		 
		                                	 }else{
		                                		 TMS.alert('error','danger');
		                                	 }
		                                 }); 
		                    		 });
		                    	}else{
		                    		//其他组不存在
		                    		var str = '';
		                    		var ownByOtherParallerGroupTsnList = '';
		                    		//权限外的terminal
		                    		var ownByOtherParallerGroupTsn = data.ownByOtherParallerGroupTsn;
		                    		//存在于当前组的terminal
		                    		var ownByOtherParallerGroupTsnList = TMS.convertArrayDescribe(ownByOtherParallerGroupTsn);
		                    		//1,terminal在权限外
		                    		if(ownByOtherParallerGroupTsn.length>0){
		                    		    str = 'Failed! Terminals exist(s) in the group(s) out of your authority.';
		                    			TMS.alert(str,'danger');
		                    			$('#clone-terminal-modal').modal('hide');
		                    			self.$table.bootstrapTable('refresh');
                         				$('.select-box .btn').addClass('disabled');
		                    			
		                    		}
		                    		//2,terminal在权限内
		                    		if(ownByOtherParallerGroupTsn.length ==0){
		                    			TMS.alert('success','success');
		                    			$('#reConfirm-clone-terminal-modal').modal('hide');
                               		    $('#clone-terminal-modal').modal('hide');
                                		self.$table.bootstrapTable('refresh');
                         				$('.select-box .btn').addClass('disabled');
		                    		}
		                    	}
							 
						 }else{
							 //error
							 TMS.alert('Network Instability','danger');
						 }
					});
				}
			});
			$('#clone-terminal-modal').on('hidden.bs.modal', function() {
				$('#clone-terminal-modal .form-edit-group').removeClass('has-error').removeClass('has-success');
				$('#clone-terminal-modal form')[0].reset();
				$('#clone-terminal-modal .help-block').remove();

			});

			//copy
			$body.on('click', '.J-select-copy', function() {
				if($(this).hasClass('disabled')) {
					return false;
				} 
				formatItems = TMS.setCheckedItems(checkedItems, 'tsn');
				self.assigntree.init();
				$('#copy-terminal-modal').modal('show');
			});

			//move
			$body.on('click', '.J-select-move', function() {
				if($(this).hasClass('disabled')) {
					return false;
				} 
				formatItems = TMS.setCheckedItems(checkedItems, 'tsn');
				self.moveTree.init();
				$('#move-terminal-tree').modal('show');
			});

			//remove
			$body.on('click', '.J-select-remove', function() {
				if($(this).hasClass('disabled')) {
					return false;
				} 
				formatItems = TMS.setCheckedItems(checkedItems, 'tsn');
				TMS.changeModalText('#remove-terminal-modal', 'remove', 'terminal', checkedItems);
				$('#remove-terminal-modal').modal('show');
			});
			
			$('#remove-terminal-modal .J-confirm-btn').click(function() {
				var _remove_data = {
					url: _WEB_SITE_ + '/terminal/service/dismiss',
					type: 'post',
					dataType: 'json',
					data: {
						tsns: formatItems,
						groupId: groupId
					},
				}
				if (tsn) {
					_remove_data.followTo = _WEB_SITE_ + "/terminal/list/" + groupId
				}
				TMS.getData(_remove_data, function(data) {
					self.ajaxBack(data, '#remove-terminal-modal');
				});
			});

			
			//delete
			$body.on('click', '.J-select-delete', function() {
				if($(this).hasClass('disabled')) {
					return false;
				} 
				formatItems = TMS.setCheckedItems(checkedItems, 'tsn');
				TMS.changeModalText('#delete-terminal-modal', 'delete', 'terminal', checkedItems);
				$('#delete-terminal-modal').modal('show');
			});
			$('#delete-terminal-modal .J-confirm-btn').click(function() {
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				TMS.getData({
					url: _WEB_SITE_ + '/terminal/service/delete',
					type: 'post',
					dataType: 'json',
					data: {
						tsns: formatItems,
						groupId: groupId
					},
					followTo: _WEB_SITE_ + "/terminal/list/" + groupId
				}, function(data) {
					if (data.statusCode == 200 || data.statusCode == undefined) {
						TMS.alert(data.message, 'success');
						$('#delete-terminal-modal').modal('hide');
					} else {
						TMS.alert(data.message, 'danger');
					}
				});
			});

			$('body').on('click', '.J-show-cloneTree', function() {
				self.tree.init();
				$('#clone-terminal-tree').modal('show');
			});
		},
		ajaxBack: function(data, modal) { //请求回调
			var self = this;
			data&&data.statusCode==200 ?selectionIds=[]:null;

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
		terminalList.init();

	})
});