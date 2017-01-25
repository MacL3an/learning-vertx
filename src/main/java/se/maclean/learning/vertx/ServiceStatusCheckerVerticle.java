/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MacL3an
 */
public class ServiceStatusCheckerVerticle extends AbstractVerticle {

  public static final String MESSAGE_BUS_ADDRESS = "messageBusAddress";
  public static final String SERVICES_CHECKED = "servicesChecked";
  public static final String CHECK_SERVICES = "checkServices";

  private ServiceProvider serviceProvider = null;

  public ServiceStatusCheckerVerticle(ServiceProvider serviceProvider) {
    this.serviceProvider = serviceProvider;
  }

  public void start(Future<Void> startFuture) {

    vertx.setPeriodic(60000, new Handler<Long>() {
      @Override
      public void handle(Long aLong) {
        checkAllServices();
      }
    });
    
      vertx.eventBus().consumer(ServiceStatusCheckerVerticle.MESSAGE_BUS_ADDRESS, message -> {
      if (message.body() == ServiceStatusCheckerVerticle.CHECK_SERVICES) {
        checkAllServices();
      }
    });
    
  }

  private void checkAllServices() {
    for (KryService service : serviceProvider.get().values()) {
      int httpStatusCode = checkServiceStatus(service.getUrl());
      String status = httpStatusCode == 200 ? "OK" : "FAIL";
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      Date now = new Date();
      String lastCheck = sdf.format(now);
      serviceProvider.setStatus(service.getId(), String.valueOf(status), lastCheck);
    }
    vertx.eventBus().publish(MESSAGE_BUS_ADDRESS, SERVICES_CHECKED);
  }

  private int checkServiceStatus(String address) {
    URL url = null;
    HttpURLConnection connection = null;
    int httpStatusCode = 500;
    try {
      url = new URL(address);
      connection = (HttpURLConnection) url.openConnection();
      connection.connect();
      httpStatusCode = connection.getResponseCode();

    } catch (MalformedURLException ex) {
      Logger.getLogger(ServiceStatusCheckerVerticle.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(ServiceStatusCheckerVerticle.class.getName()).log(Level.SEVERE, null, ex);
    }
    return httpStatusCode;
  }
}
