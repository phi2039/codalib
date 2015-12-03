/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author clance
 */
public interface CodaPropertyProvider <I> {
  public <T> T getValue(String name, I instance) throws Exception;
  public <T> void setValue(String name, T value, I instance) throws Exception;
  public void setValues(Map<String, Object> values, I instance) throws Exception;
  public void clearValue(String name, I instance) throws Exception;
  public void clearValues(List<String> values, I instance) throws Exception;
  public String getPath();
  public Collection<String> getNames(boolean includePath);
  public void setDefaults(I instance) throws Exception;
  public void clearInvalidProperties(I instance);
  public boolean hasProperty(String name);
}
