/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

/**
 *
 * @author clance
 * @param <T>
 * @param <K>
 */
public class CodaObjectResult <T, K extends CodaKey> extends CodaOperationResult{
  
  private final K key;
  private T object = null;
  
  public CodaObjectResult(CodaOperationType type, K key, boolean failed, T object) {
    super(type, failed);
    this.key = key;
    this.object = object;
  }

  public CodaObjectResult(CodaOperationType type, K key) {
    this(type, key, false, null);
  }
  
  public T getObject() {
    return object;
  }
  
  public K getKey() {
    return key;
  }
}
