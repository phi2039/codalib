/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.common;

import com.coda.efinance.schemas.common.Request;
import com.coda.efinance.schemas.common.RequestVerb;
import com.coda.efinance.schemas.common.Response;
import com.coda.efinance.schemas.common.ResponseVerb;
import com.coda.efinance.schemas.common.ServiceRequest;
import com.coda.efinance.schemas.common.ServiceResponse;
import com.sigma.codaclient.codarouter.CodaRouter;
import com.sigma.codaclient.util.BiMap;
import com.sigma.codaclient.util.ReflectionTools;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlSchema;

/**
 *
 * @author clance
 */
public class CodaGenericService<T,K extends CodaKey> extends CodaService<T,K>{

  protected class ServiceOperation{
    public String name;
    public Class<?> requestVerbClass;
    public Class<?> requestClass;
    public Class<?> responseVerbClass;
    public Class<?> responseClass;
    public Method getRequestVerbMethod;
    public Method setRequestVerbMethod;
    public Method getRequestListMethod;
    public Method setRequestInstanceMethod;
    public Method setRequestKeyMethod;
    public Method getResponseVerbMethod;
    public Method getResponseListMethod;
    public Method getResponseInstanceMethod;
    public BiMap<Field,Field> responseKeyMap = new BiMap<>();
  }
  
  protected final String serviceName;
  protected final String instanceName;
  protected final Class<T> instanceClass;
  protected final Class<?> instanceKeyClass;
  protected final Class<K> serviceKeyClass;
  protected final Constructor serviceKeyConstructor;
  protected final BiMap<Field,Field> requestKeyMap = new BiMap<>();
  protected final BiMap<Field,Field> instanceKeyMap = new BiMap<>();
  protected final Class<?> serviceRequestClass;
  protected final Class<?> serviceResponseClass;
  protected final Package servicePackage;
  protected final CodaPropertyProvider<T> propertyProvider;
  protected final String serviceNamespace;
  
  protected final ServiceOperation addOperation;
  protected final ServiceOperation getOperation;
  protected final ServiceOperation updateOperation;
  protected final ServiceOperation deleteOperation;
  
  public CodaGenericService(CodaRouter router, CodaServiceDefinition<T> serviceDef) throws Exception {   
    super(router, serviceDef.getServiceVersion(), serviceDef.getLsvType());
    if (router.getLsvType() != serviceDef.getLsvType())
      throw new Exception("Logical server type mismatch");   
    this.serviceName = serviceDef.getServiceName();
    this.instanceName = serviceDef.getInstanceName();
    this.instanceClass = serviceDef.getInstanceType();
    this.serviceKeyClass = serviceDef.getStoreKeyType();
    this.instanceKeyClass = serviceDef.getInstanceKeyType();
    
    // Get information about the object class for this service
    this.servicePackage = instanceClass.getPackage();
    XmlSchema schemaAnnotation = servicePackage.getAnnotation(XmlSchema.class);
    serviceNamespace = schemaAnnotation.namespace();
    
    // If a custom property provider is specified, use it
    Class propertyProviderClass = serviceDef.getPropertyProviderType();
    if (!CodaGenericPropertyProvider.class.equals(propertyProviderClass)) {
      Constructor<CodaPropertyProvider> ctor = propertyProviderClass.getDeclaredConstructor();
      ctor.setAccessible(true);
      propertyProvider = ctor.newInstance();
    }
    else { // Otherwise, use the generic provider
      // Create a property provider and wire all fields
      CodaGenericPropertyProvider genProvider = new CodaGenericPropertyProvider<>(serviceName.toLowerCase(), instanceClass);
      genProvider.wireAll();
      propertyProvider = genProvider;
    }
    
    // Get the request/response classes for this service
    String requestClassName = serviceName + "Request";
    serviceRequestClass = findClass(servicePackage.getName(), requestClassName, ServiceRequest.class);
    String responseClassName = serviceName + "Response";
    serviceResponseClass = findClass(servicePackage.getName(), responseClassName, ServiceResponse.class);
    
    String[]serviceKeyNames = serviceDef.getStoreKeyFields();
    String[]instanceKeyNames = serviceDef.getInstanceKeyFields();
    if (serviceKeyNames.length != instanceKeyNames.length)
      throw new Exception("Mismatched number of key fields");
    
    serviceKeyConstructor = serviceKeyClass.getDeclaredConstructor();
    if (serviceKeyConstructor == null)
      throw new Exception("Unable to find default constructor for service key class");
    serviceKeyConstructor.setAccessible(true);

    addOperation = getOperationClasses("Add", instanceKeyNames);
    getResponseKeyMapping(addOperation, serviceKeyNames, instanceKeyNames);
    getOperation = getOperationClasses("Get",instanceKeyNames);
    if (getOperation.setRequestKeyMethod == null)
      throw new Exception("No setKey method for get operation");
    updateOperation = getOperationClasses("Update", instanceKeyNames);
    getResponseKeyMapping(updateOperation, serviceKeyNames, instanceKeyNames);
    deleteOperation = getOperationClasses("Delete", instanceKeyNames);
    getResponseKeyMapping(deleteOperation, serviceKeyNames, instanceKeyNames);
    if (deleteOperation.setRequestKeyMethod == null)
      throw new Exception("No setKey method for delete operation");    
    
    // Key mapping (Instance Key <-> Service Key)
    for (int k = 0; k < serviceKeyNames.length; k++) {
      Field serviceField = ReflectionTools.findField(serviceKeyClass, serviceKeyNames[k], true);
      Field instanceKeyField = ReflectionTools.findField(instanceKeyClass, instanceKeyNames[k], true);
      Field instanceField = ReflectionTools.findField(instanceClass, instanceKeyNames[k], true);
      requestKeyMap.add(serviceField, instanceKeyField);
      instanceKeyMap.add(serviceField, instanceField);
    }
  }

