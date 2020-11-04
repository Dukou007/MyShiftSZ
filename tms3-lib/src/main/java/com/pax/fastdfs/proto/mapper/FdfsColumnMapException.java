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
package com.pax.fastdfs.proto.mapper;

/**
 * 映射例外
 * 
 * @author tobato
 *
 */
public class FdfsColumnMapException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1336200127024129847L;

    protected FdfsColumnMapException() {
    }

    protected FdfsColumnMapException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    protected FdfsColumnMapException(String message, Throwable cause) {
        super(message, cause);
    }

    protected FdfsColumnMapException(String message) {
        super(message);
    }

    protected FdfsColumnMapException(Throwable cause) {
        super(cause);
    }

}
