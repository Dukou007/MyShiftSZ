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
	var deployGroupId = $("#deployGroupId").val();
	var deletePer = $("#permission-delete").val();
	var deployment = {
		init: function() {
			this.bindEvent();
			this.ajaxDeployment();
		},
		ajaxDeployment: function(data) {
			var self = this;
			var data = data ? data : {};
			data.pageSize = 10;
			TMS.getData({
				url: _WEB_SITE_ + '/groupDeploy/service/getCurrentDeploysByGroupId/' + deployGroupId,
				type: 'get',
				data: data
			}, function(data) {
				if (data.statusCode !== undefined && data.statusCode != 200) {
					TMS.alert(data.message, 'danger')
				} else {
					self.renderList(data);
				}
			})
		},
		renderList: function(data) {
			console.log(data); 
			var self = this,
				items = data.items,
				records = '',
				temp = [];
			totalCount = data.totalCount;
			if(totalCount>0){
				records = '<p class="table-records">'+data.totalCount+' records found</p>';
			}
			if (items.length == 0 || items.length == 0) {
				$('#deploy-list').html("<div class='alert alert-info paxinfonext text-center'><span  class='paxspannext'><strong>Hint:</strong>There is no data!</span></div>");
				return
			}
			for (var i = 0; i < items.length; i++) {
				temp.push('<section class="deploy-items">');

				if (deletePer == '1') {

					temp.push('<div class="deploy-action" data-deployId="' + items[i].deployId + '" data-groupId="' + items[i].groupId + '">');
					temp.push('<i class="iconfont">&#xe619;</i>');
					temp.push('</div>');
				}

				temp.push('<div class="deploy-itemd-panel">');
				temp.push('<div class="deploy-title"><p class="p">');
				temp.push('<span class="font16px green bold deploy-title-type"><a href="' + _WEB_SITE_ + '/pkg/profileView/' + _ACTIVE_GROUP_ + '/' + items[i].pkgId + '">' +items[i].pkgName + '</a></span>');
				temp.push('<span class="grey font12px deploy-title-name">(Deployed by: ' +items[i].operator + ')</span>');
				temp.push('</p></div>');
				temp.push('<ul class="deploy-records clearfix">');
				temp.push('<li>');
				temp.push('<p class="font12px half-bold grey">Group/Terminal</p>');
				temp.push('<p class="font14px half-bold black" title="'+items[i].deploySourceNamePath+'" >' + items[i].deploySource + '</p>');
				temp.push('</li>');
				temp.push('<li>');
				temp.push('<p class="font12px half-bold grey">Terminal Type</p>');
				if (items[i].destModel) {
					temp.push('<p class="font14px half-bold black">' + items[i].destModel + '</p>');
				} else {
					temp.push('<p class="font14px half-bold black" align="center">' + '-' + '</p>');
				}
				temp.push('</li>');
				temp.push('<li>');
				temp.push('<p class="font12px half-bold grey">Package Version</p>');
				temp.push('<p class="font14px half-bold black">' + items[i].pkgVersion + '</p>');
				temp.push('</li>');
				temp.push('<li>');
				temp.push('<p class="font12px half-bold grey">Package Type</p>');
				temp.push('<p class="font14px half-bold black">' + items[i].pgmType + '</p>');
				temp.push('</li>');
				temp.push('<li>');
				temp.push('<p class="font12px half-bold grey">Download  Time</p>');
				temp.push('<p class="font14px half-bold black">' + items[i].dwnlStartTimeText + '</p>');
				//temp.push('<p class="font14px half-bold black">' + items[i].timeZone + '</p>');
				temp.push('</li>');
				temp.push('<li>');
				temp.push('<p class="font12px half-bold grey">Activation  Time</p>');
				temp.push('<p class="font14px half-bold black">' + items[i].actvStartTimeText + '</p>');
				temp.push('</li>');
				if (items[i].dwnlEndTimeText) {
					temp.push('<li>');
					temp.push('<p class="font12px half-bold grey">Expiration Time</p>');
					temp.push('<p class="font14px half-bold black">' + items[i].dwnlEndTimeText + '</p>');
					temp.push('</li>');
				}else{
					temp.push('<li style="margin-right:33px;">');
					temp.push('<p class="font12px half-bold grey">Expiration Time</p>');
					temp.push('<p class="font14px half-bold black text-center">' + '--' + '</p>');
					temp.push('</li>');
				}
				temp.push('<li>');
				temp.push('<p class="font12px half-bold grey">TimeZone</p>');
				temp.push('<p class="font14px half-bold black">' + items[i].timeZone + '</p>');
				temp.push('</li>');
				temp.push('</ul></div>');
				temp.push('</section>');
			}
	
		
			if (data.pageCount != 1) {
				var pageCount = data.pageCount;
				temp.push('<ul class="pagination pagination-deploy pull-right">');
				if (data.pageIndex == 1) {
					temp.push('<li class="pagination-pre" data-index="' + pageCount + '"><a href="#">&laquo;</a></li>');
				} else {
					var pre = data.pageIndex - 1
					temp.push('<li class="pagination-pre" data-index="' + pre + '"><a href="#">&laquo;</a></li>');
				}


				if (pageCount > 7) {
					for (var i = 1; i <= 6; i++) {
						if (i == data.pageIndex) {
							temp.push('<li class="active" data-index="' + i + '"><a href="javascript:;">' + i + '</a></li>');

						} else {
							temp.push('<li data-index="' + i + '"><a href="javascript:;" >' + i + '</a></li>');
						}

					}
					temp.push('<li class="more"><a href="javascript:;">...</a></li>');
					if (i == data.pageIndex) {
						temp.push('<li class="active" data-index="' + i + '"><a href="javascript:;" >' + i + '</a></li>');
					} else {
						temp.push('<li data-index="' + i + '"><a href="javascript:;">' + pageCount + '</a></li>');
					}

				} else {

					for (var i = 1; i <= pageCount; i++) {
						if (i == data.pageIndex) {
							temp.push('<li class="active" data-index="' + i + '"><a href="javascript:;" >' + i + '</a></li>');

						} else {
							temp.push('<li data-index="' + i + '"><a href="javascript:;" >' + i + '</a></li>');
						}

					}

				}
				if (data.pageIndex == data.pageCount) {
					temp.push('<li class="pagination-next" data-index="1"><a href="#">&raquo;</a></li>');
				} else {
					var next = Number(data.pageIndex) + 1;
					temp.push('<li class="pagination-next" data-index="' + next + '"><a href="#">&raquo;</a></li>');
				}

				temp.push('<ul>')
			}
			temp.unshift(records);
			$('#deploy-list').html(temp.join(''));

		},
		ajaxBack: function(data, modal) { //请求回调
			var self = this;
			TMS.removeAlertdanger($('.bootstrap-growl .alert'));
			if (data.statusCode != undefined && data.statusCode != '200') {
				TMS.alert(data.message, 'danger');
			} else {
				TMS.alert(data.message, 'success');
				self.ajaxDeployment();
				$(modal).modal('hide');
			}

		},
		getValue: function(v) {
			if (!v) {
				return '-'
			} else {
				return v
			}
		},
		bindEvent: function() {
			TMS.actionMenuEvent();
			var cDeployGroupId = '';
			var deployId = '';
			var isInherited;
			var data = {};
			self = this;
			$('.g-container').on('click', '.deploy-action', function() {
				cDeployGroupId = $(this).attr('data-groupId');
				if (cDeployGroupId == deployGroupId) {
					//isInherited = "false";
					inherit = 0;
				} else {
					//isInherited = "true";
					inherit = 1;
				}
				deployId = $(this).attr('data-deployId');
				data = {
					groupId: cDeployGroupId,
					//isInherited: isInherited,
					inherit:inherit,
					deployId: deployId
				}
				$('#modal_group_delete').modal('show');
			});
			$('.g-container').on('click', '.pagination li', function() {
				var $target = $(this),
					pageIndex = $target.attr('data-index');
				if ($target.hasClass('active') || $target.hasClass('more')) {
					return false;
				}
				self.ajaxDeployment({
					pageIndex: pageIndex
				});

			});
			$('#confirm_group_delete').click(function() {
				TMS.getData({
					url: _WEB_SITE_ + '/groupDeploy/service/delete',
					type: 'get',
					data: data
				}, function(data) {
					self.ajaxBack(data, '#modal_group_delete');
				});
			});
		}
	}
	$(function() {
		TMS.init();
		deployment.init();

	});
});