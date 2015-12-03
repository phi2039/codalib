/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.entity;

/**
 *
 * @author clance
 */
public class EntityMemberProperties {
  private String name;
  private String description;
  private boolean enabled;
  private boolean payment;

  public EntityMemberProperties() {
    name = "";
    description = "";
    enabled = true;
    payment = false;
  }

  public EntityMemberProperties(String name, String description, boolean enabled, boolean payment) {
    this.name = name;
    this.description = description;
    this.enabled = enabled;
    this.payment = payment;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isPayment() {
    return payment;
  }

  public void setPayment(boolean payment) {
    this.payment = payment;
  }
  
  
}
