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
    var validate = require('validate-methods');
    var selectGroup = {
        init: function() {
            this.selectGroup = [];
            this.getGroup();
            this.creatTree();

        },

        getGroup: function() {
            var self = this;
            $('.group_note_items').each(function(index, el) {
                var o = {};
                o.id = $(el).attr('data-id');
                o.path = $(el).attr('title');
                o.name = $(el).attr('data-name');
                self.selectGroup.push(o);
            })
        },
        creatTree: function() {
            var self = this;
            this.Tree = new TMS.groupTree('#add-tree', {
                isModal: true,
                nowGroup: false,
                multiselect: true,
                filterChildGroup: true,
                activeGroup: { //group 当前选择高亮
                    items: self.selectGroup
                },
                groupPanel: { //group 选择框
                    display: true,
                    titleShow: false
                }
            });
        },
    }
    $(function() {
        TMS.init();
        selectGroup.init();
        TMS.setArea('#countryId', '#provinceId');

        /*ldap y or n*/
        if (IS_LDAP) {
            $('#um_profile_edit input').attr('readonly', 'true');
        };

        $('#EditButton').click(function() {
            $("#view").hide();
            $("#edit").show();
            return false;
        });
        $('#Back').click(function() {
            $("#edit").hide();
            $("#view").show();
            return false;
        });
    });
    $("#um_profile_edit").validate({
        debug: true,
        submitHandler: function(form) {
            TMS.getData({
                url: _WEB_SITE_ + '/user/service/edit' + '/' + GROUP_ID,
                type: 'post',
                data: $("#um_profile_edit").serialize(),
                followTo: _WEB_SITE_ + '/user/view/' + USER_ID

            });
            return false;
        }
    });
})