  @Override
  protected ServiceRequest newRequest() {
    try {
      return (ServiceRequest)serviceRequestClass.newInstance();
    } catch (InstantiationException | IllegalAccessException ex) {
      return null;
    }
  }

  @Override
  public T newInstance() {
    try {
      return instanceClass.newInstance();
    } catch (InstantiationException | IllegalAccessException ex) {
      return null;
    }
  }

  @Override
  public CodaPropertyProvider<T> getPropertyProvider() throws Exception {
    return propertyProvider;
  }

  @Override
  protected void addCreateEntry(T instance, ServiceRequest request) throws Exception {
    // Ensure invalid properties are not set
    getPropertyProvider().clearInvalidProperties(instance);
    addRequestEntry(addOperation, request, instance, null);
  }

  @Override
  protected void addDeleteEntry(K key, ServiceRequest request) throws Exception {

    Object codaKey = serviceKeyToRequestKey(key);
    addRequestEntry(deleteOperation, request, null, codaKey);
  }

  @Override
  protected void addGetEntry(K key, ServiceRequest request) throws Exception {
    Object codaKey = serviceKeyToRequestKey(key);
    addRequestEntry(getOperation, request, null, codaKey);
  }

  @Override
  protected void addUpdateEntry(T instance, ServiceRequest request) throws Exception {
    addRequestEntry(updateOperation, request, instance, null);
  }
  
  protected void addRequestEntry(ServiceOperation operation, ServiceRequest request, T instance, Object instanceKey) throws Exception {
    Object verb = operation.getRequestVerbMethod.invoke(request); // addVerb = serviceRequest.getAdd();
    if (verb == null) {
      verb = operation.requestVerbClass.newInstance(); // addVerb = new AddRequestVerb();
      operation.setRequestVerbMethod.invoke(request, verb); // serviceRequest.setAdd(addVerb);
    }
    
    Object objectRequest = operation.requestClass.newInstance(); // addRequest = new AddRequest();
    if (instance != null && operation.setRequestInstanceMethod != null)
      operation.setRequestInstanceMethod.invoke(objectRequest, instance); //addRequest.setXXX(instance);
    if (instanceKey != null && operation.setRequestKeyMethod != null)
      operation.setRequestKeyMethod.invoke(objectRequest, instanceKey); //addRequest.setKey(key);

    
    List requests = (List)operation.getRequestListMethod.invoke(verb); // requests = addVerb.getRequest()
    requests.add(objectRequest); // requests.add(addRequest); 
  }
  
