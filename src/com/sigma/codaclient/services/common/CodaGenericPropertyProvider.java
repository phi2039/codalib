/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

import com.sigma.codaclient.util.Defaults;
import com.sigma.codaclient.util.ReflectionTools;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author clance
 */
public class CodaGenericPropertyProvider <I> implements CodaPropertyProvider<I> {

  protected class PropertyDef {
    public String propertyName;
    public Object clearedValue;
    public Field field;
    
    public PropertyDef(String propertyName, Field field, Object clearedValue) {
      this.propertyName = propertyName;
      this.clearedValue = clearedValue;
      this.field = field;
    }
  }

  private final Map<String, CodaPropertyType> properties = new HashMap<>();
  private final Map<String, PropertyDef> wiredProperties = new HashMap<>();  
  private final Class<I> instanceClass;
  
  protected String path;
  
  public CodaGenericPropertyProvider(Class<I> instanceClass) {
    this(instanceClass.getSimpleName().toLowerCase(), instanceClass);
  }

  public CodaGenericPropertyProvider(String path, Class<I> instanceClass) {
    this.path = path;
    this.instanceClass = instanceClass;
  }
  
  protected void addProperty(String name, CodaPropertyType type) throws Exception {
    if (properties.containsKey(name))
      throw new Exception ("Property '" + name + "' already exists.");
    properties.put(name, type);
  }

  public void wireAll(String[] fieldNames) throws Exception {
    for (String fieldName : fieldNames) {
      Field field = ReflectionTools.findField(instanceClass, fieldName, true);
      if (field != null) {
        Object clearedValue = Defaults.defaultValue(field.getType());
        wireProperty(fieldName, field, clearedValue);
      }  
    }
  }
  
  public void wireAll() throws Exception {
    List<Field> fields = ReflectionTools.getFields(instanceClass);
    for (Field field : fields) {
      Object clearedValue = Defaults.defaultValue(field.getType());
      field.setAccessible(true);
      wireProperty(field.getName(), field, clearedValue);
    }
  }  
  
  public void wireProperty(String propertyName, Field field, Object clearedValue) throws Exception {
    if (wiredProperties.containsKey(propertyName))
      throw new Exception ("Property '" + propertyName + "' is already wired.");
    
    // TODO: Map Java type to Coda type?
    CodaPropertyType codaType = CodaPropertyTypes.OBJECT; // Generic type    
    addProperty(propertyName, codaType);
    
    PropertyDef propertyDef = new PropertyDef(propertyName, field, clearedValue);
    propertyDef.field = field;
    wiredProperties.put(propertyName, propertyDef);    
  }
  
  public void wireProperty(String propertyName, String fieldName, String fieldType, CodaPropertyType codaType, Object clearedValue) throws Exception {    
    Field field = ReflectionTools.findField(instanceClass, fieldName, true);
    if (field == null)
      throw new Exception ("Unsupported or unknown property: " + propertyName);
    wireProperty(propertyName, field, clearedValue);
  }
  
  @Override
  public Collection<String> getNames(boolean includePath) {
    if (!includePath)
      return properties.keySet();
    
    ArrayList<String> names = new ArrayList<>();
    for (String name : properties.keySet())
      names.add(String.format("%s.%s", path, name));
    return names;
  }
  
  @Override
  public String getPath() {
    return path;
  }
  
  @Override
  public void setValues(Map<String, Object> values, I instance) throws Exception {
    for (String name : values.keySet())
      setValue(name, values.get(name), instance);
  }

  @Override
  public void clearValues(List<String> names, I instance) throws Exception {
    for (String name : names)
      clearValue(name, instance);
  }
  
  @Override
  public void clearInvalidProperties(I instance) {
    
  }

  @Override
  public void clearValue(String name, I instance) throws Exception {
    PropertyDef propertyDef = wiredProperties.get(name);
    if (propertyDef == null)
      throw new Exception ("Unsupported or unknown property: " + name);
    propertyDef.field.set(instance, propertyDef.clearedValue);
  }
  
  @Override
  public void setDefaults(I instance) throws Exception {
    
  }  
  @Override
  public <T> T getValue(String name, I instance) throws Exception {
    PropertyDef propertyDef = wiredProperties.get(name);
    if (propertyDef == null)
      throw new Exception ("Unsupported or unknown property: " + name);
    T value = (T)propertyDef.field.get(instance);
    return value;
  }

  @Override
  public <T> void setValue(String name, T value, I instance) throws Exception {
    PropertyDef propertyDef = wiredProperties.get(name);
    if (propertyDef == null)
      throw new Exception ("Unsupported or unknown property: " + name);
    propertyDef.field.set(instance, value);
  }
  
  @Override
  public boolean hasProperty(String name) {
    return properties.containsKey(name);
  }  
}