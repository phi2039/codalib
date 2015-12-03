/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.codarouter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderAdapter;

/**
 *
 * @author clance
 */
// SAX1 <-> SAX2 Adapter for CODA Router
class RequestEventProducer implements com.coda.xml.router.EventProducer
{
  private DocumentHandler handler;
  private final Marshaller marshaller;
  private final Object request;

  public RequestEventProducer(Marshaller marshaller, Object request) {
    this.marshaller = marshaller;
    this.request = request;
  }

  @Override
  public void setDocumentHandler(DocumentHandler handler)
  {
    this.handler = handler;
  }

  @Override
  public void start()
    throws SAXException
  {
    try
    {
      XMLReaderAdapter adapter = new XMLReaderAdapter();
      adapter.setDocumentHandler(handler);

      marshaller.marshal(request, adapter);
    }
    catch (JAXBException e)
    {
      throw new SAXException(e);
    }
  }
} 