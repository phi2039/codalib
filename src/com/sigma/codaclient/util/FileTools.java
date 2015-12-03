/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.util;

import java.io.File;

/**
 *
 * @author clance
 */
public class FileTools {
  public static boolean confirmPath(String path) {
    try {
      File f = new File(path);
      if (f.exists())
        return true;
      else
        return f.mkdirs();
    } catch (Exception ex) {
      return false;
    }    
  }
}
