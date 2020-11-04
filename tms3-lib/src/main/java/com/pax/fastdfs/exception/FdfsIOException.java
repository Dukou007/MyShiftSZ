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
package com.pax.fastdfs.exception;

/**
 * 非fastdfs本身的错误码抛出的异常，而是java客户端向服务端发送命令、文件或从服务端读取结果、下载文件时发生io异常
 * 
 * @author yuqihuang
 * @author tobato
 * 
 */
public class FdfsIOException extends FdfsException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param cause
     */
    public FdfsIOException(Throwable cause) {
        super("Client connect exception", cause);
    }

    /**
     * @param message
     * @param cause
     */
    public FdfsIOException(String message, Throwable cause) {
        super("Client connect exception:" + message, cause);
    }

    public FdfsIOException(String message) {
        super(message);
    }

}
