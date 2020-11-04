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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.pax.common.model.AbstractModel;

@Entity
@Table(name = "TMSTPROGRAM_FILE")
public class ProgramFile extends AbstractModel {

	private static final long serialVersionUID = -1316702131427814127L;

	public static final String ID_SEQUENCE_NAME = "TMSTPROGRAM_FILE_ID";
	public static final String INCREMENT_SIZE = "1";
	public static final String PROGRAM_FILE_KEY = "PGT_ZIP_FILE";
	public static final String CONF_FILE_KEY = "PCT_FILE_PATH";
	public static final String DIGEST_FILE_KEY = "PGD_ZIP_FILE";
	public static final String SIGN_FILE_KEY = "PGS_ZIP_FILE";
	
	public static final String PROGRAM_FILE = "program";
	public static final String CONF_FILE = "config";
	public static final String DIGEST_FILE = "degist";
	public static final String SIGN_FILE = "sign";
	private static final String[] FILE_TYPES={PROGRAM_FILE, CONF_FILE, DIGEST_FILE, SIGN_FILE};

	@Id
	@GeneratedValue(generator = ID_SEQUENCE_NAME + "_GEN")
	@GenericGenerator(name = ID_SEQUENCE_NAME + "_GEN", strategy = "enhanced-table", parameters = {
			@Parameter(name = "table_name", value = "PUBTSEQUENCE"),
			@Parameter(name = "value_column_name", value = "NEXT_VALUE"),
			@Parameter(name = "segment_column_name", value = "SEQ_NAME"),
			@Parameter(name = "segment_value", value = ID_SEQUENCE_NAME),
			@Parameter(name = "increment_size", value = INCREMENT_SIZE),
			@Parameter(name = "optimizer", value = "pooled-lo") })
	@Column(name = "FILE_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PKG_ID")
	private Pkg pkg;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PGM_ID")
	private PkgProgram pkgProgram;

	@Column(name = "FILE_NAME")
	private String name;
	
	@Column(name = "FILE_VERSION")
	private String version;

	@Column(name = "FILE_TYPE")
	private String type;

	@Column(name = "FILE_DESC")
	private String desc;

	@Column(name = "FILE_SIZE")
	private Long size;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "FILE_MD5")
	private String fileMD5;

	@Column(name = "FILE_SHA256")
	private String fileSHA256;

	@Column(name = "CREATOR")
	private String creator;

	@Column(name = "CREATE_DATE")
	private Date createDate;

	@Column(name = "MODIFIER")
	private String modifier;

	@Column(name = "MODIFY_DATE")
	private Date modifyDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PkgProgram getPkgProgram() {
		return pkgProgram;
	}

	public void setPkgProgram(PkgProgram pkgProgram) {
		this.pkgProgram = pkgProgram;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
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

	public Pkg getPkg() {
		return pkg;
	}

	public void setPkg(Pkg pkg) {
		this.pkg = pkg;
	}

	public static String[] getFileTypes() {
		return FILE_TYPES;
	}
	

}
