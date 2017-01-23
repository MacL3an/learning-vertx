/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author MacL3an
 */
public class LocalDiskDataHandler implements DataHandler {
  private String fileName;
  
  public LocalDiskDataHandler(String fileName){
    this.fileName = fileName;
  }
  
  @Override
  public String getData() {
    ClassLoader classLoader = getClass().getClassLoader();
    
    String result = "";
    try {
      result = IOUtils.toString(classLoader.getResourceAsStream(fileName));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    System.out.println(result);
    return result;
  }
}
