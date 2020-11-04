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
package com.pax.tms.terminal.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.pax.common.model.AbstractModel;

@Entity
@Table(name = "TMSPTSNS")
@IdClass(TMSPTSNS.class)
public class TMSPTSNS extends AbstractModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "BATCH_ID")
	private long batchId;

	@Id
	@Column(name = "TSN")
	private String tsn;

	public long getBatchId() {
		return batchId;
	}

	public void setBatchId(long batchId) {
		this.batchId = batchId;
	}

	public String getTsn() {
		return tsn;
	}

	public void setTsn(String tsn) {
		this.tsn = tsn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (batchId ^ (batchId >>> 32));
		result = prime * result + ((tsn == null) ? 0 : tsn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TMSPTSNS other = (TMSPTSNS) obj;
		if (batchId != other.batchId)
			return false;
		if (tsn == null) {
			if (other.tsn != null)
				return false;
		} else if (!tsn.equals(other.tsn))
			return false;
		return true;
	}

}
