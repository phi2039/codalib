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
public class CodaLocalKey extends CodaKey {
  
  protected final String companyCode;
  
  private CodaLocalKey() {
    this("","");
  }
  
  public CodaLocalKey(String companyCode, String code) {
    super(code);
    this.companyCode = companyCode;
  }

  // Copy Constructor
  public CodaLocalKey(CodaLocalKey key) {
    this(key.companyCode, key.code);
  }  
  
  public String getCompanyCode() {
    return companyCode;
  }

  @Override
  public String toString() {
    return String.format("%s.%s", companyCode, code);
  }

  @Override
  public String[] toStringArray() {
    if (strings == null) {
      strings = new String[2];
      strings[0] = companyCode;
      strings[1] = code;
    }
    return strings;
  }  
}
