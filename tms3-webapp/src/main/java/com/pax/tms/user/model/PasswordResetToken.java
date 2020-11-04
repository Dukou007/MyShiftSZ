package com.pax.tms.user.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.pax.common.model.AbstractModel;

@Entity
@Table(name = "PUBTPWDRESETTOKEN")
public class PasswordResetToken extends AbstractModel {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "RESET_PWD_TOKEN")
	private String resetPwdToken;

	@Column(name = "TOKEN_ISSUE_DATE")
	private Date tokenIssueDate;

	@Column(name = "MAX_TOKEN_AGE")
	private int maxTokenAge;

	@Column(name = "IS_TOKEN_USED")
	private boolean isTokenUsed;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getResetPwdToken() {
		return resetPwdToken;
	}

	public void setResetPwdToken(String resetPwdToken) {
		this.resetPwdToken = resetPwdToken;
	}

	public Date getTokenIssueDate() {
		return tokenIssueDate;
	}

	public void setTokenIssueDate(Date tokenIssueDate) {
		this.tokenIssueDate = tokenIssueDate;
	}

	public int getMaxTokenAge() {
		return maxTokenAge;
	}

	public void setMaxTokenAge(int maxTokenAge) {
		this.maxTokenAge = maxTokenAge;
	}

	public boolean isTokenUsed() {
		return isTokenUsed;
	}

	public void setTokenUsed(boolean isTokenUsed) {
		this.isTokenUsed = isTokenUsed;
	}

}
