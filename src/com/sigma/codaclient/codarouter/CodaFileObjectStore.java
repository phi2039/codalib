/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.codarouter;

import com.sigma.codaclient.services.common.CodaBatchOperation;
import com.sigma.codaclient.services.common.CodaCreateOperation;
import com.sigma.codaclient.services.common.CodaDeleteOperation;
import com.sigma.codaclient.services.common.CodaFilter;
import com.sigma.codaclient.services.common.CodaGenericPropertyProvider;
import com.sigma.codaclient.services.common.CodaGetOperation;
import com.sigma.codaclient.services.common.CodaKey;
import com.sigma.codaclient.services.common.CodaObjectResult;
import com.sigma.codaclient.services.common.CodaOperation;
import com.sigma.codaclient.services.common.CodaOperationError;
import com.sigma.codaclient.services.common.CodaOperationResult;
import com.sigma.codaclient.services.common.CodaOperationType;
import com.sigma.codaclient.services.common.CodaPropertyProvider;
import com.sigma.codaclient.services.common.CodaObjectStoreResult;
import com.sigma.codaclient.services.common.CodaUpdateOperation;
import com.sigma.codaclient.util.BiMap;
import com.sigma.codaclient.util.ReflectionTools;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author clance
 * @param <T>
 * @param <K>
 */
public class CodaFileObjectStore<T,K extends CodaKey> implements CodaObjectStore<T,K> {
  private final String filePath;
  private final Class<T> instanceClass;
  private final CodaFileObjectSerializer<T,K> serializer;
  private final String instanceName;
  private final Class storeKeyClass;
  private final Class instanceKeyClass;
  protected final CodaPropertyProvider<T> propertyProvider;
  protected final Constructor serviceKeyConstructor;
  protected final BiMap<Field,Field> instanceKeyMap = new BiMap<>();
  
  public CodaFileObjectStore(String filePath, CodaObjectStoreDefinition<T> storeDef) throws Exception {
    this.filePath = filePath;
    this.instanceClass = storeDef.getInstanceType();
    serializer = new CodaFileObjectSerializer<>(filePath, instanceClass);
    this.instanceName = storeDef.getInstanceName();
    this.storeKeyClass = storeDef.getStoreKeyType();
    this.instanceKeyClass = storeDef.getInstanceKeyType();

    // If a custom property provider is specified, use it
    Class propertyProviderClass = storeDef.getPropertyProviderType();
    if (!CodaGenericPropertyProvider.class.equals(propertyProviderClass)) {
      Constructor<CodaPropertyProvider> ctor = propertyProviderClass.getDeclaredConstructor();
      ctor.setAccessible(true);
      propertyProvider = ctor.newInstance();
    }
    else { // Otherwise, use the generic provider
      // Create a property provider and wire all fields
      CodaGenericPropertyProvider genProvider = new CodaGenericPropertyProvider<>(instanceClass);
      genProvider.wireAll();
      propertyProvider = genProvider;
    }

    String[]serviceKeyNames = storeDef.getStoreKeyFields();
    String[]instanceKeyNames = storeDef.getInstanceKeyFields();
    if (serviceKeyNames.length != instanceKeyNames.length)
      throw new Exception("Mismatched number of key fields");
    
    serviceKeyConstructor = storeKeyClass.getDeclaredConstructor();
    if (serviceKeyConstructor == null)
      throw new Exception("Unable to find default constructor for service key class");
    serviceKeyConstructor.setAccessible(true);
    
    // Key mapping (Instance Key <-> Service Key)
    for (int k = 0; k < serviceKeyNames.length; k++) {
      Field storeField = ReflectionTools.findField(storeKeyClass, serviceKeyNames[k], true);
      Field instanceField = ReflectionTools.findField(instanceClass, instanceKeyNames[k], true);
      instanceKeyMap.add(storeField, instanceField);
    }    
  }          

