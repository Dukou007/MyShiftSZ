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

import java.net.Socket;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.pax.common.web.HttpUtils;

public class IpChecker {

	private String[] whiteIpLst = null;

	public IpChecker() {
	}

	public IpChecker(String ipLst) {
		setIpCheck(ipLst);
	}

	public IpChecker(String[] ipLst) {
		setIpCheck(ipLst);
	}

	public IpChecker(List<String> ipLst) {
		setIpCheck(ipLst);
	}

	public static String getIpAddr(HttpServletRequest request) {
		return HttpUtils.getRemoteAddr(request);
	}

	public void setIpCheck(String ipLst) {
		if (ipLst == null) {
			return;
		}
		whiteIpLst = ipLst.split("\\|");
	}

	public void setIpCheck(String[] ipLst) {
		if (ipLst == null) {
			return;
		}
		whiteIpLst = ipLst;
	}

	public void setIpCheck(List<String> ipLst) {
		if (ipLst == null) {
			return;
		}
		whiteIpLst = ipLst.toArray(new String[0]);
	}

	public boolean check(String ip) {
		return containsIp(ip);
	}

	public boolean check(HttpServletRequest request) {
		return containsIp(getIpAddr(request));
	}

	public boolean check(Socket socket) {
		return containsIp(socket.getInetAddress().getHostAddress());
	}

	private boolean containsIp(String ip) {
		if (whiteIpLst == null) {
			return true;
		}

		for (int i = 0; i < whiteIpLst.length; i++) {
			if (StringUtils.equals(whiteIpLst[i], ip)) {
				return true;
			}
		}
		return false;
	}
}
