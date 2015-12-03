/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.codarouter;

import com.sigma.codaclient.services.common.CodaGenericPropertyProvider;

/**
 *
 * @author clance
 */
public class CodaObjectStoreDefinition<T> {
  protected final Class instanceType;
  protected final String instanceName;
  protected final Class storeKeyType;
  protected final Class instanceKeyType;
  protected final String[] storeKeyFields;
  protected final String[] instanceKeyFields;
  protected final Class propertyProviderType;
  
  public CodaObjectStoreDefinition(Class instanceType, String instanceName, Class serviceKeyType, String[] serviceKeyFields, Class instanceKeyType, String[] instanceKeyFields, Class propertyProviderType) {
    this.instanceType = instanceType;
    this.instanceName = instanceName;
    this.storeKeyType = serviceKeyType;
    this.instanceKeyType = instanceKeyType;
    this.storeKeyFields = serviceKeyFields;
    this.instanceKeyFields = instanceKeyFields;
    this.propertyProviderType = propertyProviderType;
  }
  
  public CodaObjectStoreDefinition(Class instanceType, String instanceName, Class serviceKeyType, String[] serviceKeyFields, Class instanceKeyType, String[] instanceKeyFields) {
    this(instanceType, instanceName, serviceKeyType, serviceKeyFields, instanceKeyType, instanceKeyFields, CodaGenericPropertyProvider.class);
  }

  public Class getInstanceType() {
    return instanceType;
  }

  public String getInstanceName() {
    return instanceName;
  }

  public Class getStoreKeyType() {
    return storeKeyType;
  }

  public String[] getStoreKeyFields() {
    return storeKeyFields;
  }

  public String[] getInstanceKeyFields() {
    return instanceKeyFields;
  }
  
  public Class getInstanceKeyType() {
    return instanceKeyType;
  }

  public Class getPropertyProviderType() {
    return propertyProviderType;
  }    
}