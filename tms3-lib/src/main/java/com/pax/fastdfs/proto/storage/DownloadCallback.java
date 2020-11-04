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
package com.pax.fastdfs.proto.storage;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件下载回调接口
 * 
 * @author tobato
 *
 * @param <T>
 */
public interface DownloadCallback<T> {

    /**
     * 注意不能直接返回入参的InputStream，因为此方法返回后将关闭原输入流
     * 
     * 不能关闭ins? TODO验证是否可以关闭
     * 
     * @param ins
     * @throws IOException
     * @return
     */
    T recv(InputStream ins) throws IOException;

}
