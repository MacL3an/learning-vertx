/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx.test;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import se.maclean.learning.vertx.DataHandler;
import se.maclean.learning.vertx.LocalDiskDataHandler;
import se.maclean.learning.vertx.ServiceContainer;
import se.maclean.learning.vertx.ServiceStatusVerticle;

/**
 *
 * @author MacL3an
 */
@RunWith(VertxUnitRunner.class)
public class ServiceStatusVerticleTest {

  private Vertx vertx;

  @Before
  public void setUp(TestContext context) {
    vertx = Vertx.vertx();
    DataHandler dataHandler = new LocalDiskDataHandler("testServices.json");
    vertx.deployVerticle(new ServiceStatusVerticle(dataHandler),
        context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void testGetEndpointReturnsSampleData(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/services",
     response -> {
      response.handler(body -> {
        context.assertTrue(body.toString().contains("kry.se"));
        async.complete();
      });
    });
  }
  
  
  @Test
  public void testGetEndpointReturnsCorrectNoOfServices(TestContext context) {
    final Async async = context.async();

    vertx.createHttpClient().getNow(8080, "localhost", "/services",
     response -> {
      response.handler(body -> {        
        ServiceContainer serviceContainer = Json.decodeValue(body.toString(), ServiceContainer.class);
        context.assertTrue(serviceContainer.getServices().size() == 2, "Wrong no of services");        
        async.complete();
      });
    });
  }
}
