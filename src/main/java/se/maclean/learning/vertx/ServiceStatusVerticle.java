/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.http.HttpServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MacL3an
 */
public class ServiceStatusVerticle extends AbstractVerticle {

  private HttpServer httpServer = null;
  private DataHandler dataHandler = null;
  private Map<String, KryService> services = null;

  public ServiceStatusVerticle(DataHandler dataHandler) {
    this.dataHandler = dataHandler;
    String receivedData = dataHandler.getData();
    ServiceContainer serviceContainer = Json.decodeValue(receivedData, ServiceContainer.class);
    services = new HashMap<>();
    for (KryService service : serviceContainer.getServices()) {
      services.put(service.getId(), service);
    }
  }

  @Override
  public void start(Future<Void> fut) {
    Router router = Router.router(vertx);
    router.route("/services*").handler(BodyHandler.create());
    router.get("/services").handler(this::getAllServices);
    router.post("/services").handler(this::addService);
    router.delete("/services/:id").handler(this::removeService);

    httpServer = vertx.createHttpServer();
    httpServer.requestHandler(request -> {
      router.accept(request);
    });
    httpServer.listen(8080, result -> {
      if (result.succeeded()) {
        fut.complete();
      } else {
        fut.fail(result.cause());
      }
    });
  }

  private void getAllServices(RoutingContext routingContext) {
    ServiceContainer serviceContainer = new ServiceContainer(new ArrayList(services.values()));
    String serializedServices = Json.encodePrettily(serviceContainer);
    routingContext.response().end(serializedServices);
  }

  private void addService(RoutingContext routingContext) {
    KryService newService = Json.decodeValue(routingContext.getBodyAsString(),
            KryService.class);
    services.put(newService.getId(), newService);
    routingContext.response()
      .setStatusCode(201)
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(Json.encodePrettily(newService));
  }
  
  private void removeService(RoutingContext routingContext) {
    String idToDelete = routingContext.request().getParam("id");
    if (idToDelete == null || !services.containsKey(idToDelete)) {
      routingContext.response().setStatusCode(400).end();
    }
    else {
      services.remove(idToDelete);
      routingContext.response().setStatusCode(204).end();
    }
  }
}
