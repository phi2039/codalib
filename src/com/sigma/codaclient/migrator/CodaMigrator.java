/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.migrator;

import com.sigma.codaclient.codarouter.CodaObjectStore;
import com.sigma.codaclient.services.common.CodaCopyOperation;
import com.sigma.codaclient.services.common.CodaCreateOperation;
import com.sigma.codaclient.services.common.CodaGetOperation;
import com.sigma.codaclient.services.common.CodaKey;
import com.sigma.codaclient.services.common.CodaObjectResult;
import com.sigma.codaclient.services.common.CodaObjectStoreResult;
import com.sigma.codaclient.services.common.CodaOperation;
import com.sigma.codaclient.services.common.CodaOperationResult;
import com.sigma.codaclient.services.common.CodaOperationType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author clance
 */
public class CodaMigrator<T, K extends CodaKey> {
  
  private class MigrateJob {
    public final List<CodaOperation> getOps = new ArrayList<>();
    public final List<CodaOperation> createOps = new ArrayList<>();
    public final CodaOperationResult<T,K> result = new CodaOperationResult<>(CodaOperationType.CODA_OP_ADD, false);
    private final Map<String,CodaCopyOperation> sourceMap = new HashMap<>();
    private final List<CodaObjectResult<T,K>> getResults = new ArrayList<>();
    public List<CodaCopyOperation<T,K>> copyOps;
  }
  
  private final CodaObjectStore<T,K> source;
  private final CodaObjectStore<T,K> dest;
  private final int fetchSize;
  private final int putSize;
  private MigrateJob activeJob;
  
  public CodaMigrator(CodaObjectStore<T,K> source, CodaObjectStore<T,K> dest, int fetchSize, int putSize) {
    this.source = source;
    this.dest = dest;
    this.fetchSize = fetchSize;
    this.putSize = putSize;
  }

  public CodaMigrator(CodaObjectStore<T,K> source, CodaObjectStore<T,K> dest) {
    this (source, dest, 1, 1);
  }

  public CodaObjectStoreResult<T,K> run(List<CodaCopyOperation<T,K>> operations) throws Exception {
    
    activeJob = new MigrateJob();
    activeJob.copyOps = operations;
    
    // TODO: Capture source errors
    // TODO: make this more elegant
    Iterator<CodaCopyOperation<T,K>> copyOps = activeJob.copyOps.iterator();
    boolean sourceWork = false;
    boolean destWork = false;
    // TODO: Could this be cleaner?
    while (true) {
      // Process the next copy operation
      if (copyOps.hasNext())
        sourceWork = processSource(copyOps.next(), false);
      else
        sourceWork = processSource(null, true); // Flush any remaining source operations
      for (CodaObjectResult<T,K> getResult : activeJob.getResults)
        destWork = processDest(getResult, false);
      
      if (!copyOps.hasNext() && !sourceWork && !destWork)
        break;
    }
    
    // Flush any remaining source or destination operations
    processSource(null, true);
    processDest(null, true);

    CodaObjectStoreResult<T,K> result = new CodaObjectStoreResult<>();
    result.setOperation(CodaOperationType.CODA_OP_ADD, activeJob.result);
    return result;
  }
  
  protected boolean processSource(CodaCopyOperation<T,K> op, boolean flush) throws Exception {

    // Add the next source operation to the list
    if (op != null) {
      K opKey = op.getSourceKey();
      activeJob.getOps.add(new CodaGetOperation<>(opKey));
      activeJob.sourceMap.put(opKey.toString(),op);
    }

    // If the batch size has been reached (or flush has been indicated), retrieve results and add to the result list
    if (activeJob.getOps.size() >= fetchSize || flush) {
      // Make sure there are operations pending
      if (!activeJob.getOps.isEmpty()) {
        CodaOperationResult<T,K> result = source.execute(activeJob.getOps).getOperation(CodaOperationType.CODA_OP_GET);
        activeJob.getOps.clear();
        // Add results, if any, to the result queue
        if (!result.isFailed())
          activeJob.getResults.addAll(result.getObjectResults());
      }
    }
    
    // Return true if there are source operations pending, otherwise false
    return !activeJob.getOps.isEmpty();
  }

  protected boolean processDest(CodaObjectResult<T,K> objResult, boolean flush) throws Exception {
    // If there are any source results in the queue, add a create operation to the output queue
    if (objResult != null) {
    // Find the original copy operation for this result
      K sourceKey = objResult.getKey();
      CodaCopyOperation<T,K> copyOp = activeJob.sourceMap.get(sourceKey.toString());
      // Get the actual source object instance
      T sourceObject = objResult.getObject();
      if (sourceObject != null) {
        // Make requested changes to the source instance
        dest.setProperties(sourceObject, copyOp.getNewValues());
        dest.clearProperties(sourceObject, copyOp.getClearedProperties());
        // Add a new create operation to the queue
        activeJob.createOps.add(new CodaCreateOperation(sourceObject));
      }
    }
      
    // If the batch size has been reached (or flush has been indicated), create objects and add results to the result list
    if (activeJob.createOps.size() >= putSize || flush) {
      // Make sure there are operations pending
      if (!activeJob.createOps.isEmpty()) {
        CodaOperationResult<T,K> result = dest.execute(activeJob.createOps).getOperation(CodaOperationType.CODA_OP_ADD);
        activeJob.createOps.clear();
        activeJob.result.merge(result);
      }
    }
    
    // Return true if there are destination operations pending, otherwise false
    return !activeJob.createOps.isEmpty();
  }
  
  public void loadDirect() {
    
  }
}
