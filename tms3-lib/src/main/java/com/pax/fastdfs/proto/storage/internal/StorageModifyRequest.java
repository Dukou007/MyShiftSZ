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

import java.io.InputStream;
import java.nio.charset.Charset;

import com.pax.fastdfs.proto.CmdConstants;
import com.pax.fastdfs.proto.FdfsRequest;
import com.pax.fastdfs.proto.ProtoHead;
import com.pax.fastdfs.proto.mapper.DynamicFieldType;
import com.pax.fastdfs.proto.mapper.FdfsColumn;

/**
 * 文件修改请求
 * 
 * @author tobato
 *
 */
public class StorageModifyRequest extends FdfsRequest {

    /** 文件路径长度 */
    @FdfsColumn(index = 0)
    private long pathSize;
    /** 开始位置 */
    @FdfsColumn(index = 1)
    private long fileOffset;
    /** 发送文件长度 */
    @FdfsColumn(index = 2)
    private long fileSize;
    /** 文件路径 */
    @FdfsColumn(index = 3, dynamicField = DynamicFieldType.ALLRESTBYTE)
    private String path;

    /**
     * 构造函数
     * 
     * @param inputStream
     * @param fileExtName
     * @param fileSize
     * @param storeIndex
     * @param isAppenderFile
     */
    public StorageModifyRequest(InputStream inputStream, long fileSize, String path, long fileOffset) {
        super();
        this.inputFile = inputStream;
        this.fileSize = fileSize;
        this.path = path;
        this.fileOffset = fileOffset;
        head = new ProtoHead(CmdConstants.STORAGE_PROTO_CMD_MODIFY_FILE);

    }

    /**
     * 打包参数
     */
    @Override
    public byte[] encodeParam(Charset charset) {
        // 运行时参数在此计算值
        this.pathSize = path.getBytes(charset).length;
        return super.encodeParam(charset);
    }

    public long getPathSize() {
        return pathSize;
    }

    public long getFileOffset() {
        return fileOffset;
    }

    public String getPath() {
        return path;
    }

    @Override
    public long getFileSize() {
        return fileSize;
    }

}
