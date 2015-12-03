/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

import com.sigma.codaclient.codarouter.CodaLogicalServerType;
import com.sigma.codaclient.codarouter.CodaObjectStoreDefinition;

/**
 *
 * @author clance
 */
public class CodaServiceDefinition<T> extends CodaObjectStoreDefinition<T>{
  protected final String serviceName;
  protected final String serviceVersion;
  protected final CodaLogicalServerType lsvType;
  
  public CodaServiceDefinition(String serviceName, String serviceVersion, Class instanceType, String instanceName, Class serviceKeyType, String[] serviceKeyFields, Class instanceKeyType, String[] instanceKeyFields, CodaLogicalServerType lsvType, Class propertyProviderType) {
    super(instanceType, instanceName, serviceKeyType, serviceKeyFields, instanceKeyType, instanceKeyFields, propertyProviderType);
    this.serviceName = serviceName;
    this.serviceVersion = serviceVersion;
    this.lsvType = lsvType;
  }
  
  public CodaServiceDefinition(String serviceName, String serviceVersion, Class instanceType, String instanceName, Class serviceKeyType, String[] serviceKeyFields, Class instanceKeyType, String[] instanceKeyFields, CodaLogicalServerType lsvType) {
    this(serviceName, serviceVersion, instanceType, instanceName, serviceKeyType, serviceKeyFields, instanceKeyType, instanceKeyFields, lsvType, CodaGenericPropertyProvider.class);
  }  
  
  public String getServiceName() {
    return serviceName;
  }

  public String getServiceVersion() {
    return serviceVersion;
  }

  public CodaLogicalServerType getLsvType() {
    return lsvType;
  }  
}
