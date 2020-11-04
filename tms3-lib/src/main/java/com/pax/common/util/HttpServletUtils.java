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
package com.pax.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.Json;

public class HttpServletUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServletUtils.class);

	private HttpServletUtils() {
	}

	public static boolean isJsonRequest(HttpServletRequest request) {
		String requestType = request.getHeader("X-Requested-With");
		if (requestType != null && "XMLHttpRequest".equalsIgnoreCase(requestType)) {
			return true;
		}
		return false;
	}

	public static void writeJson(HttpServletResponse response, Map<?, ?> map) throws IOException {
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().println(Json.encode(map));
		response.getWriter().flush();
	}

	/**
	 * 2010年 RFC 5987 发布，正式规定了 HTTP Header 中多语言编码的处理方式，应当采用类似 MIME 扩展的
	 * parameter*=charset'lang'value 的格式，但是其中 value 应根据 RFC 3986 Section 2.1
	 * 使用百分号进行编码，并且规定浏览器至少应该支持 ASCII 和 UTF-8 。随后，2011年 RFC 6266 发布，正式将
	 * Content-Disposition 纳入 HTTP 标准，并再次强调了 RFC 5987
	 * 中多语言编码的方法，还给出了一个范例用于解决向后兼容的问题——就是我在一开始给出的例子： Content-Disposition:
	 * attachment; filename="encoded_text"; filename*=utf-8''encoded_text
	 * 
	 * 
	 * @param filename
	 * @param response
	 */
	public static void setDownloadFilename(HttpServletResponse response, String filename) {
		response.setCharacterEncoding("UTF-8");
		try {
			String encodedText = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + encodedText + "\"; filename*=utf-8''" + encodedText);
		} catch (UnsupportedEncodingException e1) {
			LOGGER.error("UTF-8 Character Encoding is not supported", e1);
		}
	}

	public static void setDownloadXlsx(HttpServletResponse response, String filename) {
		setDownloadFilename(response, filename);
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
	}

	public static void setDownloadXls(HttpServletResponse response, String filename) {
		setDownloadFilename(response, filename);
		response.setContentType("application/vnd.ms-excel;charset=UTF-8");
	}

	public static void setDownloadExcel(String filename, HttpServletResponse response) {
		if (filename.endsWith(".xlsx")) {
			setDownloadXlsx(response, filename);
		} else {
			setDownloadXls(response, filename);
		}
	}

	public static void setDownloadOctet(HttpServletResponse response, String filename) throws IOException {
		setDownloadFilename(response, filename);
		response.setContentType("application/octet-stream;");
	}

	public static void setDownloadMs(HttpServletResponse response, String filename) throws IOException {
		setDownloadFilename(response, filename);
		response.setContentType("application/x-msdownload;");
	}

	public static void downloadFile(HttpServletResponse response, File file) throws IOException {
		try (InputStream in = new FileInputStream(file);) {
			response.setHeader("Content-Length", String.valueOf(file.length()));
			IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
		}
	}
}
