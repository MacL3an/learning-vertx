/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx;

import io.vertx.core.json.Json;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author MacL3an
 */
public class LocalDiscServiceProviderWithoutPersistance implements ServiceProvider {

  protected String fileName;
  HashMap<String, KryService> services;

  public LocalDiscServiceProviderWithoutPersistance(String fileName) {
    this.fileName = fileName;
    loadServices();
  }

  private void loadServices() {
    String json = readJsonFromDisk();
    KryServicesForJson kryServices = Json.decodeValue(json, KryServicesForJson.class);

    services = new HashMap<>();
    for (KryService service : kryServices.getServices()) {
      services.put(service.getId(), service);
    }
  }

  private String readJsonFromDisk() {
    ClassLoader classLoader = getClass().getClassLoader();

    String result = "";
    try {
      File file = new File(classLoader.getResource(fileName).getPath());
      result = FileUtils.readFileToString(file);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
//    System.out.println(result);
    return result;
  }

  @Override
  public Map<String, KryService> get() {
    loadServices();
    return services;
  }

  @Override
  public void remove(String id) {
    if (!exists(id)) {
      return;
    }

    services.remove(id);
  }

  @Override
  public void add(KryService newService) {
    services.put(newService.getId(), newService); //TODO: What if already exists?
  }

  @Override
  public boolean exists(String id) {
    return services.containsKey(id);
  }
}
