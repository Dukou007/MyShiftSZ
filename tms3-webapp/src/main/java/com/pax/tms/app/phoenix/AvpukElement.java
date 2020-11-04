package com.pax.tms.app.phoenix;

import org.dom4j.Element;

public class AvpukElement extends ChildElement {

	@Override
	public void parse(String packageType, Element el) {
		setPackageType(packageType);
	}

}
