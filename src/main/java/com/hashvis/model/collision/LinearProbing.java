package com.hashvis.model.collision;

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