  @Override
  public void createObject(Map<String, Object> values) throws Exception {
    T instance = newInstance();
    CodaPropertyProvider<T> props = getPropertyProvider();
    props.setDefaults(instance);
    setProperties(instance, values);
    createObject(instance);
  }

  @Override
  public void createObject(T instance) throws Exception {
    K key = getObjectKey(instance);
    if (serializer.exists(key))
      throw new Exception("Object with key " + key.toString() + " already exists");

    serializer.writeObjectToFile(instance, key);
  }

  @Override
  public void deleteObject(K key) throws Exception {
    serializer.deleteObject(key);
  }

  @Override
  public void updateObject(K key, Map<String, Object> values) throws Exception {
    T instance = serializer.readObjectFromFile(key);
    if (instance != null) {
      propertyProvider.setValues(values, instance);
      serializer.writeObjectToFile(instance, key);
    }
    else {
      throw new Exception("Object with key " + key.toString() + " does not exist");
    }
  }

  @Override
  public void updateObject(T instance) throws Exception {
    K key = getObjectKey(instance);
    serializer.writeObjectToFile(instance, key);
  }

  @Override
  public void updateObject(K key, String propertyName, Object value) throws Exception {
    T instance = serializer.readObjectFromFile(key);
    if (instance != null) {
      propertyProvider.setValue(filePath, value, instance);
      serializer.writeObjectToFile(instance, key);
    }
    else {
      throw new Exception("Object with key " + key.toString() + " does not exist");
    }
  }

  @Override
  public T getObject(K key) throws Exception {
    return serializer.readObjectFromFile(key);
  }

  @Override
  public List<T> getObjects(List<K> keys, List<CodaOperationError> errors) throws Exception {
    List<T> instances = new ArrayList<>();
    for (K key : keys) {
      T instance = getObject(key);
      // TODO: Add to error list
      if (instance != null)
        instances.add(instance);
    }
    return instances;
  }

