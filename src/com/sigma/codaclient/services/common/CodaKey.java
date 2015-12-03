/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

/**
 *
 * @author clance
 */
public abstract class CodaKey {
  protected String code;
  protected String[] strings = null;
  
  private CodaKey() {
    this.code = "";
  }
  
  public CodaKey(String code) {
    this.code = code;
  }
  
  public CodaKey(CodaKey key) {
    this(key.code);
  }

  public String getCode() {
    return code;
  }
  
  public String[] toStringArray() {
    if (strings == null) {
      return new String[0];      
    }
    return strings;
  }
}
