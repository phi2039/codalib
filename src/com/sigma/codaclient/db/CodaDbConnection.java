/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author clance
 */
public class CodaDbConnection {
  private String host;
  private Integer port;
  private String serviceId;
  private String username;
  private String password;
  
  private Connection conn;
  
  public CodaDbConnection() {
    conn = null;
  }
  
  public CodaDbConnection(String host, Integer port, String serviceId, String username, String password) {
    this();
    
    this.host = host;
    this.port = port;
    this.serviceId = serviceId;
    this.username = username;
    this.password = password;
  }
  
  public Connection getConnection() throws Exception {
    if (conn == null)
      throw new Exception("No connection to database");
    return conn;
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }

  public String getServiceId() {
    return serviceId;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
  
  public void connect() throws Exception {
    conn = DriverManager.getConnection("jdbc:oracle:thin:@//" + host + ":" + port + "/" + serviceId, username, password);    
  }

  public void connect(String host, Integer port, String serviceId, String username, String password) throws Exception {
    this.host = host;
    this.port = port;
    this.serviceId = serviceId;
    this.username = username;
    this.password = password;
    
    connect();
  }
}
