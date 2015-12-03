/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author clance
 */
public class RecursiveFileIterator implements Iterator<Path>, Iterable<Path> {

    private Iterator<Path> iterator;
    private RecursiveFileIterator child;
    private Path nextFile;
    private Path lastFile;
    
    public RecursiveFileIterator(Path folder) {
      lastFile = null;
      try {
        iterator = Files.newDirectoryStream(folder).iterator();
        nextFile = findNext();
      } catch (Exception ex) {
        nextFile = null;
      }
    }
    
    @Override
    public boolean hasNext() {
      return (nextFile != null);
    }

    @Override
    public Path next() {
      if (nextFile == null)
        throw new NoSuchElementException();
      lastFile = nextFile;
      try {
        nextFile = findNext();
      } catch (Exception ex) {
        nextFile = null;
      }
      return lastFile;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("Cannot remove items from this collection");
    }
    
    // TODO: Could be much more elegant...
    private Path findNext() throws Exception {
      Path returnVal = null;
      if (child != null) {
        if (child.hasNext())
          returnVal = child.next();
        if (returnVal == null)
          child = null; // Give up on this child
      }
      
      // Keep looking until we are out of options or we get a result
      while (returnVal == null && iterator.hasNext()) {
        // Get the next result from this iterator
        Path next = iterator.next();
        // If the result is a directory, recurse into it
        if (Files.isDirectory(next)) {
          child = new RecursiveFileIterator(next);
          if (child.hasNext())
            returnVal = child.next();
        }
        // If the result is a filename, return it
        else if (Files.isReadable(next)) {
          returnVal = next;
        }
      }
      return returnVal;
    }

    @Override
    public Iterator<Path> iterator() {
      return this;
    }
  }