/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.maclean.learning.vertx;

/**
 *
 * @author MacL3an
 */
public class KryService {
  private String id; //TODO: Convert to UUID
  private String name;
  private String url;
  private String status; //TODO: Convert to enum
  private String lastCheck; //TODO: Convert to date

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }
  
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * @return the lastCheck
   */
  public String getLastCheck() {
    return lastCheck;
  }

  /**
   * @param lastCheck the lastCheck to set
   */
  public void setLastCheck(String lastCheck) {
    this.lastCheck = lastCheck;
  }
}
