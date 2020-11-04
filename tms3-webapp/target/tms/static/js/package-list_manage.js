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
	var treeDepth = $("#treeDepth").val();
	var checkedItems = [], formatItems = '';//用于ajax传参
    var childPkgId=[],childPkgItems=[];//选中的子pcg id	
	var assignPer = $("#perssion-assign").val();
	var activePer = $("#perssion-active").val();
	var deactivePer = $("#perssion-deactive").val();
	var removePer = $("#perssion-remove").val();
	var addPer = $("#perssion-add").val();
	var deletePer = $('#perssion-delete').val();
	var packageList = {
		init: function() {
			var self = this,
				assignBtn = '',
				activeBtn = '',
				deactiveBtn = '',
				removeBtn = '',
				deleteBtn = '',
				addBtn = '';
			var columns = [{
				field: 'name',
				title: 'Package Name',
				sortable: false,
				formatter: self.packageFormatter
			}, {
				field: 'version',
				title: 'Version',
				sortable: false
			}, {
				field: 'pgmType',
				title: 'Package Type',
				sortable: false
			}, {
				field: 'fileSize',
				title: 'Size',
				sortable: false,
				formatter: self.sizeFormatter
			}, {
                field: 'signed',
                title: 'Signed',
                sortable: false,
                formatter: self.signedFormatter
            }, {
				field: 'status',
				title: 'Status',
				sortable: false,
				cellStyle:{
					css: {width: '125px'}
				},
				formatter: self.statusFormatter
			}, {
				field: 'modifyDate',
				title: 'Update Time/Date',
				sortable: false,
				formatter: self.dateFormatter,
				cellStyle:{
					css: {width: '130px'}
				}
			}];
			if (assignPer == '1') {
				columns.unshift({
					field: 'checkStatus',
					checkbox: true //开启全选
				});
			}
			columns.push({
				field: 'expanded',
				formatter: self.expandFormatter,
				title: '',
				sortable: false,
				cellStyle:{
					css: {width: '50px','text-align':'center'}
				}
			});
			self.bindEvent();
			self.$table = $('#table');
			if (assignPer == '1') {
				assignBtn = '<button class="btn btn-default J-select-assign">Assign</button>';
			}
			if (activePer == '1') {
				activeBtn = '<button class="btn btn-default J-select-activate">Activate</button>';
			}
			if (deactivePer == '1') {
				deactiveBtn = '<button class="btn btn-default J-select-deactivate">Deactivate</button>';
			}
			if (removePer == '1') {
				if((treeDepth != '1'&& treeDepth !=0) || deletePer != '1') {
					removeBtn = '<button class="btn btn-default J-select-remove" style="color:#ff7068">Remove</button>';
				}
			}
			if (deletePer == '1') {
				deleteBtn = '<button class="btn btn-default J-select-delete" style="color:#ff7068">Delete</button>';
			}
			if (addPer == '1') {
				addBtn = '<a class="btn btn-primary" href="' + _WEB_SITE_ + '/pkg/toAdd/' + groupId + '">New Package</a>';
			}
			TMS.bootTable('#table', {
				url: _WEB_SITE_ + '/pkg/service/ajaxList/' + groupId,
				title: 'Manage Packages',
				columns: columns,
				search: true,
				rightBtn: [
					addBtn,
				].join(''),
				checkboxBtn: {
					html: [
						assignBtn,
						activeBtn,
						deactiveBtn,
						removeBtn,
						deleteBtn
					].join('')
				},
				detailFormatter:function(index, row){
                    var nowPcgId=row.id;
                    var nowPcgName=row.name;
                    var index=index;
                    var $table=$('.bootstrap-table');                    
                    $table.find('tr[data-index='+index+']+.detail-view').remove();
                    TMS.getData({
                        url: _WEB_SITE_ + '/pkg/service/ajaxListByName',
                        type: 'post',
                        dataType: 'json',
                        data: {
                            // pkgId: nowPcgId,
                            name:nowPcgName,
                            groupId:groupId
                        },
                    }, function(data) {
                        // data=data.items;
                        if(data&&data.length>1){
                            var $table=$('.bootstrap-table');
                            var $container=$('<div></div>')
                            for(var i=0;i<data.length;i++){
								var row=data[i];
								childPkgItems[row.id]=row;
                                if(row.id==nowPcgId){
                                    continue;
                                }
                                
                                var cloneTr=$table.find('tr[data-index='+index+']').clone();
                                cloneTr.data('index','');
                                cloneTr.addClass('detail-view');
                                cloneTr.find('td').eq(1).addClass('td-name');
                                var isChecked=childPkgId.indexOf(row.id)!=-1 && 'checked';
                                var nameHtml=self.packageFormatter(row.name,row,0);
                                var signed=self.signedFormatter(row.signed,row,0);
                                var size=self.sizeFormatter(row.fileSize,row,0);
                                var status=self.statusFormatter(row.status,row,0);
                                var nowDate=self.dateFormatter(row.modifyDate,row,0);
                                cloneTr.attr('data-id',row.id)
                                cloneTr.attr('data-index',row.id);
                                cloneTr.attr('data-parent',index);
                               
                                cloneTr.find('td:last').html('')
                                var $checkbox='<div class="checkbox checkbox-primary"><input '+isChecked+' data-i="'+row.id+'" name="btSelectItem" type="checkbox"><label></label></div>';
                                
                                if(assignPer==1){
                                    cloneTr.find('td').eq(1).html(nameHtml);
                                    cloneTr.find('td').eq(2).text(row.version)
                                    cloneTr.find('td').eq(4).text(size)
                                    cloneTr.find('td').eq(5).html(signed)
                                    cloneTr.find('td').eq(6).html(status)
                                    cloneTr.find('td').eq(7).html(nowDate)
                                    cloneTr.find('td:first').html($checkbox).addClass('bs-checkbox').css({
                                        width: '50px', textAlign: 'center' 
                                    });
                                }else{
                                    cloneTr.find('td').eq(0).html(nameHtml);
                                    cloneTr.find('td').eq(1).text(row.version)
                                    cloneTr.find('td').eq(3).text(size)
                                    cloneTr.find('td').eq(4).html(signed)
                                    cloneTr.find('td').eq(5).html(status)
                                    cloneTr.find('td').eq(6).html(nowDate)
                                }
                                // var nowClickTr=$table.find('tr[data-index='+index+']+.detail-view:last');
                                
                                // if(typeof nowClickTr.data('index')=='undefined' && !nowClickTr.data('inited')){
                                //     nowClickTr.data('inited',true)
                                //     nowClickTr.attr('data-id',row.id);
                                //     nowClickTr.attr('data-index',row.id);
                                //     nowClickTr.attr('data-parent',index);
                                //     nowClickTr.html(cloneTr.html());
                                // }else{
                                //     nowClickTr.after(cloneTr.clone());
                                // }
                                $container.append(cloneTr.clone())
                            }
                            $table.find('tr[data-index='+index+']').after($container.html());
                        }else{
                            var $table=$('.bootstrap-table');                            
                            var nowClickTr=$table.find('tr[data-index='+index+']');
                            var newTr=$('<tr class="detail-view"><td colspan="8"></td></tr>')
                            newTr.find('td').text('No Datas').css('textAlign','center');   
                            nowClickTr.after(newTr)                            
                        }
                        

                        
                    });

                    
                    // return '<table><tr class="detail-pcg-container" data-index="2"><td></td><td class="bs-checkbox "><div class="checkbox checkbox-primary"><input data-index="2" name="btSelectItem" type="checkbox"><label></label></div></td><td style=""><a href="/tms/pkg/profileView/1/28?type=1">Project_newChild</a></td><td style="">000235.0001</td><td style="">form</td><td style="">33399.69KB</td><td style="">Active</td><td style=""><span data-rowtime="1536213697000">14:01 09/06/2018</span></td></tr></table>'
                },
				loadend: function(res) {
					//加载完执行code
					$('.search input').attr('placeholder','Package Name,Version');
					if (res.totalCount && res.totalCount > 0) {
			                $('.table-records').removeClass('hide').html(res.totalCount + ' records found');
		            } else {
		                $('.table-records').addClass('hide');
		            }
				}
			});
			this.tree = new TMS.groupTree('#assign-package-tree', { //assign树
				nowGroup: false,
				loadAll: false,
				selectUp:true,
				isassign:true,
				multiselect: true,
				confirm: function(activeId) {
					var idArr = '';
					for (var i = 0; i < activeId.length; i++) {
						idArr += activeId[i].id + ',';

					}
					if (idArr) {
						idArr = idArr.substring(0, idArr.length - 1)
					} else {
						idArr='';
					}
					targetGroupId = idArr;
					TMS.getData({
						url: _WEB_SITE_ + '/pkg/service/assign',
						type: 'post',
						dataType: 'json',
						data: {
							pkgId: formatItems,
							groupIds: targetGroupId
						},
					}, function(data) {
						self.ajaxBack(data, '#assign-package-tree');
					});
				}
			});
		},
		packageFormatter: function(value, row, index) {
			return [
				'<a href="' + _WEB_SITE_ + "/pkg/profileManage/" + groupId + "/" + row.id + '">',
				value,
				'</a>'
			].join('');
		},
		signedFormatter: function(value, row, index) {
            if (!row.signed) {
            	return [
                    '<span class="g-icon-item g-package-unsigned" aria-hidden="true"></span>',
                    '<span>Unsigned</span>'
                ].join('');
            } else {
            	return [
            		'<span class="g-icon-item g-package-signed" aria-hidden="true"></span>',
                    '<span>Signed</span>'     
                ].join('');
            }
        },
		statusFormatter: function(value, row, index) {
			if (!row.status) {
            	return [
                    '<span class="g-icon-item g-package-deactive" aria-hidden="true"></span>',
                    '<span>Deactivated</span>'
                ].join('');
            } else {
            	return [
            		'<span class="g-icon-item g-package-active" aria-hidden="true"></span>',
                    '<span>Active</span>'     
                ].join('');
            }
		},
		dateFormatter: function(value, row, index) {
			return TMS.changeTime(row.modifyDate)
		},
		sizeFormatter: function(value, row, index) {
			return (row.fileSize / 1024).toFixed(2) + "KB";
		},
		bindEvent: function() {

			var $body = $(".g-container"),self = this;
			$body.on('change', '.bs-checkbox input', function(event) {
				var id=$(this).data('i');
                if(id){
                    if($(this).prop('checked')){
                        if(childPkgId.indexOf(id)==-1){
							childPkgId.push(id);
							selectionIds[id]=childPkgItems[id];
						};
                    }else{
                        var nowItemIndex = childPkgId.indexOf(id); 
                        if(nowItemIndex!=-1){
                            childPkgId.splice(nowItemIndex, 1);                         
						}
						if(typeof selectionIds[id]!='undefined'){
							delete selectionIds[id];
						}
                    }
				}
				
				checkedItems = self.$table.bootstrapTable('getSelections');				
				//判断是否有选中的checkbox
				var activeNum = 0;
				
				for(var i in checkedItems){
					if(typeof checkedItems[i]=='undefined'){
						checkedItems.splice(i,1);
					}
				}			
				if(checkedItems.length||childPkgId.length){
					//当只选中一项
					if(checkedItems.length == 1 &&childPkgId.length==0) {
						//按钮取消禁用
						//根据status设置activate, deactivate按钮状态
						$('.select-box .btn').removeClass('disabled');
		          		checkedItems[0].status? $('.J-select-activate').addClass('disabled')
		          						:$('.J-select-deactivate').addClass('disabled');

					}else if(childPkgId.length==1&&checkedItems.length ==0){
						//按钮取消禁用
						
						//根据status设置activate, deactivate按钮状态
						$('.select-box .btn').removeClass('disabled');
						childPkgItems[id].status? $('.J-select-activate').addClass('disabled')
		          						:$('.J-select-deactivate').addClass('disabled');

					} else{
						
						//选中多项
						//计算active items个数
						for(var i in checkedItems){
				         	if(checkedItems[i].status){
				         		activeNum++
				         	}
						}
						for( var i in childPkgId){
							if(childPkgItems[childPkgId[i]].status){
								activeNum++								
							}
						}
						$('.select-box .btn').removeClass('disabled');
	          			$('.J-select-clone').addClass('disabled');

						activeNum==checkedItems.length+childPkgId.length? 
						$('.J-select-activate').addClass('disabled') : (activeNum == 0 ? 
						$('.J-select-deactivate').addClass('disabled'):null);

					}
				}else if(checkedItems.length +childPkgId.length==0){
					$('.select-box .btn').addClass('disabled');
				}
			});
			//assign
			$body.on('click', '.J-select-assign', function() {
				if ($(this).hasClass('disabled')) {
					return false
				}
				formatItems = TMS.setCheckedItems(checkedItems, 'id');
				var formatArr=String(formatItems).split(',');
				// formatArr=formatArr.concat(childPkgId);
				formatItems=formatArr.join(',');
				self.tree.init();
				$('#assign-package-tree').modal('show');
			});

			//activate
			$body.on('click', '.J-select-activate', function() {
				if ($(this).hasClass('disabled')) {
					return false
				}
				formatItems = TMS.setCheckedItems(checkedItems, 'id');
				var formatArr=String(formatItems).split(',');
				
				// formatArr=formatArr.concat(childPkgId);
				
				formatItems=formatArr.join(',');
				TMS.changeModalText('#activate-package-modal', 'activate', 'package', checkedItems);
				$('#activate-package-modal').modal('show');
			});
			$('#activate-package-modal .J-confirm-btn').click(function() {
				TMS.getData({
					url: _WEB_SITE_ + '/pkg/service/active',
					type: 'post',
					dataType: 'json',
					data: {
						pkgId: formatItems
					},
				}, function(data) {
					self.ajaxBack(data, '#activate-package-modal');
				});
			});

			//deactivate
			$body.on('click', '.J-select-deactivate', function() {
				if ($(this).hasClass('disabled')) {
					return false
				}
				formatItems = TMS.setCheckedItems(checkedItems, 'id');
				var formatArr=String(formatItems).split(',');
				// formatArr=formatArr.concat(childPkgId);
				formatItems=formatArr.join(',');
				TMS.changeModalText('#deactivate-package-modal', 'deactivate', 'package', checkedItems);
				$('#deactivate-package-modal').modal('show');
			});
			$('#deactivate-package-modal .J-confirm-btn').click(function() {
				TMS.getData({
					url: _WEB_SITE_ + '/pkg/service/deactive',
					type: 'post',
					dataType: 'json',
					data: {
						pkgId: formatItems
					},
				}, function(data) {
					self.ajaxBack(data, '#deactivate-package-modal');
				});
			});

			//remove
			$body.on('click', '.J-select-remove', function() {
				if ($(this).hasClass('disabled')) {
					return false
				}
				formatItems = TMS.setCheckedItems(checkedItems, 'id');
				var formatArr=String(formatItems).split(',');
				// formatArr=formatArr.concat(childPkgId);
				formatItems=formatArr.join(',');
				TMS.changeModalText('#remove-package-modal', 'remove', 'package', checkedItems);
				$('#remove-package-modal').modal('show');
			});
			$('#remove-package-modal .J-confirm-btn').click(function() {
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				TMS.getData({
					url: _WEB_SITE_ + '/pkg/service/dismiss',
					type: 'post',
					dataType: 'json',
					data: {
						pkgId: formatItems,
						groupId: groupId
					},
					followTo: _WEB_SITE_ + "/pkg/manageList/" + groupId
				});
				/*function(data) {
					if (data.statusCode == 200 || data.statusCode == undefined) {
						TMS.alert(data.message, 'success');
						$('#remove-package-modal').modal('hide');
					} else {
						TMS.alert(data.message, 'danger');
					}
				}*/
			});

			//delete
			$body.on('click', '.J-select-delete', function() {
				if ($(this).hasClass('disabled')) {
					return false
				}
				formatItems = TMS.setCheckedItems(checkedItems, 'id');
				var formatArr=String(formatItems).split(',');
				// formatArr=formatArr.concat(childPkgId);
				formatItems=formatArr.join(',');
				TMS.changeModalText('#delete-package-modal', 'delete', 'package', checkedItems);
				$('#delete-package-modal').modal('show');
			});
			$('#delete-package-modal .J-confirm-btn').click(function() {
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				TMS.getData({
					url: _WEB_SITE_ + '/pkg/service/delete',
					type: 'post',
					dataType: 'json',
					data: {
						pkgId: formatItems,
						groupId: groupId
					},
					followTo: _WEB_SITE_ + "/pkg/manageList/" + groupId
				}, function(data) {
					if (data.statusCode == 200 || data.statusCode == undefined) {
						TMS.alert(data.message, 'success');
						$('#delete-package-modal').modal('hide');
						self.$table.bootstrapTable('refresh');
						$('.select-box .btn').addClass('disabled');
						
					} else {
						TMS.alert(data.message, 'danger');
					}
				});
			});
			$body.on('click', '.detail-icon', function() {
                if ($(this).children().hasClass('glyphicon-chevron-up')) {
                    return false
                }
                var nowIndex=$(this).parents('tr').data('index');
                var $table=$('.bootstrap-table');  
                $table.find('tr.detail-view[data-parent='+nowIndex+']').remove();
            });
		},
		ajaxBack: function(data, modal) { //请求回调
			var self = this;
			data&&data.statusCode==200 ?selectionIds=[]:null;
            data&&data.statusCode==200 ?childPkgId=[]:null;			
			TMS.removeAlertdanger($('.bootstrap-growl .alert'));
			if (data.statusCode != undefined && data.statusCode != '200') {
				TMS.alert(data.message, 'danger');
			} else {
				TMS.alert(data.message, 'success');
				self.$table.bootstrapTable('refresh');
				$('.select-box .btn').addClass('disabled');
				$(modal).modal('hide');
			}
		},
		expandFormatter: function(value, row, index) {
            if (row.verCount && row.verCount>1) {
                return [
                    '<a class="detail-icon" href="javascript:"><i class="glyphicon glyphicon-chevron-down"></i></a>'
                ].join('');
            } else {
                return '';
            }
        },
	}
	$(function() {
		TMS.init();
		packageList.init();

	})
});