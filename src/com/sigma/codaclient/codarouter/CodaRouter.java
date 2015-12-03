/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.codarouter;

import com.coda.xml.router.*;
import com.sigma.codaclient.common.CodaClientProperties;
import com.sigma.codaclient.common.CodaUserCredentials;
import java.util.Properties;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.ParserAdapter;

/**
 *
 * @author clance
 */
public class CodaRouter {
  private Router router = null;
  private Authentication auth = null;
  private boolean logToStdout = false;
  private boolean logToFile = false;
  String username;
  String password;
  private int licenseSlot;
  private String companyCode;
  private CodaClientProperties profile = null;
  
  public CodaRouter (CodaClientProperties profile, boolean stdout, boolean file) {
    this.profile = profile;
    logToStdout = stdout;
    logToFile = file; 
}

  public CodaRouter (CodaClientProperties profile) {
    this(profile, false, false);
  }

  public void connect(CodaLogicalServerType lsvType) throws Exception {
    if (profile != null) {
      String lsvName = "";
      switch (lsvType) {
        case ASSETS:
          lsvName = profile.getAssetsLsvName();
          break;
        case FINANCIALS:
          lsvName = profile.getFinancialsLsvName();
          break;
        case FRAMEWORK:
          lsvName = profile.getFrameworkLsvName();
          break;
        default:
          throw new Exception("Unknown logical server type");
      }
      if (!lsvName.isEmpty())
        connect(profile.getNsvHostName(), profile.getNsvPort(), lsvName);
      else
        throw new Exception("Logical server name not found");
      
    }
    else
      throw new Exception("No profile available");
  }
  
  public void connect(String nameServer, Integer nameServerPort, String logicalServer) throws Exception {
    try {
      router = new Router(nameServer, nameServerPort, logicalServer);
    } catch (RouterException rex) {
      throw (Exception) rex;
    }
  }
  
  public CodaLogicalServerType getLsvType() {
    if (router == null)
      return CodaLogicalServerType.NONE;
    String asvType = router.getAppServerType();
    switch(asvType) {
      case "Finance":
        return CodaLogicalServerType.FINANCIALS;
      case "assets":
        return CodaLogicalServerType.ASSETS;
      case "common":
        return CodaLogicalServerType.FRAMEWORK;
      default:
        return CodaLogicalServerType.NONE;
    }
  }
  
  public void authenticate(String username, String password, String companyCode, int licenseSlot) throws Exception {
    this.username = username;
    this.password = password;
    this.licenseSlot = licenseSlot;
    this.companyCode = companyCode;
    try {
      if (companyCode.equals("*"))
        auth = new Authentication(username, password, licenseSlot);
      else
        auth = new Authentication(username, password, licenseSlot, companyCode);
      router.authenticate(auth);
    } catch (RouterException rex) {
      throw (Exception) rex;
    }
  }

  public void authenticate(String username, String password, String companyCode) throws Exception {
    authenticate(username, password, companyCode, 1);
  }

  public void authenticate(CodaUserCredentials user, String companyCode, int licenseSlot) throws Exception {
    authenticate(user.getUserName(),user.getPassword(),companyCode,licenseSlot);    
  }  

  public void authenticate(CodaUserCredentials user, int licenseSlot) throws Exception {
    authenticate(user.getUserName(),user.getPassword(),user.getDefaultCompany(),licenseSlot);    
  }

  public void authenticate(CodaUserCredentials user, String companyCode) throws Exception {
    authenticate(user,companyCode,1);    
  }  

  public void authenticate(CodaUserCredentials user) throws Exception {
    authenticate(user,1);
  }  
  
  public void authenticate(String companyCode) throws Exception {
    if (profile != null) {
      CodaUserCredentials user = profile.getCodaUser();
      authenticate(user.getUserName(),user.getPassword(),companyCode,profile.getLicenseSlot());
      }
    else
      throw new Exception("No profile available");
  }
  
  public void authenticate() throws Exception {
    if (profile != null) {
      authenticate(profile.getCodaUser().getDefaultCompany());
    }
    else
      throw new Exception("No profile available");
  }  
  
  public String getUsername() {
    return auth.getUser();
  }
  
  public String getCompanyCode() {
    return companyCode;
  }
  
  public void changeCompany(String companyCode) throws Exception {
    authenticate(username, password, companyCode, licenseSlot);
  }

  public void changeLsv(CodaLogicalServerType lsvType) throws Exception {
    connect(lsvType);
    if (auth != null)
      authenticate(username, password, companyCode, licenseSlot);
  }  
  
  public static void setNamespaces(boolean include) {
      Properties props = new Properties();
      props.setProperty("com.coda.xml.router.responseNamespaces",include ? "True" : "False"); 
      Router.setProperties(props);
  }
  
  public void setLogging(boolean stdout, boolean file) {
    logToStdout = stdout;
    logToFile = file; 
  }
  
  public boolean getLogToStdout() {
    return logToStdout;
  }
  
  public boolean getLogToFile() {
    return logToFile;
  }
  
	public Object send(JAXBElement request, Class responseClass) throws Exception
	{
    if (router == null || auth == null) {
      throw new Exception("No session");
    }
    
    // Create marshalling context
    JAXBContext context = JAXBContext.newInstance(responseClass.getPackage().getName());

    // Input/Marshal Handling
    Marshaller marshaller = context.createMarshaller();
    if (logToStdout) {
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      System.out.print("Request: ");
      marshaller.marshal(request, System.out);
    }
    
    RequestEventProducer requestProducer = new RequestEventProducer(marshaller, request); // SAX2 <-> SAX1 Adapter (ContentHandler <- DocumentHandler)

    // Output/Unmarshal Handling
    ParserAdapter adapter = new ParserAdapter(); // SAX2 <-> SAX1 Adapter (ContentHandler -> DocumentHandler)
    Unmarshaller unmarshaller = context.createUnmarshaller();
    UnmarshallerHandler umHandler = unmarshaller.getUnmarshallerHandler();
//    NamespaceFilter filter = new NamespaceFilter(null, "");
//    ContentHandler contentHandler = filter;
    ContentHandler contentHandler = umHandler;
    adapter.setContentHandler(contentHandler);
    
    // Send request to CODA and retrieve response
    try {
      router.send(auth.getToken(), requestProducer, adapter);
    } catch (Exception ex) {
      throw ex;
    }
    JAXBElement response = (JAXBElement) umHandler.getResult();

    if (logToStdout) {
      System.out.print("Response: ");
      marshaller.marshal(response, System.out);
    }

    return response.getValue();
	}
}
