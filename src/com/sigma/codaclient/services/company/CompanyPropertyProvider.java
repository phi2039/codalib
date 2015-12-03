/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.company;

import com.coda.efinance.schemas.common.TypeCtAddressSource;
import com.coda.efinance.schemas.common.TypeCtBudgetExceeded;
import com.coda.efinance.schemas.common.TypeCtCmpCurRateControl;
import com.coda.efinance.schemas.common.TypeCtDocNumGen;
import com.coda.efinance.schemas.common.TypeCtEntityType;
import com.coda.efinance.schemas.common.TypeCtNoBudget;
import com.coda.efinance.schemas.common.TypeCtObligationPeriod;
import com.coda.efinance.schemas.common.TypeCtProcAccValidation;
import com.coda.efinance.schemas.common.TypeCtRateRules;
import com.coda.efinance.schemas.common.TypeCtReuseDefault;
import com.coda.efinance.schemas.common.TypeCtTTRPriority;
import com.coda.efinance.schemas.company.CommitmentAccounting;
import com.coda.efinance.schemas.company.Company;
import com.coda.efinance.schemas.company.DeliveryDates;
import com.coda.efinance.schemas.company.EntListDataElement;
import com.coda.efinance.schemas.company.Procurement;
import com.sigma.codaclient.services.common.CodaPropertyProvider;
import com.sigma.codaclient.services.common.CodaGenericPropertyProvider;
import com.sigma.codaclient.services.common.CodaPropertyTypes;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author clance
 */
public class CompanyPropertyProvider extends CodaGenericPropertyProvider<Company> implements CodaPropertyProvider<Company> {
  protected static CompanyPropertyProvider instance;
 
  protected CompanyPropertyProvider() throws Exception {
    super("company",Company.class);
    wireAll();

    // Custom handlers
    addProperty("address", CompanyPropertyTypes.ADDRESS);
    addProperty("balances", CodaPropertyTypes.LIST);
    addProperty("address_categories", CodaPropertyTypes.STRING);
    addProperty("element_statuses", CodaPropertyTypes.STRING);
    addProperty("reason_codes", CodaPropertyTypes.STRING);
    addProperty("diary_action_codes", CodaPropertyTypes.STRING);
    addProperty("resolution_codes", CodaPropertyTypes.STRING);
    addProperty("reporting_code", CodaPropertyTypes.STRING);
    addProperty("reporting_code_1", CodaPropertyTypes.STRING);
    addProperty("reporting_code_2", CodaPropertyTypes.STRING);
    addProperty("reporting_code_3", CodaPropertyTypes.STRING);  
  }
  
  public static CompanyPropertyProvider getInstance() throws Exception {
    if (instance == null)
      instance = new CompanyPropertyProvider();
    return instance;
  }
  
