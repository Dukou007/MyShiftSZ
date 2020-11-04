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
	var vid = $("#vid").val();
	var groupId = $("#gid").val();
	require('validate-methods');
	$(function(){
		TMS.init();
		$('#globalSettingForm select').css('cursor','not-allowed');
		$('#globalEdit').click(function() {
			$("#viewBtnGroup").addClass('hide');
			$("#editBtnGroup").removeClass('hide');	
			$('#globalSettingForm select').css('cursor','pointer');
			$('#globalSettingForm input').removeAttr('disabled');
			$('#globalSettingForm select').removeAttr('disabled');
			$('.global-input1:first > input').focus();
		});	
		
		$('#Back').click(function() {
			$("#viewBtnGroup").removeClass('hide');	
			$("#editBtnGroup").addClass('hide');
			TMS.backReset($("#globalSettingForm"));
			$('#globalSettingForm input').attr('disabled','true');
			$('#globalSettingForm select').attr('disabled','true');
			$('#globalSettingForm select').css('cursor','not-allowed');
        });
		
		$("#globalSettingForm").validate({
			
		debug: true,
            submitHandler: function(form) {
                TMS.getData({
                    url: _WEB_SITE_+'/usage/service/edit',
                    type: 'post',
                    data: getJson(),
                    followTo:_WEB_SITE_+"/usage/list/"+groupId+"/"+vid
                });
              
            }
        });
		
	function getJson() {
			var jsonArr = [],itemNames = [], thdValues = [], reportCycles = []; 
			var o = {};
			o.GroupId = $('#globalSettingForm').find('input[name="groupId"]').val();
			$('.singleRecord').each(
					function(index, el) {
						var itemName = $(el).find('input[name="itemName"]').val();
						itemNames.push(itemName); 
						var thdValue= $(el).find('.thdValue').val();
						thdValues.push(thdValue);
						var reportCycle = $(el).find('select[name="reportCycle"]').val();
						reportCycles.push(reportCycle);
					
			});
			o.itemName=itemNames.join(",");
			o.thdValue=thdValues.join(",");
			o.reportCycle=reportCycles.join(",");
		
			return o;
		}
	});
});

