/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author clance
 */
public class CodaOperationResult <T,K extends CodaKey> {
  private boolean failed;
  private CodaOperationError error = CodaOperationError.NO_ERROR;
  private final Map<String,CodaObjectResult<T,K>> objectResults = new HashMap<>();
  private final CodaOperationType operationType;
  
  public CodaOperationResult(CodaOperationType operationType, boolean failed) {
    this.operationType = operationType;
    this.failed = failed;
  }
  
  public boolean isFailed() {
    return failed;
  }

  public CodaOperationError getError() {
    return error;
  }

  public void setError(CodaOperationError error) {
    this.error = error;
  }
  
  public void addObjectResults(List<CodaObjectResult<T,K>> results) {
    for (CodaObjectResult<T,K> result : results)
      objectResults.put(result.getKey().toString(), result);
  }
  
  public void addObjectResult(CodaObjectResult<T,K> result) throws Exception {
    if (result.getKey() == null)
      throw new Exception("Invalid result. Missing Key.");
    objectResults.put(result.getKey().toString(), result);
  }  
  
  public Collection<CodaObjectResult<T,K>> getObjectResults() {
    return objectResults.values();
  }
  
  public CodaObjectResult<T,K> getObjectResult(String key) {
    if (objectResults.containsKey(key))
      return objectResults.get(key);
    return null;
  }

  public void removeObjectResult(String key) {
    if (objectResults.containsKey(key))
      objectResults.remove(key);
  }
  
  
  public CodaOperationType getOperationType() {
    return operationType;
  }

  public void merge(CodaOperationResult<T,K> instance) throws Exception {
    if (instance != this) { // Can't merge with self
      if (instance.operationType != operationType)
        throw new Exception("Incompatible operations");

      failed = instance.failed && failed; // One failure equals total failure
      if (instance.error != CodaOperationError.NO_ERROR && error != CodaOperationError.NO_ERROR)
        error = instance.error;
      
      objectResults.putAll(instance.objectResults);
    }
  }
}
