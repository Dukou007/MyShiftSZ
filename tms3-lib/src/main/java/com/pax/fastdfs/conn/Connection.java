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
package com.pax.fastdfs.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * 表示一个客户端与服务端的连接
 * 
 * 负责连接的管理
 * 
 *
 */
public interface Connection {

	/**
	 * 关闭连接
	 */
	void close();

	/**
	 * 连接是否关闭
	 * 
	 * @return
	 */
	boolean isClosed();

	/**
	 * 测试连接是否有效
	 * 
	 * @return
	 */
	boolean isValid();

	/**
	 * 获取输出流
	 * 
	 * @return
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException;

	/**
	 * 获取输入流
	 * 
	 * @return 输入流
	 * @throws IOException
	 *             获取输入流错误
	 */
	public InputStream getInputStream() throws IOException;

	/**
	 * 获取字符集
	 * 
	 * @return 字符集
	 */
	public Charset getCharset();

}
