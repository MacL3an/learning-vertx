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

    router.route("/services").handler(routingContext -> {
      getAllServices(routingContext);
    });

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
}
