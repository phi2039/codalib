/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.common;

import com.sigma.codaclient.codarouter.CodaLogicalServerType;
import com.sigma.codaclient.services.common.CodaServiceDefinition;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author clance
 */
public class CodaFrameworkConfiguration extends Properties{
  
  public CodaFrameworkConfiguration(String fileName) throws FileNotFoundException, IOException {
    FileInputStream configFile = new FileInputStream(fileName);
    load(configFile);
  }
  
  public CodaServiceDefinition getServiceDefinition(String name) throws Exception {
    String prefix = String.format("object.%s.",name.toLowerCase());
    String instanceName = getProperty(prefix + "instance_name","");
    String serviceName = getProperty(prefix + "service_name","");
    String serviceVersion = getProperty(prefix + "service_version","");
    String[] storeKeyFields = getProperty(prefix + "store_key_fields","").split(",");
    String[] instanceKeyFields = getProperty(prefix + "instance_key_fields","").split(",");
    String instanceClassName = getProperty(prefix + "instance_class","");
    String instanceKeyClassName = getProperty(prefix + "instance_key_class","");
    String storeKeyClassName = getProperty(prefix + "store_key_class","");
    String propertyProviderClassName = getProperty(prefix + "property_provider","");
    String lsvTypeName = getProperty(prefix + "lsv_type","");
    
    Class instanceType = null;
    Class storeKeyType = null;
    Class instanceKeyType = null;
    Class propertyProviderType = null;
    CodaLogicalServerType lsvType = null;

    if (!instanceClassName.isEmpty())
      instanceType = Class.forName(instanceClassName);
    if (!instanceKeyClassName.isEmpty())
      instanceKeyType = Class.forName(instanceKeyClassName);
    if (!storeKeyClassName.isEmpty())
      storeKeyType = Class.forName(storeKeyClassName);
    if (!propertyProviderClassName.isEmpty())
      propertyProviderType = Class.forName(propertyProviderClassName);
    if (!lsvTypeName.isEmpty())
      lsvType = CodaLogicalServerType.valueOf(lsvTypeName.toUpperCase());

    CodaServiceDefinition serviceDef = new CodaServiceDefinition(serviceName, serviceVersion, instanceType, instanceName, storeKeyType, storeKeyFields, instanceKeyType, instanceKeyFields, lsvType, propertyProviderType);    
    return serviceDef;
  }
}
