/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

import com.coda.efinance.schemas.common.Reason;
import com.coda.efinance.schemas.common.Response;
import com.coda.efinance.schemas.common.ResponseVerb;
import com.coda.efinance.schemas.common.ServiceRequest;
import com.coda.efinance.schemas.common.ServiceResponse;
import com.coda.efinance.schemas.common.TypeResponseStatus;
import com.sigma.codaclient.codarouter.CodaFileObjectSerializer;
import com.sigma.codaclient.codarouter.CodaLogicalServerType;
import com.sigma.codaclient.codarouter.CodaObjectStore;
import com.sigma.codaclient.codarouter.CodaRouter;
import com.sigma.codaclient.util.FileTools;
import com.sigma.codaclient.util.StringTools;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author clance
 * @param <T> Instance type
 * @param <K> Key type (must extend CodaKey)
 */
public abstract class CodaService <T, K extends CodaKey> implements CodaObjectStore<T,K>{
  
  protected final CodaRouter router;
  protected final String version;
  protected final CodaLogicalServerType lsvType;
  
  public CodaService(CodaRouter router, String version, CodaLogicalServerType lsvType) {
    this.router = router;
    this.version = version;
    this.lsvType = lsvType;
  }
  
  public CodaLogicalServerType getLsvType() {
    return lsvType;
  }
  
  @Override
  public void createObject(Map<String,Object> values) throws Exception {
    
    ServiceRequest request = newRequest();
    request.setVersion(version);

    addCreateEntry(values, request);
    
    try {
      executeSingle(CodaOperationType.CODA_OP_ADD, request);
    } catch(CodaOperationException cex) {
      throw new Exception(cex.getCause());
    }
  }
  
  @Override
  public void createObject(T instance) throws Exception {
    
    ServiceRequest request = newRequest();
    request.setVersion(version);

    addCreateEntry(instance, request);
    
    try {
      executeSingle(CodaOperationType.CODA_OP_ADD, request);
    } catch(CodaOperationException cex) {
      throw new Exception(cex.getCause());
    }
  }
  
  protected void addCreateEntry(Map<String,Object> values, ServiceRequest request) throws Exception {
    T instance = newInstance();
    CodaPropertyProvider<T> props = getPropertyProvider();
    props.setDefaults(instance);
    setProperties(instance, values);
    
    addCreateEntry(instance, request);
  }
  
  protected void addCreateEntry(CodaCreateOperation op, ServiceRequest request) throws Exception {
    Object instance = op.getInstance();
    if (instance == null)
      addCreateEntry(op.getProperties(), request);
    else
      addCreateEntry((T)instance, request);
  }
  
  @Override
  public void deleteObject(K key) throws Exception {
    
    ServiceRequest request = newRequest();
    request.setVersion(version);

    addDeleteEntry(key, request);
    
    try {
      executeSingle(CodaOperationType.CODA_OP_DELETE, request);
    } catch(CodaOperationException cex) {
      throw new Exception(cex.getCause());
    }
  }
   
  protected void addDeleteEntry(CodaDeleteOperation<K> op, ServiceRequest request) throws Exception {
    addDeleteEntry(op.getKey(), request);
  }
  
  @Override
  public void updateObject(K key, Map<String,Object> values) throws Exception {

    // Get current property values
    T instance = getObject(key);
    
    // Update values
    CodaPropertyProvider<T> props = getPropertyProvider();
    props.setDefaults(instance);
    setProperties(instance, values);
    
    updateObject(instance);
  }
  
  @Override
  public void updateObject(T instance) throws Exception {
    ServiceRequest request = newRequest();
    request.setVersion(version);

    addUpdateEntry(instance, request);
    
    try {
      executeSingle(CodaOperationType.CODA_OP_UPDATE, request);
    } catch(CodaOperationException cex) {
      throw new Exception(cex.getCause());
    }
  }  
  
  @Override
  public void updateObject(K key, String propertyName, Object value) throws Exception {
    
    Map<String,Object> values = new HashMap<>();
    values.put(propertyName, value);  

    updateObject(key, values);
  }
  
  @Override
  public T getObject(K key) throws Exception {

    ServiceRequest request = newRequest();
    request.setVersion(version);

    addGetEntry(key, request);

    try {
      return executeSingle(CodaOperationType.CODA_OP_GET, request);
    } catch(CodaOperationException cex) {
      throw new Exception(cex.getCause());
    }
  }
  
