/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author clance
 */
public class DateTools {
  
  public static XMLGregorianCalendar getXMLDate(int year, int month, int date, int hour, int minute, int second) throws Exception {
    try {
      XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(year, month, date, hour, minute, second, 0, 0);
      return xmlDate;
    } catch (DatatypeConfigurationException dce) {
      throw new Exception(dce);
    }
  }
  
  public static XMLGregorianCalendar getXMLDate(int year, int month, int date) throws Exception {
    return getXMLDate(year, month, date, 0, 0, 0);
  }
  
  public static XMLGregorianCalendar getXMLDate(Date date) throws Exception {
    if (date == null)
      date = new Date();
    GregorianCalendar gc = new GregorianCalendar();
//    gc.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    gc.setTime(date);
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
    } catch (DatatypeConfigurationException dce) {
      throw new Exception(dce);
    }
  }

  public static XMLGregorianCalendar getXMLDate() throws Exception {
    return getXMLDate(null);
  }
  
  public static java.util.Date getDate(XMLGregorianCalendar xmlDate) {
    GregorianCalendar cal = xmlDate.toGregorianCalendar();
    java.util.Date date = new java.util.Date(cal.getTimeInMillis());
    return date;
  }

  public static java.sql.Date getSqlDate(XMLGregorianCalendar xmlDate) {
    return new java.sql.Date(getDate(xmlDate).getTime());
  }
}
