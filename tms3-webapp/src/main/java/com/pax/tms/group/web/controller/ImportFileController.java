/*		
 * ============================================================================		
 * = COPYRIGHT		
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *      Copyright (C) 2016-? PAX Technology, Inc. All rights reserved.		
 * Description: Import/Delete/List file
 * Revision History:		
 * Date	                 Author	                Action
 * 20170111  	         Aaron           	    Modify ImportFileController
 * ============================================================================		
 */	
package com.pax.tms.group.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pax.common.exception.BusinessException;
import com.pax.common.pagination.Page;
import com.pax.common.security.CsrfProtect;
import com.pax.common.web.controller.BaseController;
import com.pax.tms.group.model.Group;
import com.pax.tms.group.service.GroupService;
import com.pax.tms.group.service.ImportFileService;
import com.pax.tms.group.web.form.DeleteFileForm;
import com.pax.tms.group.web.form.ImportFileForm;
import com.pax.tms.group.web.form.QueryFileForm;
import com.pax.tms.user.security.AclManager;


@Controller
@RequestMapping(value = "/importFile")
public class ImportFileController extends BaseController {

	@Autowired
	private ImportFileService importFileService;
	@Autowired
    private GroupService groupService;

	@ResponseBody
	@RequestMapping(value = "/service/save", method = { RequestMethod.PUT,RequestMethod.POST})
	@CsrfProtect
	public Map<String, Object> save(ImportFileForm command, HttpServletRequest request)
			throws FileUploadException {
		if (command.getParentId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		return importFileService.save(command);
	}

	@ResponseBody
	@RequestMapping(value = "/service/ajaxList")
	public Page<Map<String, Object>> getFileList(QueryFileForm command) {
		if (command.getGroupId() == null) {
			throw new BusinessException("msg.group.groupRequired");
		}
		Page<Map<String, Object>> page = importFileService.page(command);
		return page;
	}

	@ResponseBody
	@RequestMapping(value = "/service/delete")
	@CsrfProtect
	public Map<String, Object> delete(DeleteFileForm deleteFileForm) {
		if (deleteFileForm.getFileId() == null) {
			throw new BusinessException("msg.importFile.Required");
		}
		if (deleteFileForm.getGroupId() == null) {
            throw new BusinessException("msg.group.groupRequired");
        }
        Group group = groupService.get(deleteFileForm.getGroupId());
        AclManager.checkPermissionOfGroup(deleteFileForm.getLoginUserId(), group);
        AclManager.checkPermissionOfImportFileByGroup(deleteFileForm.getGroupId(), deleteFileForm.getFileId());
		importFileService.delete(deleteFileForm.getFileId());
		return this.ajaxDoneSuccess();
	}

}
