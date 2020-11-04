package com.pax.tms.pxretailer.message;

import java.io.Serializable;

public class TerminalInstalledApp implements Serializable {

	private static final long serialVersionUID = 2071241781342110505L;
	private String pkgName;
	private String pkgVersion;
	private String pkgType;

	public TerminalInstalledApp() {

	}

	public TerminalInstalledApp(String pkgName, String pkgVersion, String pkgType) {
		this.pkgName = pkgName;
		this.pkgVersion = pkgVersion;
		this.pkgType = pkgType;
	}

	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public String getPkgVersion() {
		return pkgVersion;
	}

	public void setPkgVersion(String pkgVersion) {
		this.pkgVersion = pkgVersion;
	}

	public String getPkgType() {
		return pkgType;
	}

	public void setPkgType(String pkgType) {
		this.pkgType = pkgType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pkgName == null) ? 0 : pkgName.hashCode());
		result = prime * result + ((pkgType == null) ? 0 : pkgType.hashCode());
		result = prime * result + ((pkgVersion == null) ? 0 : pkgVersion.hashCode());
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
		TerminalInstalledApp other = (TerminalInstalledApp) obj;
		if (pkgName == null) {
			if (other.pkgName != null)
				return false;
		} else if (!pkgName.equals(other.pkgName))
			return false;
		if (pkgType == null) {
			if (other.pkgType != null)
				return false;
		} else if (!pkgType.equals(other.pkgType))
			return false;
		if (pkgVersion == null) {
			if (other.pkgVersion != null)
				return false;
		} else if (!pkgVersion.equals(other.pkgVersion))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TerminalInstalledApp [pkgName=" + pkgName + ", pkgVersion=" + pkgVersion + ", pkgType=" + pkgType + "]";
	}
	
}
