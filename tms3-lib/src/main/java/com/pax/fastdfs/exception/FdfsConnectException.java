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
 * 非fastdfs本身的错误码抛出的异常，socket连不上时抛出的异常
 * 
 * @author yuqihuang
 * @author tobato
 * 
 */
public class FdfsConnectException extends FdfsUnavailableException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     */
    public FdfsConnectException(String message, Throwable t) {
        super(message, t);
    }

}
