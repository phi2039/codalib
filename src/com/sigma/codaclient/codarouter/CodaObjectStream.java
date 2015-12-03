/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.codarouter;

import java.util.Iterator;

/**
 *
 * @author clance
 */
public class CodaObjectStream<T> implements Iterable<T> {
  protected Iterator<T> iterator;
  
  public CodaObjectStream(Iterator<T> iterator) {
    if (iterator == null)
      throw new NullPointerException();
    this.iterator = iterator;
  }

  @Override
  public Iterator<T> iterator() {
    return iterator;
  }
}
