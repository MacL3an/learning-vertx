/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx;

import io.vertx.core.json.Json;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author MacL3an
 */
public class ServiceProvider {
  HashMap<String, KryService> services;
  private DataSource dataSource;

  public ServiceProvider(DataSource dataSource) {
    this.dataSource = dataSource;
    loadServices();
  }

  private void loadServices() {
    String json = dataSource.readFromSource();
    KryServicesForJson kryServices = Json.decodeValue(json, KryServicesForJson.class);

    services = new HashMap<>();
    for (KryService service : kryServices.getServices()) {
      services.put(service.getId(), service);
    }
  }

  public Map<String, KryService> get() {
    return services;
  }

  public void remove(String id) {
    if (!exists(id)) {
      return;
    }

    services.remove(id);
    dataSource.persist(getServicesAsJson());
  }

  public void add(KryService newService) {
    services.put(newService.getId(), newService); //TODO: What if already exists?
    dataSource.persist(getServicesAsJson());    
  }

  public boolean exists(String id) {
    return services.containsKey(id);
  }

  public void reload() {
    loadServices();
  }

  public void setStatus(String id, String status, String lastCheck) {
    KryService serviceToUpdate = services.get(id);
    if (serviceToUpdate == null) {
      throw new UnsupportedOperationException("Cannot find status object");
    }
    serviceToUpdate.setStatus(status);
    serviceToUpdate.setLastCheck(lastCheck);
    dataSource.persist(getServicesAsJson());
  }

  private String getServicesAsJson() {
    KryServicesForJson kryServices = new KryServicesForJson(
            new ArrayList(services.values()));
    String serializedServices = Json.encodePrettily(kryServices);
    return serializedServices;
  }
}
