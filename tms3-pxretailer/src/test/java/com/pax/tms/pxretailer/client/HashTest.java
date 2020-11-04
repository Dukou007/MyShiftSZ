package com.pax.tms.pxretailer.client;

import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.CRC32;

import org.junit.Test;

import com.pax.tms.pxretailer.message.TerminalInstalledApp;
import com.pax.tms.pxretailer.message.TerminalInstalledAppList;

import io.vertx.core.json.Json;

public class HashTest {

	@Test
	public void testHash() {
		TerminalInstalledApp terminalInstallation0 = new TerminalInstalledApp("Proline-Phoenixaa", "2.5.24.1112", "OS");
		TerminalInstalledApp terminalInstallation1 = new TerminalInstalledApp("Platform_Phoenix", "1.0.08", "MainApp");

		TerminalInstalledApp terminalInstallation4 = new TerminalInstalledApp("Proline-Phoenixaa",  "2.5.24.1112","OS");
		TerminalInstalledApp terminalInstallation3 = new TerminalInstalledApp("Platform_Phoenix",  "1.0.08","MainApp");
		List<TerminalInstalledApp> terminalInstalledApps = new TerminalInstalledAppList();
		List<TerminalInstalledApp> terminalInstalledApps1 = new TerminalInstalledAppList();
		
		terminalInstalledApps1.add(terminalInstallation0);
		terminalInstalledApps1.add(terminalInstallation1);
		
		terminalInstalledApps.add(terminalInstallation3);
		terminalInstalledApps.add(terminalInstallation4);
		System.out.println(Json.encode(terminalInstalledApps1));
		System.out.println(Json.encode(terminalInstalledApps));
		System.out.println(terminalInstalledApps.equals(terminalInstalledApps1));

		/*
		 * JsonArray jsonArray = new JsonArray();
		 * 
		 * JsonObject jsonObject = new JsonObject(); jsonObject.put("pkgName",
		 * "Proline-Phoenixaa"); jsonObject.put("pkgVersion", "2.5.24.1112");
		 * jsonObject.put("pkgType", "OS"); jsonArray.add(jsonObject);
		 * 
		 * JsonObject jsonObject1 = new JsonObject(); jsonObject1.put("pkgName",
		 * "Platform_Phoenix"); jsonObject1.put("pkgVersion", "1.0.08");
		 * jsonObject1.put("pkgType", "MainApp"); jsonArray.add(jsonObject1);
		 * 
		 * JsonArray jsonArray1 = new JsonArray();
		 * 
		 * JsonObject jsonObject3 = new JsonObject();
		 * 
		 * jsonObject3.put("pkgVersion", "1.0.08"); jsonObject3.put("pkgName",
		 * "Platform_Phoenix");
		 * 
		 * jsonObject3.put("pkgType", "MainApp"); jsonArray1.add(jsonObject3);
		 * 
		 * JsonObject jsonObject4 = new JsonObject();
		 * jsonObject4.put("pkgVersion", "2.5.24.1112");
		 * jsonObject4.put("pkgName", "Proline-Phoenixaa");
		 * jsonObject4.put("pkgType", "OS"); jsonArray1.add(jsonObject4);
		 * System.out.println(jsonArray); System.out.println(jsonArray1);
		 * System.out.println(jsonArray.equals(jsonArray1));
		 */

	}

	private long crc32Hash(String installedApps) {
		CRC32 crc32 = new CRC32();
		crc32.update(installedApps.getBytes(Charset.forName("UTF-8")));
		return crc32.getValue();

	}

}
