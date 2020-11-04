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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;

public class FilesElement extends ChildElement {

	private List<FileElement> fileElements = Collections.emptyList();

	@Override
	@SuppressWarnings("unchecked")
	public void parse(String packageType, Element el) {
		setPackageType(packageType);

		fileElements = new ArrayList<FileElement>();
		List<Element> files = el.elements("file");
		for (Element file : files) {
			FileElement fileElement = new FileElement();
			fileElement.parse(packageType, file);
			fileElements.add(fileElement);
		}
	}

	public List<FileElement> getFileElements() {
		return fileElements;
	}

	public void setFileElements(List<FileElement> fileElements) {
		this.fileElements = fileElements;
	}

}
