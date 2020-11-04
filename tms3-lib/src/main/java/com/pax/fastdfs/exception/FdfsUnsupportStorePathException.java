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
 * 从Url解析StorePath文件路径对象错误
 * 
 * @author wuyf
 *
 */
public class FdfsUnsupportStorePathException extends FdfsException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8116336411011152869L;

    public FdfsUnsupportStorePathException(String message) {
        super(message);
    }

}
