/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author clance
 */
public class CodaClientConfiguration {
  CodaClientProperties activeProfile = null;
  final Properties properties = new Properties();
  
  public CodaClientConfiguration(String path) {
    try {
      final FileInputStream in = new FileInputStream(path);
      properties.load(in);
      in.close();
    } catch (FileNotFoundException fe) {
       System.err.println("Could not read configuration from file: " + path);
    } catch (IOException ioe) {
       System.err.println("IOException encountered while reading from " + path);
    }
    String activeProfileName = properties.getProperty("active_configuration","");
    activeProfile = new CodaClientProperties(properties, activeProfileName);
  }
  
  public CodaClientProperties getActiveProfile() {
    return activeProfile;
  }
  
  public void setActiveProfile(String profileName) {
    activeProfile = getProfile(profileName);
  }
  
  public CodaClientProperties getProfile(String profileName) {
    return new CodaClientProperties(properties, profileName);
  }  
}
