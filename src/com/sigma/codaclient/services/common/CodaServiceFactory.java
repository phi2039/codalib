/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

import com.coda.common.schemas.capabilitymaster.CapabilityMaster;
import com.coda.common.schemas.homepagetemplatemaster.HomePageTemplate;
import com.coda.common.schemas.usermaster.User;
import com.coda.common.schemas.usermaster.UserKey;
import com.coda.efinance.schemas.common.GlobalKey;
import com.coda.efinance.schemas.common.Key;
import com.coda.efinance.schemas.company.Company;
import com.coda.efinance.schemas.currency.Currency;
import com.coda.efinance.schemas.documentmaster.DocumentMaster;
import com.coda.efinance.schemas.elementmaster.Element;
import com.coda.efinance.schemas.elementmaster.ElmFullKey;
import com.sigma.codaclient.codarouter.CodaLogicalServerType;
import com.sigma.codaclient.codarouter.CodaRouter;
import com.sigma.codaclient.services.company.CompanyPropertyProvider;
import com.sigma.codaclient.services.element.ElementKey;
import com.sigma.codaclient.services.element.ElementPropertyProvider;
import java.util.HashMap;
import java.util.Map;

import static com.sigma.codaclient.util.Defaults.checkNotNull;
import java.lang.reflect.Constructor;

/**
 *
 * @author clance
 */
public final class CodaServiceFactory {
  
  private static final CodaServiceDefinition[] serviceDefs = {
    new CodaServiceDefinition("Company", "11.3", Company.class, "Company", CodaGlobalKey.class, new String[] {"code"}, GlobalKey.class, new String[] {"code"}, CodaLogicalServerType.FINANCIALS, CompanyPropertyProvider.class),
    new CodaServiceDefinition("Currency", "2.0", Currency.class, "Currency", CodaLocalKey.class, new String[] {"companyCode","code"}, Key.class, new String[] {"cmpCode","code"}, CodaLogicalServerType.FINANCIALS),
    new CodaServiceDefinition("ElementMaster", "11.3", Element.class, "Element", ElementKey.class, new String[] {"companyCode","code","level"}, ElmFullKey.class, new String[] {"cmpCode","code","level"}, CodaLogicalServerType.FINANCIALS, ElementPropertyProvider.class),
    new CodaServiceDefinition("UserMaster", "11.3", User.class, "User", CodaGlobalKey.class, new String[] {"code"}, UserKey.class, new String[] {"code"}, CodaLogicalServerType.FRAMEWORK),
    new CodaServiceDefinition("CapabilityMaster", "3.0", CapabilityMaster.class, "CapabilityMaster", CodaGlobalKey.class, new String[] {"code"}, GlobalKey.class, new String[] {"code"}, CodaLogicalServerType.FRAMEWORK),
    new CodaServiceDefinition("DocumentMaster", "11.3", DocumentMaster.class, "DocumentMaster", CodaLocalKey.class, new String[] {"companyCode","code"}, Key.class, new String[] {"cmpCode","code"}, CodaLogicalServerType.FINANCIALS),
    new CodaServiceDefinition("HomePageTemplateMaster", "1.0", HomePageTemplate.class, "HomePageTemplate", CodaGlobalKey.class, new String[] {"code"}, GlobalKey.class, new String[] {"code"}, CodaLogicalServerType.FRAMEWORK)
  };

  private static final Map<Class<?>, CodaServiceDefinition> SERVICES;

  private CodaServiceFactory() {}
  
  static {
    Map<Class<?>, CodaServiceDefinition> map = new HashMap<>();
    for (CodaServiceDefinition serviceDef : serviceDefs)
      put(map,serviceDef);
    SERVICES = map;
  }
 
  private static void put(Map<Class<?>, CodaServiceDefinition> map, CodaServiceDefinition serviceDef) {
    map.put(serviceDef.getInstanceType(), serviceDef);
  }
  
  public static void registerService(CodaServiceDefinition serviceDef) {
    put(SERVICES, serviceDef);
  }
  
  public static <T,K extends CodaKey> CodaService<T,K> createService(Class<T> instanceClass, CodaRouter router) throws Exception {
    CodaServiceDefinition serviceDef = SERVICES.get(instanceClass);
    if (serviceDef == null)
      throw new Exception("No service defined for class: " + instanceClass.getSimpleName());
    return new CodaGenericService<>(checkNotNull(router), serviceDef);
  }
  
  public static CodaLogicalServerType getLsvType(Class instanceClass) {
    CodaServiceDefinition serviceDef = SERVICES.get(instanceClass);
    if (serviceDef == null)
      return CodaLogicalServerType.NONE;
    return serviceDef.getLsvType();
  }
  
  public static <T> CodaPropertyProvider<T> createPropertyProvider(Class<T> instanceClass) throws Exception {
    CodaServiceDefinition serviceDef = SERVICES.get(instanceClass);
    if (serviceDef == null)
      throw new Exception("No service defined for class: " + instanceClass.getSimpleName());
    Constructor ctor = serviceDef.getPropertyProviderType().getConstructor();
    ctor.setAccessible(true);
    return (CodaPropertyProvider<T>)ctor.newInstance();
  }
}
