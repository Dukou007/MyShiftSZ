package com.pax.tms.app.phoenix;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import junit.framework.TestCase;

public class PhoenixPackageTest extends TestCase {

	@Test
	public void testComboPackage() {
		PhoenixPackage pack = new PhoenixPackage();
		File file = new File("PXMaster package/ComboOs2_5_8_4694RMain1_0_03_1471DPxR80PxU77_20160812.zip");
		try {
			pack.parse(file, file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testApplicationPackage() {
		PhoenixPackage pack = new PhoenixPackage();
		File file = new File("PXMaster package/platform-phoenix-1.0.04.1508T_sign_appsigned.zip");
		try {
			pack.parse(file, file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFirmwarePackage() {
		PhoenixPackage pack = new PhoenixPackage();
		File file = new File("PXMaster package/prolin-phoenix-2.5.8.4694R_SIG.zip");
		try {
			pack.parse(file, file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testApplicationPackage2() {
		PhoenixPackage pack = new PhoenixPackage();
		File file = new File("PXMaster package/PXRetailer-0.91.0.zip");
		try {
			pack.parse(file, file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testApplicationPackage3() {
		PhoenixPackage pack = new PhoenixPackage();
		File file = new File("PXMaster package/PXUpdater-0.1005.0.zip");
		try {
			pack.parse(file, file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testWhitelistPackage() {
		PhoenixPackage pack = new PhoenixPackage();
		File file = new File("PXMaster package/whitelist_Encryption_Off_paxsigned.zip");
		try {
			pack.parse(file, file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUninstallPackage() {
		PhoenixPackage pack = new PhoenixPackage();
		File file = new File("PXMaster package/Uninstall_All_Forms.zip");
		try {
			pack.parse(file, file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUninstallPackage2() {
		PhoenixPackage pack = new PhoenixPackage();
		File file = new File("PXMaster package/Uninstall_PXRetailer_Forms.zip");
		try {
			pack.parse(file, file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
