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
public class CodaGlobalKey extends CodaKey {
  
  private CodaGlobalKey() {
    this("");
  }
  
  public CodaGlobalKey(String code) {
    super(code);
  }
  
  // Copy Constructor
  public CodaGlobalKey(CodaGlobalKey key) {
    this(key.code);
  }

  @Override
  public String toString() {
    return code;
  }
  
  @Override
  public String[] toStringArray() {
    if (strings == null) {
      strings = new String[1];
      strings[0] = code;   
    }
    return strings;
  }
}
