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

import com.pax.fastdfs.proto.CmdConstants;
import com.pax.fastdfs.proto.FdfsRequest;
import com.pax.fastdfs.proto.OtherConstants;
import com.pax.fastdfs.proto.ProtoHead;
import com.pax.fastdfs.proto.mapper.FdfsColumn;

/**
 * 文件上传命令
 * 
 * @author tobato
 *
 */
public class StorageUploadFileRequest extends FdfsRequest {

    private static final byte UPLOAD_CMD = CmdConstants.STORAGE_PROTO_CMD_UPLOAD_FILE;
    private static final byte UPLOAD_APPENDER_CMD = CmdConstants.STORAGE_PROTO_CMD_UPLOAD_APPENDER_FILE;

    /** 存储节点index */
    @FdfsColumn(index = 0)
    private byte storeIndex;
    /** 发送文件长度 */
    @FdfsColumn(index = 1)
    private long fileSize;
    /** 文件扩展名 */
    @FdfsColumn(index = 2, max = OtherConstants.FDFS_FILE_EXT_NAME_MAX_LEN)
    private String fileExtName;

    /**
     * 构造函数
     * 
     * @param inputStream
     * @param fileExtName
     * @param fileSize
     * @param storeIndex
     * @param isAppenderFile
     */
    public StorageUploadFileRequest(byte storeIndex, InputStream inputStream, String fileExtName, long fileSize,
            boolean isAppenderFile) {
        super();
        this.inputFile = inputStream;
        this.fileSize = fileSize;
        this.storeIndex = storeIndex;
        this.fileExtName = fileExtName;
        if (isAppenderFile) {
            head = new ProtoHead(UPLOAD_APPENDER_CMD);
        } else {
            head = new ProtoHead(UPLOAD_CMD);
        }
    }

    public byte getStoreIndex() {
        return storeIndex;
    }

    public void setStoreIndex(byte storeIndex) {
        this.storeIndex = storeIndex;
    }

    public String getFileExtName() {
        return fileExtName;
    }

    public void setFileExtName(String fileExtName) {
        this.fileExtName = fileExtName;
    }

    @Override
    public long getFileSize() {
        return fileSize;
    }

}
