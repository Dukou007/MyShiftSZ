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
package com.pax.tms.res.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;

@Entity
@Table(name = "TMSTPKG_PROGRAM")
public class PkgProgram extends AbstractModel {

	private static final long serialVersionUID = -1952773583787390292L;

	public static final String ID_SEQUENCE_NAME = "TMSTPKG_PROGRAM_ID";
	public static final String INCREMENT_SIZE = "1";

	@Id
	@GeneratedValue(generator = ID_SEQUENCE_NAME + "_GEN")
	@GenericGenerator(name = ID_SEQUENCE_NAME + "_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "PGM_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PKG_ID")
	private Pkg pkg;

	@Column(name = "PGM_ABBR_NAME")
	private String abbrName;

	@Column(name = "PGM_VERSION")
	private String version;

	@Column(name = "PGM_NAME")
	private String name;

	@Column(name = "PGM_DISPLAY_NAME")
	private String displayName;

	@Column(name = "PGM_TYPE")
	private String type;

	@Column(name = "PGM_DESC")
	private String desc;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PGM_FILE_ID")
	private ProgramFile programFile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONF_FILE_ID")
	private ProgramFile configFile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIGEST_FILE_ID")
	private ProgramFile digestFile;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SIGN_FILE_ID")
	private ProgramFile signFile;

	@Column(name = "SIGN_VERSION")
	private String signVersion;

	@Column(name = "CREATOR")
	private String creator;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIER")
	private String modifier;

	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	@OneToMany(mappedBy = "pkgProgram")
	private List<ProgramFile> programFiles;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Pkg getPkg() {
		return pkg;
	}

	public void setPkg(Pkg pkg) {
		this.pkg = pkg;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public ProgramFile getProgramFile() {
		return programFile;
	}

	public void setProgramFile(ProgramFile programFile) {
		this.programFile = programFile;
	}

	public ProgramFile getConfigFile() {
		return configFile;
	}

	public void setConfigFile(ProgramFile configFile) {
		this.configFile = configFile;
	}

	public ProgramFile getDigestFile() {
		return digestFile;
	}

	public void setDigestFile(ProgramFile digestFile) {
		this.digestFile = digestFile;
	}

	public ProgramFile getSignFile() {
		return signFile;
	}

	public void setSignFile(ProgramFile signFile) {
		this.signFile = signFile;
	}

	public String getSignVersion() {
		return signVersion;
	}

	public void setSignVersion(String signVersion) {
		this.signVersion = signVersion;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}

	public List<ProgramFile> getProgramFiles() {
		return programFiles;
	}

	public void setProgramFiles(List<ProgramFile> programFiles) {
		this.programFiles = programFiles;
	}

	public String getAbbrName() {
		return abbrName;
	}

	public void setAbbrName(String abbrName) {
		this.abbrName = abbrName;
	}

	public void addProgrmFile(ProgramFile programFile) {
		if (programFiles == null) {
			programFiles = new ArrayList<>();
		}
		programFiles.add(programFile);

		switch (programFile.getType()) {
		case ProgramFile.PROGRAM_FILE:
			this.setProgramFile(programFile);
			break;
		case ProgramFile.CONF_FILE:
			this.setConfigFile(programFile);
			break;
		case ProgramFile.DIGEST_FILE:
			this.setDigestFile(programFile);
			break;
		case ProgramFile.SIGN_FILE:
			this.setSignFile(programFile);
			break;
		default:
			break;
		}

		programFile.setPkgProgram(this);
	}
}
