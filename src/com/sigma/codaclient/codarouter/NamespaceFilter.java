/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.codarouter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 *
 * @author clance
 */

public class NamespaceFilter implements ContentHandler {

  private ContentHandler next;
  private String addUri;
  boolean addedUri = false;
  
  public NamespaceFilter(ContentHandler next, String uri) {
    this.next = next;
    this.addUri = uri;
  }
  
  @Override
  public void setDocumentLocator(Locator locator) {
    if (next != null)
      next.setDocumentLocator(locator);
  }

  @Override
  public void startDocument() throws SAXException {
    if (next != null)
      next.startDocument();
  }

  @Override
  public void endDocument() throws SAXException {
    if (next != null)
      next.endDocument();
  }

  @Override
  public void startPrefixMapping(String prefix, String uri) throws SAXException {
  if (next != null)
    next.startPrefixMapping(prefix, uri);
  }

  @Override
  public void endPrefixMapping(String prefix) throws SAXException {
    if (next != null)
      next.endPrefixMapping(prefix);
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    System.out.print(String.format("<%s>\n",qName));
    if (next != null)
      next.startElement(addUri, localName, "yr:" + qName, atts);
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    System.out.print(String.format("</%s>\n",qName));
    if (next != null)
      next.endElement(addUri, localName, "yr:" + qName);
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String s = new String(ch, start, length);
    System.out.print(s);
    if (next != null)
      next.characters(ch, start, length);
  }

  @Override
  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    next.ignorableWhitespace(ch, start, length);
  }

  @Override
  public void processingInstruction(String target, String data) throws SAXException {
    next.processingInstruction(target, data);
  }

  @Override
  public void skippedEntity(String name) throws SAXException {
    next.skippedEntity(name);
  }
  
  private void startControlledPrefixMapping() throws  SAXException {
    if (!addUri.equals("") && !addedUri) {
      
      addedUri = true;
    }
  }
}