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
package com.pax.tms.app.broadpos;

import java.util.HashMap;
import java.util.Map;

public interface FirmwareTypes {

	String OS = "OS";
	String BASE_LIB = "BASE_LIB";
	String UAI_LIB = "UAI_LIB";
	String BROWER = "BROWER";
	String APP_LIB = "APP_LIB";
	String SYSTEM = "SYSTEM";// Font
	String PUBLIC_FILE = "PUBLIC_FILE";
	String US_PUK = "US_PUK";
	String BASE_DRIVER = "BASE_DRIVER";
	String ROOT_CERT = "ROOT_CERT";
	String SCRIPT_CERT = "SCRIPT_CERT";

	Map<String, String> FIRMWARE_TYPES = getFirmwareTypes();

	static Map<String, String> getFirmwareTypes() {
		Map<String, String> firmwareTypes = new HashMap<String, String>();
		firmwareTypes.put("01", OS);
		firmwareTypes.put("02", BASE_LIB);
		firmwareTypes.put("03", UAI_LIB);
		firmwareTypes.put("04", BROWER);
		firmwareTypes.put("05", APP_LIB);
		firmwareTypes.put("06", SYSTEM);
		firmwareTypes.put("07", PUBLIC_FILE);
		firmwareTypes.put("08", US_PUK);
		firmwareTypes.put("09", BASE_DRIVER);
		return firmwareTypes;
	}

	static String convent(String firmwareType) {
		String conventedFirmwareType = FIRMWARE_TYPES.get(firmwareType);
		if (conventedFirmwareType == null) {
			return firmwareType;
		}
		return conventedFirmwareType;
	}
}