  @Override
  public Collection<CodaKey> listObjects(CodaFilter<K> filter) throws Exception {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public CodaObjectStoreResult<T, K> execute(CodaBatchOperation batch) throws Exception {
    return execute(batch.getOperations());
  }

  @Override
  public CodaObjectStoreResult<T, K> execute(Collection<CodaOperation> ops) throws Exception {
    CodaOperationResult<T,K> addResults = new CodaOperationResult(CodaOperationType.CODA_OP_ADD, false);
    CodaOperationResult<T,K> getResults = new CodaOperationResult(CodaOperationType.CODA_OP_GET, false);
    CodaOperationResult<T,K> updateResults = new CodaOperationResult(CodaOperationType.CODA_OP_UPDATE, false);
    CodaOperationResult<T,K> deleteResults = new CodaOperationResult(CodaOperationType.CODA_OP_DELETE, false);

    for (CodaOperation op : ops) {
      if (op instanceof CodaCreateOperation) {
        CodaCreateOperation createOp = (CodaCreateOperation)op;
        K key = null;
        T instance = null;
        try {
          instance = (T)createOp.getInstance();
          key = getObjectKey(instance);
          createObject(instance);
          addResults.addObjectResult(new CodaObjectResult<>(CodaOperationType.CODA_OP_ADD, key, false, instance));
        } catch (Exception ex) {
          // TODO: Set errors (top-level if key == null)
          CodaObjectResult<T,K> result = new CodaObjectResult<>(CodaOperationType.CODA_OP_ADD, key, true, instance);
          CodaOperationError err = new CodaOperationError(ex.getMessage(), "", "");
          result.setError(err);
          addResults.addObjectResult(result);
        }
      }
      else if (op instanceof CodaUpdateOperation) {
        CodaUpdateOperation updateOp = (CodaUpdateOperation)op;
        K key = null;
        try {
          key = (K)updateOp.getKey();
          updateObject(key, updateOp.getProperties());
          updateResults.addObjectResult(new CodaObjectResult<>(CodaOperationType.CODA_OP_UPDATE, key, false, (T)null));
        } catch (Exception ex) {
          // TODO: Set errors (top-level if key == null)
          CodaObjectResult<T,K> result = new CodaObjectResult<>(CodaOperationType.CODA_OP_UPDATE, key, true, (T)null);
          CodaOperationError err = new CodaOperationError(ex.getMessage(), "", "");
          result.setError(err);
          updateResults.addObjectResult(result);
        }
      }
      else if (op instanceof CodaDeleteOperation) {
        CodaDeleteOperation deleteOp = (CodaDeleteOperation)op;
        K key = null;
        try {
          key = (K)deleteOp.getKey();
          deleteObject(key);
          deleteResults.addObjectResult(new CodaObjectResult<>(CodaOperationType.CODA_OP_DELETE, key, false, (T)null));          
        } catch (Exception ex) {
          // TODO: Set errors (top-level if key == null)
          CodaObjectResult<T,K> result = new CodaObjectResult<>(CodaOperationType.CODA_OP_DELETE, key, true, (T)null);
          CodaOperationError err = new CodaOperationError(ex.getMessage(), "", "");
          result.setError(err);
          deleteResults.addObjectResult(result);
        }
      }
      else if (op instanceof CodaGetOperation) {
        CodaGetOperation getOp = (CodaGetOperation)op;
        K key = null;
        T instance = null;
        try {
          key = (K)getOp.getKey();
          instance = getObject(key);
          getResults.addObjectResult(new CodaObjectResult<>(CodaOperationType.CODA_OP_GET, key, false, instance));
        } catch (Exception ex) {
          // TODO: Set errors (top-level if key == null)
          CodaObjectResult<T,K> result = new CodaObjectResult<>(CodaOperationType.CODA_OP_GET, key, true, instance);
          CodaOperationError err = new CodaOperationError(ex.getMessage(), "", "");
          result.setError(err);
          getResults.addObjectResult(result);
        }
      }
    }

    CodaObjectStoreResult<T,K> results = new CodaObjectStoreResult();
    results.setOperation(CodaOperationType.CODA_OP_ADD, addResults);
    results.setOperation(CodaOperationType.CODA_OP_GET, getResults);
    results.setOperation(CodaOperationType.CODA_OP_UPDATE, updateResults);
    results.setOperation(CodaOperationType.CODA_OP_DELETE, deleteResults);
    return results; 
  }

  @Override
  public void setProperties(T instance, Map<String, Object> values) throws Exception {
    propertyProvider.setValues(values, instance);
  }

  @Override
  public void clearProperties(T instance, List<String> names) throws Exception {
    propertyProvider.clearValues(names, instance);
  }

  @Override
  public K getObjectKey(T instance) throws Exception {
    return instanceKeyToServiceKey(instance);
  }

  protected K instanceKeyToServiceKey(T instance) throws Exception {
    K dest = (K)serviceKeyConstructor.newInstance();
    mapKeys(instance, dest, instanceKeyMap.getBackward());
    return dest;
  }
  
  protected void mapKeys(Object source, Object dest, Map<Field,Field> map) throws Exception {
    for (Map.Entry<Field,Field> entry : map.entrySet()) {
      Object value = entry.getKey().get(source);
      entry.getValue().set(dest, value);
    }
  }
  
  @Override
  public CodaPropertyProvider<T> getPropertyProvider() throws Exception {
    return propertyProvider;
  }
  
  public T newInstance() {
    try {
      return instanceClass.newInstance();
    } catch (InstantiationException | IllegalAccessException ex) {
      return null;
    }    
  }
  
  @Override
  public Iterable<T> getObjectStream() {
    return serializer.getObjectStream();
  }

  @Override
  public Iterable<T> getObjectStream(String[] filter) {
    return serializer.getObjectStream(filter);
  }

  @Override
  public K createKey(Map<String,Object> fields) throws Exception {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
