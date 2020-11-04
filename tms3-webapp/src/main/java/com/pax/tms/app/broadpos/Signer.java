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
package com.pax.tms.app.broadpos;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class Signer {

	private String keystore;

	private String keystorePassword;

	private String entryAlias;

	private String entryPassword;

	private String algorithm = "SHA1withRSA";

	private PrivateKey privateKey;

	private String signVersion;

	private String rootCertVersion;

	private String rootCert;

	private byte[] rootCertData;

	private String scriptCertVersion;

	private String scriptCert;

	private byte[] scriptCertData;

	public void init() {
		String keystoreFilePath = null;
		File file = new File(keystore);
		if (file.exists()) {
			keystoreFilePath = file.getAbsolutePath();
		} else {
			keystoreFilePath = Signer.class.getClassLoader().getResource(keystore).getFile();
		}

		if (keystoreFilePath == null) {
			throw new SignatureException("keystore not found: " + keystore);
		}

		try (InputStream fis = new FileInputStream(keystoreFilePath);) {
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(fis, keystorePassword.toCharArray());
			KeyStore.Entry entry = ks.getEntry(entryAlias,
					new KeyStore.PasswordProtection(entryPassword.toCharArray()));
			privateKey = ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
		} catch (Exception e) {
			throw new SignatureException("init signer failed", e);
		}
	}

	public void loadCerts() throws IOException {
		if (StringUtils.isNotEmpty(rootCert)) {
			this.rootCertData = loadCertData(rootCert);
		}

		if (StringUtils.isNotEmpty(scriptCert)) {
			this.scriptCertData = loadCertData(scriptCert);
		}
	}

	private byte[] loadCertData(String certFile) throws IOException {
		String filePath = null;
		File file = new File(certFile);
		if (file.exists()) {
			filePath = file.getAbsolutePath();
		} else {
			filePath = Signer.class.getClassLoader().getResource(certFile).getFile();
		}
		if (filePath == null) {
			throw new SignatureException("cert file not found: " + certFile);
		}
		return FileUtils.readFileToByteArray(new File(filePath));
	}

	public byte[] sign(byte[] data) {
		try {
			Signature s = Signature.getInstance(algorithm);
			s.initSign(privateKey);
			s.update(data);
			return s.sign();
		} catch (Exception e) {
			throw new SignatureException("sign failed", e);
		}
	}

	public byte[] sign(String filePath) {
		return sign(new File(filePath));
	}

	public byte[] sign(File file) {
		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
			Signature s = Signature.getInstance(algorithm);
			s.initSign(privateKey);

			byte[] buff = new byte[4096];
			int l = 0;
			while ((l = inputStream.read(buff)) != -1) {
				s.update(buff, 0, l);
			}

			return s.sign();
		} catch (Exception e) {
			throw new SignatureException("sign failed", e);
		}
	}

	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}

	public void setKeystorePassword(String keystorePassword) {
		this.keystorePassword = keystorePassword;
	}

	public void setEntryAlias(String entryAlias) {
		this.entryAlias = entryAlias;
	}

	public void setEntryPassword(String entryPassword) {
		this.entryPassword = entryPassword;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getSignVersion() {
		return signVersion;
	}

	public void setSignVersion(String signVersion) {
		this.signVersion = signVersion;
	}

	public String getRootCert() {
		return rootCert;
	}

	public void setRootCert(String rootCert) {
		this.rootCert = rootCert;
	}

	public String getScriptCert() {
		return scriptCert;
	}

	public void setScriptCert(String scriptCert) {
		this.scriptCert = scriptCert;
	}

	public String getRootCertVersion() {
		return rootCertVersion;
	}

	public void setRootCertVersion(String rootCertVersion) {
		this.rootCertVersion = rootCertVersion;
	}

	public String getScriptCertVersion() {
		return scriptCertVersion;
	}

	public void setScriptCertVersion(String scriptCertVersion) {
		this.scriptCertVersion = scriptCertVersion;
	}

	public boolean containsRootCert(String certVersion) {
		return StringUtils.equals(rootCertVersion, certVersion);
	}

	public boolean containsScriptCert(String certVersion) {
		return StringUtils.equals(scriptCertVersion, certVersion);
	}

	public byte[] getRootCertData(String certVersion) {
		if (containsRootCert(certVersion)) {
			return this.rootCertData;
		}
		return null;
	}

	public byte[] getScriptCertData(String certVersion) {
		if (containsScriptCert(certVersion)) {
			return this.scriptCertData;
		}
		return null;
	}
}
