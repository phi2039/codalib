/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.matching;

import com.coda.efinance.schemas.association.*;
import com.coda.efinance.schemas.common.*;

import com.coda.efinance.schemas.matching.*;
import com.coda.xml.router.Router;
import com.sigma.codaclient.codarouter.CodaRouter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author clance
 */
public class MatchingService {
  private final CodaRouter router;
  
  public class MatchDefinition {
    private String company;
    private String matchingMaster;
    private MatchingElement matchingElement;
    private String matchCurrency;
    private GregorianCalendar effectiveDate;
    private String settlementPeriod;
    private ArrayList<MatchLine> lines = new ArrayList<>();
    
    private BigDecimal lineTotal = new BigDecimal(0);
    private BigDecimal matchTotal = new BigDecimal(0);
    private BigDecimal remTotal = new BigDecimal(0);
    private int drivingLineIndex = 0;
    
    public MatchDefinition(String company, String matchingMaster, MatchingElement matchingElement, String matchCurrency) {
      this.company = company;
      this.matchingMaster = matchingMaster;
      this.matchingElement = new MatchingElement(matchingElement);
      this.matchCurrency = matchCurrency;
      
      effectiveDate = new GregorianCalendar();
      effectiveDate.setTime(new Date());
      settlementPeriod = String.format("%d/%d", effectiveDate.get(GregorianCalendar.YEAR), effectiveDate.get(GregorianCalendar.MONTH));
    }
    
    public String getCompany() {
      return company;
    }

    public String getMatchingMaster() {
      return matchingMaster;
    }

    public void setMatchingMaster(String matchingMaster) {
      this.matchingMaster = matchingMaster;
    }

    public MatchingElement getMatchingElement() {
      return matchingElement;
    }

    public String getMatchCurrency() {
      return matchCurrency;
    }

    public void setMatchCurrency(String matchCurrency) {
      this.matchCurrency = matchCurrency;
    }

    public GregorianCalendar getEffectiveDate() {
      return effectiveDate;
    }

    public void setEffectiveDate(GregorianCalendar cal) {
      this.effectiveDate = cal;
    }

    public void setEffectiveDate(Date date) {
      this.effectiveDate = new GregorianCalendar();
      this.effectiveDate.setTime(date);
    }
    
    public String getSettlementPeriod() {
      return settlementPeriod;
    }

    public void setSettlementPeriod(String settlementPeriod) {
      this.settlementPeriod = settlementPeriod;
    }

    public ArrayList<MatchLine> getLines() {
      return lines;
    }
    
    public void addLine(MatchLine line) {
      lineTotal = lineTotal.add(line.getLineValue());
      matchTotal = matchTotal.add(line.getMatchValue());
      remTotal = remTotal.add(line.getRemValue());
      lines.add(line);
    }
    
    public MatchLine addLine(String docCode, int docNumber, int docLine, String docCurrency, BigDecimal lineValue, BigDecimal matchValue) {
      MatchLine line = new MatchLine(docCode, docNumber, docLine, docCurrency, lineValue, matchValue);      
      addLine(line);
      return line;
    }
    
    public MatchLine addLine(String docCode, int docNumber, int docLine, String docCurrency, BigDecimal lineValue) {
      MatchLine line = new MatchLine(docCode, docNumber, docLine, docCurrency, lineValue);      
      addLine(line);
      return line;
    }
    
    public MatchLine addLine(String docCode, int docNumber, int docLine, String docCurrency) {
      MatchLine line = new MatchLine(docCode, docNumber, docLine, docCurrency);      
      addLine(line);
      return line;
    }
    
    public void setLines(ArrayList<MatchLine> lines) {
      this.lines = lines;
    }
    
    public BigDecimal getLineTotal() {
      return lineTotal;
    }

    public MatchLine getDrivingLine() {
      // TODO: Is there a better way to detect out of bounds
      if (lines.isEmpty() || drivingLineIndex >= lines.size())
        return null;
      return lines.get(drivingLineIndex);
    }
    
    // TODO:
    // Driving line
    // Multiple currencies
    
  }
  
  public class MatchLine {
    private String docCode;
    private int docNumber;
    private int docLine;
    private String docCurrency;
    private BigDecimal lineValue;
    private BigDecimal matchValue;

    public MatchLine(String docCode, int docNumber, int docLine, String docCurrency) {
      this.docCode = docCode;
      this.docNumber = docNumber;
      this.docLine = docLine;
      this.docCurrency = docCurrency;
    }
    
    public MatchLine(String docCode, int docNumber, int docLine, String docCurrency, BigDecimal lineValue) {
      this(docCode, docNumber, docLine, docCurrency);
      this.lineValue = lineValue;
      this.matchValue = lineValue; // Assume full match by default
    }
    
    public MatchLine(String docCode, int docNumber, int docLine, String docCurrency, BigDecimal lineValue, BigDecimal matchValue) {
      this(docCode, docNumber, docLine, docCurrency, lineValue);
      this.matchValue = matchValue;
    }
    
    public String getDocCode() {
      return docCode;
    }

    public int getDocNumber() {
      return docNumber;
    }

    public int getDocLine() {
      return docLine;
    }

    public String getDocCurrency() {
      return docCurrency;
    }

    public BigDecimal getLineValue() {
      return lineValue;
    }

    public void setLineValue(BigDecimal lineValue) {
      this.lineValue = lineValue;
    }

    public BigDecimal getMatchValue() {
      return matchValue;
    }

