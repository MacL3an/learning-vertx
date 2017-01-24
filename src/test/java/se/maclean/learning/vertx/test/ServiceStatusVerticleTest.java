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
import se.maclean.learning.vertx.KryService;
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
  private final int port = 8080;
  private final String server = "localhost";
  private final String servicesEndPoint = "/services";

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

    vertx.createHttpClient().getNow(port, server, servicesEndPoint,
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

    vertx.createHttpClient().getNow(port, server, servicesEndPoint,
     response -> {
      response.handler(body -> {        
        ServiceContainer serviceContainer = Json.decodeValue(body.toString(), ServiceContainer.class);
        context.assertTrue(serviceContainer.getServices().size() == 2, "Wrong no of services");        
        async.complete();
      });
    });
  }
  
    
  @Test
  public void testPostEndpointAddsOneService(TestContext context) {
    final Async async = context.async();
    final String testName = "testName";
    final String testId = "testId";
    KryService kryService = new KryService(testId, testName, "url", "status", "lasCheck");
    final String serviceAsJson = Json.encodePrettily(kryService);
    final String length = Integer.toString(serviceAsJson.length());
    
    vertx.createHttpClient().post(port, server, servicesEndPoint)
            .putHeader("content-type", "application/json")
            .putHeader("content-length", length)
            .handler(response -> {
              context.assertEquals(response.statusCode(), 201);
              context.assertTrue(response.headers().get("content-type").contains("application/json"));
              response.bodyHandler(body -> {
                final KryService receivedService = Json.decodeValue(body.toString(), KryService.class);
                context.assertEquals(receivedService.getName(), testName);
                async.complete();
              });
            })
            .write(serviceAsJson)
            .end();
  }
  
   @Test
  public void testDeleteEndpointStatus204(TestContext context) {
    final Async async = context.async();
    final String existingId = "07a9953d-6604-4968-8bd1-df33a075980a";
    
    vertx.createHttpClient().delete(port, server, servicesEndPoint + "/" + existingId)
            .handler(response -> {
              context.assertEquals(response.statusCode(), 204);
              async.complete();
            })
            .end();
  }
  
    
   @Test
  public void testDeleteEndpointStatus400(TestContext context) {
    final Async async = context.async();
    final String nonExistingId = "apa";
    
    vertx.createHttpClient().delete(port, server, servicesEndPoint + "/" + nonExistingId)
            .handler(response -> {
              context.assertEquals(response.statusCode(), 400);
              async.complete();
            })
            .end();
  }
}
