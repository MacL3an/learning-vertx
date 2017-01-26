/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx;

import io.vertx.core.Vertx;

/**
 *
 * @author MacL3an
 */
public class ServiceStatusApp {
    private static final String servicesFileName = "services.json";   
  
      public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        DataSource dataSource = new LocalDiscDataSource(servicesFileName);
        ServiceProvider serviceProvider = new ServiceProvider(dataSource);
        
        vertx.deployVerticle(new ServiceStatusRestProviderVerticle(serviceProvider));
        vertx.deployVerticle(new ServiceStatusCheckerVerticle(serviceProvider));
    }
}
