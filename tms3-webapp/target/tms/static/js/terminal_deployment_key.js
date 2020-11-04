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
    var laydate = require('date-picker');
    require('fileupload');
    var tsn = $("#first-tsn").val();
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
                url: _WEB_SITE_ + '/terminalDeploykey/service/getCurrentDeploysByTsn/' + _ACTIVE_GROUP_ + '/' + tsn,
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
            var self = this,
                items = data.items,
                temp = [];
            if (items.length == 0) {
                $('#deploy-list').html("<div class='alert alert-info paxinfonext text-center'><span  class='paxspannext'><strong>Hint:</strong>There is no data!</span></div>");
                return
            }
            for (var i = 0; i < items.length; i++) {
                temp.push('<section class="deploy-items">');

                if (deletePer == '1') {

                    temp.push('<div class="deploy-action" data-deployId="' + items[i].deployId + '" data-inherit="' + items[i].inherit + '">');
                    temp.push('<i class="iconfont">&#xe619;</i>');
                    temp.push('</div>');
                }

                temp.push('<div class="deploy-itemd-panel">');
                temp.push('<div class="deploy-title"><p class="p">');
                temp.push('<span class="font16px green bold deploy-title-type"><a href="' + _WEB_SITE_ + '/offlinekey/profileView/' + _ACTIVE_GROUP_ + '/' + tsn + '/' + items[i].pkgId + '">' + items[i].pkgName + '</a></span>');
                temp.push('<span class="grey font12px deploy-title-name">(Deployed by: ' + items[i].operator + ')</span>');
                temp.push('</p></div>');
                temp.push('<ul class="deploy-records clearfix">');
                temp.push('<li>');
                temp.push('<p class="font12px half-bold grey">Group/Terminal</p>');
                if (!items[i].deploySource) {
                    temp.push('<p class="font14px half-bold green"><a href="'+_WEB_SITE_ + "/terminal/profile/" +_ACTIVE_GROUP_+'/'+items[i].tsn+'" >' + items[i].tsn + '</a></p>');
                } else {
                    temp.push('<p class="font14px half-bold black" title="'+items[i].deploySourceNamePath+'">' + items[i].deploySource + '</p>');
                }
                temp.push('</li>');
                temp.push('<li>');
                temp.push('<p class="font12px half-bold grey">Key Version</p>');
                temp.push('<p class="font14px half-bold black">' + self.getValue(items[i].pkgVersion) + '</p>');
                temp.push('</li>');
                temp.push('<li>');
                temp.push('<p class="font12px half-bold grey">Key Type</p>');
                temp.push('<p class="font14px half-bold black">' + self.getValue(items[i].pgmType) + '</p>');
                temp.push('</li>');
                temp.push('<li>');
                temp.push('<p class="font12px half-bold grey">Download Time</p>');
                temp.push('<p class="font14px half-bold black">' + self.getValue(items[i].dwnlStartTimeText) + '</p>');
                temp.push('</li>');
                temp.push('<li>');
                temp.push('<p class="font12px half-bold grey">Download Status</p>');
                temp.push('<p class="font14px half-bold black">' + self.getValue(items[i].dwnStatus) + '</p>');
                temp.push('</li>');
                temp.push('<li>');
                temp.push('<p class="font12px half-bold grey">Activation Time</p>');
                temp.push('<p class="font14px half-bold black">' + self.getValue(items[i].actvStartTimeText) + '</p>');
                temp.push('</li>');
                temp.push('<li>');
                temp.push('<p class="font12px half-bold grey">Activation Status</p>');
                temp.push('<p class="font14px half-bold black">' + self.getValue(items[i].actvStatus) + '</p>');
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
            $('#deploy-list').html(temp.join(''));

        },
        getValue: function(v) {
            if (!v) {
                return '-'
            } 
            if(v=='SUCCESS'){
            	return 'SUCCESSFUL'
            }
            else {
                return v
            }
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
        bindEvent: function() {
            TMS.actionMenuEvent();

            self = this;
            var inherit = '';
            var deployId = '';
            var deployParaId = '';
            var data = {};
            $('.g-container').on('click', '.deploy-action', function() {
                inherit = $(this).attr('data-inherit');
                deployId = $(this).attr('data-deployId');

                data = {
                    tsn: tsn,
                    inherit: inherit,
                    deployId: deployId
                };
                $('#modal_terminal_delete').modal('show');
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
            $('#confirm_terminal_delete').click(function() {
                TMS.getData({
                    url: _WEB_SITE_ + '/terminalDeploykey/service/delete',
                    type: 'get',
                    data: data
                }, function(data) {
                    self.ajaxBack(data, '#modal_terminal_delete');
                });
            });
        }
    }
    $(function() {
        TMS.init();

        deployment.init();



    });
});