  @Override
  public List<T> getObjects(List<K> keys, List<CodaOperationError> errors) throws Exception {

    ServiceRequest request = newRequest();
    request.setVersion(version);

    for (K key : keys)
      addGetEntry(key, request);

    ServiceResponse updateResponse = sendRequest(request);    
    CodaOperationResult<T,K> results = processServiceResponse(updateResponse).getOperation(CodaOperationType.CODA_OP_GET);
    if (results.isFailed())
      throw new Exception("Failed to retrieve specified masters:" + results.getError().toString());

    List<T> objects = new ArrayList<>();
    for (CodaObjectResult<T,K> result: results.getObjectResults()) {
      if (!result.isFailed())
        objects.add(result.getObject());
      else {
        if (errors != null)
          errors.add(result.getError());
      }
    }
    return objects;
  }  

  public List<T> getObjects(List<K> keys) throws Exception {
    return getObjects(keys, null);
  }
  
  protected void addGetEntry(CodaGetOperation<K> op, ServiceRequest request) throws Exception {
    addGetEntry(op.getKey(), request);
  }  
  
  @Override
  public Collection<CodaKey> listObjects(CodaFilter<K> filter) throws Exception {
    throw new UnsupportedOperationException("Operation not yet supported");
  }

  @Override
  public CodaObjectStoreResult<T,K> execute(CodaBatchOperation batch) throws Exception {
    return execute(batch.getOperations());
  }  
  
  @Override
  public CodaObjectStoreResult<T,K> execute(Collection<CodaOperation> ops) throws Exception {
    ServiceRequest request = newRequest();
    request.setVersion(version);
    
    Map<String,CodaUpdateOperation> updateOps = new HashMap<>();
    for (CodaOperation op : ops) {
      if (op instanceof CodaCreateOperation)
        addCreateEntry((CodaCreateOperation)op, request);
      else if (op instanceof CodaUpdateOperation) {
        // Add a "get" entry to retrieve the current values
        K key = ((CodaUpdateOperation<K>)op).getKey();
        addGetEntry(new CodaGetOperation<>(key), request);
        // Add to map so we can look it up later
        updateOps.put(key.toString(),(CodaUpdateOperation<K>)op);
      }
      else if (op instanceof CodaDeleteOperation)
        addDeleteEntry((CodaDeleteOperation<K>)op, request);
      else if (op instanceof CodaGetOperation)
        addGetEntry((CodaGetOperation<K>)op, request);    
    }
    
    ServiceResponse response = sendRequest(request);
    CodaObjectStoreResult<T,K> result = processServiceResponse(response);
    if (updateOps.isEmpty())
      return result;

    // Get the "get" results from the last request
    CodaOperationResult<T,K> getResults = result.getOperation(CodaOperationType.CODA_OP_GET);
    // Create a new request to handle updates
    request = newRequest();
    for (CodaUpdateOperation<K> op : updateOps.values()) {
      CodaObjectResult<T,K> getResult = getResults.getObjectResult(op.getKey().toString());
      if (getResult == null)
        throw new Exception("To be handled"); // Create error collection and add to result collection
      T instance = getResult.getObject();
      if (instance == null)
        throw new Exception("To be handled"); // Create error collection and add to result collection
      // Update the specified properties
      setProperties(instance, op.getProperties());
      // Add an update operation to the request
      addUpdateEntry(instance, request);
    }
    
    // Remove "get" results added earlier
    for (CodaUpdateOperation<K> op : updateOps.values())
      getResults.removeObjectResult(op.getKey().toString());
    
    ServiceResponse updateResponse = sendRequest(request);
    CodaObjectStoreResult<T,K> updateResult = processServiceResponse(updateResponse);
    result.merge(updateResult);

    return result; 
  }

  @Override
  public void setProperties(T instance, Map<String,Object> values) throws Exception {
    CodaPropertyProvider<T> props = getPropertyProvider();
    props.setValues(values, instance);
  }
  
  @Override
  public void clearProperties(T instance, List<String> names) throws Exception {
    CodaPropertyProvider<T> props = getPropertyProvider();
    props.clearValues(names, instance);
  }
    
  @Override
  public abstract CodaPropertyProvider<T> getPropertyProvider() throws Exception; 
  @Override
  public abstract K getObjectKey(T instance) throws Exception;
  @Override
  public abstract T newInstance();
  protected abstract ServiceRequest newRequest();
  protected abstract void addCreateEntry(T instance, ServiceRequest request) throws Exception;
  protected abstract void addDeleteEntry(K key, ServiceRequest request) throws Exception;
  protected abstract void addGetEntry(K key, ServiceRequest request) throws Exception;
  protected abstract void addUpdateEntry(T instance, ServiceRequest request) throws Exception;
  protected abstract ServiceResponse sendRequest(ServiceRequest request) throws Exception;
  protected abstract ResponseVerb getResponseVerb(CodaOperationType operationType, ServiceResponse response) throws Exception;
  protected abstract List<Response> getObjectResponses(ResponseVerb verb) throws Exception;
  protected abstract K getResponseKey(Response response) throws Exception;
  protected abstract T getResponseObject(Response response) throws Exception;
  
