/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.codarouter;

import com.sigma.codaclient.services.common.CodaBatchOperation;
import com.sigma.codaclient.services.common.CodaFilter;
import com.sigma.codaclient.services.common.CodaKey;
import com.sigma.codaclient.services.common.CodaOperation;
import com.sigma.codaclient.services.common.CodaOperationError;
import com.sigma.codaclient.services.common.CodaPropertyProvider;
import com.sigma.codaclient.services.common.CodaObjectStoreResult;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author clance
 */
public interface CodaObjectStore<T,K extends CodaKey> {
  public T newInstance();
  public void createObject(Map<String,Object> values) throws Exception;
  public void createObject(T instance) throws Exception;
//  public void createObject(T instance, boolean overwrite) throws Exception;
  public void deleteObject(K key) throws Exception;
  public void updateObject(K key, Map<String,Object> values) throws Exception;
  public void updateObject(T instance) throws Exception;
  public void updateObject(K key, String propertyName, Object value) throws Exception;
  public T getObject(K key) throws Exception;
  public List<T> getObjects(List<K> keys, List<CodaOperationError> errors) throws Exception;
  public Collection<CodaKey> listObjects(CodaFilter<K> filter) throws Exception;
  public CodaObjectStoreResult<T,K> execute(CodaBatchOperation batch) throws Exception;
  public CodaObjectStoreResult<T,K> execute(Collection<CodaOperation> ops) throws Exception;
  public void setProperties(T instance, Map<String,Object> values) throws Exception;
  public void clearProperties(T instance, List<String> names) throws Exception;
  public K getObjectKey(T instance) throws Exception;
  public K createKey(Map<String,Object> fields) throws Exception;
  public CodaPropertyProvider<T> getPropertyProvider() throws Exception; 
  public Iterable<T> getObjectStream();
  public Iterable<T> getObjectStream(String[] filter);
}
