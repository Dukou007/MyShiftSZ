package com.pax.tms.testing.demo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.pax.common.exception.BusinessException;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class TerminalSimulator {

	private String server;
	private String port;
	private String serialNumber;

	private final static String CHS_URL = "chs/request";

	private final static String HMS_URL = "hms/uploadStatus";

	private final static String ADS_GETSCHEDULEDPACKAGES_URL = "ads/getScheduledPackages";

	private final static String ADS_GETFILESINFORMATION_URL = "ads/getFilesInformation";

	private final static String ADS_DOWNLOADFILE_URL = "ads";

	private final static String ADS_UPDATEPACKAGES_URL = "ads/updatePackagesDeploymentStatus";

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public TerminalSimulator(String server, String port, String serialNumber) {
		this.server = server;
		this.port = port;
		this.serialNumber = serialNumber;
	}

	public Map<String, Object> chs() throws Exception {
		Map<String, Object> msgMap = new HashMap<>();
		msgMap.put("version", 1);
		msgMap.put("requestType", "CALLHOME");
		msgMap.put("deviceType", "Px7");
		msgMap.put("deviceSerialNumber", serialNumber);
		msgMap.put("stateChanged", false);

		String uri = server + ":" + port + "/" + TerminalSimulator.CHS_URL;
		Map<String, Object> resultMap = sendPostRequest(Json.encode(msgMap), uri);
		if (!(((int) resultMap.get("statusCode") == 0)
				&& StringUtils.equals("SUCCESS", (String) resultMap.get("statusMessage")))) {
			throw new BusinessException();
		}

		return resultMap;
	}

	public Map<String, Object> hms(int privacyShield, int stylus) throws Exception {
		Map<String, Object> msgMap = new HashMap<>();
		Map<String, Object> terminalState = new HashMap<>();
		terminalState.put("online", 0);
		terminalState.put("privacyShieldAttached", privacyShield);
		terminalState.put("stylusAttached", stylus);
		msgMap.put("version", 1);
		msgMap.put("requestType", "UPDATEINFORMATION");
		msgMap.put("deviceType", "Px7");
		msgMap.put("deviceSerialNumber", serialNumber);
		msgMap.put("terminalState", terminalState);

		String uri = server + ":" + port + "/" + TerminalSimulator.HMS_URL;
		Map<String, Object> resultMap = sendPostRequest(Json.encode(msgMap), uri);

		if (!(((int) resultMap.get("statusCode") == 0)
				&& StringUtils.equals("SUCCESS", (String) resultMap.get("statusMessage")))) {
			throw new BusinessException();
		}
		return resultMap;
	}

	public Map<String, Object> getSchedulePackages() throws Exception {
		Map<String, Object> getScheduledPackagesMap = new HashMap<>();
		getScheduledPackagesMap.put("version", 1);
		getScheduledPackagesMap.put("requestType", "GETSCHEDULEDPACKAGES");
		getScheduledPackagesMap.put("deviceType", "Px7");
		getScheduledPackagesMap.put("deviceSerialNumber", serialNumber);

		String uri = server + ":" + port + "/" + TerminalSimulator.ADS_GETSCHEDULEDPACKAGES_URL;
		Map<String, Object> resultMap = sendPostRequest(Json.encode(getScheduledPackagesMap), uri);
		if (!(((int) resultMap.get("statusCode") == 0)
				&& StringUtils.equals("SUCCESS", (String) resultMap.get("statusMessage")))) {
			throw new BusinessException();
		}
		return resultMap;
	}

	public Map<String, Object> getFilesInformation(String deploymentUUID, String fileName) throws Exception {
		Map<String, Object> getFilesInformationMap = new HashMap<>();
		List<Map<String, Object>> filesInformationList = new ArrayList<>();
		Map<String, Object> filesInformation = new HashMap<>();
		filesInformation.put("deploymentUUID", deploymentUUID);
		filesInformation.put("fileUUID", "");
		filesInformation.put("fileName", fileName);
		filesInformationList.add(filesInformation);
		getFilesInformationMap.put("version", 1);
		getFilesInformationMap.put("requestType", "GETFILESINFORMATION");
		getFilesInformationMap.put("deviceType", "Px7");
		getFilesInformationMap.put("deviceSerialNumber", serialNumber);
		getFilesInformationMap.put("filesInformation", filesInformationList);

		String uri = server + ":" + port + "/" + TerminalSimulator.ADS_GETFILESINFORMATION_URL;
		Map<String, Object> resultMap = sendPostRequest(Json.encode(getFilesInformationMap), uri);
		if (!(((int) resultMap.get("statusCode") == 0)
				&& StringUtils.equals("SUCCESS", (String) resultMap.get("statusMessage")))) {
			throw new BusinessException();
		}

		return resultMap;
	}

	public void downLoadFile(String deploymentUUID, String fileUUID, String fileName, String md5String)
			throws Exception {
		String uri = server + ":" + port + "/" + TerminalSimulator.ADS_DOWNLOADFILE_URL
				+ "/downloadFile?version=1?requestType=DOWNLOADFILE?deviceType=Px7?deviceSerialNumber=" + serialNumber
				+ "?deploymentUUID=" + deploymentUUID + "?fileUUID=" + fileUUID + "?fileName=" + fileName;
		HttpResponse response = sendGetRequest(uri);

		// check MD5
		checkMD5(response, md5String);
	}

	public Map<String, Object> updatePackagesDeploymentStatus(String downloadStatus, String activationStatus)
			throws Exception {

		Map<String, Object> updatePackagesMap = new HashMap<>();
		List<Map<String, Object>> deploymentStatusList = new ArrayList<>();
		Map<String, Object> deploymentStatusMap = new HashMap<>();
		deploymentStatusMap.put("deploymentUUID", "19");
		deploymentStatusMap.put("downloadStatus", downloadStatus);
		deploymentStatusMap.put("activationStatus", activationStatus);
		deploymentStatusList.add(deploymentStatusMap);
		updatePackagesMap.put("version", 1);
		updatePackagesMap.put("requestType", "UPDATEPACKAGESDEPLOYMENTSTATUS");
		updatePackagesMap.put("deviceType", "Px7");
		updatePackagesMap.put("deviceSerialNumber", serialNumber);
		updatePackagesMap.put("deploymentStatus", deploymentStatusList);

		String uri = server + ":" + port + "/" + TerminalSimulator.ADS_UPDATEPACKAGES_URL;
		Map<String, Object> resultMap = sendPostRequest(Json.encode(updatePackagesMap), uri);
		if (!(((int) resultMap.get("statusCode") == 0)
				&& StringUtils.equals("SUCCESS", (String) resultMap.get("statusMessage")))) {
			throw new BusinessException();
		}

		return resultMap;

	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> down() throws Exception {
		// send chs request
		chs();

		// send getSchedulePackages request
		Map<String, Object> scheduleResult = getSchedulePackages();
		List<Map<String, Object>> packages = (List<Map<String, Object>>) scheduleResult.get("packageInformation");
		Map<String, Object> pkg = packages.get(0);
		String deployment = (String) pkg.get("deploymentUUID");
		// send getFilesInformation request
		Map<String, Object> filesInfoResult = getFilesInformation(deployment, "manifest.xml");
		List<Map<String, Object>> files = (List<Map<String, Object>>) filesInfoResult.get("filesInformation");
		Map<String, Object> file = files.get(0);
		String deploymentUUID = (String) file.get("deploymentUUID");
		String fileUUID = (String) file.get("fileUUID");
		String fileName = (String) file.get("fileName");
		String md5String = (String) file.get("md5");
		// send downLoadFile
		downLoadFile(deploymentUUID, fileUUID, fileName, md5String);

		// send updatePackages request
		Map<String, Object> updateResult = updatePackagesDeploymentStatus("Success", "Completed");

		return updateResult;
	}

	@SuppressWarnings({ "resource", "deprecation" })
	private Map<String, Object> sendPostRequest(String param, String url) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = new StringEntity(param, "UTF-8");
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		HttpResponse response = httpClient.execute(httpPost);

		String result = null;
		if (response != null) {
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				result = EntityUtils.toString(resEntity, "UTF-8");
			}
		}

		Map<String, Object> resultMap = new HashMap<>();
		JsonObject jsonObject = new JsonObject(result);
		resultMap = jsonObject.getMap();

		return resultMap;
	}

	@SuppressWarnings({ "resource", "deprecation" })
	private HttpResponse sendGetRequest(String url) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = httpClient.execute(httpGet);

		return response;
	}

	private void checkMD5(HttpResponse response, String md5String) throws Exception {

		if (response != null) {
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = entity.getContent();// 将返回的内容流入输入流内
				String actualMd5 = DigestUtils.md5Hex(inputStream);

				System.out.println("actual:" + actualMd5);
				System.out.println("expect:" + md5String);

				if (!StringUtils.endsWith(md5String, actualMd5)) {
					throw new BusinessException();
				}
			}
		}

	}

	public static void main(String[] args) throws Exception {
		TerminalSimulator terminalSimulator = new TerminalSimulator("http://192.168.6.176", "9070", "70000208");
		// Map<String, Object> resultMap = terminalSimulator.chs();
		// Map<String, Object> resultMap = terminalSimulator.hms(1, 1);
		// Map<String, Object> resultMap =
		// terminalSimulator.getSchedulePackages();
		// Map<String, Object> resultMap =
		// terminalSimulator.getFilesInformation("19","manifest.xml");
		// Map<String, Object> resultMap =
		// terminalSimulator.updatePackagesDeploymentStatus("Success",
		// "Completed");
		Map<String, Object> resultMap = terminalSimulator.down();
		System.out.println(resultMap);
	}

}
