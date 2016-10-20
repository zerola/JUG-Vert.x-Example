package com.deutscheboerse.risk;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpVerticle extends AbstractVerticle {

    private static final Integer DEFAULT_HTTP_PORT = 8080;
    private final Logger LOG = LoggerFactory.getLogger(HttpVerticle.class);

    @Override
    public void start(Future<Void> fut) {
        LOG.info("Starting {} with configuration: {}", HttpVerticle.class.getSimpleName(), config().encodePrettily());

        Router router = Router.router(vertx);
        router.get("/hello/:name").handler(this::getHello);

        HttpServer server = vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(config().getInteger("httpPort", HttpVerticle.DEFAULT_HTTP_PORT), res -> {
                    if (res.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(res.cause());
                    }
                });
    }

    private void getHello(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        JsonObject eventMessage = new JsonObject().put("name", routingContext.request().getParam("name"));
        vertx.eventBus().send("request.hello", eventMessage, ar -> {
            if (ar.succeeded()) {
                response.putHeader("content-type", "text/plain");
                response.end((String)ar.result().body());
            } else {
                LOG.error("Failed to query the DB service", ar.cause());
                response.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()).end();
            }
        });
    }
}