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
package com.pax.common.vertx;

import org.apache.commons.lang3.StringUtils;

import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.Http2Settings;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.OpenSSLEngineOptions;

public class NetServerOptionsSetting {

	private NetServerOptionsSetting() {
	}

	public static void setHttpServerOptions(HttpServerOptions options, JsonObject config) {
		options.setCompressionSupported(config.getBoolean("http.compressionSupported", false));
		options.setMaxWebsocketFrameSize(config.getInteger("http.maxWebsocketFrameSize", 65536));
		options.setHandle100ContinueAutomatically(config.getBoolean("http.handle100ContinueAutomatically", false));
		options.setMaxChunkSize(config.getInteger("http.maxChunkSize ", 8192));
		options.setMaxInitialLineLength(config.getInteger("http.maxInitialLineLength", 4096));
		options.setMaxHeaderSize(config.getInteger("http.maxHeaderSize", 8192));
		options.setHttp2ConnectionWindowSize(config.getInteger("http.http2ConnectionWindowSize", -1));
		options.setInitialSettings(new Http2Settings()
				.setMaxConcurrentStreams(config.getLong("http.initialSetting.maxConcurrentStreams", 100L)));

		setTcpOptions(options, config);
	}

	public static void setTcpOptions(NetServerOptions options, JsonObject config) {
		options.setAcceptBacklog(-1);// The default accept backlog = 1024
		options.setTcpNoDelay(true);// The default value
		options.setTcpKeepAlive(false);// The default value
		options.setSoLinger(-1);// The default value
		// The default value of TCP send buffer size
		options.setSendBufferSize(-1);
		// The default value of TCP receive buffer size
		options.setReceiveBufferSize(-1);
		/*
		 * 为从此 socket 上发送的包在 ip 头中设置流量类别 (traffic class) 或服务类型八位组
		 * (type-of-service octet)。由于底层网络实现可能忽略此值，应用程序应该将其视为一种提示。
		 */
		options.setTrafficClass(-1);

		/*
		 * Set the idle timeout, in seconds. zero means don't timeout. This
		 * determines if a connection will timeout and be closed if no data is
		 * received within the timeout.
		 * 
		 * Default idle timeout = 0
		 */
		options.setIdleTimeout(config.getInteger("tcp.idleTimeout", 300));

		// The default value
		options.setReuseAddress(config.getBoolean("tcp.reuseAddress", true));

		// The default value of Netty use pooled buffers = false
		options.setUsePooledBuffers(config.getBoolean("tcp.usePooledBuffers", false));

		// The default log enabled = false
		options.setLogActivity(config.getBoolean("tcp.logActivity", false));

		options.setPort(config.getInteger("tcp.port", 0));

		setSslOptions(options, config);
	}

	public static void setSslOptions(NetServerOptions options, JsonObject config) {
		// Set whether SSL/TLS is enabled
		boolean ssl = config.getBoolean("ssl", false);
		options.setSsl(ssl);
		if (!ssl) {
			return;
		}

		options.setKeyStoreOptions(new JksOptions().setPath(config.getString("ssl.keystore.path"))
				.setPassword(config.getString("ssl.keystore.password")));

		/*
		 * By default, the TLS configuration will use the Cipher suite of the
		 * JVM running Vert.x. This Cipher suite can be configured with a suite
		 * of enabled ciphers:
		 */
		String enabledCipherSuitesInput = config.getString("ssl.enabledCipherSuites");
		if (enabledCipherSuitesInput != null) {
			String[] suites = enabledCipherSuitesInput.split("\\|");
			for (String suite : suites) {
				suite = StringUtils.trimToNull(suite);
				if (suite != null) {
					options.addEnabledCipherSuite(suite);
				}
			}
		}

		/*
		 * By default, the TLS configuration will use the following protocol
		 * versions: SSLv2Hello, TLSv1, TLSv1.1 and TLSv1.2. Protocol versions
		 * can be configured by explicitly adding enabled protocols
		 */
		String enabledSecureTransportProtocolsInput = config.getString("ssl.enabledSecureTransportProtocols");
		if (enabledSecureTransportProtocolsInput != null) {
			String[] protocols = enabledSecureTransportProtocolsInput.split("\\|");
			for (String protocol : protocols) {
				protocol = StringUtils.trimToNull(protocol);
				if (protocol != null) {
					options.addEnabledSecureTransportProtocol(protocol);
				}
			}
		}

		/*
		 * The engine implementation can be configured to use OpenSSL instead of
		 * the JDK implementation. OpenSSL provides better performances and CPU
		 * usage than the JDK engine, as well as JDK version independence.
		 */
		boolean useOpenSSL = config.getBoolean("ssl.useOpenSSL", false);
		if (useOpenSSL) {
			options.setOpenSslEngineOptions(new OpenSSLEngineOptions());
		}

		/*
		 * 
		 * ALPN is a TLS extension for application layer protocol negotitation.
		 * It is used by HTTP/2: during the TLS handshake the client gives the
		 * list of application protocols it accepts and the server responds with
		 * a protocol it supports.
		 * 
		 * Java 8 does not supports ALPN out of the box, so ALPN should be
		 * enabled by other means:
		 * 
		 * OpenSSL support
		 * 
		 * Jetty-ALPN support
		 * 
		 * 
		 * <p> OpenSSL ALPN support
		 * 
		 * OpenSSL provides native ALPN support.
		 * 
		 * OpenSSL requires to configure setOpenSslEngineOptions and use
		 * netty-tcnative jar on the classpath. Using tcnative may require
		 * OpenSSL to be installed on your OS depending on the tcnative
		 * implementation. </p>
		 * 
		 * <p> Jetty-ALPN support
		 * 
		 * Jetty-ALPN is a small jar that overrides a few classes of Java 8
		 * distribution to support ALPN.
		 * 
		 * The JVM must be started with the alpn-boot-${version}.jar in its
		 * bootclasspath:
		 * 
		 * -Xbootclasspath/p:/path/to/alpn-boot${version}.jar</p>
		 * 
		 */
		options.setUseAlpn(config.getBoolean("ssl.useAlpn", false));

		// Set whether client auth is required
		boolean clientAuth = config.getBoolean("ssl.clientAuth", false);
		options.setClientAuth(clientAuth ? ClientAuth.REQUIRED : ClientAuth.NONE);
		if (!clientAuth) {
			return;
		}

		options.setTrustStoreOptions(new JksOptions().setPath(config.getString("ssl.truststore.path"))
				.setPassword(config.getString("ssl.truststore.password")));

		/*
		 * Trust can be configured to use a certificate revocation list (CRL)
		 * for revoked certificates that should no longer be trusted.
		 */
		String crlPathsInput = config.getString("ssl.crlPaths");
		if (crlPathsInput != null) {
			String[] crlPaths = crlPathsInput.split("\\|");
			for (String crlPath : crlPaths) {
				crlPath = StringUtils.trimToNull(crlPath);
				if (crlPath != null) {
					options.addCrlPath(crlPath);
				}
			}
		}
	}
}
