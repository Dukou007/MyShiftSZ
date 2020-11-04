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
package com.pax.fastdfs.service;

import java.io.InputStream;

import com.pax.fastdfs.domain.StorePath;

/**
 * 支持断点续传的文件服务接口
 * 
 * <pre>
 * 适合处理大文件，分段传输
 * </pre>
 * 
 * @author tobato
 *
 */
public interface AppendFileStorageClient extends GenerateStorageClient {

    /**
     * 上传支持断点续传的文件
     * 
     * @param groupName
     * @param inputStream
     * @param fileSize
     * @param fileExtName
     * @return
     */
    StorePath uploadAppenderFile(String groupName, InputStream inputStream, long fileSize, String fileExtName);

    /**
     * 断点续传文件
     * 
     * @param groupName
     * @param path
     * @param inputStream
     * @param fileSize
     */
    void appendFile(String groupName, String path, InputStream inputStream, long fileSize);

    /**
     * 修改续传文件的内容
     * 
     * @param groupName
     * @param path
     * @param inputStream
     * @param fileSize
     * @param fileOffset
     */
    void modifyFile(String groupName, String path, InputStream inputStream, long fileSize, long fileOffset);

    /**
     * 清除续传类型文件的内容
     * 
     * @param groupName
     * @param path
     * @param truncatedFileSize
     */
    void truncateFile(String groupName, String path, long truncatedFileSize);

    /**
     * 清除续传类型文件的内容
     * 
     * @param groupName
     * @param path
     */
    void truncateFile(String groupName, String path);

}
