/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author clance
 */
public class CodaUpdateOperation <K extends CodaKey> extends CodaOperation{
  private K key;
  private final Map<String,Object> values = new HashMap<>();

  public CodaUpdateOperation() {
    
  }
  
  public CodaUpdateOperation(K key, Map<String,Object> values) {
    this.key = key;
    this.values.putAll(values);
  }
  
  public Map<String,Object> getProperties() {
    return values;
  }
  
  public void addProperty(String name, Object value) {
    values.put(name, value);
  }
  
  public K getKey() {
    return key;
  }  
}
