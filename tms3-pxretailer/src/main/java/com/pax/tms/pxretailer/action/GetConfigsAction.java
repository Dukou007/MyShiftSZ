package com.pax.tms.pxretailer.action;

import org.springframework.beans.factory.annotation.Autowired;

import com.pax.tms.pxretailer.PxRetailerConfig;
import com.pax.tms.pxretailer.message.GetConfigsResponse;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class GetConfigsAction implements ITerminalAction {

	private static final String GET_SERVER_CONFIGS = "/getConfigs";

	private JsonObject localConfig;

	private PxRetailerConfig globalConfig;

	@Autowired
	public void setGlobalConfig(PxRetailerConfig globalConfig) {
		this.globalConfig = globalConfig;
	}

	@Override
	public void registerAction(Router router, JsonObject config) {
		router.get(GET_SERVER_CONFIGS).handler(this::handleGetConfigs);
		this.localConfig = config;
	}

	private void handleGetConfigs(RoutingContext routingContext) {
		String name = localConfig.getString("tms.serverName", "PxMessageServer");
		String version = localConfig.getString("tms.serverVersion", "0.0.0");
		String chsAddress = localConfig.getString("tms.chsAddress");
		String hmsAddress = localConfig.getString("tms.hmsAddress");
		String adsAddress = localConfig.getString("tms.adsAddress");
		int fileDownloadChunkSize = localConfig.getInteger("tms.fileDownloadChunkSize");

		int chsDelay = globalConfig.getChsDelay();
		int hmsDelay = globalConfig.getHmsDelay();
		int adsDelay = globalConfig.getAdsDelay();

		int servicePort = globalConfig.getTcpPort();
		long offlineDuration = globalConfig.getMaxHeartBeatInterval();
		int maxSimultaneousDownloads = globalConfig.getMaxSimultaneousDownloads();

		GetConfigsResponse response = new GetConfigsResponse();
		response.setName(name);
		response.setVersion(version);
		response.setChsAddress(chsAddress);
		response.setHmsAddress(hmsAddress);
		response.setAdsAddress(adsAddress);

		response.setServicePort(servicePort);
		response.setChsDelay(chsDelay);
		response.setHmsDelay(hmsDelay);
		response.setAdsDelay(adsDelay);
		response.setOfflineDuration(offlineDuration);
		response.setMaxSimultaneousDownloads(maxSimultaneousDownloads);
		response.setFileDownloadChunkSize(fileDownloadChunkSize);

		routingContext.response().bodyEndHandler(v -> routingContext.response().close());
		routingContext.response().putHeader("content-type", "application/json").end(Json.encode(response));
	}
}
