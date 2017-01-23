/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx;

import io.vertx.core.http.HttpServer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;

/**
 *
 * @author MacL3an
 */
public class ServiceStatusVerticle extends AbstractVerticle {
    private HttpServer httpServer = null;
  
  @Override
  public void start(Future<Void> fut) {
    httpServer = vertx.createHttpServer();
    
    httpServer.requestHandler(request -> {
              getAndPostServices(request);
            });
    
    httpServer.listen(8080, result -> {
              if (result.succeeded()) {
                fut.complete();
              } else {
                fut.fail(result.cause());
              }
            });
  }
  
  private void getAndPostServices(HttpServerRequest r) {
    //TODO: Run this synch or asynch?
    // Buffer servicesBuffer = null;
    //servicesBuffer = vertx.fileSystem().readFileBlocking("services.json"); 
    //r.response().end(servicesBuffer);       
    
    vertx.fileSystem().readFile("services.json", servicesBuffer -> {
      {
        r.response().end(servicesBuffer.result());    
      }
    }); 
  }
}
