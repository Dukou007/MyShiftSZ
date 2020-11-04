package com.pax.tms.ssl;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLTest {

	public static void main(String[] args)
			throws KeyManagementException, NoSuchAlgorithmException, UnknownHostException, IOException {

		TrustManager tm = new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

		};
		SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
		sslContext.init(new KeyManager[0], new TrustManager[] { tm }, null);
		SSLSocketFactory sslsocketfactory = sslContext.getSocketFactory();
		Socket socket = sslsocketfactory.createSocket("192.168.6.125", 8543);
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		// BufferedReader reader = new BufferedReader(new
		// InputStreamReader(in));

		writer.write("HELLO");
		writer.flush();

		byte[] buffer = new byte[4096];
		int c = in.read(buffer);
		System.out.println(new String(buffer, 0, c));

		in.close();
		out.close();
		socket.close();

		Socket s = new Socket("192.168.6.125", 8580);
		OutputStream o = s.getOutputStream();
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(o));
		o.close();
		s.close();
		System.out.println(w);
	}

}
