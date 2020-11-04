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
 * Date                  Author                 Action
 * 2017-1-10             TMS_HZ             Create/Add/Modify/Delete
 * ============================================================================     
 */
define(function(require, exports, module) {
    var TMS = require('TMS');
    var groupId = $("#gid").val();
    var pId = $("#pid").val();
    $(function() {
        var groupIds = $("#groupIds").val()?$("#groupIds").val().replace(/\[|\]/gi,'').split(','):'';
        TMS.init();
        var addPer = $("#permission-add").val();
        var deletePer = $("#permission-delete").val();
        var copyPer = $("#permission-copy").val();
        var movePer = $("#permission-move").val();
        var importPer = $("#permission-import").val();
        var globalSettingPer = $("#permission-global-setting").val();
        var addEnterprisePer = $("#permission-addEnterprise").val();
        $('.group-import').removeClass('hide');
        var myGroupTree = new TMS.groupTree('#my-grouptree', {
            checkBox: false,
            operator: true,
            loadAll: false,
            treeGroupUrl: _WEB_SITE_ + "/group/service/tree/descantGroup",
            detail: function(data) { // group-detail 页面
                return _WEB_SITE_ + '/group/profile/' + groupId + "/" + data.id;
            },
            operatorHtml: function(data) {
                var html = '',iscopy='',ismmove='',strAdd='',strDel='';
                if (data.treeDepth && data.treeDepth == 1 && globalSettingPer == '1') {
                    html = '<li><a href="' + _WEB_SITE_ + '/usage/list/' + groupId + '/' + data.id + '">Global&nbsp;Setting</a></li>'
                }
                if(copyPer == '1'){
                    iscopy = '<li><a class="J-copy" >'
                        +'Copy'
                        + '</a></li>';
                }
                if (movePer == '1') {
                    ismove = '<li><a class="J-move">'
                        + 'Move'
                        + '</a></li>';
                
		            if (addPer == '1') {
		                strAdd = '<li><a href="' + _WEB_SITE_ + '/group/toAdd/' + groupId + "/" + data.id + '">Add</a></li>';
		            }
//		            if (deletePer == '1' && !(data.namePath && data.namePath.indexOf('/') == -1 && globalSettingPer == '1')) {
		            if (deletePer == '1') {
		                strDel = '<li class="J-remove"><a style="color:#ff7068">Delete</a></li>';
		            }
		            if(groupIds.indexOf(data.id.toString())!=-1){
		                iscopy = '';
		                ismove = '';
		                strDel = '';
		            }
		            return [
		                '<div class="g-action g-tree-operator" data-id="' + data.id + '">',
		                '<div class="g-action-btn">',
		                '<i class="iconfont">&#xe60f;</i>',
		                '</div>',
		                '<ul class="g-action-menu">' + strAdd,
		
		                ''+ iscopy+ismove + strDel,
		                html,
		                '</ul>',
		                '</div>'
		            ].join('')
                
                }

            },

        }).init();
        var sourceGroupId; // 选择的groupId
        var sourceParentGroupId;
        var copyTree = new TMS.groupTree('#copy-group', { // copy树
            nowGroup: false,
            loadAll: false,
            confirm: function(activeId) {
            	TMS.removeAlertdanger($('.bootstrap-growl .alert'));
            	//未选择group
            	
            	if(!activeId.length) {
            		activeId.push({'id': ''});
            	}
            	
                TMS.getData({
                        url: _WEB_SITE_ + '/group/service/copy',
                        type: 'post',
                        data: {
                            sourceGroupId: sourceGroupId,
                            targetGroupId: activeId[0].id
                        }
                    },
                    function(data) {
                        if (data.statusCode !== undefined && data.statusCode != '200') {
                            TMS.alert(data.message, 'danger');
                        	return false;
                        } else {
                        	$('#copy-group').modal('hide');
                            TMS.alert(data.message, 'success');
                            myGroupTree.init();
                            TMS.topLeftTree.refresh();
                        }
                    }

                );
               
            }
        });
        var moveTree = new TMS.groupTree('#move-group', { // move树
            nowGroup: false,
            loadAll: false,
            confirm: function(activeId) {
            	//未选择group
            	if(!activeId.length) {
            		activeId.push({'id': ''});
            	}
                TMS.removeAlertdanger($('.bootstrap-growl .alert'));
                TMS.getData({
                    url: _WEB_SITE_ + '/group/service/move',
                    type: 'post',
                    data: {
                        sourceGroupId: sourceGroupId,
                        targetGroupId: activeId[0].id
                    }
                }, function(data) {
                    if (data.statusCode !== undefined && data.statusCode != '200') {
                        TMS.alert(data.message, 'danger')
                    } else {
                    	$('#move-group').modal('hide');
                        TMS.alert(data.message, 'success')
                        if (sourceGroupId == _ACTIVE_GROUP_) {
                            setTimeout(function() {
                                window.location.href = _WEB_SITE_ + '/group/list/' + _ACTIVE_GROUP_;
                            }, 1000)
                            return false
                        }
                        myGroupTree.init();
                        TMS.topLeftTree.refresh();
                    }
                });
                
            }
        });
        TMS.actionMenuEvent(null, function(target) {
                // 修复手机端按钮超出范围滚动
                var $menu = $(target).siblings('.g-action-menu'),
                    w = 0;
                $menu.find('li').each(function(index, el) {
                    w += $(el).outerWidth(true);
                });
                w = w + 2;
                var _w = $(target).closest('.tree-item').outerWidth(true) - $(target).closest('.g-tree-operator').find('.g-action-btn').outerWidth(true);
                if (w > _w) {
                    $menu.css({
                        'max-width': _w + 'px',
                        'overflow-x': 'auto',
                        'overflow-y': 'hidden',
                        'white-space': 'nowrap'
                    });
                } else {
                    $menu.css({
                        'max-width': 'none',
                        'overflow-x': 'hidden',
                        'overflow-y': 'hidden'
                    });
                }

            }) // 绑定设置触发事件;

        $('.g-container').on('click', '.J-copy', function() {

            $('#copy-group').modal('show');
            sourceGroupId = $(this).closest('.g-action').attr('data-id');

            copyTree.init(); // 重置树
        });
        $('.g-container').on('click', '.J-move', function() {

            $('#move-group').modal('show');
            sourceGroupId = $(this).closest('.g-action').attr('data-id');

            moveTree.init();
        });
        $('.g-container').on('click', '.J-remove', function() {

            $('#remove-group').modal('show');
            sourceGroupId = $(this).closest('.g-action').attr('data-id');

            sourceParentGroupId = $(this).closest('.tree-item').parent().closest('.tree-item').attr('data-id');
        });

        $('body').on('click', '#remove-group .group-confirm-btn', function() {
            TMS.getData({
                    url: _WEB_SITE_ + '/group/service/delete',
                    data: {
                        id: sourceGroupId
                    },
                    type: 'post'
                },
                function(data) {
                    if (data.statusCode !== undefined && data.statusCode != '200') {
                        TMS.alert(data.message, 'danger')
                    } else {
                        TMS.alert(data.message, 'success');
                        if (sourceGroupId == _ACTIVE_GROUP_) {
                            if (sourceParentGroupId == undefined || sourceParentGroupId == null) {
                                if (pid == null || pid == undefined) {
                                    sourceParentGroupId = 1;
                                } else {
                                    sourceParentGroupId = pId;
                                }
                            }
                            setTimeout(function() {
                                window.location.href = _WEB_SITE_ + '/group/list/' + sourceParentGroupId;
                            }, 1000)
                            return false
                        }

                        myGroupTree.init();
                        TMS.topLeftTree.refresh();

                        $('#remove-group').modal('hide');
                    }
                }

            );

        });

    });
})