/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.company;

import com.coda.efinance.schemas.company.Company;

/**
 *
 * @author clance
 */
public class CompanyAddress {
    private final String[] addressLines = new String[6];
    private String postCode;
    private String countryCode;
    private String telNumber;
    private String faxNumber;
    private String taxNumber;

    public CompanyAddress() {
      
    }
    
    public CompanyAddress(Company company) {
      addressLines[0] = company.getAddr1();
      addressLines[1] = company.getAddr2();
      addressLines[2] = company.getAddr3();
      addressLines[3] = company.getAddr4();
      addressLines[4] = company.getAddr5();
      addressLines[5] = company.getAddr6();

      postCode = company.getPostCode();
      countryCode = company.getCountryCode();
      telNumber = company.getTelephone();
      faxNumber = company.getFax();
      taxNumber = company.getVat();
    }
    
    public void set(Company company) {
      company.setAddr1(addressLines[0]);
      company.setAddr2(addressLines[1]);
      company.setAddr3(addressLines[2]);
      company.setAddr4(addressLines[3]);
      company.setAddr5(addressLines[4]);
      company.setAddr6(addressLines[5]);
      company.setPostCode(postCode);
      company.setCountryCode(countryCode);
      company.setTelephone(telNumber);
      company.setFax(faxNumber);
      company.setVat(taxNumber);      
    }
    
    public static void clear(Company company) {
      company.setAddr1(null);
      company.setAddr2(null);
      company.setAddr3(null);
      company.setAddr4(null);
      company.setAddr5(null);
      company.setAddr6(null);
      company.setPostCode(null);
      company.setCountryCode(null);
      company.setTelephone(null);
      company.setFax(null);
      company.setVat(null);      
    }    
    
    public String getLine(int line) {
      if (line < 1 || line > 6)
        return "";
      return addressLines[line - 1];
    }

    public void setLine(int line, String value) {
      if (line < 1 || line > 6)
        return;
      this.addressLines[line - 1] = value;
    }

    public String getPostCode() {
      return postCode;
    }

    public void setPostCode(String postCode) {
      this.postCode = postCode;
    }

    public String getCountryCode() {
      return countryCode;
    }

    public void setCountryCode(String countryCode) {
      this.countryCode = countryCode;
    }

    public String getTelNumber() {
      return telNumber;
    }

    public void setTelNumber(String telNumber) {
      this.telNumber = telNumber;
    }

    public String getFaxNumber() {
      return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
      this.faxNumber = faxNumber;
    }

    public String getTaxNumber() {
      return taxNumber;
    }

    public void setTaxNumber(String taxNumber) {
      this.taxNumber = taxNumber;
    }
  }
