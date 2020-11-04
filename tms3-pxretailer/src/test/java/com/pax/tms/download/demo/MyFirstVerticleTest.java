package com.pax.tms.download.demo;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.http.Http2Settings;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.StreamResetException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JdkSSLEngineOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.OpenSSLEngineOptions;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class MyFirstVerticleTest {

	private Vertx vertx;

	private int port = 8081;

	@Before
	public void setUp(TestContext context) throws IOException {
		ServerSocket socket = new ServerSocket(0);
		port = socket.getLocalPort();
		socket.close();

		// vertx = Vertx.vertx();
		VertxOptions vertxOptions = new VertxOptions();
		vertxOptions.setWorkerPoolSize(40);
		vertxOptions.setEventLoopPoolSize(8);// where N by default is core*2

		vertx = Vertx.vertx(vertxOptions);

		JsonObject config = new JsonObject().put("name", "tim").put("directory", "/blah").put("http.port", port);
		DeploymentOptions options = new DeploymentOptions().setConfig(config);
		options.setInstances(16);
		vertx.deployVerticle(MyFirstVerticle.class.getName(), options, context.asyncAssertSuccess());

	}

	public void clustering() {
		VertxOptions options = new VertxOptions().setEventBusOptions(new EventBusOptions().setSsl(true)
				.setKeyStoreOptions(new JksOptions().setPath("keystore.jks").setPassword("wibble"))
				.setTrustStoreOptions(new JksOptions().setPath("keystore.jks").setPassword("wibble"))
				.setClientAuth(ClientAuth.REQUIRED).setClusterPublicHost("whatever").setClusterPublicPort(1234));

		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				Vertx vertx = res.result();
				EventBus eventBus = vertx.eventBus();
				System.out.println("We now have a clustered event bus: " + eventBus);
			} else {
				System.out.println("Failed: " + res.cause());
			}
		});
	}

	public void createTcpServer() {
		NetServerOptions options = new NetServerOptions().setPort(4321).setLogActivity(true).setSsl(true)
				.setKeyStoreOptions(new JksOptions().setPath("/path/to/your/server-keystore.jks")
						.setPassword("password-of-your-keystore"))
				.setPemKeyCertOptions(new PemKeyCertOptions().setKeyPath("/path/to/your/server-key.pem")
						.setCertPath("/path/to/your/server-cert.pem"))
				.setClientAuth(ClientAuth.REQUIRED)
				.setTrustStoreOptions(new JksOptions().setPath("/path/to/your/truststore.jks")
						.setPassword("password-of-your-truststore"))
				.addEnabledCipherSuite("ECDHE-RSA-AES128-GCM-SHA256")
				.addEnabledCipherSuite("ECDHE-ECDSA-AES128-GCM-SHA256").addEnabledSecureTransportProtocol("TLSv1.1")
				.addEnabledSecureTransportProtocol("TLSv1.2").setJdkSslEngineOptions(new JdkSSLEngineOptions())
				.setOpenSslEngineOptions(new OpenSSLEngineOptions());
		;
		NetServer server = vertx.createNetServer(options);
		server.listen(1234, "localhost", res -> {
			if (res.succeeded()) {
				System.out.println("Server is now listening!");
			} else {
				System.out.println("Failed to bind!");
			}
		});

		server.connectHandler(socket -> {
			// Handle the connection in here
			socket.handler(buffer -> {
				System.out.println("I received some bytes: " + buffer.length());

				Buffer buffer1 = Buffer.buffer().appendFloat(12.34f).appendInt(123);
				socket.write(buffer1);

				// Write a string in UTF-8 encoding
				socket.write("some data");

				// Write a string using the specified encoding
				socket.write("some data", "UTF-16");
			});

			socket.closeHandler(v -> {
				System.out.println("The socket has been closed");
			});

			socket.exceptionHandler(e -> {
			});

			System.out.println(socket.writeHandlerID());
			System.out.println(socket.localAddress());
			System.out.println(socket.remoteAddress());

			// This can be a very efficient way to send files, as it can be
			// handled by the OS kernel directly where supported by the
			// operating system.
			socket.sendFile("myfile.dat");

			// socket.upgradeToSsl(handler);
		});

		server.close(res -> {
			if (res.succeeded()) {
				System.out.println("Server is now closed");
			} else {
				System.out.println("close failed");
			}
		});
	}

	public void createHttpServer() {
		HttpServerOptions options = new HttpServerOptions().setMaxWebsocketFrameSize(1000000).setUseAlpn(true)
				.setSsl(true).setKeyStoreOptions(new JksOptions().setPath("/path/to/my/keystore")).setLogActivity(true);

		HttpServer server = vertx.createHttpServer(options);

		server.connectionHandler(connection -> {
			System.out.println("A client connected");

			connection.updateSettings(new Http2Settings().setMaxConcurrentStreams(100), ar -> {
				if (ar.succeeded()) {
					System.out.println("The settings update has been acknowledged ");
				}
			});

			Buffer data = Buffer.buffer();
			for (byte i = 0; i < 8; i++) {
				data.appendByte(i);
			}
			connection.ping(data, pong -> {
				System.out.println("Remote side replied");
			});

			connection.pingHandler(ping -> {
				System.out.println("Got pinged by remote side");
			});
		});

		server.requestHandler(request -> {
			request.response().end("Hello world");
			System.out.println(request.version());
			System.out.println(request.method());
			System.out.println(request.uri());
			System.out.println(request.path());
			System.out.println(request.query());
			System.out.println(request.host());
			System.out.println(request.remoteAddress());
			System.out.println(request.absoluteURI());

			// request.endHandler(endHandler);

			request.handler(buffer -> {
				System.out.println("I have received a chunk of the body of length " + buffer.length());
			});

			Buffer totalBuffer = Buffer.buffer();

			request.handler(buffer -> {
				System.out.println("I have received a chunk of the body of length " + buffer.length());
				totalBuffer.appendBuffer(buffer);
			});

			request.endHandler(v -> {
				System.out.println("Full body received, length = " + totalBuffer.length());
			});

			request.bodyHandler(totalBuffer2 -> {
				System.out.println("Full body received, length = " + totalBuffer2.length());
			});

			request.setExpectMultipart(true);
			request.endHandler(v -> {
				// The body has now been fully read, so retrieve the form
				// attributes
				// MultiMap formAttributes = request.formAttributes();
			});

			request.uploadHandler(upload -> {
				System.out.println("Got a file upload " + upload.name());

				upload.handler(chunk -> {
					System.out.println("Received a chunk of the upload of length " + chunk.length());
				});
			});

			request.uploadHandler(upload -> {
				upload.streamToFileSystem("myuploads_directory/" + upload.filename());
			});

			MultiMap headers = request.headers();
			System.out.println("User agent is " + headers.get("user-agent"));

			MultiMap params = request.params();

			System.out.println("param ss is " + params.get("ss"));

			String file = "";
			if (request.path().equals("/")) {
				file = "index.html";
			} else if (!request.path().contains("..")) {
				file = request.path();
			}
			request.response().sendFile("web/" + file);

			long offset = 0;
			try {
				offset = Long.parseLong(request.getParam("start"));
			} catch (NumberFormatException e) {
				// error handling...
			}

			long end = Long.MAX_VALUE;
			try {
				end = Long.parseLong(request.getParam("end"));
			} catch (NumberFormatException e) {
				// error handling...
			}

			request.response().sendFile("web/mybigfile.txt", offset, end);

			// HttpServerResponse response = request.response();
			// if (request.method() == HttpMethod.PUT) {
			// response.setChunked(true);
			// Pump.pump(request, response).start();
			// request.endHandler(v -> response.end());
			// } else {
			// response.setStatusCode(400).end();
			// }

			request.customFrameHandler(frame -> {

				System.out.println("Received a frame type=" + frame.type() + " payload" + frame.payload().toString());
			});

			request.response().exceptionHandler(err -> {
				if (err instanceof StreamResetException) {
					StreamResetException reset = (StreamResetException) err;
					System.out.println("Stream reset " + reset.getCode());
				}
			});

			// response = request.response();
			// response.setStatusCode(200);
			// response.setStatusMessage("success");
			// // response.write(buffer);
			//
			// response.write("hello world!");
			// response.write("hello world!", "UTF-16");
			// response.end();
			//
			// response.end("hello world!");
			//
			// MultiMap headers1 = response.headers();
			// headers1.set("content-type", "text/html");
			// headers1.set("other-header", "wibble");
			// response.putHeader("content-type",
			// "text/html").putHeader("other-header", "wibble");
			//
			// response.setChunked(true);
			//
			// MultiMap trailers = response.trailers();
			// trailers.set("X-wibble", "woobble").set("X-quux", "flooble");
			//
			// response.putTrailer("X-wibble", "woobble").putTrailer("X-quux",
			// "flooble");
		});

		server.listen(8080, "myhost.com", res -> {
			if (res.succeeded()) {
				System.out.println("Server is now listening!");
			} else {
				System.out.println("Failed to bind!");
			}
		});

	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}

	@Test
	public void testMyApplication(TestContext context) {
		final Async async = context.async();
		vertx.createHttpClient().getNow(port, "localhost", "/", response -> {
			response.handler(body -> {
				context.assertTrue(body.toString().contains("Hello"));
				async.complete();
			});
		});
	}
}
