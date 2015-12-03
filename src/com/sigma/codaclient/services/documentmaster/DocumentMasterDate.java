/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.documentmaster;

/**
 *
 * @author clance
 */
public class DocumentMasterDate {
  private boolean modify;
  private boolean docwide;
  private String rule;
  
  public DocumentMasterDate() {
    modify = false;
    docwide = false;
    rule = "";
  }
  
  public DocumentMasterDate(boolean modify, boolean docwide, String rule) {
    this.modify = modify;
    this.docwide = docwide;
    this.rule = rule;
  } 

  public boolean isModifiable() {
    return modify;
  }

  public boolean isDocwide() {
    return docwide;
  }

  public void setModifiable(boolean modify) {
    this.modify = modify;
  }

  public void setDocwide(boolean docwide) {
    this.docwide = docwide;
  }

  public String getRule() {
    return rule;
  }

  public void setRule(String rule) {
    this.rule = rule;
  }
}