  protected T executeSingle(CodaOperationType operationType, ServiceRequest request) throws CodaOperationException, Exception {

    ServiceResponse response = sendRequest(request);
    CodaObjectStoreResult<T,K> svcResult = processServiceResponse(response);
    CodaOperationResult<T,K> opResult = svcResult.getOperation(operationType);
    if (opResult == null)
      throw new Exception("Failed to retrieve operation");
    
    if (opResult.isFailed())
      throw new CodaOperationException(opResult.getError());
    
    if (opResult.getObjectResults().isEmpty())
      throw new Exception("Failed to retrieve result");
    
    Iterator<CodaObjectResult<T,K>> iter = opResult.getObjectResults().iterator();
    CodaObjectResult<T,K> objResult = iter.next();
    if (objResult.isFailed())
        throw new CodaOperationException(objResult.getError());
    
    return objResult.getObject();
  }
  
  protected CodaObjectStoreResult<T,K> processServiceResponse(ServiceResponse response) throws Exception {
    
    CodaObjectStoreResult<T,K> result = new CodaObjectStoreResult();

    for (CodaOperationType operationType : CodaOperationType.values()) {
      ResponseVerb verb = getResponseVerb(operationType, response);
      if (verb != null)
        result.setOperation(operationType, processResponseVerb(operationType, verb));
    }
    return result;
  }
  
  protected CodaOperationResult<T,K> processResponseVerb(CodaOperationType operationType, ResponseVerb verb) throws Exception {
    boolean failed = (verb.getStatus() == TypeResponseStatus.FAILED);
    CodaOperationResult<T,K> opResult = new CodaOperationResult<>(operationType, failed);
    if (failed)
      opResult.setError(reasonToError(verb.getReason()));
    else {
      List<Response> responses = getObjectResponses(verb);
      for (Response response : responses)
        opResult.addObjectResult(processResponse(operationType, response));
    }
    return opResult;
  }
  
  protected CodaObjectResult<T,K> processResponse(CodaOperationType operationType, Response response) throws Exception {
    
    boolean failed = (response.getStatus() == TypeResponseStatus.FAILED);
    K key = getResponseKey(response);
    T object = getResponseObject(response);
    CodaObjectResult<T,K> result = new CodaObjectResult<>(operationType, key, failed, object);
    if (failed)
      result.setError(reasonToError(response.getReason()));
    return result;
  }
  
  protected CodaOperationError reasonToError(Reason reason) {
    String text = StringTools.joinAsString(reason.getText(), "\n");
    String path = StringTools.joinString(reason.getPath(), "\n");
    String hint = StringTools.joinString(reason.getHint(), "\n");
    return new CodaOperationError(text, path, hint);
  }
  
  public void exportObjects(String destPath, List<K> keys, Map<String,Object> newValues, int batchSize) throws Exception {
    if (FileTools.confirmPath(destPath)) {
      // Get information about the object class for this service
      ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
      Class objectClass = (Class) parameterizedType.getActualTypeArguments()[0];
      
      CodaFileObjectSerializer<T,K> destStore = new CodaFileObjectSerializer<>(destPath,objectClass);
      List<K> keyBatch = new ArrayList<>();
      Iterator<K> iter = keys.iterator();
      while (iter.hasNext()) {
        K key = iter.next();
        keyBatch.add(key);
        // If the batch is full, or this is the last item, fetch a batch of objects and write them
        if (keyBatch.size() >= batchSize || !iter.hasNext()) {
          List<T> objects = getObjects(keyBatch);
          for (T object : objects) {
            // If new values were provided for some properties, write them now (values are the same for every object)
            if (newValues != null)
              setProperties(object, newValues);
            // Write the object to a file
            destStore.writeObjectToFile(object, getObjectKey(object));
          }
          keyBatch.clear(); // Clear batch list and start again
        }
      }
    }    
  }
  
  public void exportObjects(String destPath, List<K> keys) throws Exception {
    exportObjects(destPath, keys, null, 10000);
  }  

  public void exportObjects(String destPath, List<K> keys, Map<String, Object> newValues) throws Exception {
    exportObjects(destPath, keys, newValues, 10000);
  }  

  public void exportObjects(String destPath, List<K> keys, int batchSize) throws Exception {
    exportObjects(destPath, keys, null, batchSize);
  }  

  @Override
  public K createKey(Map<String,Object> fields) throws Exception {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Iterable<T> getObjectStream() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Iterable<T> getObjectStream(String[] filter) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }  
}
