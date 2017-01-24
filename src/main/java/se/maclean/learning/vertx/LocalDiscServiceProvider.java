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
import org.apache.commons.io.FileUtils;

/**
 *
 * @author MacL3an
 */
public class LocalDiscServiceProvider extends LocalDiscServiceProviderWithoutPersistance {

  public LocalDiscServiceProvider(String fileName) {
    super(fileName);
  }

  @Override
  public void remove(String id) {
    super.remove(id);
    persistToDisk();
  }

  @Override
  public void add(KryService newService) {
    super.add(newService);
    persistToDisk();
  }

  private void persistToDisk() {
    KryServicesForJson kryServices = new KryServicesForJson(
            new ArrayList(services.values()));
    String serializedServices = Json.encodePrettily(kryServices);
    
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getPath());
    try {
      FileUtils.writeStringToFile(file, serializedServices);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

}
