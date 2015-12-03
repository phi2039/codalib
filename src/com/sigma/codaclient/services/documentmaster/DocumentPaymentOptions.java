/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.documentmaster;

import com.coda.efinance.schemas.documentmaster.Pay;
import com.coda.efinance.schemas.documentmaster.PayData;
import com.coda.efinance.schemas.documentmaster.PayElement;

/**
 *
 * @author clance
 */
public class DocumentPaymentOptions {
  private boolean enabled;
  private DocumentPaymentOption mediaCode;
  private DocumentPaymentOption companyBank;
  private DocumentPaymentOption elementBank;
  private DocumentPaymentOption elementAddress;

  private final DocumentUserReference[] userRefs = new DocumentUserReference[3];

  public DocumentPaymentOptions() {
    enabled = false;
    mediaCode = new DocumentPaymentOption();
    companyBank = new DocumentPaymentOption();
    elementBank = new DocumentPaymentOption();
    elementAddress = new DocumentPaymentOption();
    
    for (int r = 0; r < userRefs.length; r++) {
      userRefs[r] = new DocumentUserReference(DocumentUserReference.DocumentUserReferenceType.USER_REF_TEXT);
    }
  }
  
  public DocumentPaymentOptions(Pay instance) {
    this();
    
    enabled = instance.isEnable();
    mediaCode.setRequired(instance.getMedia().isRequired());
    mediaCode.setProtected(!instance.getMedia().isModifiable());
    companyBank.setRequired(instance.getCompanyBank().isRequired());
    companyBank.setProtected(!instance.getCompanyBank().isModifiable());
    elementAddress.setRequired(instance.getElement().isAddressRequired());
    elementBank.setRequired(instance.getElement().isBankCodeRequired());
  }
  
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  public boolean isEnabled() {
    return enabled;
  }

  public DocumentPaymentOption getMediaCode() {
    return mediaCode;
  }

  public void setMediaCode(DocumentPaymentOption mediaCode) {
    this.mediaCode = mediaCode;
  }

  public DocumentPaymentOption getCompanyBank() {
    return companyBank;
  }

  public void setCompanyBank(DocumentPaymentOption companyBank) {
    this.companyBank = companyBank;
  }

  public DocumentPaymentOption getElementBank() {
    return elementBank;
  }

  public void setElementBank(DocumentPaymentOption elementBank) {
    this.elementBank = elementBank;
  }

  public DocumentPaymentOption getElementAddress() {
    return elementAddress;
  }

  public void setElementAddress(DocumentPaymentOption elementAddress) {
    this.elementAddress = elementAddress;
  }
  
  public DocumentUserReference getUserReference(int index) {
    return userRefs[index];
  }
  
  public void set(Pay instance) {
    instance.setEnable(enabled);
    PayData media = new PayData();
    media.setRequired(mediaCode.isRequired());
    media.setModifiable(!mediaCode.isProtected());
    instance.setMedia(media);
    PayData cmpBank = new PayData();
    cmpBank.setRequired(companyBank.isRequired());
    cmpBank.setModifiable(!companyBank.isProtected());
    instance.setCompanyBank(cmpBank);
    PayElement payElm = new PayElement();
    payElm.setAddressRequired(elementAddress.isRequired());
    payElm.setBankCodeRequired(elementBank.isRequired());
    instance.setElement(payElm);
  }
  
}
