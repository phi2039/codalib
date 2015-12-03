/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.documentmaster;

import com.coda.efinance.schemas.common.TypeCtAssetDest;
import com.coda.efinance.schemas.common.TypeCtAuthorisingUser;
import com.coda.efinance.schemas.common.TypeCtBalElmRule;
import com.coda.efinance.schemas.common.TypeCtControlTotals;
import com.coda.efinance.schemas.common.TypeCtCurRateControl;
import com.coda.efinance.schemas.common.TypeCtDocCancel;
import com.coda.efinance.schemas.common.TypeCtDocCurr;
import com.coda.efinance.schemas.common.TypeCtDocDefSense;
import com.coda.efinance.schemas.common.TypeCtDocDest;
import com.coda.efinance.schemas.common.TypeCtDocDestCtrl;
import com.coda.efinance.schemas.common.TypeCtDocExtAcRule;
import com.coda.efinance.schemas.common.TypeCtDocExtRefUsage;
import com.coda.efinance.schemas.common.TypeCtDocNumberType;
import com.coda.efinance.schemas.common.TypeCtDocSeqRule;
import com.coda.efinance.schemas.common.TypeCtDocumentType;
import com.coda.efinance.schemas.common.TypeCtDupXR;
import com.coda.efinance.schemas.common.TypeCtIntrayCheck;
import com.coda.efinance.schemas.common.TypeCtModDueDate;
import com.coda.efinance.schemas.common.TypeCtModValDate;
import com.coda.efinance.schemas.common.TypeCtPeriodUsage;
import com.coda.efinance.schemas.common.TypeCtStatPayDoc;
import com.coda.efinance.schemas.common.TypeCtTaxMethod;
import com.coda.efinance.schemas.common.TypeCtUserRefType;
import com.coda.efinance.schemas.common.TypeCtWorkflowAuthReq;
import com.coda.efinance.schemas.documentmaster.AnalysisLines;
import com.coda.efinance.schemas.documentmaster.BalancingElements;
import com.coda.efinance.schemas.documentmaster.Currency;
import com.coda.efinance.schemas.documentmaster.DocNumList;
import com.coda.efinance.schemas.documentmaster.DocNumListItem;
import com.coda.efinance.schemas.documentmaster.DocTax;
import com.coda.efinance.schemas.documentmaster.DocumentMaster;
import com.coda.efinance.schemas.documentmaster.DueDate;
import com.coda.efinance.schemas.documentmaster.ExtRef;
import com.coda.efinance.schemas.documentmaster.ExtRefChecking;
import com.coda.efinance.schemas.documentmaster.ExtRefCompare;
import com.coda.efinance.schemas.documentmaster.ExternalReferences;
import com.coda.efinance.schemas.documentmaster.FieldAccess;
import com.coda.efinance.schemas.documentmaster.Intercompany;
import com.coda.efinance.schemas.documentmaster.Pay;
import com.coda.efinance.schemas.documentmaster.PayData;
import com.coda.efinance.schemas.documentmaster.PayElement;
import com.coda.efinance.schemas.documentmaster.PreDefinedLines;
import com.coda.efinance.schemas.documentmaster.Quantities;
import com.coda.efinance.schemas.documentmaster.SummaryLines;
import com.coda.efinance.schemas.documentmaster.UserReference;
import com.coda.efinance.schemas.documentmaster.UserReferences;
import com.coda.efinance.schemas.documentmaster.ValueDate;
import com.sigma.codaclient.services.common.CodaPropertyProvider;
import com.sigma.codaclient.services.common.CodaGenericPropertyProvider;
import com.sigma.codaclient.services.common.CodaPropertyTypes;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author clance
 */
public class DocumentMasterPropertyProvider extends CodaGenericPropertyProvider<DocumentMaster> implements CodaPropertyProvider<DocumentMaster> {
  protected static DocumentMasterPropertyProvider instance;

