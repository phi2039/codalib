/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.common;

/**
 *
 * @author clance
 */
public class CodaUserCredentials extends UserCredentials {
  
  private String defaultCompany;
  
  public CodaUserCredentials(String userName, String password, String defaultCompany) {
    super(userName, password);
    this.defaultCompany = defaultCompany;
  }
  
  public String getDefaultCompany() {
    return defaultCompany;
  }
}
