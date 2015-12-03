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
public class CodaOperationError {
  private final String text;
  private final String path;
  private final String hint;
  
  public static CodaOperationError NO_ERROR = new CodaOperationError("No error","","");
  
  public CodaOperationError(String text, String path, String hint) {
    this.text = text;
    this.path = path;
    this.hint = hint;
  }

  public CodaOperationError() {
    this.text = "";
    this.path = "";
    this.hint = "";
  }

  public String getText() {
    return text;
  }

  public String getPath() {
    return path;
  }

  public String getHint() {
    return hint;
  }  
}
