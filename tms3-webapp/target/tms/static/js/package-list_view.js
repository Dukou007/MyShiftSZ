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
    var packageId; //选中的packageId
    var childPkgId=[];//选中的子pcg id
    var assignPer = $("#permission-assign").val();
    var deployPer = $("#permission-deploy").val();
    var packageList = {
        init: function() {
            var self = this,
                assignBtn = '';
            	deployBtn = '';
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
                    formatter: self.statusFormatter,
                    cellStyle:{
                        css: {width: '125px'}
                    }
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
                    // formatter: self.checkboxFormatter,
                    // class:'bs-checkbox',
                    // cellStyle:{
                    //     css: {width: '50px','text-align':'center'}
                    // }
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
            TMS.bootTable('#table', {
                url: _WEB_SITE_ + '/pkg/service/ajaxList/' + groupId,
                title: 'Packages List',
                columns: columns,
                search: true,
                // detailView:true,
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
                                if(row.id==nowPcgId){
                                    continue;
                                }
                                
                                var cloneTr=$table.find('tr[data-index='+index+']').clone();
                                console.log(cloneTr.clone());
                                cloneTr.data('index','');
                                cloneTr.addClass('detail-view');
                                cloneTr.find('td').eq(1).addClass('td-name');
                                var isChecked=childPkgId.indexOf(row.id)!=-1 && 'checked';
                                var nameHtml=self.packageFormatter(row.name,row,0);
                                var size=self.sizeFormatter(row.fileSize,row,0);
                                var signed=self.signedFormatter(row.signed,row,0);
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
                checkboxBtn: {
                    html: [
                        assignBtn,
                        deployBtn

                    ].join('')
                },
                loadend: function(res) {
                    //加载完执行code
                    $('.search input').attr('placeholder', 'Package Name,Type');

                    //加载完执行code
                    if (res.totalCount && res.totalCount > 0) {
                        $('.table-records').removeClass('hide').html(res.totalCount + ' records found');
                    } else {
                        $('.table-records').addClass('hide');
                    }

                }
            });
            this.tree = new TMS.groupTree('#assign-package-tree', { //assign树
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
                        $('#assign-package-tree').modal('hide');
                        return false;
                    }
                    targetGroupId = idArr;
                    TMS.getData({
                        url: _WEB_SITE_ + '/pkg/service/assign',
                        type: 'post',
                        dataType: 'json',
                        data: {
                            pkgId: packageId,
                            groupIds: targetGroupId
                        },
                    }, function(data) {
                        self.ajaxBack(data, '#assign-package-tree');
                    });
                }
            });
        },

        packageFormatter: function(value, row, index) {
            if (tsn == null || tsn == undefined || tsn == "") {
                return [
                    '<a href="' + _WEB_SITE_ + "/pkg/profileView/" + groupId + "/" + row.id + "?type=1" + '">',
                    value,
                    '</a>' 
                ].join('');
            } else {
                return [
                    '<a href="' + _WEB_SITE_ + "/pkg/profileView/" + groupId + "/" + tsn + "/" + row.id + "?type=1" + '">',
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
        // checkboxFormatter: function(value, row, index) {
        //     var $context='<div class="checkbox checkbox-primary"><input data-index="'+index+'" name="btSelectItem" type="checkbox"><label></label></div>'
        //     if (row.child) {
        //         return [
        //             '<a class="detail-icon" style="display:block;width: 27px;" href="javascript:"><i class="glyphicon glyphicon-chevron-down"></i></a>'
        //         ].join('');
        //     } else {
        //         return $context;
        //     }
        // },
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

            var $body = $(".g-container"),
                self = this;
            $body.on('change', '.bs-checkbox input', function(event) {
                var objArr = self.$table.bootstrapTable('getSelections');
                var id=$(this).data('i');
                if($(this).attr('name')=='btSelectAll'){
                    // $('.bootstrap-table .detail-view input').prop('checked',$(this).prop('checked'));                    
                }
                if(id){
                    if($(this).prop('checked')){
                        if(childPkgId.indexOf(id)==-1)childPkgId.push(id);
                    }else{
                        var nowItemIndex = childPkgId.indexOf(id); 
                        if(nowItemIndex!=-1){
                            childPkgId.splice(nowItemIndex, 1);                         
                        }
                    }
                }
                if(objArr.length == 0 && childPkgId.length==0){
                	$('.select-box .btn').addClass('disabled');
                } else if ((objArr.length)+(childPkgId.length) == 1) {
                    $('.select-box .btn').removeClass('disabled');
                    
                } else if((objArr.length)+(childPkgId.length) > 1){
                		$('.select-box .J-select-deploy').addClass('disabled');
                    	$('.select-box .J-select-assign').removeClass('disabled');
                    
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
                idArr=idArr.concat(childPkgId)
                packageId = idArr.join(',');
                self.tree.init();

                $('#assign-package-tree').modal('show');
            });
            $body.on('click', '.J-select-deploy', function() {
                if ($(this).hasClass('disabled')) {
                    return false
                }
                var objArr = self.$table.bootstrapTable('getSelections');
                
                if(childPkgId.length>0){
                    packageId = childPkgId.join(',')
                }else{
                    packageId = objArr[0].id;
                }
                if (tsn == null || tsn == undefined || tsn == "") {
                	window.location.href = _WEB_SITE_ + '/groupDeploy/toDeploy/'+groupId + "?pkgId=" + packageId
                } else {
                	window.location.href = _WEB_SITE_ + '/terminalDeploy/toDeploy/'+groupId + "/"+ tsn +"?pkgId=" + packageId
                }
            });
            $body.on('click', '.detail-icon', function() {
                if ($(this).children().hasClass('glyphicon-chevron-up')) {
                    return false
                }
                var nowIndex=$(this).parents('tr').data('index');
                var $table=$('.bootstrap-table');  
                $table.find('tr.detail-view[data-parent='+nowIndex+']').remove();
            });
            // $body.on('click', '.detail-view input', function(event) {
            //     console.log(this,event.target.value,$(this).prop('checked'))
            //     if($(this).prop('checked')){
            //         self.childPkgId.push()
            //     }else{

            //     }
            // });
           

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

        }
    }
    $(function() {
        TMS.init();
        packageList.init();

    })
});
