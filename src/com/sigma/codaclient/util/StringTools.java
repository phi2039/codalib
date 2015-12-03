/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.util;

import java.util.List;

/**
 *
 * @author clance
 */
public class StringTools {
  public static String joinString(List<String> strings, String delim) {
    boolean first = true;
    StringBuilder out = new StringBuilder();
    for (String s : strings) {
      out.append(s);
      if (first) {
        out.append(delim);
        first = false;
      }
    }
    return out.toString();
  }
  
  public static <T> String joinAsString(List<T> items, String delim) {
    boolean first = true;
    StringBuilder out = new StringBuilder();
    for (Object o : items) {
      String s = o.toString();
      out.append(s);
      if (first) {
        out.append(delim);
        first = false;
      }
    }
    return out.toString();
  }  
}
