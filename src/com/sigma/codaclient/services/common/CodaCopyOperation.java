/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 *
 * @author clance
 */
public class CodaCopyOperation <T,K extends CodaKey> extends CodaOperation {
  protected final K sourceKey;
  protected final Map<String,Object> newValues = new HashMap<>();
  protected final List<String> clearedProperties = new ArrayList<>();
  
  public CodaCopyOperation(K sourceKey) {
    this.sourceKey = sourceKey;
  }

  public CodaCopyOperation(K sourceKey, Map<String,Object> newValues) {
    this.sourceKey = sourceKey;
    this.newValues.putAll(newValues);
  }

  public K getSourceKey() {
    return sourceKey;
  }

  public Map<String, Object> getNewValues() {
    return newValues;
  }
  
  public List<String> getClearedProperties() {
    return clearedProperties;
  }
  
  public void addClearedProperty(String name) {
    clearedProperties.add(name);
  }
}
