/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx;

import java.util.Map;

/**
 *
 * @author MacL3an
 */
public interface ServiceProvider {
  public Map<String, KryService> get();
  public void remove(String id);
  public void add(KryService kryService);
  public boolean exists(String id);
}
