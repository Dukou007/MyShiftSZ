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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.springframework.core.GenericTypeResolver;

import com.pax.fastdfs.proto.mapper.FdfsParamMapper;

/**
 * Fdfs交易应答基类
 * 
 * @author tobato
 *
 */
public abstract class FdfsResponse<T> {
	/** 报文头 */
	protected ProtoHead head;

	/** 返回值类型 */
	protected final Class<T> genericType;

	/**
	 * 构造函数
	 * 
	 * @param genericType
	 */
	@SuppressWarnings("unchecked")
	public FdfsResponse() {
		super();
		this.genericType = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), FdfsResponse.class);
		// Type theclass = this.getClass().getGenericSuperclass();
		// this.genericType = ((ParameterizedType)
		// theclass).getActualTypeArguments()[0];
	}

	/** 获取报文长度 */
	protected long getContentLength() {
		return head.getContentLength();
	}

	/**
	 * 解析反馈结果,head已经被解析过
	 * 
	 * @param head
	 * @param in
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public T decode(ProtoHead head, InputStream in, Charset charset) throws IOException {
		this.head = head;
		return decodeContent(in, charset);
	}

	/**
	 * 解析反馈内容
	 * 
	 * @param in
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public T decodeContent(InputStream in, Charset charset) throws IOException {
		// 如果有内容
		if (getContentLength() > 0) {
			byte[] bytes = new byte[(int) getContentLength()];
			int contentSize = in.read(bytes);
			// 获取数据
			if (contentSize != getContentLength()) {
				throw new IOException("读取到的数据长度与协议长度不符");
			}
			return FdfsParamMapper.map(bytes, genericType, charset);
		}
		return null;
	}

}
