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
public class CodaDeleteOperation <K extends CodaKey> extends CodaOperation{
  private final K key;
  
  public CodaDeleteOperation(K key) {
    this.key = key;
  }
  
  public K getKey() {
    return key;
  }
}