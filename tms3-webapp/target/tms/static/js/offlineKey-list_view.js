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
    var tsn = $("#tsn").val();
    var targetGroupId;
    var keyId; //选中的keyId
    var childKeyId=[];//选中的子pcg id
    var selectedChildSn = '';//选中的子Key sn用于deploy
    var assignPer = $("#permission-assign").val();
    var deployPer = $("#permission-deploy").val();
    var deletePer = $('#permission-delete').val();
    var keyList = {
        init: function() {
            var self = this,
                assignBtn = '';
            	deployBtn = '';
            	deleteBtn = '';
                var columns = [{
                    field: 'name',
                    title: 'Key Name',
                    sortable: false,
                    formatter: self.keyFormatter
                }, {
                    field: 'version',
                    title: 'Version',
                    sortable: false
                }, {
    				field: 'sn',
    				title: 'Terminal SN',
    				sortable: false
    			}, {
                    field: 'terminalType',
                    title: 'Terminal Type',
                    sortable: false
                }, {
                    field: 'modifyDate',
                    title: 'Update Time/Date',
                    sortable: false,
                    formatter: self.dateFormatter,
                    cellStyle:{
                        css: {width: '130px'}
                    }
                }];
            self.bindEvent();
            self.$table = $('#table');
            if (assignPer == '1') {
            	columns.unshift({
                    field: 'checkStatus',
                    checkbox: true, //开启全选
                });
                
                assignBtn = '<button class="btn btn-default J-select-assign">Assign</button>';
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
            if(deployPer == '1'){
            deployBtn = '<button class="btn btn-default J-select-deploy" href="">Deploy</button>';
            }
            if (deletePer == '1') {
				deleteBtn = '<button class="btn btn-default J-select-delete" style="color:#ff7068">Delete</button>';
			}
            TMS.bootTable('#table', {
                url: _WEB_SITE_ + '/offlinekey/service/ajaxList/' + groupId,
                queryParams: function(params) { // 接口参数处理
					params.sn = tsn;
					return params
				},
                title: 'Keys List',
                columns: columns,
                search: true,
                // detailView:true,
                detailFormatter:function(index, row){
                    var nowKeyId=row.id;
                    var nowKeyName=row.name;
                    var index=index;
                    var $table=$('.bootstrap-table');                    
                    $table.find('tr[data-index='+index+']+.detail-view').remove();
                    TMS.getData({
                        url: _WEB_SITE_ + '/offlinekey/service/ajaxListByName',
                        type: 'post',
                        dataType: 'json',
                        data: {
                            // offlineKeyId: nowKeyId,
                            name:nowKeyName,
                            groupId:groupId
                        },
                    }, function(data) {
                        // data=data.items;
                        if(data&&data.length>1){
                            var $table=$('.bootstrap-table');
                            var $container=$('<div></div>')
                            for(var i=0;i<data.length;i++){
                                var row=data[i];
                                if(row.id==nowKeyId){
                                    continue;
                                }
                                
                                var cloneTr=$table.find('tr[data-index='+index+']').clone();
                                //console.log(cloneTr.clone());
                                cloneTr.data('index','');
                                cloneTr.addClass('detail-view');
                                cloneTr.find('td').eq(1).addClass('td-name');
                                var isChecked=childKeyId.indexOf(row.id)!=-1 && 'checked';
                                var nameHtml=self.keyFormatter(row.name,row,0);
                                var nowDate=self.dateFormatter(row.modifyDate,row,0);
                                cloneTr.attr('data-id',row.id)
                                cloneTr.attr('data-index',row.id);
                                cloneTr.attr('data-parent',index);
                               
                                cloneTr.find('td:last').html('')
                                var $checkbox='<div class="checkbox checkbox-primary"><input '+isChecked+' data-i="'+row.id+'" data-sn="'+ row.sn +'" name="btSelectItem" type="checkbox"><label></label></div>';
                                
                                if(assignPer==1){
                                    cloneTr.find('td').eq(1).html(nameHtml);
                                    cloneTr.find('td').eq(2).text(row.version)
                                    cloneTr.find('td').eq(5).html(nowDate)
                                    cloneTr.find('td:first').html($checkbox).addClass('bs-checkbox').css({
                                        width: '50px', textAlign: 'center' 
                                    });
                                }else{
                                    cloneTr.find('td').eq(0).html(nameHtml);
                                    cloneTr.find('td').eq(1).text(row.version)
                                    cloneTr.find('td').eq(4).html(nowDate)
                                }

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
                },
                checkboxBtn: {
                    html: [
                        assignBtn,
                        deployBtn,
                        deleteBtn

                    ].join('')
                },
                loadend: function(res) {
                    //加载完执行code
                    $('.search input').attr('placeholder', 'Key Name');

                    //加载完执行code
                    if (res.totalCount && res.totalCount > 0) {
                        $('.table-records').removeClass('hide').html(res.totalCount + ' records found');
                    } else {
                        $('.table-records').addClass('hide');
                    }

                }
            });
            this.tree = new TMS.groupTree('#assign-key-tree', { //assign树
                nowGroup: false,
                multiselect: true,
                loadAll: false,
                selectUp: true,
                isassign: true,
                confirm: function(activeId) {
                    var idArr = '';
                    for (var i = 0; i < activeId.length; i++) {
                        idArr += activeId[i].id + ',';

                    }
                    if (idArr) {
                        idArr = idArr.substring(0, idArr.length - 1)
                    } else {
                        $('#assign-key-tree').modal('hide');
                        return false;
                    }
                    targetGroupId = idArr;
                    TMS.getData({
                        url: _WEB_SITE_ + '/offlinekey/service/assign',
                        type: 'post',
                        dataType: 'json',
                        data: {
                        	offlineKeyId: keyId,
                            groupIds: targetGroupId
                        },
                    }, function(data) {
                        self.ajaxBack(data, '#assign-key-tree');
                    });
                }
            });
        },

        keyFormatter: function(value, row, index) {
            if (tsn == null || tsn == undefined || tsn == "") {
                return [
                    '<a href="' + _WEB_SITE_ + "/offlinekey/profileView/" + groupId + "/" + row.id + "?type=1" + '">',
                    value,
                    '</a>' 
                ].join('');
            } else {
                return [
                    '<a href="' + _WEB_SITE_ + "/offlinekey/profileView/" + groupId + "/" + tsn + "/" + row.id + "?type=1" + '">',
                    value,
                    '</a>'
                ].join('');
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
        dateFormatter: function(value, row, index) {
            
            return TMS.changeTime(row.modifyDate)
        },
        bindEvent: function() {

            var $body = $(".g-container"),
                self = this;
            $body.on('change', '.bs-checkbox input', function(event) {
                var objArr = self.$table.bootstrapTable('getSelections');
                var id=$(this).data('i');
                var sn=$(this).data('sn');
                if($(this).attr('name')=='btSelectAll'){
                    // $('.bootstrap-table .detail-view input').prop('checked',$(this).prop('checked'));                    
                }
                if(id){
                    if($(this).prop('checked')){
                        if(childKeyId.indexOf(id)==-1) {
                        	childKeyId.push(id);
                        	selectedChildSn = sn;
                        }
                    }else{
                        var nowItemIndex = childKeyId.indexOf(id); 
                        if(nowItemIndex!=-1){
                            childKeyId.splice(nowItemIndex, 1);
                            selectedChildSn = '';
                        }
                    }
                }
                if(objArr.length == 0 && childKeyId.length==0){
                	$('.select-box .btn').addClass('disabled');
                } else if ((objArr.length)+(childKeyId.length) == 1) {
                    $('.select-box .btn').removeClass('disabled');
                    
                } else if((objArr.length)+(childKeyId.length) > 1){
                		$('.select-box .J-select-deploy').addClass('disabled');
                    	$('.select-box .J-select-assign').removeClass('disabled');
                    	$('.select-box .J-select-delete').removeClass('disabled');
                    
                }
            })
            $body.on('click', '.J-select-assign', function() {
                if ($(this).hasClass('disabled')) {
                    return false
                }
                var objArr = self.$table.bootstrapTable('getSelections'),
                    idArr = [];
                for (var i = 0; i < objArr.length; i++) {
                    idArr.push(objArr[i].id);
                }
                idArr=idArr.concat(childKeyId)
                keyId = idArr.join(',');
                self.tree.init();

                $('#assign-key-tree').modal('show');
            });
            $body.on('click', '.J-select-deploy', function() {
                if ($(this).hasClass('disabled')) {
                    return false
                }
                var objArr = self.$table.bootstrapTable('getSelections');
                var selectedSn = '';
                if (objArr && objArr.length > 0) {
                	selectedSn = objArr[0].sn;
                } else {
                	selectedSn = selectedChildSn;
                }

                if(childKeyId.length>0){
                    keyId = childKeyId.join(',')
                }else{
                    keyId = objArr[0].id;
                }
                if (tsn == null || tsn == undefined || tsn == "") {
                	window.location.href = _WEB_SITE_ + '/terminalDeploykey/toDeploy/'+groupId + "/"+ selectedSn + "?pkgId=" + keyId
                } else {
                	window.location.href = _WEB_SITE_ + '/terminalDeploykey/toDeploy/'+groupId + "/"+ tsn +"?pkgId=" + keyId
                }
            });
          //delete
			$body.on('click', '.J-select-delete', function() {
				if ($(this).hasClass('disabled')) {
					return false
				}
				var objArr = self.$table.bootstrapTable('getSelections'),
				idArr = [];
				for (var i = 0; i < objArr.length; i++) {
				    idArr.push(objArr[i].id);
				}
				idArr=idArr.concat(childKeyId)
				keyId = idArr.join(',');
                TMS.changeModalText('#delete-key-modal', 'delete', 'key', idArr);
				$('#delete-key-modal').modal('show');
			});
			$('#delete-key-modal .J-confirm-btn').click(function() {
				TMS.removeAlertdanger($('.bootstrap-growl .alert'));
				TMS.getData({
					url: _WEB_SITE_ + '/offlinekey/service/delete',
					type: 'post',
					dataType: 'json',
					data: {
						offlineKeyId: keyId,
						groupId: groupId
					}
				}, function(data) {
					if (data.statusCode == 200 || data.statusCode == undefined) {
						TMS.alert(data.message, 'success');
						$('#delete-key-modal').modal('hide');
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
            data&&data.statusCode==200 ?childKeyId=[]:null;
            data&&data.statusCode==200 ?selectedChildSn='':null;
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
        keyList.init();

    })
});