  @Override
  protected ServiceResponse sendRequest(ServiceRequest request) throws Exception {    
//    Object factory = factoryClass.newInstance();
//    JAXBElement jaxbRequest = (JAXBElement)factoryCreateMethod.invoke(factory, serviceRequestClass.cast(request));
    QName qname = new QName(serviceNamespace, serviceRequestClass.getSimpleName());
    JAXBElement jaxbRequest = new JAXBElement(qname, serviceRequestClass, null, request);
    Object response = router.send(jaxbRequest, serviceResponseClass);
    return (ServiceResponse) response;
  }

  @Override
  protected ResponseVerb getResponseVerb(CodaOperationType operationType, ServiceResponse response) throws Exception {

    ResponseVerb verb = null;
    switch (operationType) {
    case CODA_OP_ADD:
      verb = (ResponseVerb)addOperation.getResponseVerbMethod.invoke(response);
      break;
    case CODA_OP_DELETE:
      verb = (ResponseVerb)deleteOperation.getResponseVerbMethod.invoke(response);
      break;
    case CODA_OP_UPDATE:
      verb = (ResponseVerb)updateOperation.getResponseVerbMethod.invoke(response);
      break;
    case CODA_OP_GET:
      verb = (ResponseVerb)getOperation.getResponseVerbMethod.invoke(response);
      break;
    }
    return  verb;
  }

  @Override
  protected List<Response> getObjectResponses(ResponseVerb verb) throws Exception {
    ServiceOperation op = null;
    if (addOperation.responseVerbClass.isInstance(verb))
      op = addOperation;
    else if (deleteOperation.responseVerbClass.isInstance(verb))
      op = deleteOperation;
    else if (updateOperation.responseVerbClass.isInstance(verb))
      op = updateOperation;
    else if (getOperation.responseVerbClass.isInstance(verb))
      op = getOperation;
    else
      throw new Exception("Unknown or unsupported response type");
    
    List<Response> responses = new ArrayList<>();
    List list = (List)op.getResponseListMethod.invoke(verb);
    responses.addAll(list);
    
    return responses;
  }

  @Override
  public K getObjectKey(T instance) throws Exception {
    return instanceKeyToServiceKey(instance);
  }

  @Override
  protected K getResponseKey(Response response) throws Exception {
    if (addOperation.responseClass.isInstance(response)) {
      return responseKeyToServiceKey(response, addOperation.responseKeyMap.getBackward());
    }
    else if (deleteOperation.responseClass.isInstance(response)) {
      return responseKeyToServiceKey(response, deleteOperation.responseKeyMap.getBackward());
    }
    else if (updateOperation.responseClass.isInstance(response)) {
      return responseKeyToServiceKey(response, updateOperation.responseKeyMap.getBackward());
    }
    else if (getOperation.responseClass.isInstance(response)) {
      T instance = (T)getOperation.getResponseInstanceMethod.invoke(response);
      return instanceKeyToServiceKey(instance);
    }
    else
      throw new Exception("Unknown or unsupported response type");
  }

  @Override
  protected T getResponseObject(Response response) throws Exception {
    if (addOperation.responseClass.isInstance(response))
      return null;
    else if (deleteOperation.responseClass.isInstance(response))
      return null;
    else if (updateOperation.responseClass.isInstance(response))
      return null;
    else if (getOperation.responseClass.isInstance(response))
      return (T)getOperation.getResponseInstanceMethod.invoke(response);
    else
      throw new Exception("Unknown or unsupported response type");  }
  
  protected Object serviceKeyToRequestKey(K source) throws Exception {
    Object dest = instanceKeyClass.newInstance();
    mapKeys(source, dest, requestKeyMap.getForward());
    return dest;
  }

  protected K requestKeyToServiceKey(Object source) throws Exception {
    K dest = (K)serviceKeyConstructor.newInstance();
    mapKeys(source, dest, requestKeyMap.getBackward());
    return dest;
  }
  
  protected void serviceKeyToInstanceKey(K source, T instance) throws Exception {
    mapKeys(source, instance, instanceKeyMap.getForward());
  }

