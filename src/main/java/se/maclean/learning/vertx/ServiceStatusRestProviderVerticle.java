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
import java.time.Instant;
import java.util.ArrayList;

/**
 *
 * @author MacL3an
 */
public class ServiceStatusRestProviderVerticle extends AbstractVerticle {

  private HttpServer httpServer = null;
  private ServiceProvider serviceProvider = null;

  public ServiceStatusRestProviderVerticle(ServiceProvider serviceProvider) {
    this.serviceProvider = serviceProvider;
  }

  @Override
  public void start(Future<Void> fut) {
    Router router = Router.router(vertx);
    router.route("/services*").handler(BodyHandler.create());
    router.get("/services").handler(this::getAllServices);
    router.post("/services").handler(this::addService);
    router.delete("/services/:id").handler(this::removeService);

    vertx.eventBus().consumer(ServiceStatusCheckerVerticle.MESSAGE_BUS_ADDRESS, message -> {
      if (message.body() == ServiceStatusCheckerVerticle.RELOAD_SERVICES) {
        System.out.println(Instant.now() + ": Reloading service status...");
        serviceProvider.reload();
      }
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
    KryServicesForJson kryServices = new KryServicesForJson(
            new ArrayList(serviceProvider.get().values()));
    String serializedServices = Json.encodePrettily(kryServices);
    routingContext.response()
            .setStatusCode(200)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(serializedServices);
  }

  private void addService(RoutingContext routingContext) {
    KryService newService = Json.decodeValue(routingContext.getBodyAsString(),
            KryService.class);
    serviceProvider.add(newService);
    routingContext.response()
            .setStatusCode(201)
            .putHeader("content-type", "application/json; charset=utf-8")
            .end(Json.encodePrettily(newService));
  }

  private void removeService(RoutingContext routingContext) {
    String idToDelete = routingContext.request().getParam("id");
    if (idToDelete == null || !serviceProvider.exists(idToDelete)) {
      routingContext.response().setStatusCode(400).end();
    } else {
      serviceProvider.remove(idToDelete);
      routingContext.response().setStatusCode(204).end();
    }
  }
}
