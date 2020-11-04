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
@Table(name = "TMSTPACKAGE")
public class Pkg extends AbstractModel {

	private static final long serialVersionUID = 758315362829385033L;

	public static final String ID_SEQUENCE_NAME = "TMSTPACKAGE_ID";
	public static final String INCREMENT_SIZE = "1";
	public static final String LIST_URL = "/pkg/list/";
	public static final String LIST_MANAGE_URL = "/pkg/manageList/";
	public static final String TITLE = "PACKAGE";
	public static final long DISABLE = 0;
	public static final long ENABLE = 1;
	
    public static final String KEY_LIST_URL = "/offlinekey/list/";
    public static final String KEY_TITLE = "OFFLINEKEY";

    public static final int QUERY_PKG = 1;
    public static final int QUERY_KEY = 0;
    
	@Id
	@GeneratedValue(generator = ID_SEQUENCE_NAME + "_GEN")
	@GenericGenerator(name = ID_SEQUENCE_NAME + "_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "PKG_ID")
	private Long id;

	@Column(name = "PKG_UUID")
	private String uuid;

	@Column(name = "PKG_NAME")
	private String name;

	@Column(name = "PKG_VERSION")
	private String version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	private Model model;
	
	@Column(name = "TRM_SN")
    private String sn;

	@Column(name = "PKG_TYPE")
	private String type;

	@Column(name="PGM_TYPE")
	private String pgmType;

	@Column(name = "PKG_DESC")
	private String desc;

	@Column(name = "PKG_STATUS")
	private boolean status = true;

	@Column(name = "EXPIRATION_DATE")
	private Date expirationDate;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "FILE_SIZE")
	private Long fileSize;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "FILE_MD5")
	private String fileMD5;

	@Column(name = "FILE_SHA256")
	private String fileSHA256;

	@Column(name = "SCHEMA_FILE_PATH")
	private String schemaFilePath;

	@Column(name = "SCHEMA_FILE_SIZE")
	private Long schemaFileSize;

	@Column(name = "PARAM_SET")
	private String paramSet;
	
	@Column(name = "PKG_NOTES")
	private String notes;

	@OneToMany(mappedBy = "pkg")
	private List<PkgGroup> pkgGroups;

	@OneToMany(mappedBy = "pkg")
	private List<PkgProgram> pkgPrograms;

	@OneToMany(mappedBy = "pkg")
	private List<ProgramFile> programFiles;

	@Column(name = "CREATOR")
	private String creator;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIER")
	private String modifier;

	@Column(name = "MODIFY_DATE")
	private Date modifyDate;
	
	@Column(name = "PKG_SIGNED")
    private Boolean signed = true;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPgmType() {
		return pgmType;
	}

	public void setPgmType(String pgmType) {
		this.pgmType = pgmType;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
	public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileMD5() {
		return fileMD5;
	}

	public void setFileMD5(String fileMD5) {
		this.fileMD5 = fileMD5;
	}

	public String getFileSHA256() {
		return fileSHA256;
	}

	public void setFileSHA256(String fileSHA256) {
		this.fileSHA256 = fileSHA256;
	}

	public String getSchemaFilePath() {
		return schemaFilePath;
	}

	public void setSchemaFilePath(String schemaFilePath) {
		this.schemaFilePath = schemaFilePath;
	}

	public Long getSchemaFileSize() {
		return schemaFileSize;
	}

	public void setSchemaFileSize(Long schemaFileSize) {
		this.schemaFileSize = schemaFileSize;
	}

	public String getParamSet() {
		return paramSet;
	}

	public void setParamSet(String paramSet) {
		this.paramSet = paramSet;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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
	
	public Boolean isSigned() {
        return signed;
    }

    public void setSigned(Boolean signed) {
        this.signed = signed;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Pkg other = (Pkg) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public void addProgram(PkgProgram pkgProgram) {
		if (pkgPrograms == null) {
			pkgPrograms = new ArrayList<PkgProgram>();
		}
		pkgPrograms.add(pkgProgram);
		pkgProgram.setPkg(this);
	}

	public void addProgramFile(ProgramFile programFile) {
		if (programFiles == null) {
			programFiles = new ArrayList<>();
		}
		programFiles.add(programFile);
		programFile.setPkg(this);

	}

	public List<PkgGroup> getPkgGroups() {
		return pkgGroups;
	}

	public void setPkgGroups(List<PkgGroup> pkgGroups) {
		this.pkgGroups = pkgGroups;
	}

	public List<PkgProgram> getPkgPrograms() {
		return pkgPrograms;
	}

	public void setPkgPrograms(List<PkgProgram> pkgPrograms) {
		this.pkgPrograms = pkgPrograms;
	}

	public List<ProgramFile> getProgramFiles() {
		return programFiles;
	}

	public void setProgramFiles(List<ProgramFile> programFiles) {
		this.programFiles = programFiles;
	}
}
