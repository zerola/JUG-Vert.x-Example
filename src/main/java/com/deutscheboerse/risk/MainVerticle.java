package com.deutscheboerse.risk;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {
        Future<String> httpFuture = Future.future();
        Future<String> responderFuture = Future.future();
        vertx.deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setConfig(config()), httpFuture.completer());
        vertx.deployVerticle(ProcessorVerticle.class.getName(), new DeploymentOptions().setConfig(config()), responderFuture.completer());
        CompositeFuture.all(httpFuture, responderFuture).setHandler(ar -> {
           if (ar.succeeded())  {
               startFuture.complete();
           } else {
               startFuture.fail(ar.cause());
           }
        });
    }

}