    public void setMatchValue(BigDecimal matchValue) {
      this.matchValue = matchValue;
    }

    public BigDecimal getRemValue() {
      return lineValue.subtract(matchValue);
    }    
  }
  
  public class MatchingElement {
    private int level;
    private String code;

    public int getLevel() {
      return level;
    }

    public String getCode() {
      return code;
    }
    
    public MatchingElement() {
      
    }
    
    public MatchingElement(MatchingElement e) {
      this.level = e.level;
      this.code = e.code;
    }
    
    public MatchingElement(int level, String code) {
      this.level = level;
      this.code = code;
    }  
  }
  
  public MatchingService(CodaRouter router) {
    this.router = router;
  }

public void RequestMatch(MatchDefinition def) throws Exception {
    
    // Configure Matching Parameters
    MatchingParams params = new MatchingParams();
    params.setCmpCode(def.getCompany());
    params.setElmLevel((short)def.getMatchingElement().getLevel());
    params.setElmCode(def.getMatchingElement().getCode());
    params.setCurrCode(def.getMatchCurrency());

    GregorianCalendar gc = def.getEffectiveDate();
    XMLGregorianCalendar xmlEffDate = null;
    try {
      xmlEffDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
    } catch (DatatypeConfigurationException dce) {
      throw new Exception(dce);
    }
    params.setDiscDate(xmlEffDate);
    params.setMatchingDate(xmlEffDate);
    params.setLogTitle(this.getClass().getPackage().getName() + " - matching");
    params.setPeriod(def.getSettlementPeriod());
    params.setMatchingMasterCode(def.getMatchingMaster());
    
    // Default to document currency matching
    params.setCurrRule(TypeCtMatchingCurr.DOC_CUR);    
    
    // Create matching groups
    MatchGroups groups = new MatchGroups();
    MatchGroup group = new MatchGroup();
    
    // TODO: Allow multiple Defs/Groups per request
    group.setGroupNum((short)1);
    // TODO: Allow disperse
    group.setCommitType(TypeCtMatchCommitType.STANDARD); // STANDARD = Commit
    
    MatchLines matchLines = new MatchLines();
    PartMatchRequestVerb partVerb = new PartMatchRequestVerb();
    
    for (MatchLine defLine : def.getLines()) {

      // Specify matching lines
      MatchDetails reqLine = new MatchDetails();

      // Specify document line key
      AsoKey lineKey = new AsoKey();
      lineKey.setCmpCode(def.getCompany());
      lineKey.setCode(defLine.getDocCode());
      lineKey.setNumber(String.valueOf(defLine.getDocNumber()));
      lineKey.setLineNumber(defLine.getDocLine());
      lineKey.setLineType(TypeCtAsoLineType.DOCLINE);
      reqLine.setKey(lineKey);
      
      reqLine.setMatchCurrCode(def.getMatchCurrency());
      
      CurrencyDetails currencyDetails = new CurrencyDetails();
      currencyDetails.setCurrCode(defLine.getDocCurrency());
      WhichCurrs which = new WhichCurrs();
      which.getCurr().add(TypeCtMatWhichCur.CURR_DOC);
      currencyDetails.setLineValue(defLine.getLineValue());
      currencyDetails.setMatchValue(defLine.getMatchValue());
      BigDecimal remValue = defLine.getRemValue();
      currencyDetails.setRemValue(remValue);

      if (!remValue.equals(BigDecimal.ZERO)) {
        currencyDetails.setPartMatchRate(new BigDecimal(1.0));
        currencyDetails.setValidPartMatchRate(Boolean.TRUE);

        PartMatchRequest partMatchRequest = new PartMatchRequest();
        partMatchRequest.setMatchingParams(params);
        partMatchRequest.setDetails(reqLine);
        partVerb.getRequest().add(partMatchRequest);
      }
      Currencies currencies = new Currencies();
      currencies.getCurrency().add(currencyDetails);
      reqLine.setCurrencies(currencies);
      
      // Add line to match group
      matchLines.getLine().add(reqLine);
      
    // Set the driving line
      if (defLine == def.getDrivingLine())
        group.setDrivingLine(lineKey);
    }
    group.setLines(matchLines);

    // Add group to group list
    groups.getGroup().add(group);
    
    // Configure commit verb
    CommitRequest commitRequest = new CommitRequest();
    commitRequest.setMatchingParams(params);
    commitRequest.setGroups(groups);
    CommitRequestVerb verb = new CommitRequestVerb();
    verb.getRequest().add(commitRequest);

    // Configure root request
    MatchingRequest matchingRequest = CreateMatchingRequest();
    matchingRequest.setCommit(verb);
    
    // Configure part match verb
    if (partVerb.getRequest().size() > 0)
      matchingRequest.setPartMatch(partVerb);

    // Send request to CODA
    com.coda.efinance.schemas.matching.ObjectFactory factory = new com.coda.efinance.schemas.matching.ObjectFactory();
    MatchingResponse response = (MatchingResponse) router.send(factory.createMatchingRequest(matchingRequest), MatchingResponse.class);
    
    if (response.getCommit().getStatus() == TypeResponseStatus.FAILED) {
      throw new Exception(response.getCommit().getReason().getText().get(0).getValue());
    }
  }

  private MatchingRequest CreateMatchingRequest() {
      MatchingRequest request = new MatchingRequest();
			request.setVersion("6.0");
      
      return request;
  }
}
