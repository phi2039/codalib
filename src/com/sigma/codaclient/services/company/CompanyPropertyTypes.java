/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.company;

import com.sigma.codaclient.services.common.CodaPropertyType;
import com.sigma.codaclient.services.common.CodaPropertyTypes;

/**
 *
 * @author clance
 */
public class CompanyPropertyTypes extends CodaPropertyTypes {
  public static CodaPropertyType ADDRESS = new CompanyAddressProperty();
}
