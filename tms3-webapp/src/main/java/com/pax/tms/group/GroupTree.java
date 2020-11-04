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
package com.pax.tms.group;

import java.io.Serializable;

public class GroupTree implements Serializable {

	private static final long serialVersionUID = 7894689540658495075L;

	private GroupNode groupNode;

	public GroupNode getGroupNode() {
		return groupNode;
	}

	public void setGroupNode(GroupNode groupNode) {
		this.groupNode = groupNode;
	}

}
