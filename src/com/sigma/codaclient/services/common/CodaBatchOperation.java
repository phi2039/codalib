/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author clance
 */
public class CodaBatchOperation {

  protected final Collection<CodaOperation> ops = new ArrayList<>();
  
  public CodaBatchOperation() {
    
  }
  
  public void addOperation(CodaOperation op) {
    ops.add(op);
  }
  
  public Collection<CodaOperation> getOperations() {
    return ops;
  }
  
  public void addCreateOperation(Map<String,Object> values) {
    ops.add(new CodaCreateOperation(values));
  }

  public void addCreateOperation(Object instance) {
    ops.add(new CodaCreateOperation(instance));
  }
  
  public <K extends CodaKey> void addDeleteOperation(K key) {
    ops.add(new CodaDeleteOperation(key));
  }
  
  public <K extends CodaKey> void addUpdateOperation(K key, Map<String,Object> values) {
    ops.add(new CodaUpdateOperation(key, values));
  }

  public <K extends CodaKey> void addGetOperation(K key) {
    ops.add(new CodaGetOperation(key));
  }
}
