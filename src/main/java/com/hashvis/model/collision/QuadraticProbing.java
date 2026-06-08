package com.hashvis.model.collision;

/**
 * Open addressing strategy that probes buckets with a quadratic step.
 * The bucket index at probe step {@code k} is:
 * {@code (hash + k*k) mod tableSize}.
 */
public class QuadraticProbing extends OpenAddressing  {
  @Override
  protected String getcurrent_ResolverType(){
    return " i = (base + step**2) % size of HT";
  }
  @Override
  protected int handleBucketSelection(int hashValue, int probeCount) {
    return (hashValue+probeCount*probeCount)%table.size();
  }
}