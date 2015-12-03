/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.documentmaster;

import com.coda.efinance.schemas.documentmaster.*;

/**
 *
 * @author clance
 */
public class DocumentMasterPropertyValue {
  DocumentMasterProperty property;
  Object value;

  public DocumentMasterPropertyValue(DocumentMasterProperty property, Object value) {
    this.property = property;
    this.value = value;
  }

  public DocumentMasterPropertyValue(DocumentMasterProperty property) {
    this.property = property;
    this.value = null;
  }

  public DocumentMasterProperty getProperty() {
    return property;
  }

  public void setProperty(DocumentMasterProperty property) {
    this.property = property;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }  
  
  public void setDocumentMasterProperty(DocumentMaster master) throws Exception{
    switch (property) {

    default:
      throw new Exception("Unknown or unsupported property");
    }
  }

  public static Object getDocumentMasterProperty(DocumentMaster master, DocumentMasterProperty property) throws Exception {
    switch (property) {    
    default:
      throw new Exception("Unknown or unsupported property");
    }
  }  
}
