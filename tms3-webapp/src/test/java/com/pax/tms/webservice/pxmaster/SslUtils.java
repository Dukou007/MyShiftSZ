/*	
 * Copyright (C) 2018 PAX Computer Technology(Shenzhen) CO., LTD。 All rights reserved.			
 * ----------------------------------------------------------------------------------
 * PAX Computer Technology(Shenzhen) CO., LTD PROPRIETARY INFORMATION。
 * 
 * This software is supplied under the terms of a license agreement or nondisclosure 
 * agreement with PAX Computer Technology(Shenzhen) CO., LTD and may not be copied 
 * or disclosed except in accordance with the terms in that agreement.
 */
package com.pax.tms.webservice.pxmaster;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * @author Elliott.Z
 *
 */
public class SslUtils {

	public static class TrustAllX509TrustManager implements X509TrustManager {

		private static final X509Certificate[] acceptedIssuers = new X509Certificate[] {};

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.
		 * X509Certificate[], java.lang.String)
		 */
		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {// NOPMD
			// trust all
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.
		 * X509Certificate[], java.lang.String)
		 */
		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {// NOPMD
			// trust all;//NOPMD
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
		 */
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return acceptedIssuers;
		}

	}

	public static X509TrustManager trustAllX509TrustManager() {
		return new TrustAllX509TrustManager();
	}

	private SslUtils() {
		throw new IllegalStateException("Utility class");
	}

}
