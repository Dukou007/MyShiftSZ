package com.pax.tms.pxretailer.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TerminalUsage implements Serializable {

	private static final long serialVersionUID = 7238117297243033042L;
	
	private String Category;
	private int Total;
	private int Success;
	private int Fail;
	
	public TerminalUsage() {
		
	}

	public TerminalUsage(String Category, int Total, int Success, int Fail) {
		this.Category = Category;
		this.Total = Total;
		this.Success = Success;
		this.Fail = Fail;
	}
	
	@JsonProperty("Category")
	public String getCategory() {
		return Category;
	}
	@JsonProperty("Category")
	public void setCategory(String category) {
		Category = category;
	}
	@JsonProperty("Total")
	public int getTotal() {
		return Total;
	}
	@JsonProperty("Total")
	public void setTotal(int total) {
		Total = total;
	}
	@JsonProperty("Success")
	public int getSuccess() {
		return Success;
	}
	@JsonProperty("Success")
	public void setSuccess(int success) {
		Success = success;
	}
	@JsonProperty("Fail")
	public int getFail() {
		return Fail;
	}
	@JsonProperty("Fail")
	public void setFail(int fail) {
		Fail = fail;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Category == null) ? 0 : Category.hashCode());
		result = prime * result + Fail;
		result = prime * result + Success;
		result = prime * result + Total;
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
		TerminalUsage other = (TerminalUsage) obj;
		if (Category == null) {
			if (other.Category != null)
				return false;
		} else if (!Category.equals(other.Category))
			return false;
		if (Fail != other.Fail)
			return false;
		if (Success != other.Success)
			return false;
		if (Total != other.Total)
			return false;
		return true;
	}
	
	
	
}
