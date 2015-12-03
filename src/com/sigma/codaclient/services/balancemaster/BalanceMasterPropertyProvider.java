/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.balancemaster;

import com.coda.efinance.schemas.balancemaster.BalanceMaster;
import com.sigma.codaclient.services.common.CodaPropertyProvider;
import com.sigma.codaclient.services.common.CodaGenericPropertyProvider;
import com.sigma.codaclient.services.common.CodaPropertyTypes;

/**
 *
 * @author clance
 */
public class BalanceMasterPropertyProvider extends CodaGenericPropertyProvider<BalanceMaster> implements CodaPropertyProvider<BalanceMaster> {
  protected static BalanceMasterPropertyProvider instance;

  protected BalanceMasterPropertyProvider() throws Exception {
    super("balancemaster",BalanceMaster.class);

    addProperty("company_code", CodaPropertyTypes.STRING);
    addProperty("code", CodaPropertyTypes.STRING);
    addProperty("name", CodaPropertyTypes.STRING);
    addProperty("short_name", CodaPropertyTypes.STRING);
    addProperty("decimal_places", CodaPropertyTypes.INT);
    addProperty("maint_financials", CodaPropertyTypes.BOOL);
    addProperty("maint_commitments", CodaPropertyTypes.BOOL); 
  }
  
  public static BalanceMasterPropertyProvider getInstance() throws Exception {
    if (instance == null)
      instance = new BalanceMasterPropertyProvider();
    return instance;
  }
  
  @Override
  public <T> void setValue(String name, T value, BalanceMaster instance) throws Exception {
    switch(name) {
    case "company_code": // String
      instance.setCmpCode((String)value);
      break;
    case "code": // String
      instance.setCode((String)value);
      break;
    case "name": // String
      instance.setName((String)value);
      break;
    case "short_name": // String
      instance.setShortName(null);
      break;
    case "decimal_places": // Integer
      instance.setDecimalPlaces(((Integer)value).shortValue());
      break;
    case "maint_financials": // Boolean
      instance.setCODAMaintained((Boolean)value);
      break;
    case "maint_commitments": // Boolean
      instance.setBalMaintainedByCommitments((Boolean)value);
      break;
    default:
      throw new Exception ("Unsupported or unknown property: " + name);
    }  
  }

  @Override
  public void clearValue(String name, BalanceMaster instance) throws Exception {
    switch(name) {
    case "company_code": // String
      instance.setCmpCode(null);
      break;
    case "code": // String
      instance.setCode(null);
      break;
    case "name": // String
      instance.setName(null);
      break;
    case "short_name": // String
      instance.setShortName(null);
      break;
    case "decimal_places": // Integer
      instance.setDecimalPlaces((short)0);
      break;
    case "maint_financials": // Boolean
      instance.setCODAMaintained(false);
      break;
    case "maint_commitments": // Boolean
      instance.setBalMaintainedByCommitments(false);
      break;
    default:
      throw new Exception ("Unsupported or unknown property: " + name);
    }  
  }

  @Override
  public <T> T getValue(String name, BalanceMaster instance) throws Exception {
    switch(name) {
    case "company_code": // String
      return (T)instance.getCmpCode();
    case "code":
      return (T)instance.getCode();
    case "name":
      return (T)instance.getName();
    case "short_name":
      return (T)instance.getShortName();
    case "decimal_places": // Integer
      return (T)(new Integer(instance.getDecimalPlaces()));
    case "maint_financials": // Boolean
      return (T)(Boolean)instance.isCODAMaintained();
    case "maint_commitments": // Boolean
      return (T)(Boolean)instance.isBalMaintainedByCommitments();
    default:
      throw new Exception ("Unsupported or unknown property: " + name);
    }  
  }  
  
  @Override
  public void setDefaults(BalanceMaster instance) throws Exception {
    instance.setDecimalPlaces((short)2);
  }    
}
