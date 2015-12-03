/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.input;

import com.coda.efinance.schemas.common.*;
import com.coda.efinance.schemas.input.*;
import com.coda.efinance.schemas.transaction.*;
import com.sigma.codaclient.codarouter.CodaRouter;
import com.sigma.codaclient.util.DateTools;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class InputService {
  private final CodaRouter router;
  
  public enum PostLocation {BOOKS, INTRAY, ANYWHERE}
  public enum LineSense {DEBIT, CREDIT}
  public enum LineType {SUMMARY, ANALYSIS, TAX, NULL}
  
  public class PostingDocument {
    private final String docCode;
    private final String periodString;
    private String currencyCode = "*";
    private Date docDate = null;
    private String description = "";
    private final ArrayList<PostingLine> lines = new ArrayList<>();
    
    public PostingDocument(String docCode, int year, int period) {
      this.docCode = docCode;    
      periodString = String.format("%d/%d", year, period);
    }
    
    public String getDocCode() {
      return docCode;
    }
    
    public void setCurrency(String code) {
      currencyCode = code;
    }
    
    public String getCurrency() {
      return currencyCode;
    }
    
    public String getPeriod() { 
      return periodString;
    }
    
    public void setDate(Date date) {
      docDate = date;
    }
    
    public Date getDate() {
      return docDate;
    }

    public void setDescription(String description) {
      this.description = description;
    }
    
    public String getDescription() {
      return description;
    } 
    
    public void addLine(PostingLine line) {
      lines.add(line);
    }
    
    public PostingLine addLine(String account, LineSense lineSense, LineType lineType, BigDecimal value) {
      PostingLine line = new PostingLine(account, lineSense, lineType, value);
      lines.add(line);
      return line;
    }
    
    public List<PostingLine> getLines() {
      return lines;
    }
  }

  public class PostingLine {
    private final String accountCode;
    private final LineSense sense;
    private final LineType type;
    private final BigDecimal value;
    private final String[] extRefs = new String[6];
    private String description = "";
    
    public PostingLine(String account, LineSense lineSense, LineType lineType, BigDecimal value) {
      this.accountCode = account;
      this.sense = lineSense;
      this.type = lineType;
      this.value = value;
    }
    
    public String getAccountCode() {
      return accountCode;
    }
    
    public LineSense getSense() {
      return sense;
    }
    
    public LineType getType() {
      return type;
    }
    
    public BigDecimal getValue() {
      return value;
    }
    
    public void setExtRef(int number, String value) {
      extRefs[number] = value;
    }
    
    public String getExtRef(int number) {
      return extRefs[number];
    }
    
    public void setDescription(String description) {
      this.description = description;
    }
    
    public String getDescription() {
      return description;
    }
  }
  
  public InputService(CodaRouter router) {
    this.router = router;
  }
  
  public PostingDocument createDocument(String docCode, int year, int period) {
    return new PostingDocument(docCode, year, period);
  }
  
  // Returns the posted document number
  public int PostDocument(PostLocation loc, String company, PostingDocument doc) throws Exception {
    InputRequest request = CreateInputRequest(loc);
    
    // Create Transaction/Document Header
    Transaction trans = new Transaction();
    Header header = new Header();
    
    // The posting key is a combination of the company code and the document code
    TxnKey docKey = new TxnKey();
    docKey.setCmpCode(company);
    docKey.setCode(doc.getDocCode());
    header.setKey(docKey);

    header.setPeriod(doc.getPeriod());

    header.setTimeStamp((short)0);

    String currencyCode = doc.getCurrency();
    if (!currencyCode.equals("*"))
      header.setCurCode(currencyCode);
    
    Date docDate = doc.getDate();
    if (docDate != null) {
      header.setDate(DateTools.getXMLDate(docDate));
    }

    // TODO:
    // Original Company/Doc/Number*** Yakidoo?
    // User (use login user?)

    // Assign header to transaction
    trans.setHeader(header);
    
    // Create Transaction Lines
    Lines requestLines = new Lines();
    List<PostingLine> docLines = doc.getLines();
    for (int l = 0; l < docLines.size(); l++) {
      // Create new line in request
      Line reqLine = new Line();
      
      PostingLine docLine = docLines.get(l);
      reqLine.setNumber(l+1);
      reqLine.setTimeStamp((short)0);
      reqLine.setAccountCode(docLine.getAccountCode());
      reqLine.setDocValue(docLine.getValue());
      switch (docLine.getType()) {
        case SUMMARY:
          reqLine.setLineType(TypeCtDocLineTypeB.SUMMARY);
          break;
        case ANALYSIS:
          reqLine.setLineType(TypeCtDocLineTypeB.ANALYSIS);
          break;
        case TAX:
          reqLine.setLineType(TypeCtDocLineTypeB.TAX);
          break;
        case NULL:
          reqLine.setLineType(TypeCtDocLineTypeB.NULL);
      }
      switch (docLine.getSense()) {
        case DEBIT:
          reqLine.setLineType(TypeCtDocLineTypeB.SUMMARY);
          break;
        case CREDIT:
          reqLine.setLineType(TypeCtDocLineTypeB.ANALYSIS);
          break;
      }
      reqLine.setLineOrigin(TypeCtDocLineOrigin.DL_ORIG_DEFINED);
      reqLine.setDescription(docLine.getDescription());
      reqLine.setExtRef1(docLine.getExtRef(0));
      reqLine.setExtRef2(docLine.getExtRef(1));
      reqLine.setExtRef3(docLine.getExtRef(2));
      reqLine.setExtRef4(docLine.getExtRef(3));
      reqLine.setExtRef5(docLine.getExtRef(4));
      reqLine.setExtRef6(docLine.getExtRef(5));

      // Add line to transaction
      requestLines.getLine().add(reqLine);
    }
    // Add lines to transaction
    trans.setLines(requestLines);
  
    // Add this transaction to the request
    AddTransactionToRequest(request, trans);
    
    com.coda.efinance.schemas.input.ObjectFactory factory = new com.coda.efinance.schemas.input.ObjectFactory();
    InputResponse response = (InputResponse) router.send(factory.createInputRequest(request), InputResponse.class);
    
    PostResponse postResponse = response.getPost().getResponse().get(0);
    if (postResponse.getStatus() == TypeResponseStatus.FAILED) {
      throw new Exception(postResponse.getReason().getText().get(0).getValue());
    }
    return Integer.parseInt(postResponse.getKey().getNumber().trim());
  }
  
  private InputRequest CreateInputRequest(PostLocation loc) {

    // Create Root Request
    InputRequest inputRequest = new InputRequest();
		inputRequest.setVersion("11.2");
    
    // Create Post Verb
    PostRequestVerb verb = new PostRequestVerb();
    inputRequest.setPost(verb);
    switch(loc) {
      case BOOKS:
        verb.setPostto(TypeCtDocDest.BOOKS);
        break;
      case INTRAY:
         verb.setPostto(TypeCtDocDest.INTRAY);
        break;
      case ANYWHERE:
         verb.setPostto(TypeCtDocDest.ANYWHERE);    
        break;
    }
    
    return inputRequest;
  }
  
  // TODO: Catch null/invalid structure
  private void AddTransactionToRequest(InputRequest request, Transaction trans) {
    // Create Single Post Request
    PostRequest postRequest = new PostRequest();
    postRequest.setTransaction(trans);
    request.getPost().getRequest().add(postRequest);
  }
}