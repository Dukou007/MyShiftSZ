package com.pax.tms.pxretailer.client;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.pax.tms.download.model.TerminalUsageReport;
import com.pax.tms.pxretailer.message.CallHomeRequest;
import com.pax.tms.pxretailer.message.CallHomeResponse;
import com.pax.tms.pxretailer.message.DeploymentStatus;
import com.pax.tms.pxretailer.message.FileInfo;
import com.pax.tms.pxretailer.message.GetFileInfoResponse;
import com.pax.tms.pxretailer.message.GetFilesInfoRequest;
import com.pax.tms.pxretailer.message.GetScheduledPackagesRequest;
import com.pax.tms.pxretailer.message.GetScheduledPackagesResponse;
import com.pax.tms.pxretailer.message.HmsRequest;
import com.pax.tms.pxretailer.message.HmsResponse;
import com.pax.tms.pxretailer.message.TerminalInstalledApp;
import com.pax.tms.pxretailer.message.TerminalState;
import com.pax.tms.pxretailer.message.UpdateDeploymentStatusRequest;
import com.pax.tms.pxretailer.message.UpdateDeploymentStatusResponse;

@RunWith(VertxUnitRunner.class)
public class PxRetailerClient {

	private Vertx vertx;
	private String host = "localhost";// 192.168.6.176
	private int port = 8089;
	private String deviceSerialNumber = "PX7L0000";
	private String deviceType = "Px7";

	private String deviceSerialNumber2 = "1234567891";
	private String deviceType2 = "S80";

	@Before
	public void setUp(TestContext context) throws IOException {
		vertx = Vertx.vertx();
	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}

