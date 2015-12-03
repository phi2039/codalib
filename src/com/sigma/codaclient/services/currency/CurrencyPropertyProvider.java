/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.currency;

import com.coda.efinance.schemas.common.TypeCtAfterBefore;
import com.coda.efinance.schemas.common.TypeCtCurLinkType;
import com.coda.efinance.schemas.common.TypeCtCurParentType;
import com.coda.efinance.schemas.common.TypeCtCurRateControl;
import com.coda.efinance.schemas.common.TypeCtMulDiv;
import com.coda.efinance.schemas.currency.Currency;
import com.coda.efinance.schemas.currency.RateData;
import com.coda.efinance.schemas.currency.RateDataElement;
import com.sigma.codaclient.services.common.CodaGenericPropertyProvider;
import com.sigma.codaclient.util.DateTools;
import java.math.BigDecimal;

/**
 *
 * @author clance
 */
public class CurrencyPropertyProvider extends CodaGenericPropertyProvider<Currency> {
  protected static CurrencyPropertyProvider instance;

  protected CurrencyPropertyProvider() throws Exception {
    super("currency",Currency.class);
    wireAll();
  }
  
  public static CurrencyPropertyProvider getInstance() throws Exception {
    if (instance == null)
      instance = new CurrencyPropertyProvider();
    return instance;
  }
  
  @Override
  public void clearValue(String name, Currency instance) throws Exception {
    switch(name) {
    case "rates":
      instance.setRates(getDefaultRates());
      break;
    default:
      super.clearValue(name, instance);
    }  
  }
  
  @Override
  public void setDefaults(Currency instance) throws Exception {
    instance.setSymbolPos(TypeCtAfterBefore.BEFORE);
    instance.setCurRateControl(TypeCtCurRateControl.NOTSET);
    instance.setLinkType(TypeCtCurLinkType.NO_LINK);
    instance.setLinkDate(DateTools.getXMLDate());
    
    instance.setRates(getDefaultRates());    
  }
  
  protected RateData getDefaultRates() throws Exception {
    // Default exchange rate (1:1)
    RateData rates = new RateData();
    RateDataElement rate = new RateDataElement();
    rate.setDate(DateTools.getXMLDate(1753, 1, 1, 0, 0, 0));
    rate.setParentCode(null);
    rate.setRate(BigDecimal.ONE);
    rate.setMultiplyOrDivide(TypeCtMulDiv.RATE_MUL);
    rate.setRateScaling(1);
    rate.setParentType(TypeCtCurParentType.CURREL_HOME);
    rates.getRate().add(rate);
    return rates;
  }
}
