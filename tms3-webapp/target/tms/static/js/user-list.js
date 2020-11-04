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
	require('validate-methods');
	var targetUserId = '';
	var targetUserName = '';
	var PRIVATE_KEY = 'MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJoM/HU3Bkg+i/QHbyg0H2lpfDqPj/4Kj90rPivb6jfbfNvdBoy/B+GU27ognOEp6zKqLHFt5BgJkxyKR3XtId1NOEdiAOJ/ESUWTet32LqIGF35NAy5iAK9Nz6iun7EfuAtViRrihArY1b3K1BWs6p3VBQsrF/SaV7UAOWvy4o1AgMBAAECgYBIrFmo3lVrXX5el+c7eyNacjX11mYifU8TEmRZAn0C7tt/SUzetvv70mK58sqvejwHgbpHpITXRiUNcLp3im/PoFCEKBURdGWOS1CxwheOnfaTArz5ZRNmYSqo9s5dRgvnUcyxr9aPEKD6ZoJr9G5xcST4obb+ybP+lEu6+eUYfQJBAMbNkK0IYEZhdO/LLV10/U6ExgzcEyhRCrzjfULBuWiuL8YJdSAi7gHZPhy93KdXfwoezuf927w2bOSG7OzQTaMCQQDGX0h3mqO2DIdUd1LTfduAXNhCD44KKnH9QT96n4SDQsEqSErNHFbButN1+STVY0qr3HeIKy/yCoCG5x6Y3hZHAkBCHS0HDmkOHu0HrjcpBHYVPbyrnCjW2JTMmo8Wu1xQvtTudEi6ZkNm4/tvDYkrMcLy96nxpxADeMof+esjGmcnAkAYCIs7El0rPTFYJmy+es0RLC53MnM/AA13ZWRPcuwXCwtkGAYX+4r3Ra9A58Jyp+jwEXHZ6YGRjXws2+t1EdMZAkB/SW1DsR9PyZmJOSU+sgvmWal9Tu920KpoSQW547isRJIMn9XZ4/4HPyLwSi/TN/gvHypMVY80+UzA4nUxraP7';
	var decrypt = new JSEncrypt();
	decrypt.setPrivateKey('-----BEGIN RSA PRIVATE KEY-----'+PRIVATE_KEY+'-----END RSA PRIVATE KEY-----');
	var userList = {
		init: function() {
			var self = this;
			self.bindEvent();
			self.$table = $('#user-table');
			this.activeSystem = 'PPM';
			this.fuzzyCondition = '';
			this.activeRole = '';
			self.getRole(function() {
				self.renderTable(self.checkTableName('PPM'));
			})

		},
		checkTableName: function(name) {
			var targetName = '',
				name;
			switch (name) {
				case 'PPM':
					targetUrl = '/user/service/list/TMS/' + GROUP_ID;
					break;
				case 'Px Designer':
					targetUrl = '/user/service/list/PxDesigner/' + GROUP_ID;
					break;
				default:
					name = 'PPM';
					targetUrl = '/user/service/list/TMS/' + GROUP_ID;
			}
			return {
				targetUrl: targetUrl,
				name: name
			};
		},
		getRole: function(callback) {
			var self = this,
				url;
			if (this.activeSystem == 'PPM') {
				url = _WEB_SITE_ + '/user/service/getRoleAndCount/TMS/' + GROUP_ID;
			} else {
				url = _WEB_SITE_ + '/user/service/getRoleAndCount/PxDesigner/' + GROUP_ID;
			}
		
			TMS.getData({
					url: url,
					type: 'get'
				}, function(data) {
					if (data.statusCode !== undefined && data.statusCode != '200') {
						TMS.alert(data.message, 'danger')
					} else {
						callback && callback();
						self.renderRole(self.activeRole, data);
					}
				}

			);
		},
		renderRole: function(name, data) {
			var self = this,
				items = data.items,
				temp = [],
				sum = data.sum;
			if (!name) {
				temp
					.push('<li role="presentation" class="active" data-type=""><a >All(' + sum + ')</a></li>');
			} else {
				temp.push('<li role="presentation"  data-type=""><a >All(' + sum + ')</a></li>');
			}

			for (var i = 0; i < items.length; i++) {
				var select = '';
				if (name == items[i].id) {
					select = 'active';
				}
				temp.push('<li role="presentation" class="' + select + '" data-type="' + items[i].id + '"><a >' + items[i].name + '(' + items[i].count + ')</a></li>');

			}
			$('#roleTab').html(temp.join(''));
		},
		reSetTable: function(o) {
			var self = this;
			self.$table.bootstrapTable('refreshOptions', {
				url: _WEB_SITE_ + o.targetUrl,
				queryParams: function(params) { // 接口参数处理
					params.fuzzyCondition = self.fuzzyCondition;
					params.activeRole = self.activeRole;
					return params
				},
				pageNumber: 1
					// 重置页码为1
			});
		},
		renderTable: function(o) {
			var self = this;
			TMS.bootTable('#user-table', {
				url: _WEB_SITE_ + o.targetUrl,
				columns: [{
					field: 'username',
					title: 'User',
					sortable: false,
					formatter: self.userFormatter
				}, {
					field: 'email',
					title: 'Email',
					sortable: false
				}, {
					field: 'directory',
					title: 'Domain',
					sortable: false
				}, {
					field: 'lastLoginDate',
					title: 'Last Login Time',
					sortable: false,
					formatter: self.dateFormatter
				}, {
					field: 'enabled',
					title: 'Status',
					sortable: false,
					formatter: self.statusFormatter
				}, {
					width: '45px',
					class: 'g-table-nopadding',
					formatter: self.tableAction
				}],
				loadend: function() {
					self.getRole();
				
				}
			});
		},
		statusFormatter: function(value, row, index) {
			var status;
			if (!row.enabled) {
				status = 'Deactivated';
			} else {
				status = 'Active';
			}
			return status;
		},
		dateFormatter: function(value, row, index) {
			return TMS.changeTime(row.lastLoginDate)
		},
		userFormatter: function(value, row, index) {
			return [
					'<a href="' + _WEB_SITE_ + '/user/view/' + row.id + '/' + GROUP_ID + '" class="td-link">', value, '</a>'
				]
				.join('');
		},
		tableAction: function(value, row, index) {
			var status;
			if (row.enabled) {
				status = '<li class="J-deactivate"><a>Deactivate</a></li>';
			} else {
				status = '<li class="J-activate"><a>Activate</a></li>';
			}
			var ldap = '';
			if (!row.ldap) {
				ldap = '<li class="J-reset"><a>Reset&nbsp;Password</a></li>';
			}
			var showDelete = '';
			if (!row.sys) {
				showDelete = '<li><a class="J-delete" style="color:#ff7068">Delete</a></li>';
			}
			return ['<div class="g-action" data-id="' + row.id + '" data-name="' + row.username + '">',
				'<div class="g-action-btn">',
				'<i class="iconfont">&#xe60f;</i>', '</div>',
				'<ul class="g-action-menu ">', ldap,
				showDelete, status, '</ul>', '</div>'
			].join('')
		},
		bindEvent: function() {
			var $body = $(".g-container"),
				self = this;
			$body.on('click', '#systemTab li', function() {
				var targetName = $(this).attr('data-type');
				if (self.activeSystem != targetName) {
					self.activeSystem = targetName;
					self.activeRole = '';
					$('#search-btn-group input').val('');
					self.fuzzyCondition = '';
					self.reSetTable(self.checkTableName(targetName));
					$('#systemTab').siblings('.dropdown-toggle').html(targetName + '<span class="caret"></span>');
				}


			});
			$body.on('click', '#roleTab li', function() {
				if ($(this).hasClass('active'))
					return false;
				$('#roleTab li').removeClass('active');
				$(this).addClass('active');
				var targetRole = $(this).attr('data-type');
				self.activeRole = targetRole;
				self.fuzzyCondition = '';
				$('#search-btn-group input').val('');
				self.reSetTable(self.checkTableName(self.activeSystem));
			});
			$('#search-btn-group .btn').click(function() {
				var keyWord = $('#search-btn-group input').val();
				if (keyWord == self.fuzzyCondition) {
					return false
				}
				self.fuzzyCondition = keyWord;
				self.reSetTable(self.checkTableName(self.activeSystem));
			});
			$('#search-btn-group input').keyup(function(e) {
				if (e.keyCode == 13) {
					var keyWord = $(this).val();
					if (keyWord == self.fuzzyCondition) {
						return false
					}
					self.fuzzyCondition = keyWord;
					self.reSetTable(self.checkTableName(self.activeSystem));
				}
			});
			$('#delete-user-modal .J-confirm-btn').click(function() {
				TMS.getData({
					url: _WEB_SITE_ + '/user/service/delete/' + targetUserId + '/'+ GROUP_ID ,
					type: 'get',
					data: {
						id: targetUserId
					}
				}, function(data) {
					self.ajaxBack(data, '#delete-user-modal')
				});
			});
			$('#activate-user-modal .J-confirm-btn').click(function() {
				TMS.getData({
					url: _WEB_SITE_ + '/user/service/active/' + targetUserId + '/'+ GROUP_ID ,
					type: 'get',
					data: {
						id: targetUserId
					}
				}, function(data) {
					self.ajaxBack(data, '#activate-user-modal')
				});
			});
			$('#deactivate-user-modal .J-confirm-btn').click(
				function() {
					TMS.getData({
						url: _WEB_SITE_ + '/user/service/deactive/' + targetUserId + '/'+ GROUP_ID,
						type: 'get',
						data: {
							id: targetUserId
						}
					}, function(data) {
						self.ajaxBack(data, '#deactivate-user-modal')
					});
				});
			$('#reset-user-modal .J-confirm-btn').click(
				function() {
					TMS.getData({
						url: _WEB_SITE_ + '/user/service/resetPassword/' + targetUserId + '/'+ GROUP_ID,
						type: 'post',
						data: {
							id: targetUserId
						}
					}, function(data) {
						self.ajaxBack(data, '#reset-user-modal')
					});
				});
			$('#reset-user-modal .J-confirm-btn2').click(
					function() {
						$('#reset-user-modal .J-confirm-btn2').attr("disabled", true);
						TMS.getData({
							url: _WEB_SITE_ + '/user/service/generatePassword',
							type: 'post',
							data: {
								username: targetUserName,
								groupId: GROUP_ID
							}
						}, function(data) {
							TMS.removeAlertdanger($('.bootstrap-growl .alert'));
							if (data.statusCode != undefined && data.statusCode != '200') {
								TMS.alert(data.message, 'danger');
								$('#reset-user-modal .J-confirm-btn2').attr("disabled", false);
							} else {
								var generatePwd = decrypt.decrypt(data.message);
								var info = [
									'<div class="gen-password-item">Generated password: ' + generatePwd + '</div>',
									'<div class="gen-password-tip"><i class="iconfont" style="color:#FAB540;">&#xe6a8;</i>',
									'<span>Please save this password before close the dialog</span></div>'
				                ].join('');
								$('#reset-user-modal .modal-body').html(info);
								setTimeout(function() {
									$('#reset-user-modal .J-confirm-btn2').attr("disabled", false);
								}, 10000)
							}
						});
					});
			$('#reset-user-modal').on('hide.bs.modal', function () {
						$('#reset-user-modal .modal-body').html('Choose the options to reset the password.');
					});
			TMS.actionMenuEvent(function(e) {
				targetUserId = $(e).parents('.g-action').attr('data-id');
				targetUserName = $(e).parents('.g-action').attr('data-name');
			
				var type = $(e).text();
				if (type == 'Delete') {
					$('#delete-user-modal').modal('show');
				} else if (type == 'Activate') {
					$('#activate-user-modal').modal('show');
				} else if (type == 'Deactivate') {
					$('#deactivate-user-modal').modal('show');
				} else if (type.indexOf('Reset') != -1) {
					$('#reset-user-modal').modal('show');
				}
			});
		},
		ajaxBack: function(data, modal) { // 请求回调
			var self = this;
			TMS.removeAlertdanger($('.bootstrap-growl .alert'));
			if (data.statusCode != undefined && data.statusCode != '200') {
				TMS.alert(data.message, 'danger');
			} else {
				TMS.alert(data.message, 'success');
				self.$table.bootstrapTable('refresh');
			
				$(modal).modal('hide');
			}

		}
	}
	$(function() {
		TMS.init();
		userList.init();

	})
});