  protected DocumentMasterPropertyProvider() throws Exception {
    super("documentmaster", DocumentMaster.class);
    
    addProperty("company_code", CodaPropertyTypes.STRING);
    addProperty("code", CodaPropertyTypes.STRING);
    addProperty("name", CodaPropertyTypes.STRING);
    addProperty("short_name", CodaPropertyTypes.STRING);
    addProperty("pay_options", DocumentMasterPropertyTypes.PAYOPT);
    addProperty("summary_line_sense", CodaPropertyTypes.STRING);
    addProperty("analysis_line_sense", CodaPropertyTypes.STRING);
    addProperty("pay_status", CodaPropertyTypes.STRING);
    addProperty("reverse_cr_quantity_signs", CodaPropertyTypes.BOOL);
    addProperty("reverse_dr_quantity_signs", CodaPropertyTypes.BOOL);
    addProperty("document_location", CodaPropertyTypes.STRING);
    addProperty("checking_location", CodaPropertyTypes.STRING);
    addProperty("control_totals", CodaPropertyTypes.STRING);
    addProperty("document_dates", DocumentMasterPropertyTypes.DOCDATES);
    addProperty("currency_rate_control", CodaPropertyTypes.STRING);
    addProperty("document_numbering", CodaPropertyTypes.LIST);
  }
  
  public static DocumentMasterPropertyProvider getInstance() throws Exception {
    if (instance == null)
      instance = new DocumentMasterPropertyProvider();
    return instance;
  }
  
  @Override
  public <T> void setValue(String name, T value, DocumentMaster instance) throws Exception {
    switch(name) {
    case "company_code": // String
      instance.setCmpCode((String)value);
      break;
    case "code": // String
      instance.setCode((String)value);
      break;
    case "name": // String
      instance.setName((String)value);
      break;
    case "short_name": // String
      instance.setShortName((String)value);
      break;
    case "pay_options":// DocumentPaymentOptions
      DocumentPaymentOptions opt = (DocumentPaymentOptions)value;
      Pay pay = new Pay();
      opt.set(pay);
      instance.setPay(pay);
      break;
    case "summary_line_sense":// String (TypeCtDocDefSense)
      SummaryLines summLines = instance.getSummaryLines();
      if (summLines == null) {
        summLines = new SummaryLines();
        instance.setSummaryLines(summLines);
      }
      summLines.setDefaultSense(TypeCtDocDefSense.fromValue(value.toString().toUpperCase()));
      break;
    case "analysis_line_sense":// String (TypeCtDocDefSense)
      AnalysisLines anaLines = instance.getAnalysisLines();
      if (anaLines == null) {
        anaLines = new AnalysisLines();
        instance.setAnalysisLines(anaLines);
      }
      anaLines.setDefaultSense(TypeCtDocDefSense.fromValue(value.toString().toUpperCase()));
      break;
    case "pay_status":// DocumentPayStatus (TypeCtStatPayDoc)
      instance.setPayStatus(TypeCtStatPayDoc.fromValue(value.toString().toUpperCase()));
      break;
    case "reverse_cr_quantity_signs":// Boolean
      Quantities quant = instance.getQuantities();
      if (quant == null) {
        quant = new Quantities();
        quant.setReverseOnDebit(false);        
        instance.setQuantities(quant);
      }
      quant.setReverseOnCredit((Boolean)value);
      break;
    case "reverse_dr_quantity_signs":// Boolean
      quant = instance.getQuantities();
      if (quant == null) {
        quant = new Quantities();
        quant.setReverseOnCredit(false);        
        instance.setQuantities(quant);
      }
      quant.setReverseOnDebit((Boolean)value);
      break;
    case "document_location":// String (TypeCtDocDest)
      instance.setDestination(TypeCtDocDest.fromValue(value.toString().toUpperCase()));
      break;
    case "checking_location":// String (TypeCtIntrayCheck)
      instance.setChecking(TypeCtIntrayCheck.fromValue(value.toString().toUpperCase()));
      break;
    case "control_totals":// String (TypeCtControlTotals)
      instance.setControlTotals(TypeCtControlTotals.fromValue(value.toString().toUpperCase()));
      break;
    case "document_dates":// DocumentMasterDates
      DocumentMasterDates dates = (DocumentMasterDates)value;
      dates.set(instance);
      break;
    case "currency_rate_control":// String (TypeCtCurRateControl)
      Currency curr = instance.getCurrency();
      if (curr == null) {
        curr = new Currency();
        curr.setRule(TypeCtDocCurr.ENTERING);
        instance.setCurrency(curr);
      }
      curr.setRateControl(TypeCtCurRateControl.fromValue(value.toString().toUpperCase()));
      break;
    case "document_numbering":// List<DocumentNumberingRule>
      List<DocumentNumberingRule> rules = (List<DocumentNumberingRule>)value;
      DocNumList list = instance.getDocNumList();
      if (list == null) {
        list = new DocNumList();
        instance.setDocNumList(list);
      }
      for (DocumentNumberingRule rule : rules) {
        DocNumListItem docNumItem = new DocNumListItem();
        docNumItem.setOpen(rule.isOpen());
        docNumItem.setFirst(rule.getFirst());
        docNumItem.setNext(rule.getNext());
        docNumItem.setLast(rule.getLast());
        list.getDocNumListItem().add(docNumItem);
      }
    default:
      throw new Exception ("Unsupported or unknown property: " + name);
    }  
  }

