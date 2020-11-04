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
package com.pax.tms.pxretailer.constant;

public class AlertConstants {
	private AlertConstants() {
		super();
	}

	public static final String TAMPERS = "Tampers";

	public static final String OFFLINE = "Offline";

	public static final String PRIVACY_SHIELD = "Privacy Shield";
	
	public static final String SRED = "sred";

	public static final String STYLUS = "Stylus";

	public static final String DOWNLOADS = "Downloads";

	public static final String ACTIVATIONS = "Activations";

	public static final String MSR_READ = "MSR Read";

	public static final String CONTACT_IC_READ = "Contact IC Read";

	public static final String PIN_ENCRYPTION_FAILURE = "PIN Encryption Failure";

	public static final String SIGNATURE = "Signature";

	public static final String DOWNLOAD_HISTORY = "Download History";

	public static final String ACTIVATION_HISTORY = "Activation History";

	public static final String CONTACTLESS_IC_READ = "Contactless IC Read";

	public static final String TRANSACTIONS = "Transactions";

	public static final String POWER_CYCLES = "Power-cycles";

	private static final String[] USAGE_ITEMS = { DOWNLOAD_HISTORY, ACTIVATION_HISTORY, MSR_READ, CONTACT_IC_READ,
			PIN_ENCRYPTION_FAILURE, SIGNATURE, CONTACTLESS_IC_READ, TRANSACTIONS, POWER_CYCLES };

	private static final String[] REAL_ITEMS = { TAMPERS, OFFLINE, PRIVACY_SHIELD, STYLUS, DOWNLOADS, ACTIVATIONS,SRED };

	public static int getRealItemsCount() {
		return REAL_ITEMS.length;
	}

	public static int getUsageItemsCount() {
		return USAGE_ITEMS.length;
	}

	public static String[] getUsageItems() {
		return USAGE_ITEMS;
	}

	public static String[] getRealItems() {
		return REAL_ITEMS;
	}
}
