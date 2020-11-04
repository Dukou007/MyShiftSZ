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

public class ChildElementSpec {
	private String elementName;
	private boolean elementRepeatable;
	private boolean elementRequired;
	private Class<? extends ChildElement> instanceClass;

	public ChildElementSpec(String elementName, boolean elementRepeatable, boolean elementRequired,
			Class<? extends ChildElement> instanceClass) {
		super();
		this.elementName = elementName;
		this.elementRepeatable = elementRepeatable;
		this.elementRequired = elementRequired;
		this.instanceClass = instanceClass;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public boolean isElementRepeatable() {
		return elementRepeatable;
	}

	public void setElementRepeatable(boolean elementRepeatable) {
		this.elementRepeatable = elementRepeatable;
	}

	public boolean isElementRequired() {
		return elementRequired;
	}

	public void setElementRequired(boolean elementRequired) {
		this.elementRequired = elementRequired;
	}

	public Class<? extends ChildElement> getInstanceClass() {
		return instanceClass;
	}

	public void setInstanceClass(Class<? extends ChildElement> instanceClass) {
		this.instanceClass = instanceClass;
	}

}