  @Override
  public void clearValue(String name, DocumentMaster instance) throws Exception {
    switch(name) {
    case "company_code": // String
      instance.setCmpCode(null);
      break;
    case "code": // String
      instance.setCode(null);
      break;
    case "name": // String
      instance.setName(null);
      break;
    case "short_name": // String
      instance.setShortName(null);
      break;
    case "pay_options":// DocumentPaymentOptions
      instance.setPay(null);
      break;
    case "pay_status":// DocumentPayStatus (TypeCtStatPayDoc)
      instance.setPayStatus(null);
      break;
    case "document_location":// String (TypeCtDocDest)
      instance.setDestination(null);
      break;
    case "checking_location":// String (TypeCtIntrayCheck)
      instance.setChecking(null);
      break;
    case "control_totals":// String (TypeCtControlTotals)
      instance.setControlTotals(null);
      break;
    case "document_dates":// DocumentMasterDates
      instance.setDueDate(null);
      instance.setValueDate(null);
      break;
    case "document_numbering":// List<DocumentNumberingRule>
      instance.setDocNumList(null);
    default:
      throw new Exception ("Unsupported or unknown property: " + name);
    }  
  }

  @Override
  public <T> T getValue(String name, DocumentMaster instance) throws Exception {
    switch(name) {
    case "company_code": // String
      return (T)instance.getCmpCode();
    case "code":
      return (T)instance.getCode();
    case "name":
      return (T)instance.getName();
    case "short_name":
      return (T)instance.getShortName();
    case "pay_options":// DocumentPaymentOptions     
      Pay pay = instance.getPay();
      if (pay == null)
        pay = new Pay();
      return (T) new DocumentPaymentOptions(pay);
    case "summary_line_sense":// String (TypeCtDocDefSense)
      SummaryLines summLines = instance.getSummaryLines();
      if (summLines == null) {
        summLines = new SummaryLines();
        instance.setSummaryLines(summLines);
      }
      return (T) summLines.getDefaultSense().toString();
    case "analysis_line_sense":// String (TypeCtDocDefSense)
      AnalysisLines anaLines = instance.getAnalysisLines();
      if (anaLines == null) {
        anaLines = new AnalysisLines();
        instance.setAnalysisLines(anaLines);
      }
      return (T) anaLines.getDefaultSense().toString();
    case "pay_status":// DocumentPayStatus (TypeCtStatPayDoc)
      return (T) instance.getPayStatus().toString();
    case "reverse_cr_quantity_signs":// Boolean
      Quantities quant = instance.getQuantities();
      if (quant == null) {
        quant = new Quantities();
        quant.setReverseOnDebit(false);        
        instance.setQuantities(quant);
      }
      return (T)(Boolean)quant.isReverseOnCredit();
    case "reverse_dr_quantity_signs":// Boolean
      quant = instance.getQuantities();
      if (quant == null) {
        quant = new Quantities();
        quant.setReverseOnCredit(false);        
        instance.setQuantities(quant);
      }
      return (T)(Boolean)quant.isReverseOnDebit();
    case "document_location":// String (TypeCtDocDest)
      return (T)instance.getDestination().toString();
    case "checking_location":// String (TypeCtIntrayCheck)
      return (T)instance.getChecking().toString();
    case "control_totals":// String (TypeCtControlTotals)
      return (T)instance.getControlTotals();
    case "document_dates":// DocumentMasterDates
      return (T) new DocumentMasterDates(instance);
    case "currency_rate_control":// String (TypeCtCurRateControl)
      Currency curr = instance.getCurrency();
      if (curr == null) {
        curr = new Currency();
        curr.setRule(TypeCtDocCurr.ENTERING);
        instance.setCurrency(curr);
      }
      return (T)curr.getRateControl();
    case "document_numbering":// List<DocumentNumberingRule>
      List<DocumentNumberingRule> rules = new ArrayList<>();
      DocNumList list = instance.getDocNumList();
      if (list == null)
        return (T)rules;
      for (DocNumListItem docNumItem : list.getDocNumListItem())
        return (T)new DocumentNumberingRule(docNumItem.isOpen(), docNumItem.getFirst(), docNumItem.getNext(), docNumItem.getLast());
    default:
      throw new Exception ("Unsupported or unknown property: " + name);
    }  
  }
  
