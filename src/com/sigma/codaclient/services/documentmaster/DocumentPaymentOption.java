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
public class DocumentPaymentOption {
  
  private boolean required;
  private boolean protect;
  private String defaultValue;
  
  public DocumentPaymentOption() {
    required = false;
    protect = false;
    defaultValue = null;
  }
  
  public DocumentPaymentOption(boolean required, boolean protect, String defaultValue) {
    this.required = required;
    this.protect = protect;
    this.defaultValue = defaultValue;
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public boolean isProtected() {
    return protect;
  }

  public void setProtected(boolean protect) {
    this.protect = protect;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }
}
