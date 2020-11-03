/*
 * ============================================================================
 * = COPYRIGHT				
 *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION		
 *   This software is supplied under the terms of a license agreement or		
 *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied		
 *   or disclosed except in accordance with the terms in that agreement.		
 *   	Copyright (C) 2009-2020 PAX Technology, Inc. All rights reserved.		
 * ============================================================================		
 */
package com.pax.tms.webservice.pxmaster.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.pax.common.exception.AppException;
import com.pax.common.exception.BusinessException;
import com.pax.tms.app.phoenix.PackageException;
import com.pax.tms.group.dao.GroupDao;
import com.pax.tms.res.dao.PkgDao;
import com.pax.tms.res.model.Pkg;
import com.pax.tms.res.model.PkgType;
import com.pax.tms.res.service.PkgService;
import com.pax.tms.res.web.form.AddPkgForm;
import com.pax.tms.webservice.pxmaster.BaseServiceImpl;
import com.pax.tms.webservice.pxmaster.PubPackageService;
import com.pax.tms.webservice.pxmaster.form.BaseResponse;
import com.pax.tms.webservice.pxmaster.form.PubPackage;
import com.pax.tms.webservice.pxmaster.form.PubUser;
import com.pax.tms.webservice.pxmaster.form.PackageFile;

@Service("pxmasterPubPackageService")
public class PubPackageServiceImpl extends BaseServiceImpl implements PubPackageService {

	/**
	 * the responseCode of Parameter format invalid
	 */
	public static final String RESPONSECODE_FORMATINVALID = "PDS1002";

	/**
	 * the responseMessage of package is empty
	 */
	public static final String RESPONSEMESSAGE_FORMATINVALID_PACKAGE_CONTENT_NULL = "Package file is empty";

	@Autowired
	private PkgService pkgService;

	@Autowired
	private PkgDao pkgDao;

	@Autowired
	@Qualifier("groupDaoImpl")
	private GroupDao groupDao;

	private static String storeUploadFileTemp(String filename, byte[] inputByte, String user) throws IOException {
		String temp = new SimpleDateFormat("yyMMdd").format(new Date()) + "_"
				+ (StringUtils.isEmpty(user) ? "unknown" : user);
		String tempDir = Files.createTempDirectory(temp).toString();
		String tempFilePath = tempDir + File.separator + filename;
		FileUtils.writeByteArrayToFile(new File(tempFilePath), inputByte);
		return tempFilePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.pax.tms.webservice.pxmaster.PubPackageService#addPackageByGroupId(
	 * java.lang.Long, java.util.List,
	 * com.pax.tms.webservice.pxmaster.form.PubUser, java.lang.String)
	 */
	@Override
	public void addPackageByGroupId(BaseResponse response, List<Long> groupIds, List<PackageFile> uploadFileList,
			PubUser user, String remoteAddress) {
		CommonProfile userProfile = loadTmsUserProfile(user);
		if (userProfile != null) {
			userProfile.addAttribute("host", remoteAddress);
		}
		for (PackageFile uploadFile : uploadFileList) {
			String name = uploadFile.getName();
			byte[] inputByte = uploadFile.getContent();
			savePackage(response, groupIds, inputByte, name, user, userProfile);
			if (!StringUtils.equals(response.getResponseCode(), BaseResponse.RESPONSECODE_SUCCESS)) {
				return;
			}
		}
	}

	private void savePackage(BaseResponse response, List<Long> groupIds, byte[] inputByte, String name, PubUser user,
			CommonProfile userProfile) {
		if (StringUtils.isEmpty(name) || null == inputByte || inputByte.length == 0) {
			throw new PackageException("msg.phonenixPackage.emptyFileContent");
		}

		String filePath;
		try {
			filePath = storeUploadFileTemp(name, inputByte, "" + user.getId());
		} catch (IOException e) {
			throw new AppException(e);
		}

		savePackage(response, groupIds, filePath, name, user, userProfile, null);
	}

	private void savePackage(BaseResponse response, List<Long> groupIds, String filePath, String name, PubUser user,
			CommonProfile userProfile, Set<Long> extendedGrantedGroups) {
		AddPkgForm form = new AddPkgForm();
		setTmsUserProfile(form, userProfile);

		Set<Long> grantedGroups = new HashSet<>();
		if (extendedGrantedGroups != null && !extendedGrantedGroups.isEmpty()) {
			grantedGroups.addAll(extendedGrantedGroups);
		}
		if (groupIds != null) {
			grantedGroups.addAll(groupIds);
		} else {
			grantedGroups.addAll(groupDao.getGrantedAllGroups(user.getId()));
		}
		form.setGroupIds(grantedGroups.toArray(new Long[0]));

		form.setFilePath(filePath);
		form.setFileName(name);
		form.setType(PkgType.MULTILANE.getPkgName());

		try {
			Pkg pkg = pkgService.saveOrUpdate(form);
			response.setUuid("" + pkg.getId());
			response.setResponseCode(BaseResponse.RESPONSECODE_SUCCESS);
			response.setResponseMessage(BaseResponse.RESPONSEMESSAGE_SUCCESS);
		} catch (BusinessException e) {
			if ("msg.pkg.existNameAndVersion".equals(e.getErrorCode())) {
				response.setUuid(e.getArgs()[1]);
			}
			throw e;
		}
	}

	@Override
	public List<Pkg> getPubPackagesByUuid(String uuid) {
		if (uuid == null || uuid.isEmpty()) {
			return Collections.emptyList();
		}
		long id = 0;
		try {
			id = Long.parseLong(uuid);
		} catch (NumberFormatException e) {
			return Collections.emptyList();
		}
		Pkg pkg = pkgService.get(id);
		if (pkg == null) {
			return Collections.emptyList();
		}
		List<Pkg> pkgList = new ArrayList<>(1);
		pkgList.add(pkg);
		return pkgList;
	}

	@Override
	public List<PubPackage> getPackages(String name, String type, String version) {
		List<Pkg> packages = pkgDao.getPackages(name, version, type, PkgType.MULTILANE);
		List<PubPackage> pps = new ArrayList<>(packages.size());
		for (Pkg pkg : packages) {
			pps.add(createPubPackage(pkg));
		}
		return pps;
	}

	@Override
	public PubPackage get(Long id) {
		Pkg pkg = pkgDao.get(id);
		return createPubPackage(pkg);
	}

	private PubPackage createPubPackage(Pkg pkg) {
		if (pkg == null) {
			return null;
		}
		PubPackage pp = new PubPackage();
		pp.setId(pkg.getId());
		pp.setUuid("" + pkg.getId());
		pp.setName(pkg.getName());
		pp.setVersion(pkg.getVersion());
		pp.setType(pkg.getPgmType());
		return pp;
	}

	@Override
	public List<PubPackage> getPackagesByName(String name) {
		List<Pkg> packages = pkgDao.getPackages(name, PkgType.MULTILANE);
		List<PubPackage> pps = new ArrayList<>(packages.size());
		for (Pkg pkg : packages) {
			pps.add(createPubPackage(pkg));
		}
		return pps;
	}

}
