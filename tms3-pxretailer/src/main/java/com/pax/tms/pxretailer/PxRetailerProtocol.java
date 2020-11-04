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
package com.pax.tms.pxretailer;

import com.pax.tms.deploy.model.DownOrActvStatus;

public final class PxRetailerProtocol {

	public static final int INVALID_REQUEST_RESPONSE = 1;
	public static final int SERVER_ERROR_RESPONSE = 9;
	public static final int DEPLOYMENT_NOT_FOUND_RESPONSE = 3;
	public static final int FILE_NOT_FOUND_RESPONSE = 4;
	public static final int INVALID_FILE_SIZE_RESPONSE = 5;

	public static final String DOWNLOADING_STATUS = "Started";
	public static final String PENDING_STATUS = "Pending";
	public static final String COMPLETED_STATUS = "Completed";
	public static final String SUCCESS_STATUS = "Success";
	public static final String FAILED_STATUS = "Failed";
	public static final String CANCELLED_STATUS = "Canceled";

	private PxRetailerProtocol() {
	}

	public static DownOrActvStatus downOrActvStatus(String status) {
		if (status == null) {
			return null;
		} else if (status.equalsIgnoreCase(PxRetailerProtocol.COMPLETED_STATUS)
				|| status.equalsIgnoreCase(PxRetailerProtocol.SUCCESS_STATUS)) {
			return DownOrActvStatus.SUCCESS;
		} else if (status.equalsIgnoreCase(FAILED_STATUS)) {
			return DownOrActvStatus.FAILED;
		} else if (status.equalsIgnoreCase(CANCELLED_STATUS)) {
			return DownOrActvStatus.CANCELED;
		} else if (status.equalsIgnoreCase(PENDING_STATUS)) {
			return DownOrActvStatus.PENDING;
		} else if (status.equalsIgnoreCase(DOWNLOADING_STATUS)) {
			return DownOrActvStatus.DOWNLOADING;
		}
		return null;
	}
}
