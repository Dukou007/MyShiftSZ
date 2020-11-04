package com.pax.tms.app.broadpos;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import junit.framework.TestCase;

public class BroadposPackageTest extends TestCase {

	@Test
	public void testBroadposPackage() {
		String packageFilePath = "broadpos_package/df_sdf.zip";
		String packagefileName = "df_sdf.zip";
		String modelName = "S80";

		Signer signer = new Signer();
		signer.setKeystore("broadpos/PAXDataScript.keystore");
		signer.setKeystorePassword("dowa2012Y1a7n2g3");
		signer.setEntryAlias("PAXDataScript");
		signer.setEntryPassword("dowa2012Y1a7n2g3");
		signer.setSignVersion("sdfsdfadfdsf");
		signer.init();

		BroadposPackage pack = new BroadposPackage(packageFilePath, packagefileName,
				new File("broadpos_package/df_sdf.zip").length(), modelName, signer);
		try {
			pack.parse();
			pack.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			pack.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testSign() {
		Signer signer = new Signer();
		signer.setKeystore("broadpos/PAXDataScript.keystore");
		signer.setKeystorePassword("dowa2012Y1a7n2g3");
		signer.setEntryAlias("PAXDataScript");
		signer.setEntryPassword("dowa2012Y1a7n2g3");
		signer.setSignVersion("sdfsdfadfdsf");
		signer.init();

		byte[] signature = signer.sign("E:\\PAX\\TMS3.0\\release\\br.d");
		String signStr = bytesToHexString(signature);
		System.out.println(signStr);

		signature = signer.sign("E:\\PAX\\TMS3.0\\release\\broadpos-browser-digest.zip");
		signStr = bytesToHexString(signature);
		System.out.println(signStr);
	}

	private static String bytesToHexString(byte[] bArray) {
		if (bArray == null) {
			throw new NullPointerException();
		}
		StringBuilder sb = new StringBuilder(2 * bArray.length);

		for (int i = 0; i < bArray.length; ++i) {
			String sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

}
