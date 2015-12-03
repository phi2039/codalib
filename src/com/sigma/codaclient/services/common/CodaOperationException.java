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
public class CodaOperationException extends Exception {
  private CodaOperationError err;

  public CodaOperationException(CodaOperationError err, String message) {
    super(message);
    this.err = null;
  }

  public CodaOperationException(CodaOperationError err) {
    super();
    this.err = err;
  }
  
  public CodaOperationException(CodaOperationError err, Exception inner, String message) {
    super(message, inner);
    this.err = err;
  }

  public CodaOperationException(CodaOperationError err, Exception inner) {
    super(inner);
    this.err = err;
  }  
}
