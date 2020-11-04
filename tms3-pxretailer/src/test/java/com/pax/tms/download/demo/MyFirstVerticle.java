package com.pax.tms.download.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

public class MyFirstVerticle extends AbstractVerticle {

	/*
	 * GET /api/whiskies => get all bottles (getAll) GET /api/whiskies/:id =>
	 * get the bottle with the corresponding id (getOne) POST /api/whiskies =>
	 * add a new bottle (addOne) PUT /api/whiskies/:id => update a bottle
	 * (updateOne) DELETE /api/whiskies/id => delete a bottle (deleteOne)
	 */
	@Override
	public void start(Future<Void> fut) {
		// Create a router object.
		Router router = Router.router(vertx);

		// Bind "/" to our hello message - so we are still compatible.
		router.route("/").handler(routingContext -> {
			HttpServerResponse response = routingContext.response();
			response.putHeader("content-type", "text/html").end("<h1>Hello from my first Vert.x 3 application</h1>");
		});

		// Serve static resources from the /assets directory
		router.route("/assets/*").handler(StaticHandler.create("assets"));
		router.get("/api/whiskies").handler(this::getAll);

		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080),
				result -> {
					if (result.succeeded()) {
						fut.complete();
					} else {
						fut.fail(result.cause());
					}
				});

	}

	private void getAll(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end("sdfsdf");
	}

}
