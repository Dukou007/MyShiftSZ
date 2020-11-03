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
package com.pax.common.dao;

import org.springframework.util.Assert;

public class Sequence {

	private String segmentName;
	private int increment;
	private long id;
	private int count = 0;

	public Sequence(String segmentName, int increment) {
		Assert.hasText(segmentName);
		if (increment < 0) {
			throw new IllegalArgumentException("Increment must greater then 0");
		}
		this.segmentName = segmentName;
		this.increment = increment;
	}

	public synchronized long getId() {
		if (count == 0) {
			id = DbHelper.generateId(segmentName, increment);
			count = increment;
		}
		count--;
		return id++;
	}
}
