/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.common;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author clance
 */
public class CodaClientProperties extends Properties {

  private final String profileName;

  public CodaClientProperties(Properties props, String profileName) {
    if (profileName == null || profileName.equals("")) {
      this.profileName = "";
      return;
    }
    
    this.profileName = profileName;
    String prefix = profileName + ".";
    int prefixLen = prefix.length();
    Set<Map.Entry<Object,Object>> entries =  props.entrySet();
    for (Map.Entry<Object,Object> entry : entries) {
      String fullPropName = (String)entry.getKey();
      if (fullPropName.startsWith(prefix)) {
        String propName = fullPropName.substring(prefixLen);
        String value = entry.getValue().toString();
        setProperty(propName, value);
      }
    }
  }
  
  public CodaClientProperties(String profileName) {
    this.profileName = profileName;
  }
  
  public String getProfileName() {
    return profileName;
  }
  
  public int getIntProperty(String key, int defaultValue) {
    String stringVal = getProperty(key);
    if (stringVal != null) {
      try {
        int intVal = Integer.parseInt(stringVal);
        return intVal;
      } catch (NumberFormatException nfe) {
        return defaultValue;
      }
    }
    return defaultValue;
  }

  public String getStringProperty(String key, String defaultValue) {
    String stringVal = getProperty(key);
    if (stringVal != null)
      return stringVal;
    return defaultValue;
  }

  public String getStringProperty(String key) {
    return getStringProperty(key, "");
  }
  
  public int getIntProperty(String key) {
    return getIntProperty(key,0);
  }
  
  public String getNsvHostName() {
    return getStringProperty("nsv.host");
  }

  public int getNsvPort() {
    return getIntProperty("nsv.port");
  }

  public String getFinancialsLsvName() {
    return getStringProperty("lsv.financials");
  }

  public String getAssetsLsvName() {
    return getStringProperty("lsv.assets");
  }
  
  public String getFrameworkLsvName() {
    return getStringProperty("lsv.framework");
  }
    
  
  public CodaUserCredentials getCodaUser() {
    return new CodaUserCredentials(getStringProperty("login.username"), getStringProperty("login.password"), getStringProperty("login.default_company"));
  }

  public int getLicenseSlot() {
    return getIntProperty("login.license_slot");
  }

  public String getDbHost() {
    return getStringProperty("db.host");
  }

  public DbUserCredentials getDbUser() {
    return new DbUserCredentials(getStringProperty("db.username"), getStringProperty("db.password"));
  }
  
  public String getDbServiceId() {
    return getStringProperty("db.service_id");
  }

  public int getDbPort() {
    return getIntProperty("db.port");
  }
}
