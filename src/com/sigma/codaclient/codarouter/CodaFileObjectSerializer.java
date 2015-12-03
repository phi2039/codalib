/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.codarouter;

import com.sigma.codaclient.services.common.CodaKey;
import com.sigma.codaclient.util.FileTools;
import com.sigma.codaclient.util.RecursiveFileIterator;
import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author clance
 */
public class CodaFileObjectSerializer<T,K extends CodaKey>{
  private final String dataDir;
  private final Class<T> instanceClass;
  private final JAXBContext context;
  private final Unmarshaller unmarshaller;
  private final Marshaller marshaller;
  private final QName instanceQName;

  private class ObjectIterator<T> implements Iterator<T>{
    private final RecursiveFileIterator fileIter;
    private final CodaFileObjectSerializer<T,K> source;
    private T nextObject;

    public ObjectIterator(String basePath, CodaFileObjectSerializer<T,K> source) {
      this.source = source;
      this.fileIter = new RecursiveFileIterator(Paths.get(basePath));
      nextObject = getNext();
    }
    
    private T getNext() {
      if (fileIter.hasNext()) {
        Path nextFile = fileIter.next();
        try {
          return source.readObjectFromFile(nextFile.toFile());
        } catch (Exception ex) { }
      }
      return null;
    }

    @Override
    public boolean hasNext() {
      return (nextObject != null);
    }

    @Override
    public T next() {
      T lastObject = nextObject;
      nextObject = getNext();
      return lastObject;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Cannot remove items from this collection.");
    }
  }
  
  public CodaFileObjectSerializer(String filePath, Class<T> instanceClass) throws Exception {
    this.instanceClass = instanceClass;
    dataDir = FileSystems.getDefault().getPath(filePath, instanceClass.getSimpleName()).toAbsolutePath().toString();
    context = JAXBContext.newInstance(instanceClass);
    marshaller = context.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    unmarshaller = context.createUnmarshaller();
    
    String pkg = instanceClass.getPackage().getName();
    String cls = instanceClass.getName().toLowerCase();
    instanceQName = new QName(pkg, cls);
  }
  
  public Iterable<T> getObjectStream() {
    return new CodaObjectStream<>(new ObjectIterator(dataDir, this));
  }

  public Iterable<T> getObjectStream(String[] filter) {
    Path path = FileSystems.getDefault().getPath(dataDir, filter);
    return new CodaObjectStream<>(new ObjectIterator(path.toString(), this));
  }

  public void writeObjectToFile(T o, K key) throws Exception {
    String fileName = keyToFileName(key);
    if (checkPath(fileName)) {
      writeObjectToFile(o,fileName);
    }
    else
      throw new Exception("Invalid path:" + dataDir);
  }

  public void writeObjectToFile(T o, String destFile) throws Exception {
    // Create a root element to wrap around the provided instance
    JAXBElement<T> root = new JAXBElement<>(instanceQName, instanceClass, o);
    
    // Input/Marshal Handling
    marshaller.marshal(root, Files.newOutputStream(Paths.get(destFile), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE));
  }

  public  T readObjectFromFile(K key) throws Exception {
    String fileName = keyToFileName(key);
    if (Files.exists(Paths.get(fileName))) {
      try {
        return readObjectFromFile(fileName);
      } catch (Exception ex) {
        return null;
      }
    }
    else
      throw new Exception("Invalid file:" + fileName);
  }

  public T readObjectFromFile(String sourceFile) throws Exception {
    File inputFile = new File(sourceFile);
    return readObjectFromFile(inputFile);
  }

  public T readObjectFromFile(File inputFile) throws Exception {
    JAXBElement<T> root = unmarshaller.unmarshal(new StreamSource(inputFile), instanceClass);
    return root.getValue();
  }  
  
  public boolean exists(K key) {
    String fileName = keyToFileName(key);
    File inputFile = new File(fileName);
    return inputFile.exists();
  }
  
  public boolean deleteObject(K key) {
    String fileName = keyToFileName(key);
    File inputFile = new File(fileName);
    return inputFile.delete();
  }
  
  protected boolean checkPath(String fileName) throws Exception {
    File file = new File(fileName);
    String dirName = file.getParent();
    return FileTools.confirmPath(dirName);
  }
  
  protected String keyToFileName(K key) {
    String[] components = key.toStringArray();
    Path sourcePath = FileSystems.getDefault().getPath(dataDir, components);
    String path = sourcePath.toString() + ".xml";
    return path;
  }
}
