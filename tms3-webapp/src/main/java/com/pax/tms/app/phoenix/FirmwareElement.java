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
package com.pax.tms.app.phoenix;

import org.dom4j.Element;

public class FirmwareElement extends ChildElement {

	@Override
	public void parse(String packageType, Element el) {
		setPackageType(packageType);
	}

}
