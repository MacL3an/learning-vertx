/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx;

import io.vertx.core.http.HttpServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 *
 * @author MacL3an
 */
public class ServiceStatusVerticle extends AbstractVerticle {

  private HttpServer httpServer = null;
  private DataHandler dataHandler;
  
  public ServiceStatusVerticle(DataHandler dataHandler) {
    this.dataHandler = dataHandler;
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
      routingContext.response().end(dataHandler.getData());
  }
}
