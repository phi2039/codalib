/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.element;

import com.coda.efinance.schemas.common.ExtensionRef;
import com.coda.efinance.schemas.common.TypeCtARCOptions;
import com.coda.efinance.schemas.common.TypeCtStatPayElm;
import com.coda.efinance.schemas.common.TypeCtStatRec;
import com.coda.efinance.schemas.common.TypeCtYesNoSome;
import com.coda.efinance.schemas.elementmaster.Element;
import com.coda.efinance.schemas.elementmaster.GroupData;
import com.coda.efinance.schemas.elementmaster.QuantityData;
import com.coda.efinance.schemas.elementmaster.QuantityElement;

import com.sigma.codaclient.services.common.CodaPropertyProvider;
import com.sigma.codaclient.services.common.CodaGenericPropertyProvider;
import com.sigma.codaclient.services.common.CodaPropertyTypes;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author clance
 */
public class ElementPropertyProvider extends CodaGenericPropertyProvider<Element> implements CodaPropertyProvider<Element> {
  protected static ElementPropertyProvider instance;
  
  protected ElementPropertyProvider() throws Exception {
    super("element",Element.class);
 
    // Default property handling
    super.wireAll();

    // Custom property handling
    addProperty("group_list", CodaPropertyTypes.LIST);
  }
  
  public static ElementPropertyProvider getInstance() throws Exception {
    if (instance == null)
      instance = new ElementPropertyProvider();
    return instance;
  }
  
  @Override
  public <T> T getValue(String name, Element instance) throws Exception {
    switch(name) {
    case "group_list":
      GroupData groupData = instance.getGroups();
      if (groupData == null)
        return (T)new ArrayList<String>();
      return (T)groupData.getGroupCode();
    default:
      return super.getValue(name, instance);
    }
  }

  @Override
  public <T> void setValue(String name, T value, Element instance) throws Exception {
    switch(name) {
    case "group_list":
      GroupData groupData = new GroupData();
      groupData.getGroupCode().addAll((List<String>)value);
      instance.setGroups(groupData);
      break;
    default:
      super.setValue(name, value, instance);
    }
  }

  @Override
  public void clearValue(String name, Element instance) throws Exception {
    switch(name) {
    case "group_list":
      instance.setGroups(null);
      break;
    default:
      super.clearValue(name, instance);
    }
  }
  
  @Override
  public void setDefaults(Element instance) throws Exception {

    instance.setTimeStamp((short)-1);
    instance.setTaxesTimeStamp((short)-1);
    instance.setSubAnalyse(TypeCtYesNoSome.NO);
    instance.setTaxCode(null);
    instance.setCompulsoryDescr(false);
    instance.setCustomerSupplier(false);
    instance.setIsCustomer(false);
    instance.setIsSupplier(false);
    instance.setKeepTurnover(false);
    instance.setMatchable(false);
    instance.setPayStatus(TypeCtStatPayElm.NOTMATCHABLE);
    instance.setRecStatus(TypeCtStatRec.NULL);
    instance.setAccountSummary(null);
    instance.setMemoStatus(null);
    instance.setSubsLevel((short)0);
    instance.setUserStatus(null);
    instance.setTerms("01LL");
    instance.setTaxRepESL(false);
    instance.setTaxRepIntra(false);
    instance.setVAT(null);
    instance.setArcRecon(TypeCtARCOptions.NO);
    instance.setArcPaid(TypeCtARCOptions.NO);
    instance.setPromptForAsset(false);
    ExtensionRef ext = new ExtensionRef();
    ext.setCode(null);
    instance.setExtension(ext);
    instance.setReportingCode1(null);
    instance.setReportingCode2(null);
    instance.setReportingCode3(null);
    instance.setAutoReceipt(false);
    instance.setProcStatus(false);
    QuantityData quant = new QuantityData();
    for (int q = 0; q < 4; q++) {
      QuantityElement quantElem = new QuantityElement();
      quantElem.setUsed(false);
      quantElem.setTitle(null);
      quantElem.setMand(false);
      quantElem.setBalCode(null);
      quantElem.setDecimals(null);
      quant.getQuantity().add(quantElem);
    }
    instance.setQuantities(quant);
    instance.setEUVATCode(null);
    instance.setAddresses(null);
    instance.setBanks(null);
    instance.setComments(null);
    instance.setGroups(null);
    instance.setRightRules(null);
    instance.setMnemonics(null);
    instance.setPunchoutAdvancedParams(null);
    instance.setOutputDevices(null);    
  }  
  
  @Override
  public void clearInvalidProperties(Element instance) {

    // Clear unused (prohibited or read-only) properties
    if (!instance.isCustomerSupplier()) {
      instance.setTen99(null);
      instance.setTen99Code(null);
      instance.setFederalTax(null);
      instance.setSocialSecurity(null);
      instance.setSecondTIN(null);

      instance.setTaxAdjustment(null);
      instance.setDiscountEnable(null);

      instance.setTaxMethod(null);
      instance.setEnablePay(null);
      instance.setSummary(null);
    }

    if (!instance.isMatchable()) {
      instance.setSplit(null);
      instance.setSettlement(null);
      instance.setForceDisperse(null);
      instance.setPaperMedia(null);
      instance.setElecMedia(null);
      instance.setDefaultMedia(null);
      instance.setCreditManager(null);
      instance.setCreditLimit(null);
      instance.setCreditRating(null);
      instance.setCreditRatingDate(null);
      instance.setCreditAgency(null);
      instance.setCreditReference(null);
      instance.setSIC(null);
      instance.setIndirectCode(null);
      instance.setPaymentIndex(null);
      instance.setForceCreditLimit(null);
    }    
  }
}
