/*	
 * Copyright (C) 2018 PAX Computer Technology(Shenzhen) CO., LTD。 All rights reserved.			
 * ----------------------------------------------------------------------------------
 * PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION。
 * 
 * This software is supplied under the terms of a license agreement or nondisclosure 
 * agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied 
 * or disclosed except in accordance with the terms in that agreement.
 */
package com.pax.tms.webservice.pxmaster;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.TrustManager;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;

import com.pax.tms.webservice.pxmaster.form.BaseResponse;
import com.pax.tms.webservice.pxmaster.form.PackageFile;
import com.pax.tms.webservice.pxmaster.form.PubUser;

/**
 * @author Elliott.Z
 *
 */
public class CxfClient {

	String serviceAddress = "https://localhost:8443/tms/webservice/deployWebService?wsdl";

	private DeployWebService deployWebService;

	public void init() {

		JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
		jaxWsProxyFactoryBean.setAddress(serviceAddress);
		jaxWsProxyFactoryBean.setServiceClass(DeployWebService.class);
		deployWebService = (DeployWebService) jaxWsProxyFactoryBean.create();

		trustAll(deployWebService);
	}

	private void trustAll(Object service) {
		Client proxy = ClientProxy.getClient(service);
		HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
		TLSClientParameters tcp = new TLSClientParameters();
		tcp.setTrustManagers(new TrustManager[] { SslUtils.trustAllX509TrustManager() });
		tcp.setDisableCNCheck(true);
		conduit.setTlsClientParameters(tcp);
	}

	public void testAddPackageByGroupId() throws IOException {
		init();
		List<Long> groupIds = new ArrayList<>();
		groupIds.add(1000L);

		List<PackageFile> uploadFileList = new ArrayList<>();
		PackageFile packageFile = new PackageFile();
		packageFile.setName("PXRetailer-0.91.0.zip");
		byte[] content = FileUtils.readFileToByteArray(
				new File("D:\\Eclipse\\PPM-1\\tms3\\tms3-webapp\\PXMaster package\\PXRetailer-0.91.0.zip"));
		packageFile.setContent(content);
		uploadFileList.add(packageFile);

		PubUser user = new PubUser();
		user.setId(1L);
		BaseResponse response = deployWebService.addPackageByGroupId(groupIds, uploadFileList, user);
		System.out.println(response.getResponseCode());
		System.out.println(response.getResponseMessage());
		System.out.println(response.getUuid());
	}

	public void testDeployPackageByGroupId() throws IOException {
		JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
		jaxWsProxyFactoryBean.setAddress(serviceAddress);
		jaxWsProxyFactoryBean.setServiceClass(DeployWebService.class);
		deployWebService = (DeployWebService) jaxWsProxyFactoryBean.create();

		trustAll(deployWebService);
		
		Long groupId = 1000L;

		List<Long> groupIds = new ArrayList<>();
		groupIds.add(groupId);

		List<PackageFile> uploadFileList = new ArrayList<>();
		PackageFile packageFile = new PackageFile();
		packageFile.setName("PXRetailer-0.91.0.zip");
		byte[] content = FileUtils.readFileToByteArray(
				new File("D:\\Eclipse\\PPM-1\\tms3\\tms3-webapp\\PXMaster package\\PXRetailer-0.91.0.zip"));
		packageFile.setContent(content);
		uploadFileList.add(packageFile);

		PubUser user = new PubUser();
		user.setId(1L);
		BaseResponse response = deployWebService.addPackageByGroupId(groupIds, uploadFileList, user);
		System.out.println(response.getResponseCode());
		System.out.println(response.getResponseMessage());
		System.out.println(response.getUuid());

		response = deployWebService.deployPackageByGroupId(groupId, response.getUuid(), null, null, null, user);
		System.out.println(response.getResponseCode());
		System.out.println(response.getResponseMessage());
		System.out.println(response.getUuid());
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		CxfClient client = new CxfClient();
		client.testDeployPackageByGroupId();
		// client.updateTerminal();
	}

}
