/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.documentmaster;

/**
 *
 * @author clance
 */

public enum DocumentMasterProperty {
  PAY_OPTIONS, // DocumentPaymentOptions
  SUMMARY_LINE_SENSE, // DocumentLineSense
  ANALYSIS_LINE_SENSE, // DocumentLineSense
  PAY_STATUS, // DocumentPayStatus
  REVERSE_CR_QUANTITY_SIGNS, // boolean
  REVERSE_DR_QUANTITY_SIGNS, // boolean
  DOCUMENT_LOCATION, // DocumentLocation
  CHECKING_LOCATION, // DocumentLocation
  CONTROL_TOTALS, // DocumentControlTotals
  DOCUMENT_DATES, // DocumentMasterDate (Due date/Value date)
  CURRENCY_RATE_CONTROL, // CurrencyRateControl
  TAX_CALC_METHOD, // DocumentTaxMethod
  EXTERNAL_REFS, // DocumentExternalReferences
  DOCUMENT_NUMBERING // DocumentNumbering
}
