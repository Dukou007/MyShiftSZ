package com.pax.tms.res.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.pax.common.model.AbstractModel;

@Entity
@Table(name = "TMSTPKG_USAGE_INFO")
public class PkgUsageInfo extends AbstractModel {

	private static final long serialVersionUID = -3982673867168182656L;
	
	@Id
	@OneToOne
	@JoinColumn(name = "PKG_ID")
	private Pkg pkg;

	@Column(name = "LAST_OPT_TIME")
	private Date lastOptTime;

	public Pkg getPkg() {
		return pkg;
	}

	public void setPkg(Pkg pkg) {
		this.pkg = pkg;
	}

	public Date getLastOptTime() {
		return lastOptTime;
	}

	public void setLastOptTime(Date lastOptTime) {
		this.lastOptTime = lastOptTime;
	}

}
