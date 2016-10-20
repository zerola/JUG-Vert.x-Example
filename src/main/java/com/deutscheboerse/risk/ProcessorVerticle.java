package com.deutscheboerse.risk;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

public class ProcessorVerticle extends AbstractVerticle {
    private MessageConsumer<JsonObject> consumer;

    @Override
    public void start(Future<Void> fut) {
        consumer = vertx.eventBus().consumer("request.hello");
        consumer.handler(message -> {
            JsonObject params = message.body();
            message.reply(String.format("Hello %s from Vert.x", params.getString("name")));
        });
        consumer.completionHandler(fut.completer());
    }

    @Override
    public void stop(Future<Void> fut) throws Exception {
        consumer.unregister(fut.completer());
    }

}
