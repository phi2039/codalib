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
public class DocumentUserReference {
  
  public enum DocumentUserReferenceType {
    USER_REF_TEXT,
    USER_REF_VOCAB
  }
  
  private boolean required;
  private boolean protect;
  private String defaultValue;
  private DocumentUserReferenceType type;
  private Integer vocabId;
  
  public DocumentUserReference(DocumentUserReferenceType type) {
    this.type = type;
    required = false;
    protect = false;
    defaultValue = null;
    vocabId = 10001;
  }
  
  public DocumentUserReference(DocumentUserReferenceType type, boolean required, boolean protect, String defaultValue, Integer vocabId) throws Exception {
    this(type);
    this.required = required;
    this.protect = protect;
    switch (type) {
      case USER_REF_TEXT:
        this.defaultValue = defaultValue;
        this.vocabId = 10001;
        break;
      case USER_REF_VOCAB:
        this.defaultValue = null;
        this.vocabId = vocabId;
        break;
      default:
        throw new Exception("Invalid user refrence type " + type.toString());
    }
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public boolean isProtect() {
    return protect;
  }

  public void setProtect(boolean protect) {
    this.protect = protect;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }  
  
  public Integer getVocabId() {
    return vocabId;
  }

  public void setVocabId(Integer vocabId) {
    this.vocabId = vocabId;
  }    
  
  public DocumentUserReferenceType getType() {
    return type;
  }

  public void setDefaultValue(DocumentUserReferenceType type) {
    this.type = type;
  }    
}
