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
	
	var groupId = $("#gid").val();
	var addPer=$("#perssion-add").val();
	var editPer=$("#perssion-edit").val();
	var deletePer=$("#perssion-delete").val();
	
	var templateList = {
		init: function() {
			var self = this;
			self.bindEvent();
			self.renderTable();
			self.$table = $('#template-table');

		},
		renderTable: function() {
			var self = this;
			var addBtn='';
			var columns=[{
				field: 'name',
				title: 'Template Name',
				sortable: false,
				formatter: self.nameAction
			}, {
				field: 'pk.name',
				title: 'Package Name',
				sortable: false
			}, {
				field: 'pk.version',
				title: 'Package Version',
				sortable: false
			}, {
				field: 'createDate',
				title: 'Create Date/Time',
				sortable: false,
				formatter: self.dateFormatter
			}];
			if(deletePer=='1'){
				columns.push({
					width: '45px',
					class: 'g-table-nopadding',
					formatter: self.tableAction
				});
			}
			if(addPer == '1'){
				addBtn='<a class="btn btn-primary" href="'+_WEB_SITE_+"/pkgSchema/toAdd/"+groupId+'">New Template</a>';
			}
			TMS.bootTable('#template-table', {
				url: _WEB_SITE_ + '/pkgSchema/service/ajaxList/'+groupId,
				title: 'Template List',
				columns:columns,
				search: true,
				rightBtn:[addBtn].join('')
			});
		},
		nameAction: function(value, row, index) {
			return [
				'<a href="' + _WEB_SITE_ +'/pkgSchema/profile/'+groupId+'/'+row.id+ '">',
				value,
				'</a>'
			].join('');
		},
		tableAction: function(value, row, index) {
			var editBtn='',deleteBtn='';
			
			if(deletePer == '1'){
				deleteBtn='<li><a class="J-remove" style="color:#ff7068">Delete</a></li>';
			}
			return [
				'<div class="g-action" data-id="' + row.id +'" data-pkgid="' + row['pk.id'] + '">',
				'<div class="g-action-btn">',
				'<i class="iconfont">&#xe60f;</i>',
				'</div>',
				'<ul class="g-action-menu ">',
				'<li><a class="J-remove" style="color:#ff7068">Delete</a></li>',
				'</ul>',
				'</div>'
			].join('')
		},
		dateFormatter: function(value, row, index) {
			return TMS.changeTime(row.createDate)
		},
		bindEvent: function() {
			
			
			var $body = $(".g-container"),
			    self = this,
			    templateId;
						
			$body.on('click', '.J-remove', function() {
				 templateId = $(this).parents('.g-action').attr('data-id');
				 pkgId = $(this).parents('.g-action').attr('data-pkgid');
				$('#remove-template-modal').modal('show');
			});
			
		/*	TMS.actionMenuEvent(function(e) {
				templateId = $(e).parents('.g-action').attr('data-id');
				$('#remove-template-modal').modal('show');
			});*/
			$('#remove-template-modal .J-confirm-btn').click(function(event) {
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				TMS.getData({
					url: _WEB_SITE_ + '/pkgSchema/service/delete',
					type: 'get',
					dataType: 'json',
					data: {
						pkgSchemaId: templateId,
						pkgId:pkgId
					},
				}, function(data) {
					if (data.statusCode != undefined && data.statusCode != '200') {
						TMS.alert(data.message, 'danger');
					} else {
						TMS.alert(data.message, 'success');
						self.$table.bootstrapTable('refresh');
						$('#remove-template-modal').modal('hide');
					}
				});
			});
		}
	}
	$(function() {
		TMS.init();
		templateList.init();
		TMS.actionMenuEvent();
	
	

	})
});