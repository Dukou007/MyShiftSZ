package com.pax.tms.terminal.model;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.pax.common.model.AbstractModel;
import com.pax.tms.group.model.Group;
@Entity
@Table(name="ACLTTRM_GROUP")
@IdClass(ACLTerminalGroup.class)
public class ACLTerminalGroup extends AbstractModel{
	
	private static final long serialVersionUID = 1L;

	@Id
	@ManyToOne
	@JoinColumn(name = "TRM_ID")
	private Terminal terminal;
	
	@Id
	@ManyToOne
	@JoinColumn(name="GROUP_ID")
	private Group group;

	public Terminal getTerminal() {
		return terminal;
	}

	public void setTerminal(Terminal terminal) {
		this.terminal = terminal;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	

}
