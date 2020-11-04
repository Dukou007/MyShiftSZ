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
 * 不支持的图片格式
 * 
 * @author tobato
 *
 */
public class FdfsUnsupportImageTypeException extends FdfsException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 8498179372343498770L;

    public FdfsUnsupportImageTypeException(String message) {
        super(message);
    }

}