  @Override
  public <T> void setValue(String name, T value, Company instance) throws Exception {
    switch(name) {
    case "address": // CompanyAddress
      CompanyAddress addr = (CompanyAddress)value;
      addr.set(instance);
      break;
    case "balances": // List<CompanyBalance>
      List<CompanyBalance> balances = (List<CompanyBalance>) value;
      for (CompanyBalance balance : balances)
        balance.set(instance);
      break;
    case "address_categories": // String
      setEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.ADDRESS_CAT, (String)value);
      break;
    case "element_statuses": // String
      setEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.ELM_STATUS_CAT, (String)value);
      break;
    case "reason_codes": // String
      setEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REASON_CODE_CAT, (String)value);
      break;
    case "diary_action_codes": // String
      setEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.DIARY_ACTION_CODE, (String)value);
      break;
    case "resolution_codes": // String
      setEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.RESOLUTION_CODE, (String)value);
      break;
    case "reporting_code": // String
      setEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REPORTING_CODE, (String)value);
      break;
    case "reporting_code_1": // String
      setEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REPORTING_CODE_1, (String)value);
      break;
    case "reporting_code_2": // String
      setEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REPORTING_CODE_2, (String)value);
      break;
    case "reporting_code_3": // String    
      setEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REPORTING_CODE_3, (String)value);
      break;
    default:
      super.setValue(name, value, instance);
    }  
  }

  @Override
  public void clearValue(String name, Company instance) throws Exception {
    switch(name) {
    case "address": // CompanyAddress
      CompanyAddress.clear(instance);
      break;
    case "balances": // List<CompanyBalance>
      CompanyBalance.clear(instance);
      break;
    case "address_categories": // String
      clearEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.ADDRESS_CAT);
      break;
    case "element_statuses": // String
      clearEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.ELM_STATUS_CAT);
      break;
    case "reason_codes": // String
      clearEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REASON_CODE_CAT);
      break;
    case "diary_action_codes": // String
      clearEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.DIARY_ACTION_CODE);
      break;
    case "resolution_codes": // String
      clearEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.RESOLUTION_CODE);
      break;
    case "reporting_code": // String
      clearEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REPORTING_CODE);
      break;
    case "reporting_code_1": // String
      clearEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REPORTING_CODE_1);
      break;
    case "reporting_code_2": // String
      clearEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REPORTING_CODE_2);
      break;
    case "reporting_code_3": // String    
      clearEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REPORTING_CODE_3);
      break; 
    default:
      super.clearValue(name, instance);
    }  
  }

  @Override
  public <T> T getValue(String name, Company instance) throws Exception {
    switch(name) {
    case "address": // CompanyAddress
      CompanyAddress addr = new CompanyAddress(instance);
      return (T)addr;
    case "balances": // List<CompanyBalance>
      List<CompanyBalance> balances = new ArrayList<>();
      for (CompanyBalanceType type : CompanyBalanceType.values())
        balances.add(new CompanyBalance(type,instance));
      return (T)balances;
    case "address_categories": // String
      if (instance.getEntityList() == null)
        return (T)"";
      return (T)getEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.ADDRESS_CAT);
    case "element_statuses": // String
      if (instance.getEntityList() == null)
        return (T)"";
      return (T)getEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.ELM_STATUS_CAT);
    case "reason_codes": // String
      if (instance.getEntityList() == null)
        return (T)"";
      return (T)getEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REASON_CODE_CAT);
    case "diary_action_codes": // String
      if (instance.getEntityList() == null)
        return (T)"";
      return (T)getEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.DIARY_ACTION_CODE);
    case "resolution_codes": // String
      if (instance.getEntityList() == null)
        return (T)"";
      return (T)getEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.RESOLUTION_CODE);
    case "reporting_code": // String
      if (instance.getEntityList() == null)
        return (T)"";
      return (T)getEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REPORTING_CODE);
    case "reporting_code_1": // String
      if (instance.getEntityList() == null)
        return (T)"";
      return (T)getEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REPORTING_CODE_1);
    case "reporting_code_2": // String
      if (instance.getEntityList() == null)
        return (T)"";
      return (T)getEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REPORTING_CODE_2);
    case "reporting_code_3": // String    
      if (instance.getEntityList() == null)
        return (T)"";
      return (T)getEntityItem(instance.getEntityList().getItem(), TypeCtEntityType.REPORTING_CODE_3);
    default:
      return super.getValue(name, instance);
    }  
  }
  
  @Override
  public void setDefaults(Company instance) throws Exception {
    instance.setRateRule(TypeCtRateRules.RATE_DOC);
    instance.setDateOrder((short)1); // DMY
    instance.setDateDisplay((short)1); // dd/mm/yy
    instance.setPerSec((short)3);
    instance.setPerNo((short)12);
    instance.setCurRateControl(TypeCtCmpCurRateControl.LINEWIDE);
    instance.setDeferredVATPriority(TypeCtTTRPriority.TTR_DEFERRED_PRIORITY);
    instance.setDeferredVATEnabled(false);
    instance.setReverseQtyCredit(true);
    instance.setReverseQtyDebit(false);
    instance.setSubsChar("&");
    instance.setRateMethod(true);
    instance.setDocNumberGeneration(TypeCtDocNumGen.ON_POSTING);
    instance.setAllowReuseMissingNumbers(false);
    instance.setReuseDefaultInInput(TypeCtReuseDefault.NO_REUSE);
    instance.setDefaultDelivery(true);
    instance.setDefaultSupplierLevel((short)1);
    instance.setDefaultCustomerLevel((short)1);
    
    instance.setCommitmentAccounting(new CommitmentAccounting());
    instance.getCommitmentAccounting().setUseCommitmentAccounting(false);
    instance.getCommitmentAccounting().setObligationPeriod(TypeCtObligationPeriod.RETAIN_PERIOD_ORDER);
    instance.getCommitmentAccounting().setActionOnBudgetExceeded(TypeCtBudgetExceeded.CHECK_TOLERANCE);
    instance.getCommitmentAccounting().setMeaningOfNoBudget(TypeCtNoBudget.ZERO_BUDGET);
    instance.getCommitmentAccounting().setUseActualsIntersects(true);
    instance.getCommitmentAccounting().setBudgetCheckObligation(true);
    instance.getCommitmentAccounting().setBudgetCheckEncumbrance(true);

    instance.setProcurement(new Procurement());
    instance.getProcurement().setAccountValidation(TypeCtProcAccValidation.ON_DOCUMENT_SUBMISSION);
    DeliveryDates dd = new DeliveryDates();
    dd.setMonday(true);
    dd.setTuesday(true);
    dd.setWednesday(true);
    dd.setThursday(true);
    dd.setFriday(true);
    dd.setSaturday(false);
    dd.setSunday(false);
    instance.getProcurement().setDeliveryDateCalculation(dd);
    instance.getProcurement().setDepartmentFilterLevel((short)1);
    instance.getProcurement().setUnitPriceDecimalPlaces("def_dp");
    instance.getProcurement().setQuantityDecimalPlaces("def_dp");
    instance.getProcurement().setSourceDefaultDeliveryAddress(TypeCtAddressSource.COMPANY);
  }

  private void setEntityItem(List<EntListDataElement> items, TypeCtEntityType type, String value) {
    // See if a member already exists in the list
    for (EntListDataElement e : items) {
      if (e.getType() == type) {
        e.setEntity(value);
        return;
      }
    }
    // Add a new item if one does not exist
    EntListDataElement elem = new EntListDataElement();
    elem.setType(type);
    elem.setEntity(value);
    items.add(elem);    
  }
  
    private void clearEntityItem(List<EntListDataElement> items, TypeCtEntityType type) {
    // See if a member exists in the list
    for (EntListDataElement e : items) {
      if (e.getType() == type) { // Clear member
        e.setEntity(null);
        break;
      }
    }
  }
  
  private String getEntityItem(List<EntListDataElement> items, TypeCtEntityType type) {
    // Look for the member in the list
    for (EntListDataElement e : items) {
      if (e.getType() == type) {
        return e.getEntity();
      }
    }
    return "";
  }
}
