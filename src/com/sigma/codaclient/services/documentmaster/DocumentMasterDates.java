/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.documentmaster;

import com.coda.efinance.schemas.common.TypeCtModDueDate;
import com.coda.efinance.schemas.common.TypeCtModValDate;
import com.coda.efinance.schemas.documentmaster.DocumentMaster;
import com.coda.efinance.schemas.documentmaster.DueDate;
import com.coda.efinance.schemas.documentmaster.ValueDate;

/**
 *
 * @author clance
 */
public class DocumentMasterDates {
  private final DocumentMasterDate dueDate;
  private final DocumentMasterDate valueDate;
  
  public DocumentMasterDates() {
    dueDate = new DocumentMasterDate();
    valueDate = new DocumentMasterDate();
  }

  public DocumentMasterDates(DocumentMasterDate dueDate, DocumentMasterDate valueDate) {
    this.dueDate = dueDate;
    this.valueDate = valueDate;
  }

  public DocumentMasterDates(DocumentMaster instance) {
    this();
    if (instance.getDueDate() != null) {
      dueDate.setDocwide(instance.getDueDate().isDocumentWide());
      dueDate.setModifiable((instance.getDueDate().getModifiable() ==  TypeCtModDueDate.MOD_INPUT));      
      dueDate.setRule(instance.getDueDate().getRule());
    }
    
    if (instance.getValueDate() != null) {
      valueDate.setDocwide(instance.getValueDate().isDocumentWide());
      valueDate.setModifiable((instance.getValueDate().getModifiable() ==  TypeCtModValDate.MOD_INPUT));      
      valueDate.setRule(instance.getValueDate().getRule());
    }
  }  
  
  public DocumentMasterDate getDueDate() {
    return dueDate;
  }

  public DocumentMasterDate getValueDate() {
    return valueDate;
  }
  
  public void set(DocumentMaster instance) {
    DueDate due = new DueDate();
    due.setDocumentWide(dueDate.isDocwide());
    if (dueDate.isModifiable())
      due.setModifiable(TypeCtModDueDate.MOD_INPUT);
    else
      due.setModifiable(TypeCtModDueDate.NOT);
    due.setRule(dueDate.getRule());
    instance.setDueDate(due);
    ValueDate val = new ValueDate();
    due.setDocumentWide(valueDate.isDocwide());
    if (valueDate.isModifiable())
      val.setModifiable(TypeCtModValDate.MOD_INPUT);
    else
      val.setModifiable(TypeCtModValDate.NOT);
    val.setRule(valueDate.getRule());
    instance.setValueDate(val);
  }
}
