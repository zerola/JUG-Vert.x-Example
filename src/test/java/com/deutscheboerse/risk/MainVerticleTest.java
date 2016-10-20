package com.deutscheboerse.risk;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

    private Vertx vertx;
    private int port = Integer.getInteger("http.port", 8080);

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        JsonObject config = new JsonObject().put("httpPort", port);
        vertx.deployVerticle(MainVerticle.class.getName(), new DeploymentOptions().setConfig(config), context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testMain(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().getNow(port, "localhost", "/hello/TestName", res -> {
            res.handler(body -> {
                context.assertTrue(body.toString().contains("Hello TestName"));
                async.complete();
            });
        });
    }

}
