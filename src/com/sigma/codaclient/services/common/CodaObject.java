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
public class CodaObject <K> {
  protected K key;
  protected String name;
  protected String shortName;
  
  public CodaObject(K key) {
    this(key,"","");
  }
  
  public CodaObject(K key, String name, String shortName) {
    this.key = key;
    this.name = name;
    this.shortName = shortName;
  }
}
