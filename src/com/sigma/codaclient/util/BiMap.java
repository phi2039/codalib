/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author clance
 */
public class BiMap<K extends Object, V extends Object> {
  private final Map<K,V> forward = new HashMap<>();
  private final Map<V,K> backward = new HashMap<>();

  public void add(K key, V value) {
    forward.put(key, value);
    backward.put(value, key);
  }

  public V getForward(K key) {
    return forward.get(key);
  }

  public K getBackward(V key) {
    return backward.get(key);
  }
  
  public Map<K,V> getForward() {
    return forward;
  }
  
  public Map<V,K> getBackward() {
    return backward;    
  } 
}
