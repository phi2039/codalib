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
public class DocumentNumberingRule {
  private boolean open;
  private String first;
  private String last;
  private String next;

  public DocumentNumberingRule() {
    open = true;
    first = "1";
    next = "1";
    last = "999999999";
  }

  public DocumentNumberingRule(boolean open, String first, String next, String last) {
    this.open = open;
    this.first = first;
    this.next = next;
    this.last = last;
  }  

  public boolean isOpen() {
    return open;
  }

  public void setOpen(boolean open) {
    this.open = open;
  }

  public String getFirst() {
    return first;
  }

  public void setFirst(String first) {
    this.first = first;
  }

  public String getLast() {
    return last;
  }

  public void setLast(String last) {
    this.last = last;
  }

  public String getNext() {
    return next;
  }

  public void setNext(String next) {
    this.next = next;
  }
  
  
}
