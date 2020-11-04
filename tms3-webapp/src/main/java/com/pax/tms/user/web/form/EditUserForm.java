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
package com.pax.tms.user.web.form;

public class EditUserForm extends UserForm {

	private static final long serialVersionUID = 4190009087977062910L;

	private Long id;

	private Long favoriteGroupId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFavoriteGroupId() {
		return favoriteGroupId;
	}

	public void setFavoriteGroupId(Long favoriteGroupId) {
		this.favoriteGroupId = favoriteGroupId;
	}

}
