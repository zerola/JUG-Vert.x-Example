package com.deutscheboerse.risk;

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
public class ProcessorVerticleTest {
    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(ProcessorVerticle.class.getName(), context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testProcessor(TestContext context) {
        final Async async = context.async();

        vertx.eventBus().send("request.hello", new JsonObject().put("name", "TestName"), ar -> {
            context.assertTrue(((String)ar.result().body()).contains("Hello TestName"));
            async.complete();
        });
    }
}
