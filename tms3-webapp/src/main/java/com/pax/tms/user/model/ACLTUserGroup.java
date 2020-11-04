package com.pax.tms.user.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.pax.common.model.AbstractModel;
import com.pax.tms.group.model.Group;

@Entity
@Table(name = "ACLTUSER_GROUP")
@IdClass(ACLTUserGroup.class)
public class ACLTUserGroup extends AbstractModel {

	private static final long serialVersionUID = -6480160793493738097L;

	@Id
	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private User user;

	@Id
	@ManyToOne
	@JoinColumn(name = "GROUP_ID")
	private Group group;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

}
