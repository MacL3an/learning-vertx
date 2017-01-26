/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author MacL3an
 */
public class LocalDiscDataSource implements DataSource {
  String fileName;

  public LocalDiscDataSource(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public String readFromSource() {    
    ClassLoader classLoader = getClass().getClassLoader();

    String result = "";
    try {
      File file = new File(classLoader.getResource(fileName).getPath());
      result = FileUtils.readFileToString(file, "UTF-8");
    } catch (IOException ex) {
      ex.printStackTrace();
    }
//    System.out.println(result);
    return result;
  } 
  
  @Override
  public void persist(String dataToPersist) {    
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(fileName).getPath());
    try {
      FileUtils.writeStringToFile(file, dataToPersist);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

}