  @Override
  public void setDefaults(DocumentMaster instance) throws Exception {

    instance.setTimeStamp((short)-1);
    instance.setCancelDoc(TypeCtDocCancel.NO);
    instance.setCancelMatch(false);
    instance.setAddToTurnovers(false);
    instance.setBatch(false);
    instance.setPayStatus(TypeCtStatPayDoc.NULL);
    instance.setRequireDescription(false);
    instance.setNumberRule(TypeCtDocSeqRule.AUTOMATIC);
    instance.setNumberFormat(TypeCtDocNumberType.NUMERIC);
    instance.setControlTotals(TypeCtControlTotals.ERROR);
    instance.setDestination(TypeCtDocDest.BOOKS);
    instance.setChecking(TypeCtIntrayCheck.BOOKS);
    instance.setAuthorisingUser(TypeCtAuthorisingUser.NOT);
    instance.setValidateOnAccount(false);
    instance.setPeriodUsage(TypeCtPeriodUsage.DOC_PER_USE_NORMAL);
    instance.setPurchaseOrdering(false);
    instance.setAllowDiscounts(true);
    instance.setUpdateTransaction(TypeCtDocumentType.UPDATE_LAST_CASH);
    instance.setRetainValues(false);
    instance.setAmendInBrowse(true);
    instance.setMatchingFromInput(false);
    instance.setMatchingFromBrowse(false);
    instance.setAssetDoc(TypeCtAssetDest.NONE);
    instance.setSelfProportioning(false);
    instance.setRecurring(false);
    instance.setReversing(false);
    DueDate dueDate = new DueDate();
    dueDate.setModifiable(TypeCtModDueDate.MOD_INPUT);
    dueDate.setDocumentWide(false);
    instance.setDueDate(dueDate);
    ValueDate valDate = new ValueDate();
    valDate.setModifiable(TypeCtModValDate.MOD_INPUT);
    valDate.setDocumentWide(false);
    instance.setValueDate(valDate);
    Currency curr = new Currency();
    curr.setRule(TypeCtDocCurr.ENTERING);
    curr.setRateControl(TypeCtCurRateControl.LINEWIDE);
    instance.setCurrency(curr);
    DocTax tax = new DocTax();
    tax.setMethod(TypeCtTaxMethod.TAX_NONE);
    tax.setESL(false);
    tax.setIntrastat(false);
    tax.setTen99(false);
    tax.setNumberOfTaxCodes((short)0);
    instance.setTax(tax);
    Pay pay = new Pay();
    pay.setEnable(true);
    PayData media = new PayData();
    media.setRequired(false);
    media.setModifiable(true);
    pay.setMedia(media);
    PayData cmpBank = new PayData();
    cmpBank.setRequired(false);
    cmpBank.setModifiable(true);
    pay.setCompanyBank(cmpBank);
    PayElement payElm = new PayElement();
    payElm.setAddressRequired(false);
    payElm.setBankCodeRequired(false);
    pay.setElement(payElm);
    instance.setPay(pay);
    Quantities quant = new Quantities();
    quant.setReverseOnCredit(false);
    quant.setReverseOnDebit(false);
    instance.setQuantities(quant);
    SummaryLines summLines = new SummaryLines();
    summLines.setNumber((short)0);
    summLines.setDefaultSense(TypeCtDocDefSense.NONE);
    instance.setSummaryLines(summLines);
    AnalysisLines anaLines = new AnalysisLines();
    anaLines.setDefaultSense(TypeCtDocDefSense.NONE);    
    instance.setAnalysisLines(anaLines);
    Intercompany interco = new Intercompany();
    interco.setRule(TypeCtDocDestCtrl.DOC_DEST_DISALLOW);
    instance.setIntercompany(interco);
    BalancingElements balElm = new BalancingElements();
    balElm.setRule(TypeCtBalElmRule.ERROR);
    instance.setBalancingElements(balElm);
    ExternalReferences extRef = new ExternalReferences();
    extRef.setDocumentWide(false);
    extRef.setCombine(false);
    ExtRef[] refs = new ExtRef[6];
    for (int i = 0; i < 6; i++)
      refs[i] = new ExtRef();
    for (ExtRef ref : refs) {
      ref.setUsage(TypeCtDocExtRefUsage.ALLOW);
      ref.setModifiable(false);
      ExtRefChecking refCheck = new ExtRefChecking();
      ExtRefCompare compare = new ExtRefCompare();
      compare.setReference1(false);
      compare.setReference2(false);
      compare.setReference3(false);
      compare.setReference4(false);
      compare.setReference5(false);
      compare.setReference6(false);
      refCheck.setCompareWith(compare);
      refCheck.setAccountRule(TypeCtDocExtAcRule.CURRENT_ACCOUNT);
      refCheck.setAllDocumentTypes(false);
      refCheck.setExcludeCurrent(false);
      refCheck.setSummaryLinesOnly(false);
      refCheck.setActionOnDuplicate(TypeCtDupXR.WARNING);
      ref.setChecking(refCheck);
    }
    extRef.setReference1(refs[0]);
    extRef.setReference2(refs[1]);
    extRef.setReference3(refs[2]);
    extRef.setReference4(refs[3]);
    extRef.setReference5(refs[4]);
    extRef.setReference6(refs[5]);
    instance.setExternalReferences(extRef);    
    UserReferences userRefs = new UserReferences();
    UserReference ref1 = new UserReference();
    ref1.setRequired(false);
    ref1.setModifiable(true);
    ref1.setDefault(null);
    ref1.setType(TypeCtUserRefType.TEXT);
    ref1.setVocab((short)98);
    userRefs.setReference1(ref1);
    UserReference ref2 = new UserReference();
    ref2.setRequired(false);
    ref2.setModifiable(true);
    ref2.setDefault(null);
    ref2.setType(TypeCtUserRefType.TEXT);
    ref2.setVocab((short)99);
    userRefs.setReference2(ref2);
    UserReference ref3 = new UserReference();
    ref3.setRequired(false);
    ref3.setModifiable(true);
    ref3.setDefault(null);
    ref3.setType(TypeCtUserRefType.TEXT);
    ref3.setVocab((short)100);
    userRefs.setReference3(ref3);
    instance.setUserReferences(userRefs);
    PreDefinedLines preDefLines = new PreDefinedLines();
    preDefLines.setEnabled(false);
    preDefLines.setMaximum((short)0);
    preDefLines.setAllowAdditionalLines(false);
    instance.setPreDefinedLines(preDefLines);
    FieldAccess fieldAccess = new FieldAccess();
    fieldAccess.setLineType(false);
    fieldAccess.setLineSense(false);
    fieldAccess.setUserStatus(false);
    instance.setFieldAccess(fieldAccess);
    instance.setPrevIntrayDel(false);
    instance.setPrevIntrayMod(false);
    instance.setWorkflowRequired(TypeCtWorkflowAuthReq.NONE);
    instance.setWorkItemExplodePresenter("");
    instance.setPositionHierarchy("");
    instance.setIntrayWorkflow("");
    instance.setProtectIntrayWorkflow(false);
    instance.setConfirmIntrayWorkflow(false);
    instance.setAllowWorkflowIntrayEdits(false);
    instance.setBooksWorkflow("");
    instance.setBooksWorkflow("");
    instance.setProtectBooksWorkflow(true);
    instance.setPromptForAuthorisingUser(false);
    instance.setIsReserved(false);
    instance.setPreventFutureDate(false);    
    DocNumListItem docNumItem = new DocNumListItem();
    docNumItem.setOpen(true);
    docNumItem.setFirst("1");
    docNumItem.setNext("1");
    docNumItem.setLast("999999999");
    DocNumList docNumList = new DocNumList();
    docNumList.getDocNumListItem().add(docNumItem);
    instance.setDocNumList(docNumList);
    instance.setPreDefinedLineList(null);
  }  
}
