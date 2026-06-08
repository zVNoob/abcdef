package com.hashvis.model.collision;

/**
 * Open addressing strategy that probes consecutive buckets.
 * The bucket index at probe step {@code k} is:
 * {@code (hash + k) mod tableSize}.
 */
public class LinearProbing extends OpenAddressing {
  @Override
  protected int handleBucketSelection(int hashValue,int probeCount) {
    return (hashValue+probeCount)%table.size();
  }
  @Override
  protected String getcurrent_ResolverType(){
    return " i = (base + step) % size of HT";
  }
}