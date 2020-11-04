/*
* ========================================================================
* COPYRIGHT
*              PAX TECHNOLOGY, INC. PROPRIETARY INFORMATION
*   This software is supplied under the terms of a license agreement or
*   nondisclosure agreement with PAX Technology, Inc. and may not be copied 
 *   or disclosed except in accordance with the terms in that agreement.
*      Copyright (C) 2009-2015 PAX Technology, Inc. All rights reserved.
*
* ========================================================================
*/
package com.pax.tms.webservice.pxmaster;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.pax.common.exception.AppException;
import com.pax.common.exception.BusinessException;
import com.pax.common.web.HttpUtils;
import com.pax.tms.app.phoenix.PackageException;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.terminal.model.Terminal;
import com.pax.tms.webservice.pxmaster.form.BaseResponse;
import com.pax.tms.webservice.pxmaster.form.PackageFile;
import com.pax.tms.webservice.pxmaster.form.PubUser;

/**
 * @author Dai.L
 * @date 2014-10-23
 */
@WebService(targetNamespace = "http://ppm.paxsz.com/")
public class DeployWebServiceImpl implements DeployWebService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeployWebServiceImpl.class);

	/**
	 * the responseCode of Get PACKAGE uuid error
	 */
	private static final String RESPONSECODE_GETPACKAGEUUIDRROR = "PDS1003";
	/**
	 * the responseMessage of Get PACKAGE uuid error
	 */
	private static final String RESPONSEMESSAGE_GETPACKAGEUUIDRROR = "Package does not exist";

	/**
	 * the responseCode of Parameter format invalid
	 */
	private static final String RESPONSECODE_FORMATINVALID = "PDS1002";

	/**
	 * the responseCode of system error
	 */
	private static final String RESPONSECODE_SYSTEMERROR = "PDS9001";
	/**
	 * the responseMessage of system error
	 */
	private static final String RESPONSEMESSAGE_SYSTEMERROR = "System error";

	/**
	 * the responseCode of Parameter format invalid
	 */
	private static final String RESPONSECODE_BUSERROR = "PDS9002";

	/**
	 * the responseCode of Get Tsn error
	 */
	private static final String RESPONSECODE_GETTSNERROR = "PDS1005";
	/**
	 * the responseMessage of Get Tsn error
	 */
	private static final String RESPONSEMESSAGE_GETTSNERROR = "Terminal SN invalid";

	@Autowired
	@Qualifier("pxmasterPubPackageService")
	private PubPackageService pubPackageService;

	@Autowired
	@Qualifier("pxmasterPubPackageDeployService")
	private PubPackageDeployService pubPackageDeployService;

	@Autowired
	@Qualifier("pxmasterTmsTerminalService")
	private PxTerminalService tmsTerminalService;

	@Resource
	private WebServiceContext context;

	@Autowired(required = false)
	@Qualifier("messageSource")
	private ReloadableResourceBundleMessageSource messageSource;

	private Locale locale = Locale.forLanguageTag("en");

	@Override
	public BaseResponse addPackageByGroupId(List<Long> groupIds, List<PackageFile> uploadFileList, PubUser user) {
		BaseResponse response = new BaseResponse();
		try {
			this.pubPackageService.addPackageByGroupId(response, groupIds, uploadFileList, user, getRemoteAddr());
		} catch (PackageException pe) {
			LOGGER.error(pe.getMessage(), pe);
			response.setResponseCode(RESPONSECODE_FORMATINVALID);
			response.setResponseMessage(getMessage(pe.getErrorCode(), pe.getArgs()));
			return response;
		} catch (BusinessException be) {
			LOGGER.error(be.getMessage(), be);
			response.setResponseCode(RESPONSECODE_BUSERROR);
			response.setResponseMessage(getMessage(be.getErrorCode(), be.getArgs()));
			return response;
		} catch (AppException e) {
			LOGGER.error(e.getMessage(), e);
			response.setResponseCode(RESPONSECODE_SYSTEMERROR);
			response.setResponseMessage(RESPONSEMESSAGE_SYSTEMERROR);
			return response;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			response.setResponseCode(BaseResponse.RESPONSECODE_DATABASEERROR);
			response.setResponseMessage(BaseResponse.RESPONSEMESSAGE_DATABASEERROR);
			return response;
		}
		return response;
	}

	@Override
	public BaseResponse deployPackageByGroupId(Long groupId, String uuid, Date deployTime, String activationMode,
			Date activationTime, PubUser user) {
		BaseResponse response = new BaseResponse();

		String pkgUuid = uuid;
		if (uuid.contains("_")) {
			String[] ids = uuid.split("_", 2);
			pkgUuid = ids[1];
		}

		List<Pkg> pubPackages = this.pubPackageService.getPubPackagesByUuid(pkgUuid);
		if (pubPackages == null || pubPackages.isEmpty()) {
			response.setResponseCode(RESPONSECODE_GETPACKAGEUUIDRROR);
			response.setResponseMessage(RESPONSEMESSAGE_GETPACKAGEUUIDRROR);
			return response;
		}
		Pkg pubPackage = pubPackages.get(0);

		try {
			this.pubPackageDeployService.deployPackageByGroupId(groupId, pubPackage, deployTime, activationMode,
					activationTime, user.getId(), getRemoteAddr());
		} catch (BusinessException be) {
			LOGGER.error(be.getMessage(), be);
			response.setResponseCode(RESPONSECODE_BUSERROR);
			response.setResponseMessage(getMessage(be.getErrorCode(), be.getArgs()));
			return response;
		} catch (AppException e) {
			LOGGER.error(e.getMessage(), e);
			response.setResponseCode(RESPONSECODE_SYSTEMERROR);
			response.setResponseMessage(RESPONSEMESSAGE_SYSTEMERROR);
			return response;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			response.setResponseCode(BaseResponse.RESPONSECODE_DATABASEERROR);
			response.setResponseMessage(BaseResponse.RESPONSEMESSAGE_DATABASEERROR + ","
					+ (e.getCause() != null ? e.getCause().toString() : e.getMessage()));
			return response;
		}

		response.setResponseCode(BaseResponse.RESPONSECODE_SUCCESS);
		response.setResponseMessage(BaseResponse.RESPONSEMESSAGE_SUCCESS);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pax.tms.webservice.pxmaster.DeployWebService#deployPackageBySn(java.
	 * util.List, java.lang.String, java.util.Date, java.lang.String,
	 * java.util.Date, com.pax.tms.webservice.pxmaster.form.PubUser)
	 */
	@Override
	public BaseResponse deployPackageBySn(List<String> tsnList, String uuid, Date deployTime, String activationMode,
			Date activationTime, PubUser user) {
		BaseResponse response = new BaseResponse();

		for (String tsn : tsnList) {
			Terminal terminal = this.tmsTerminalService.getTerminalByTsn(tsn);
			if (terminal == null) {
				response.setResponseCode(RESPONSECODE_GETTSNERROR);
				response.setResponseMessage(RESPONSEMESSAGE_GETTSNERROR);
				return response;
			}
		}

		String pkgUuid = uuid;
		if (uuid.contains("_")) {
			String[] ids = uuid.split("_", 2);
			pkgUuid = ids[1];
		}

		List<Pkg> pubPackages = this.pubPackageService.getPubPackagesByUuid(pkgUuid);
		if (pubPackages == null || pubPackages.isEmpty()) {
			response.setResponseCode(RESPONSECODE_GETPACKAGEUUIDRROR);
			response.setResponseMessage(RESPONSEMESSAGE_GETPACKAGEUUIDRROR);
			return response;
		}
		Pkg pubPackage = pubPackages.get(0);

		try {
			this.pubPackageDeployService.deployPackage(tsnList, pubPackage, deployTime, activationMode, activationTime,
					user.getId(), getRemoteAddr());
		} catch (BusinessException be) {
			LOGGER.error(be.getMessage(), be);
			response.setResponseCode(RESPONSECODE_BUSERROR);
			response.setResponseMessage(getMessage(be.getErrorCode(), be.getArgs()));
			return response;
		} catch (AppException e) {
			LOGGER.error(e.getMessage(), e);
			response.setResponseCode(RESPONSECODE_SYSTEMERROR);
			response.setResponseMessage(RESPONSEMESSAGE_SYSTEMERROR);
			return response;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			response.setResponseCode(BaseResponse.RESPONSECODE_DATABASEERROR);
			response.setResponseMessage(BaseResponse.RESPONSEMESSAGE_DATABASEERROR + ","
					+ (e.getCause() != null ? e.getCause().toString() : e.getMessage()));
			return response;
		}

		response.setResponseCode(BaseResponse.RESPONSECODE_SUCCESS);
		response.setResponseMessage(BaseResponse.RESPONSEMESSAGE_SUCCESS);
		return response;
	}

	private String getMessage(String code, String[] args) {
		if (messageSource == null) {
			return code;
		}
		try {
			return messageSource.getMessage(code, args, locale);
		} catch (NoSuchMessageException e) {
			return code;
		}
	}

	private String getRemoteAddr() {
		MessageContext ctx = context.getMessageContext();
		HttpServletRequest request = (HttpServletRequest) ctx.get(AbstractHTTPDestination.HTTP_REQUEST);
		return HttpUtils.getRemoteAddr(request);
	}

}