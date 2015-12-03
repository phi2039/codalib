/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.company;

import com.coda.efinance.schemas.company.Company;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author clance
 */
  public final class CompanyBalance {
    private CompanyBalanceType type;
    private boolean required;
    private Short elmLevel;
    private List<Short> reportingBasis;
    
    public CompanyBalance(CompanyBalanceType type) {
      this.type = type;
      required = false;
      elmLevel = 0;
      reportingBasis = new ArrayList<>();
    }

    public CompanyBalance(CompanyBalanceType type, boolean required, Short elmLevel, List<Short> reportingBasis) {
      this(type);
      this.type = type;
      this.required = required;
      this.elmLevel = elmLevel;
      if (reportingBasis != null)
        this.reportingBasis.addAll(reportingBasis);
    }    
    
    public CompanyBalance(CompanyBalanceType type, Company company) throws Exception{
      this(type);

      reportingBasis.clear();
      switch(type) {
      case HOME_ACTUALS:
        required = company.isMaintActHome();
        reportingBasis.addAll(company.getActHomeList().getRepBasis());
        break;
      case DUAL_ACTUALS:
        required = company.isMaintActDual();
        reportingBasis.addAll(company.getActDualList().getRepBasis());
        break;
      case FOREIGN_ACTUALS:
        required = company.isMaintActFgn();
        reportingBasis.addAll(company.getActFgnList().getRepBasis());
        break;
      case ELEMENT_ACTUALS:
        required = company.isMaintActElement();
        reportingBasis.addAll(company.getActElmList().getRepBasis());
        elmLevel = company.getMaintActLevel();
        break;
      case HOME_TURNOVER:
        required = company.isMaintTurnHome();
        reportingBasis.addAll(company.getTurnHomeList().getRepBasis());
        break;
      case DUAL_TURNOVER:
        required = company.isMaintTurnDual();
        reportingBasis.addAll(company.getTurnDualList().getRepBasis());
        break;
      case FOREIGN_TURNOVER:
        required = company.isMaintTurnFgn();
        reportingBasis.addAll(company.getTurnFgnList().getRepBasis());
        break;
      case ELEMENT_TURNOVER:
        required = company.isMaintTurnElement();
        reportingBasis.addAll(company.getTurnElmList().getRepBasis());
        elmLevel = company.getMaintTurnLevel();
        break;
      case QUANTITIES:
        required = company.isMaintQuantities();
        reportingBasis.addAll(company.getQuantityList().getRepBasis());
        break;
      default:
        throw new Exception ("Unknown or unsupported balance type");
      }
    }

    public CompanyBalanceType getType() {
      return type;
    }

    public void setType(CompanyBalanceType type) {
      this.type = type;
    }

    public boolean isRequired() {
      return required;
    }

    public void setRequired(boolean required) {
      this.required = required;
    }

    public int getElmLevel() {
      return elmLevel;
    }

    public void setElmLevel(Short elmLevel) {
      this.elmLevel = elmLevel;
    }

    public List<Short> getReportingBasis() {
      List<Short> groups = new ArrayList<>(this.reportingBasis);
      return groups;
    }

    public void setReportingBasis(List<Short> basis) {
      this.reportingBasis.clear();
      this.reportingBasis.addAll(basis);
    }
    
    public void set(Company company) throws Exception {
      switch(type) {
      case HOME_ACTUALS:
        company.setMaintActHome(required);
        company.getActHomeList().getRepBasis().clear();
        company.getActHomeList().getRepBasis().addAll(reportingBasis);
        break;
      case DUAL_ACTUALS:
        company.setMaintActDual(required);
        company.getActDualList().getRepBasis().clear();
        company.getActDualList().getRepBasis().addAll(reportingBasis);
        break;
      case FOREIGN_ACTUALS:
        company.setMaintActFgn(required);
        company.getActFgnList().getRepBasis().clear();
        company.getActFgnList().getRepBasis().addAll(reportingBasis);
        break;
      case ELEMENT_ACTUALS:
        company.setMaintActElement(required);
        company.setMaintActLevel(elmLevel);
        company.getActElmList().getRepBasis().clear();
        company.getActElmList().getRepBasis().addAll(reportingBasis);
        break;
      case HOME_TURNOVER:
        company.setMaintTurnHome(required);
        company.getTurnHomeList().getRepBasis().clear();
        company.getTurnHomeList().getRepBasis().addAll(reportingBasis);
        break;
      case DUAL_TURNOVER:
        company.setMaintTurnDual(required);
        company.getTurnDualList().getRepBasis().clear();
        company.getTurnDualList().getRepBasis().addAll(reportingBasis);
        break;
      case FOREIGN_TURNOVER:
        company.setMaintTurnFgn(required);
        company.getTurnFgnList().getRepBasis().clear();
        company.getTurnFgnList().getRepBasis().addAll(reportingBasis);
        break;
      case ELEMENT_TURNOVER:
        company.setMaintTurnElement(required);
        company.setMaintTurnLevel(elmLevel);
        company.getTurnElmList().getRepBasis().clear();
        company.getTurnElmList().getRepBasis().addAll(reportingBasis);
        break;
      case QUANTITIES:
        company.setMaintQuantities(required);
        company.getQuantityList().getRepBasis().clear();
        company.getQuantityList().getRepBasis().addAll(reportingBasis);
        break;
      default:
        throw new Exception ("Unknown or unsupported balance type");
      }
    }
    
    public static void clear(Company company) {
      company.setMaintActHome(false);
      company.setActHomeList(null);
      company.setMaintActDual(false);
      company.setActDualList(null);
      company.setMaintActFgn(false);
      company.setActFgnList(null);
      company.setMaintActElement(false);
      company.setMaintActLevel((short)0);
      company.setActElmList(null);
      company.setMaintTurnHome(false);
      company.setTurnHomeList(null);
      company.setMaintTurnDual(false);
      company.setTurnDualList(null);
      company.setMaintTurnFgn(false);
      company.setTurnFgnList(null);
      company.setMaintTurnElement(false);
      company.setMaintTurnLevel((short)0);
      company.setTurnElmList(null);
      company.setMaintQuantities(false);
      company.setQuantityList(null);
    }    
  }
