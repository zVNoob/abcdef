package com.hashvis.model.collision;

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