  protected K instanceKeyToServiceKey(T instance) throws Exception {
    K dest = (K)serviceKeyConstructor.newInstance();
    mapKeys(instance, dest, instanceKeyMap.getBackward());
    return dest;
  }

  protected void serviceKeyToResponseKey(K source, Response response, Map<Field,Field> map) throws Exception {
    mapKeys(source, response, map);
  }

  protected K responseKeyToServiceKey(Response response, Map<Field,Field> map) throws Exception {
    K dest = (K)serviceKeyConstructor.newInstance();
    mapKeys(response, dest, map);
    return dest;
  }  
  
  protected void mapKeys(Object source, Object dest, Map<Field,Field> map) throws Exception {
    for (Map.Entry<Field,Field> entry : map.entrySet()) {
      Object value = entry.getKey().get(source);
      entry.getValue().set(dest, value);
    }
  }
  
  protected final ServiceOperation getOperationClasses(String operationName, String[] keyFieldNames) throws Exception {
    ServiceOperation op = new ServiceOperation();
    op.name = operationName;
    op.requestVerbClass = findClass(servicePackage.getName(), operationName + "RequestVerb", RequestVerb.class);
    op.requestClass = findClass(servicePackage.getName(), operationName + "Request", Request.class);
    op.responseVerbClass = findClass(servicePackage.getName(), operationName + "ResponseVerb", ResponseVerb.class);
    op.responseClass = findClass(servicePackage.getName(), operationName + "Response", Response.class);
    getOperationMethods(op);
    
    return op;
  }
  
  protected final Class<?> findClass(String packageName, String className, Class<?> superClass) throws Exception {
    String fullName = String.format("%s.%s",packageName, className);
    Class<?> cls = Class.forName(fullName);
    if (superClass != null)
      if (!superClass.isAssignableFrom(cls))
        throw new Exception("Class '" + fullName + "' does not descend from " + superClass.getName());
    return cls;
  }
  
  protected final void getOperationMethods(ServiceOperation op) throws Exception {
    op.getRequestVerbMethod = serviceRequestClass.getDeclaredMethod("get" + op.name);
    op.setRequestVerbMethod = serviceRequestClass.getDeclaredMethod("set" + op.name, op.requestVerbClass);
    op.getRequestListMethod = op.requestVerbClass.getDeclaredMethod("getRequest");
    try {
      op.setRequestInstanceMethod = op.requestClass.getDeclaredMethod("set" + instanceName, instanceClass);
    } catch (NoSuchMethodException nmx) {
      op.setRequestInstanceMethod = null;
    }
    try { // Use return type of getKey method to find setKet method
      op.setRequestKeyMethod = op.requestClass.getDeclaredMethod("setKey", instanceKeyClass);
    } catch (NoSuchMethodException nmx) {
      op.setRequestKeyMethod = null;
    }
    op.getResponseVerbMethod = serviceResponseClass.getDeclaredMethod("get" + op.name);
    op.getResponseListMethod = op.responseVerbClass.getDeclaredMethod("getResponse");
    try {
      op.getResponseInstanceMethod = op.responseClass.getDeclaredMethod("get" + instanceName);
    } catch (NoSuchMethodException nmx) {
      op.getResponseInstanceMethod = null;
    }
  }
  
  protected final void getResponseKeyMapping(ServiceOperation operation, String[] serviceKeyNames, String responseKeyNames[]) {
    for (int k = 0; k < serviceKeyNames.length; k++) {
      Field serviceField = ReflectionTools.findField(serviceKeyClass, serviceKeyNames[k], true);
      Field responseField = ReflectionTools.findField(operation.responseClass, responseKeyNames[k], true);
      operation.responseKeyMap.add(serviceField, responseField);
    }    
  }
  
  @Override
  public K createKey(Map<String,Object> fields) throws Exception {
    
    K key = (K)serviceKeyConstructor.newInstance();
    for (Field keyField : requestKeyMap.getForward().keySet()) {
      Object value = fields.get(keyField.getName());
      if (value == null)
        throw new Exception ("Missing field: " + keyField.getName());
      keyField.set(key, value);
    }
    return key;
  } 
}
