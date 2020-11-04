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
package com.pax.fastdfs.proto.storage.internal;

import java.nio.charset.Charset;
import java.util.Set;

import org.apache.commons.lang3.Validate;

import com.pax.fastdfs.domain.MateData;
import com.pax.fastdfs.proto.CmdConstants;
import com.pax.fastdfs.proto.FdfsRequest;
import com.pax.fastdfs.proto.OtherConstants;
import com.pax.fastdfs.proto.ProtoHead;
import com.pax.fastdfs.proto.mapper.DynamicFieldType;
import com.pax.fastdfs.proto.mapper.FdfsColumn;
import com.pax.fastdfs.proto.mapper.MetadataMapper;
import com.pax.fastdfs.proto.storage.enums.StorageMetdataSetType;

/**
 * 设置文件标签
 * 
 * @author tobato
 *
 */
public class StorageSetMetadataRequest extends FdfsRequest {

	private static final String EMPTY_GROUP_MSG = "分组不能为空";

	/** 文件名byte长度 */
	@FdfsColumn(index = 0)
	private int fileNameByteLengh;
	/** 元数据byte长度 */
	@FdfsColumn(index = 1)
	private int mataDataByteLength;
	/** 操作标记（重写/覆盖） */
	@FdfsColumn(index = 2)
	private byte opFlag;
	/** 组名 */
	@FdfsColumn(index = 3, max = OtherConstants.FDFS_GROUP_NAME_MAX_LEN)
	private String groupName;
	/** 文件路径 */
	@FdfsColumn(index = 4, dynamicField = DynamicFieldType.ALLRESTBYTE)
	private String path;
	/** 元数据 */
	@FdfsColumn(index = 5, dynamicField = DynamicFieldType.METADATE)
	private Set<MateData> metaDataSet;

	/**
	 * 设置文件元数据
	 * 
	 * @param groupName
	 * @param path
	 * @param metaDataSet
	 * @param type
	 */
	public StorageSetMetadataRequest(String groupName, String path, Set<MateData> metaDataSet,
			StorageMetdataSetType type) {
		super();
		Validate.notBlank(groupName, EMPTY_GROUP_MSG);
		Validate.notBlank(path, EMPTY_GROUP_MSG);
		Validate.notEmpty(metaDataSet, EMPTY_GROUP_MSG);
		Validate.notNull(type, "标签设置方式不能为空");
		this.groupName = groupName;
		this.path = path;
		this.metaDataSet = metaDataSet;
		this.opFlag = type.getType();
		head = new ProtoHead(CmdConstants.STORAGE_PROTO_CMD_SET_METADATA);
	}

	/**
	 * 打包参数
	 */
	@Override
	public byte[] encodeParam(Charset charset) {
		// 运行时参数在此计算值
		this.fileNameByteLengh = path.getBytes(charset).length;
		this.mataDataByteLength = getMetaDataSetByteSize(charset);
		return super.encodeParam(charset);
	}

	/**
	 * 获取metaDataSet长度
	 * 
	 * @param metaDataSet
	 * @param charset
	 * @return
	 */
	private int getMetaDataSetByteSize(Charset charset) {
		return MetadataMapper.toByte(metaDataSet, charset).length;
	}

	public String getGroupName() {
		return groupName;
	}

	public Set<MateData> getMetaDataSet() {
		return metaDataSet;
	}

	public byte getOpFlag() {
		return opFlag;
	}

	public String getPath() {
		return path;
	}

	public int getFileNameByteLengh() {
		return fileNameByteLengh;
	}

	public int getMataDataByteLength() {
		return mataDataByteLength;
	}

}
