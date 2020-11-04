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
package com.pax.tms.deploy.model;

public enum DownOrActvStatus {
	SUCCESS("Success"), PENDING("Pending"), DOWNLOADING("Downloading"), FAILED("Failed"), CANCELED(
			"Canceled"), NOUPDATE("-"), EXPIRED("Expired"), NOACTIVITION("Not need activition");
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private DownOrActvStatus(String name) {
		this.name = name;
	}

	public static DownOrActvStatus parse(String name) {
		if (name == null) {
			return null;
		}
		switch (name) {
		case "Success":
			return SUCCESS;
		case "Pending":
			return PENDING;
		case "Downloading":
			return DOWNLOADING;
		case "Failed":
			return FAILED;
		case "Canceled":
			return CANCELED;
		case "Not need download":
			return NOUPDATE;
		case "Expired":
			return EXPIRED;
		case "Not need activition":
			return NOACTIVITION;
		default:
			return null;
		}
	}

}