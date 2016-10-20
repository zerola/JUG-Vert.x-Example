package com.deutscheboerse.risk;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class HttpVerticleTest {

    private Vertx vertx;
    private int port = Integer.getInteger("http.port", 8080);

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        JsonObject config = new JsonObject().put("httpPort", port);
        vertx.deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setConfig(config), context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testHttpRouter(TestContext context) {
        final Async async = context.async();

        MessageConsumer<JsonObject> consumer = vertx.eventBus().consumer("request.hello");
        consumer.handler(message -> {
            JsonObject params = message.body();
            message.reply(String.format("Hello %s from Test", params.getString("name")));
        });


        vertx.createHttpClient().getNow(port, "localhost", "/hello/TestName", res -> {
            res.handler(body -> {
                context.assertTrue(body.toString().equals("Hello TestName from Test"));
                async.complete();
                consumer.unregister();
            });
        });
    }
}