	@Test
	public void testChs(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber(deviceSerialNumber);
		req.setStateChanged(true);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 0);
			context.assertEquals(resp.getStatusMessage(), "SUCCESS");
			context.assertNotNull(resp.getContactServices());
			// context.assertEquals(resp.getContactServices().get(0).serviceName,
			// "ads");
		});
	}

	@Test
	public void testChs2(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber(deviceSerialNumber);
		req.setStateChanged(true);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 0);
			context.assertEquals(resp.getStatusMessage(), "SUCCESS");
			context.assertNotNull(resp.getContactServices());
			// context.assertEquals(resp.getContactServices().get(0).serviceName,
			// "ads");
		});
	}

	@Test
	public void testChsTerminalNotExists(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType2);
		req.setDeviceSerialNumber(deviceSerialNumber2);
		req.setStateChanged(true);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 1);
			context.assertEquals(resp.getStatusMessage(), "Terminal is not registerd or has been disabled");
		});
	}

	@Test
	public void testChs3(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber(this.deviceSerialNumber);
		req.setStateChanged(false);

		for (int i = 0; i < 3; i++) {
			sendCallHomeRequest(context, req, resp -> {
				context.assertEquals(resp.getVersion(), 1);
				context.assertEquals(resp.getResponseType(), req.getRequestType());
				context.assertEquals(resp.getStatusCode(), 0);
				context.assertEquals(resp.getStatusMessage(), "SUCCESS");
				context.assertNotNull(resp.getContactServices());
				// context.assertEquals(resp.getContactServices().get(0).serviceName,
				// "ads");
			});
		}
	}

	private void sendCallHomeRequest(TestContext context, CallHomeRequest req, Handler<CallHomeResponse> handler) {
		Async async = context.async();
		final String json = Json.encode(req);
		final String length = Integer.toString(json.length());
		vertx.createHttpClient().post(port, host, "/chs/request").putHeader("content-type", "application/json")
				.putHeader("content-length", length).handler(response -> {
					context.assertEquals(response.statusCode(), 200);
					context.assertTrue(response.headers().get("content-type").contains("application/json"));
					response.bodyHandler(body -> {
						System.out.println("Response Received: " + body.toString());
						CallHomeResponse resp = new CallHomeResponse();
						try {
							resp = Json.decodeValue(body.toString(), CallHomeResponse.class);
						} catch (DecodeException e) {
							e.printStackTrace();
						}
						handler.handle(resp);

						async.complete();
					});
				}).write(json).end();
	}

	@Test
	public void testGetScheduledPackages(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber(deviceSerialNumber);
		req.setStateChanged(false);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 0);
			context.assertEquals(resp.getStatusMessage(), "SUCCESS");
			context.assertNotNull(resp.getContactServices());
			if ("ads".equals(resp.getContactServices().get(0).getServiceName())) {
				System.out.println("sendGetScheduledPackagesRequest");
				sendGetScheduledPackagesRequest(context, req, resp, null);
			}
		});
	}

	@Test
	public void testAds3(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber("12345679");
		req.setStateChanged(false);

		for (int i = 0; i < 3; i++) {
			sendCallHomeRequest(context, req, resp -> {
				context.assertEquals(resp.getVersion(), 1);
				context.assertEquals(resp.getResponseType(), req.getRequestType());
				context.assertEquals(resp.getStatusCode(), 0);
				context.assertEquals(resp.getStatusMessage(), "SUCCESS");
				context.assertNotNull(resp.getContactServices());
				// context.assertEquals(resp.getContactServices().get(0).serviceName,
				// "ads");
			});
		}
	}

	private void sendGetScheduledPackagesRequest(TestContext context, CallHomeRequest callHomeReq,
			CallHomeResponse callHomeResp, Handler<GetScheduledPackagesResponse> handler) {
		GetScheduledPackagesRequest req = new GetScheduledPackagesRequest();
		req.setDeviceType(callHomeReq.getDeviceType());
		req.setDeviceSerialNumber(callHomeReq.getDeviceSerialNumber());

		sendGetScheduledPackagesRequest(context, req, callHomeResp.getContactServices().get(0).getServiceURI(),
				resp -> {
					context.assertEquals(resp.getStatusCode(), 0);
					context.assertEquals(resp.getStatusMessage(), "SUCCESS");
					context.assertNotNull(resp.getPackageInformation());
					context.assertNotNull(resp.getPackageInformation().get(0).getDeploymentUUID());
					context.assertNotNull(resp.getPackageInformation().get(0).getFileUUID());
					if (handler != null) {
						handler.handle(resp);
					}
				});
	}

	private void sendGetScheduledPackagesRequest(TestContext context, GetScheduledPackagesRequest req,
			String serviceUrl, Handler<GetScheduledPackagesResponse> handler) {
		URL url = null;
		try {
			url = new URL(serviceUrl);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		Async async = context.async();
		final String json = Json.encode(req);
		final String length = Integer.toString(json.length());
		vertx.createHttpClient().post(port, host, url.getPath() + "/getScheduledPackages")
				.putHeader("content-type", "application/json").putHeader("content-length", length).handler(response -> {
					context.assertEquals(response.statusCode(), 200);
					context.assertTrue(response.headers().get("content-type").contains("application/json"));
					response.bodyHandler(body -> {
						System.out.println("Response Received: " + body.toString());
						GetScheduledPackagesResponse resp = new GetScheduledPackagesResponse();
						try {
							resp = Json.decodeValue(body.toString(), GetScheduledPackagesResponse.class);
						} catch (DecodeException e) {
							e.printStackTrace();
						}

						handler.handle(resp);
						context.assertEquals(resp.getVersion(), 1);
						context.assertEquals(resp.getResponseType(), req.getRequestType());
						context.assertEquals(resp.getStatusCode(), 0);
						context.assertEquals(resp.getStatusMessage(), "SUCCESS");
						context.assertNotNull(resp.getPackageInformation());
						context.assertNotNull(resp.getPackageInformation().get(0).getDeploymentUUID());
						context.assertNotNull(resp.getPackageInformation().get(0).getFileUUID());

						async.complete();
					});
				}).write(json).end();
	}

	@Test
	public void testGetFilesInformation(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber(deviceSerialNumber);
		req.setStateChanged(false);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 0);
			context.assertEquals(resp.getStatusMessage(), "SUCCESS");
			context.assertNotNull(resp.getContactServices());
			if ("ads".equals(resp.getContactServices().get(0).getServiceName())) {
				sendGetScheduledPackagesRequest(context, req, resp, packageResp -> {
					sendGetFilesInformationRequest(context, req, resp, packageResp, null);
				});
			}
		});
	}

	@Test
	public void testGetFilesInformation2(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber(deviceSerialNumber);
		req.setStateChanged(false);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 0);
			context.assertEquals(resp.getStatusMessage(), "SUCCESS");
			context.assertNotNull(resp.getContactServices());
			if ("ads".equals(resp.getContactServices().get(0).getServiceName())) {
				sendGetScheduledPackagesRequest(context, req, resp, packageResp -> {
					sendGetFilesInformationRequest2(context, req, resp, packageResp, null);
				});
			}
		});
	}

	private void sendGetFilesInformationRequest(TestContext context, CallHomeRequest callHomeReq,
			CallHomeResponse callHomeResp, GetScheduledPackagesResponse packageResp,
			Handler<GetFileInfoResponse> handler) {
		GetFilesInfoRequest req = new GetFilesInfoRequest();
		req.setDeviceType(callHomeReq.getDeviceType());
		req.setDeviceSerialNumber(callHomeReq.getDeviceSerialNumber());

		List<FileInfo> filesInformation = new ArrayList<FileInfo>();
		FileInfo fileInfoRequest = new FileInfo();
		fileInfoRequest.setDeploymentUUID(packageResp.getPackageInformation().get(0).getDeploymentUUID());
		fileInfoRequest.setFileUUID(packageResp.getPackageInformation().get(0).getFileUUID());
		// fileInfoRequest.setFileName(fileName);
		filesInformation.add(fileInfoRequest);
		req.setFilesInformation(filesInformation);
		sendGetFilesInformationRequest(context, req, callHomeResp.getContactServices().get(0).getServiceURI(), handler);
	}

	private void sendGetFilesInformationRequest2(TestContext context, CallHomeRequest callHomeReq,
			CallHomeResponse callHomeResp, GetScheduledPackagesResponse packageResp,
			Handler<GetFileInfoResponse> handler) {
		GetFilesInfoRequest req = new GetFilesInfoRequest();
		req.setDeviceType(callHomeReq.getDeviceType());
		req.setDeviceSerialNumber(callHomeReq.getDeviceSerialNumber());

		List<FileInfo> filesInformation = new ArrayList<FileInfo>();
		FileInfo fileInfoRequest = new FileInfo();
		fileInfoRequest.setDeploymentUUID(packageResp.getPackageInformation().get(0).getDeploymentUUID());
		// fileInfoRequest.setFileUUID(packageResp.getPackageInformation().get(0).getFileUUID());
		fileInfoRequest.setFileName("manifest.xml");// PXRetailer,manifest.xml
		filesInformation.add(fileInfoRequest);
		req.setFilesInformation(filesInformation);
		sendGetFilesInformationRequest(context, req, callHomeResp.getContactServices().get(0).getServiceURI(), handler);
	}

	// @Test
	// public void sendGetFilesInformationRequest3(TestContext context) {
	// GetFilesInfoRequest req = new GetFilesInfoRequest();
	// req.setDeviceType(deviceType);
	// req.setDeviceSerialNumber(deviceSerialNumber);
	//
	// List<FileInfo> filesInformation = new ArrayList<FileInfo>();
	// FileInfo fileInfoRequest = new FileInfo();
	// fileInfoRequest.setDeploymentUUID("1111111111");
	// //
	// fileInfoRequest.setFileUUID(packageResp.getPackageInformation().get(0).getFileUUID());
	// fileInfoRequest.setFileName("manifest.xml");// PXRetailer,manifest.xml
	// filesInformation.add(fileInfoRequest);
	// req.setFilesInformation(filesInformation);
	// sendGetFilesInformationRequest(context, req, "http://" + host + ":" +
	// port + "/ads/", null);
	// }

	private void sendGetFilesInformationRequest(TestContext context, GetFilesInfoRequest req, String serviceUrl,
			Handler<GetFileInfoResponse> handler) {
		URL url = null;
		try {
			url = new URL(serviceUrl);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		Async async = context.async();
		final String json = Json.encode(req);
		final String length = Integer.toString(json.length());
		vertx.createHttpClient().post(port, host, url.getPath() + "/getFilesInformation")
				.putHeader("content-type", "application/json").putHeader("content-length", length).handler(response -> {
					context.assertEquals(response.statusCode(), 200);
					context.assertTrue(response.headers().get("content-type").contains("application/json"));
					response.bodyHandler(body -> {
						System.out.println("Response Received: " + body.toString());
						GetFileInfoResponse resp = new GetFileInfoResponse();
						try {
							resp = Json.decodeValue(body.toString(), GetFileInfoResponse.class);
						} catch (DecodeException e) {
							e.printStackTrace();
						}
						context.assertEquals(resp.getVersion(), 1);
						context.assertEquals(resp.getResponseType(), req.getRequestType());
						context.assertEquals(resp.getStatusCode(), 0);
						context.assertEquals(resp.getStatusMessage(), "SUCCESS");
						context.assertNotNull(resp.getFilesInformation());
						if (handler != null) {
							handler.handle(resp);
						}
						async.complete();
					});
				}).write(json).end();
	}

	@Test
	public void testDonwloadFile(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber(deviceSerialNumber);
		req.setStateChanged(false);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 0);
			context.assertEquals(resp.getStatusMessage(), "SUCCESS");
			context.assertNotNull(resp.getContactServices());
			if ("ads".equals(resp.getContactServices().get(0).getServiceName())) {
				System.out.println("sendGetScheduledPackagesRequest");
				sendGetScheduledPackagesRequest(context, req, resp, packageResp -> {
					System.out.println("sendGetFilesInformationRequest");
					sendGetFilesInformationRequest(context, req, resp, packageResp, fileInfoResp -> {
						System.out.println("sendDownloadFileRequest");
						sendDownloadFileRequest(context, resp.getContactServices().get(0).getServiceURI(), req,
								fileInfoResp);
					});
				});
			}
		});
	}

	@Test
	public void testDonwloadFile2(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(this.deviceType);
		req.setDeviceSerialNumber(this.deviceSerialNumber);
		req.setStateChanged(false);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 0);
			context.assertEquals(resp.getStatusMessage(), "SUCCESS");
			context.assertNotNull(resp.getContactServices());
			if ("ads".equals(resp.getContactServices().get(0).getServiceName())) {
				sendGetScheduledPackagesRequest(context, req, resp, packageResp -> {
					sendGetFilesInformationRequest2(context, req, resp, packageResp, fileInfoResp -> {
						sendDownloadFileRequest(context, resp.getContactServices().get(0).getServiceURI(), req,
								fileInfoResp);
					});
				});
			}
		});
	}

	@Test
	public void testDonwloadFile4(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber(deviceSerialNumber);
		req.setStateChanged(false);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 0);
			context.assertEquals(resp.getStatusMessage(), "SUCCESS");
			context.assertNotNull(resp.getContactServices());
			if ("ads".equals(resp.getContactServices().get(0).getServiceName())) {
				sendGetScheduledPackagesRequest(context, req, resp, packageResp -> {
					sendGetFilesInformationRequest2(context, req, resp, packageResp, fileInfoResp -> {
						sendDownloadFileRequest2(context, resp.getContactServices().get(0).getServiceURI(), req,
								fileInfoResp);
					});
				});
			}
		});
	}

	private void sendDownloadFileRequest(TestContext context, String serviceUrl, CallHomeRequest callHomeReq,
			GetFileInfoResponse fileInfoResp) {
		Async async = context.async();
		URL url = null;
		try {
			url = new URL(serviceUrl);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		String downloadFilePath = "downloaded-" + fileInfoResp.getFilesInformation().get(0).getFileUUID() + "-"
				+ fileInfoResp.getFilesInformation().get(0).getFileName();
		String path = url.getPath() + "/downloadFile?version=1?requestType=DOWNLOADFILE?deviceType="
				+ callHomeReq.getDeviceType() + "?deviceSerialNumber=" + callHomeReq.getDeviceSerialNumber()
				+ "?deploymentUUID=" + fileInfoResp.getFilesInformation().get(0).getDeploymentUUID() + "?fileUUID="
				+ fileInfoResp.getFilesInformation().get(0).getFileUUID() + "?fileName="
				+ fileInfoResp.getFilesInformation().get(0).getFileName();
		File file = new File(downloadFilePath);
		System.out.println(file.getAbsolutePath());
		FileUtils.deleteQuietly(file);

		Long startTime = System.currentTimeMillis();
		vertx.createHttpClient().get(port, host, path).handler(response -> {
			System.out.println("Response Received: " + response.getHeader("content-disposition"));
			context.assertEquals(response.statusCode(), 200);
			String contentHeader = response.getHeader("content-disposition");
			String fileUUIDFromDB = contentHeader.substring(contentHeader.lastIndexOf("=") + 1).replace("\"", "");
			context.assertEquals(fileUUIDFromDB, fileInfoResp.getFilesInformation().get(0).getFileUUID());

			response.handler(body -> {
				System.out.println("Downloading");
				try {
					System.out.println("Body contents:" + body.getBytes());
					FileUtils.writeByteArrayToFile(file, body.getBytes(), true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			response.endHandler(v -> {
				System.out.println("Download finished");
				System.out.println((file.length() * 1000 / (System.currentTimeMillis() - startTime)));
				context.assertEquals((int) file.length(), fileInfoResp.getFilesInformation().get(0).getFileSize());
				async.complete();
			});
		}).end();
	}

	private void sendDownloadFileRequest2(TestContext context, String serviceUrl, CallHomeRequest callHomeReq,
			GetFileInfoResponse fileInfoResp) {
		Async async = context.async();
		URL url = null;
		try {
			url = new URL(serviceUrl);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		String path = url.getPath() + "/downloadFile?version=1?requestType=DOWNLOADFILE?deviceType="
				+ callHomeReq.getDeviceType() + "?deviceSerialNumber=" + callHomeReq.getDeviceSerialNumber()
				+ "?deploymentUUID=" + fileInfoResp.getFilesInformation().get(0).getDeploymentUUID() + "?fileUUID="
				+ fileInfoResp.getFilesInformation().get(0).getFileUUID() + "?fileName="
				+ fileInfoResp.getFilesInformation().get(0).getFileName();

		Long startTime = System.currentTimeMillis();
		vertx.createHttpClient().get(port, host, path).handler(response -> {
			context.assertEquals(response.statusCode(), 200);
			String contentHeader = response.getHeader("content-disposition");
			String fileUUIDFromDB = contentHeader.substring(contentHeader.lastIndexOf("=") + 1).replace("\"", "");
			context.assertEquals(fileUUIDFromDB, fileInfoResp.getFilesInformation().get(0).getFileUUID());
			AtomicInteger length = new AtomicInteger(0);
			response.handler(body -> {
				length.addAndGet(body.getBytes().length);
			});

			response.endHandler(v -> {
				System.out.println("Download finished");
				System.out.println((length.get() * 1000 / ((System.currentTimeMillis() - startTime) * 1024)));
				context.assertEquals(length, fileInfoResp.getFilesInformation().get(0).getFileSize());
				async.complete();
			});
		}).end();
	}

	@Test
	public void testUpdatePackageDeploymentStatus(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber(deviceSerialNumber);
		req.setStateChanged(false);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 0);
			context.assertEquals(resp.getStatusMessage(), "SUCCESS");
			context.assertNotNull(resp.getContactServices());
			if ("ads".equals(resp.getContactServices().get(0).getServiceName())) {
				sendGetScheduledPackagesRequest(context, req, resp, packageResp -> {
					UpdateDeploymentStatusRequest deployStatusReq = new UpdateDeploymentStatusRequest();
					deployStatusReq.setDeviceType(req.getDeviceType());
					deployStatusReq.setDeviceSerialNumber(req.getDeviceSerialNumber());

					List<DeploymentStatus> deployList = new ArrayList<DeploymentStatus>();
					DeploymentStatus deploymentStatus = new DeploymentStatus(
							packageResp.getPackageInformation().get(0).getDeploymentUUID(), "Success", "Completed","");
					deployList.add(deploymentStatus);
					deployStatusReq.setDeploymentStatus(deployList);

					System.out.println("sendUpdatePackageDeploymentStatusRequest");
					sendUpdatePackageDeploymentStatusRequest(context, resp.getContactServices().get(0).getServiceURI(),
							deployStatusReq);
				});
			}
		});
	}

	@Test
	public void testUpdatePackageDeploymentStatusDownloadFailed(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber(deviceSerialNumber);
		req.setStateChanged(false);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 0);
			context.assertEquals(resp.getStatusMessage(), "SUCCESS");
			context.assertNotNull(resp.getContactServices());
			if ("ads".equals(resp.getContactServices().get(0).getServiceName())) {
				sendGetScheduledPackagesRequest(context, req, resp, packageResp -> {
					UpdateDeploymentStatusRequest deployStatusReq = new UpdateDeploymentStatusRequest();
					deployStatusReq.setDeviceType(req.getDeviceType());
					deployStatusReq.setDeviceSerialNumber(req.getDeviceSerialNumber());

					List<DeploymentStatus> deployList = new ArrayList<DeploymentStatus>();
					DeploymentStatus deploymentStatus = new DeploymentStatus(
							packageResp.getPackageInformation().get(0).getDeploymentUUID(), "Failed", null,null);
					deployList.add(deploymentStatus);
					deployStatusReq.setDeploymentStatus(deployList);

					System.out.println("sendUpdatePackageDeploymentStatusRequest");
					sendUpdatePackageDeploymentStatusRequest(context, resp.getContactServices().get(0).getServiceURI(),
							deployStatusReq);
				});
			}
		});
	}

	private void sendUpdatePackageDeploymentStatusRequest(TestContext context, String serviceUrl,
			UpdateDeploymentStatusRequest req) {
		Async async = context.async();
		URL url = null;
		try {
			url = new URL(serviceUrl);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		final String json = Json.encode(req);
		final String length = Integer.toString(json.length());
		vertx.createHttpClient().post(port, host, url.getPath() + "/updatePackagesDeploymentStatus")
				.putHeader("content-type", "application/json").putHeader("content-length", length).handler(response -> {
					context.assertEquals(response.statusCode(), 200);
					context.assertTrue(response.headers().get("content-type").contains("application/json"));
					response.bodyHandler(body -> {
						System.out.println("Response Received: " + body.toString());
						UpdateDeploymentStatusResponse resp = new UpdateDeploymentStatusResponse();
						try {
							resp = Json.decodeValue(body.toString(), UpdateDeploymentStatusResponse.class);
							context.assertEquals(resp.getVersion(), 1);
							context.assertEquals(resp.getResponseType(), req.getRequestType());
							context.assertEquals(resp.getStatusCode(), 0);
							context.assertEquals(resp.getStatusMessage(), "SUCCESS");

						} catch (DecodeException e) {
							e.printStackTrace();
						}
						async.complete();
					});
				}).write(json).end();
	}

	@Test
	public void testHmsRequest(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber(deviceSerialNumber);
		req.setStateChanged(true);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 0);
			context.assertEquals(resp.getStatusMessage(), "SUCCESS");
			context.assertNotNull(resp.getContactServices());

			HmsRequest hmsReq = new HmsRequest();
			hmsReq.setDeviceType(deviceType);
			hmsReq.setDeviceSerialNumber(deviceSerialNumber);
			hmsReq.setDeviceType(req.getDeviceType());
			hmsReq.setDeviceSerialNumber(req.getDeviceSerialNumber());

			TerminalState terminalState = new TerminalState();
			terminalState.setOnline(1);
			terminalState.setPrivacyShieldAttached(2);
			terminalState.setStylusAttached(2);
			terminalState.setTampered(2);

			TerminalUsageReport usageReport1 = new TerminalUsageReport();
			usageReport1.setTxnTots(2345);
			usageReport1.setPinFails(11);
			hmsReq.setTerminalState(terminalState);
			TerminalInstalledApp terminalInstallation0 = new TerminalInstalledApp("Proline-Phoenixaa4", "2.5.24.1112",
					"OS");
			TerminalInstalledApp terminalInstallation1 = new TerminalInstalledApp("Platform_Phoenix", "1.0.08",
					"MainApp");
			hmsReq.setTerminalInstallations(
					new TerminalInstalledApp[] { terminalInstallation1, terminalInstallation0 });
			System.out.println(Json.encodePrettily(hmsReq));
			System.out.println("sendHmsRequest");
			sendHmsRequest(context, hmsReq);

		});
	}

	@Test
	public void testHmsRequest2(TestContext context) {
		CallHomeRequest req = new CallHomeRequest();
		req.setDeviceType(deviceType);
		req.setDeviceSerialNumber(deviceSerialNumber);
		req.setStateChanged(true);
		sendCallHomeRequest(context, req, resp -> {
			context.assertEquals(resp.getVersion(), 1);
			context.assertEquals(resp.getResponseType(), req.getRequestType());
			context.assertEquals(resp.getStatusCode(), 0);
			context.assertEquals(resp.getStatusMessage(), "SUCCESS");
			context.assertNotNull(resp.getContactServices());

			HmsRequest hmsReq = new HmsRequest();
			hmsReq.setDeviceType(deviceType);
			hmsReq.setDeviceSerialNumber(deviceSerialNumber);
			hmsReq.setDeviceType(req.getDeviceType());
			hmsReq.setDeviceSerialNumber(req.getDeviceSerialNumber());

			TerminalState terminalState = new TerminalState();
			terminalState.setOnline(1);
			terminalState.setPrivacyShieldAttached(1);
			terminalState.setStylusAttached(1);
			hmsReq.setTerminalState(terminalState);
			System.out.println(Json.encodePrettily(hmsReq));

			System.out.println("sendHmsRequest");
			sendHmsRequest(context, hmsReq);

		});
	}

	private void sendHmsRequest(TestContext context, HmsRequest req) {
		Async async = context.async();
		final String json = Json.encode(req);
		final String length = Integer.toString(json.length());
		vertx.createHttpClient().post(port, host, "/hms/uploadStatus").putHeader("content-type", "application/json")
				.putHeader("content-length", length).handler(response -> {
					context.assertEquals(response.statusCode(), 200);
					context.assertTrue(response.headers().get("content-type").contains("application/json"));
					response.bodyHandler(body -> {
						System.out.println("Response Received: " + body.toString());
						HmsResponse resp = null;
						try {
							resp = Json.decodeValue(body.toString(), HmsResponse.class);
						} catch (DecodeException e) {
							e.printStackTrace();
						}
						context.assertEquals(resp.getVersion(), 1);
						context.assertEquals(resp.getResponseType(), req.getRequestType());
						context.assertEquals(resp.getStatusCode(), 0);
						context.assertEquals(resp.getStatusMessage(), "SUCCESS");

						async.complete();
					});
				}).write(json).end();
	}
}
