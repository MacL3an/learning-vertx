/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx;

import java.util.List;

/**
 *
 * @author MacL3an
 */
public class ServiceContainer {
  private List<KryService> services;

  public ServiceContainer() {
  }
  
  public ServiceContainer(List<KryService> services) {
    this.services = services;
  }
  
  public List<KryService> getServices() {
    return services;
  }
}
