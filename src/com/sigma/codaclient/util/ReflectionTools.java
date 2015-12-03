/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author clance
 */
public class ReflectionTools {
  public static Field findField(Class<?> c, String fieldName, boolean makeAccessible) {
    Field field = null;
    // Loop through super-classes to find the field
    while (field == null && c != null) {
      try {
        field = c.getDeclaredField(fieldName);
      } catch (NoSuchFieldException nfx) {
        c = c.getSuperclass();
      }
    }
    if (field != null) {
      if (makeAccessible)
        field.setAccessible(true);
      return field;
    }
    else
      return null;     
  }
  
  public static List<Field> getFields(Class<?> c) {
    Class<?> currentClass = c;
    List<Field> fields = new ArrayList<>();
    while (currentClass != null) {
        Field[] currentFields = currentClass.getDeclaredFields();
        fields.addAll(Arrays.asList(currentFields));
        currentClass = currentClass.getSuperclass();
    }
    return fields;
  }
}
