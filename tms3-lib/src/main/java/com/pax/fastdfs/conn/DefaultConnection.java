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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pax.fastdfs.exception.FdfsConnectException;
import com.pax.fastdfs.proto.CmdConstants;
import com.pax.fastdfs.proto.OtherConstants;
import com.pax.fastdfs.proto.mapper.BytesUtil;

/**
 * 默认连接实现
 * 
 *
 */
public class DefaultConnection implements Connection {

	/** 封装socket */
	private Socket socket;

	/** 字符集 */
	private Charset charset;

	/** 日志 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConnection.class);

	/**
	 * 创建与服务端连接
	 * 
	 * @param address
	 * @param soTimeout
	 * @param connectTimeout
	 */
	public DefaultConnection(InetSocketAddress address, int soTimeout, int connectTimeout, Charset charset) {
		try {
			socket = new Socket();
			socket.setSoTimeout(soTimeout);
			LOGGER.debug("connect to {} soTimeout={} connectTimeout={}", address, soTimeout, connectTimeout);
			this.charset = charset;
			socket.connect(address, connectTimeout);
			socket.setTcpNoDelay(true);
			socket.setKeepAlive(false);
		} catch (IOException e) {
			throw new FdfsConnectException("can't create connection to" + address, e);
		}
	}

	/**
	 * 正常关闭连接
	 */
	@Override
	public synchronized void close() {
		LOGGER.debug("disconnect from {}", socket);
		if (socket.isOutputShutdown()) {
			LOGGER.debug("output stream to {} has already been shutdown", socket);
			IOUtils.closeQuietly(socket);
			return;
		}

		byte[] header = new byte[OtherConstants.FDFS_PROTO_PKG_LEN_SIZE + 2];
		Arrays.fill(header, (byte) 0);

		byte[] hexLen = BytesUtil.long2buff(0);
		System.arraycopy(hexLen, 0, header, 0, hexLen.length);
		header[OtherConstants.PROTO_HEADER_CMD_INDEX] = CmdConstants.FDFS_PROTO_CMD_QUIT;
		header[OtherConstants.PROTO_HEADER_STATUS_INDEX] = (byte) 0;
		try {
			socket.getOutputStream().write(header);
			socket.close();
		} catch (IOException e) {
			LOGGER.error("close connection error", e);
		} finally {
			IOUtils.closeQuietly(socket);
		}
	}

	/**
	 * 连接是否关闭
	 */
	@Override
	public boolean isClosed() {
		return socket.isClosed();
	}

	/**
	 * 检查连接是否有效
	 */
	@Override
	public boolean isValid() {
		LOGGER.debug("check connection status of {} ", this);
		if (socket.isClosed()) {
			return false;
		}
		try {
			byte[] header = new byte[OtherConstants.FDFS_PROTO_PKG_LEN_SIZE + 2];
			Arrays.fill(header, (byte) 0);

			byte[] hexLen = BytesUtil.long2buff(0);
			System.arraycopy(hexLen, 0, header, 0, hexLen.length);
			header[OtherConstants.PROTO_HEADER_CMD_INDEX] = CmdConstants.FDFS_PROTO_CMD_ACTIVE_TEST;
			header[OtherConstants.PROTO_HEADER_STATUS_INDEX] = (byte) 0;
			socket.getOutputStream().write(header);
			if (socket.getInputStream().read(header) != header.length) {
				return false;
			}

			return header[OtherConstants.PROTO_HEADER_STATUS_INDEX] == 0 ? true : false;
		} catch (IOException e) {
			LOGGER.error("valid connection error", e);
			return false;
		}
	}

	/**
	 * 获取输出流
	 * 
	 * @return
	 * @throws IOException
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	/**
	 * 获取输入流
	 * 
	 * @return
	 * @throws IOException
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	/**
	 * 获取字符集
	 * 
	 * @return
	 */
	@Override
	public Charset getCharset() {
		return charset;
	}

}
