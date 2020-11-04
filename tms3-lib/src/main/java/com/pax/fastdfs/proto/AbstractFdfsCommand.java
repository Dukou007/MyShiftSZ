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
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pax.fastdfs.conn.Connection;
import com.pax.fastdfs.exception.FdfsIOException;

/**
 * 交易命令抽象类
 * 
 * @author tobato
 * @param <T>
 *
 */
public abstract class AbstractFdfsCommand<T> implements FdfsCommand<T> {

	/** 日志 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFdfsCommand.class);

	/** 表示请求消息 */
	protected FdfsRequest request;

	/** 解析反馈消息对象 */
	protected FdfsResponse<T> response;

	/**
	 * 对服务端发出请求然后接收反馈
	 */
	@Override
	public T execute(Connection conn) {
		// 封装socket交易 send
		try {
			send(conn.getOutputStream(), conn.getCharset());
		} catch (IOException e) {
			LOGGER.error("send conent error", e);
			throw new FdfsIOException("socket io exception occured while sending cmd", e);
		}

		try {
			return receive(conn.getInputStream(), conn.getCharset());
		} catch (IOException e) {
			LOGGER.error("receive conent error", e);
			throw new FdfsIOException("socket io exception occured while receive content", e);
		}

	}

	/**
	 * 将报文输出规范为模板方法
	 * 
	 * <pre>
	 * 1.输出报文头
	 * 2.输出报文参数
	 * 3.输出文件内容
	 * </pre>
	 * 
	 * @param out
	 * @throws IOException
	 */
	protected void send(OutputStream out, Charset charset) throws IOException {
		// 报文分为三个部分
		// 报文头
		byte[] head = request.getHeadByte(charset);
		// 交易参数
		byte[] param = request.encodeParam(charset);
		// 交易文件流
		InputStream inputFile = request.getInputFile();
		long fileSize = request.getFileSize();
		LOGGER.debug("Send Request..{}", request.getHead());
		LOGGER.debug("Request Param {}", param);

		if (param == null || param.length == 0) {
			// 输出报文头
			out.write(head);
		} else {
			// 输出报文头和参数
			byte[] buffer = new byte[head.length + param.length];
			System.arraycopy(head, 0, buffer, 0, head.length);
			System.arraycopy(param, 0, buffer, head.length, param.length);
			out.write(buffer);
		}

		// 输出文件流
		if (null != inputFile) {
			sendFileContent(inputFile, fileSize, out);
		}
	}

	/**
	 * 接收这里只能确切知道报文头，报文内容(参数+文件)只能靠接收对象分析
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected T receive(InputStream in, Charset charset) throws IOException {

		// 解析报文头
		ProtoHead head = ProtoHead.createFromInputStream(in);
		LOGGER.debug("Receive Response {}", head);
		// 校验报文头
		head.validateResponseHead();

		// 解析报文体
		return response.decode(head, in, charset);

	}

	/**
	 * 发送文件
	 * 
	 * @param ins
	 * @param size
	 * @param ous
	 * @throws IOException
	 */
	protected void sendFileContent(InputStream ins, long size, OutputStream ous) throws IOException {
		LOGGER.debug("开始上传文件流大小为{}", size);
		long remainBytes = size;
		byte[] buff = new byte[256 * 1024];
		int bytes;
		while (remainBytes > 0) {
			if ((bytes = ins.read(buff, 0, remainBytes > buff.length ? buff.length : (int) remainBytes)) < 0) {
				throw new IOException("the end of the stream has been reached. not match the expected size ");
			}

			ous.write(buff, 0, bytes);
			remainBytes -= bytes;
			LOGGER.debug("剩余数据量{}", remainBytes);
		}
	}

}
