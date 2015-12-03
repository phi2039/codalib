/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

/**
 *
 * @author clance
 */
public class CodaPropertyTypes {
  public static final CodaPropertyType STRING = new CodaStringProperty();
  public static final CodaPropertyType INT = new CodaIntegerProperty();
  public static final CodaPropertyType LIST = new CodaListProperty();
  public static final CodaPropertyType BOOL = new CodaBooleanProperty();
  public static final CodaPropertyType OBJECT = new CodaObjectProperty();
}
