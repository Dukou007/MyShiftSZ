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
 * 上传图片例外
 * 
 * @author tobato
 *
 */
public class FdfsUploadImageException extends FdfsException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    protected FdfsUploadImageException(String message) {
        super(message);
    }

    public FdfsUploadImageException(String message, Throwable cause) {
        super(message, cause);
    }

}
