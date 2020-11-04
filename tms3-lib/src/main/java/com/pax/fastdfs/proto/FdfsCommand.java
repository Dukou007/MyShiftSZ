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
package com.pax.fastdfs.proto;

import com.pax.fastdfs.conn.Connection;

/**
 * Fdfs交易命令抽象
 * 
 * @author tobato
 *
 */
public interface FdfsCommand<T> {

    /** 执行交易 */
    public T execute(Connection conn);

}
