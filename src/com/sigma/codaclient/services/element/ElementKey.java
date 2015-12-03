/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.element;

import com.sigma.codaclient.services.common.CodaLocalKey;

/**
 *
 * @author clance
 */
public class ElementKey extends CodaLocalKey {
  
  private final short level;
  
  private ElementKey() {
    this("","",0);
  }
  
  public ElementKey(String companyCode, String code, int level) {
    super(companyCode, code);
    this.level = (short)level;
  }
  
  // Copy Constructor
  public ElementKey(ElementKey key) {    
    this(key.companyCode, key.code, key.level);
  }  

  
  public short getLevel() {
    return level;
  }

  @Override
  public String toString() {
    return String.format("%s.EL%d.%s", companyCode, level, code);
  }

  @Override
  public String[] toStringArray() {
    if (strings == null) {
      strings = new String[3];
      strings[0] = companyCode;
      strings[1] = String.valueOf(level);
      strings[2] = code;    
    }
    return strings;
  }
}
