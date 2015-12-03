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
public class CodaCreateOperation extends CodaOperation{
  private final Map<String,Object> properties = new HashMap<>();
  private Object instance;

  public CodaCreateOperation() {
    instance = null;
  }
  
  public CodaCreateOperation(Object instance) {
    this.instance = instance;
  }
  
  public CodaCreateOperation(Map<String,Object> values) {
    this.properties.putAll(values);
  }
  
  public Map<String,Object> getProperties() {
    return properties;
  }
  
  public void addProperty(String name, Object value) {
    properties.put(name, value);
  }
  
  public Object getInstance() {
    return instance;
  }
}
