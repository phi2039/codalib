/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author clance
 */
public class CodaObjectStoreResult <T,K extends CodaKey> {
  private final Map<CodaOperationType, CodaOperationResult<T,K>> operations = new HashMap<>();

  public void setOperation(CodaOperationType type, CodaOperationResult<T,K> op) throws Exception {
    operations.put(type, op);
  }
  
  public CodaOperationResult<T,K> getOperation(CodaOperationType type) throws Exception {
    if (!operations.containsKey(type))
      throw new Exception("Invalid operation");
  
    return operations.get(type);
  }
  
  public void merge(CodaObjectStoreResult<T,K> instance) throws Exception {
    if (instance == this)
      return;

    for (CodaOperationResult<T,K> op : instance.operations.values()) {
      if (operations.containsKey(op.getOperationType()))
        operations.get(op.getOperationType()).merge(op);
      else
        operations.put(op.getOperationType(), op);
    }
  }
}
