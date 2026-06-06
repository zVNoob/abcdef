package com.hashvis.model.collision;

import java.util.List;
import java.util.ArrayList;

import com.hashvis.model.hashfunc.HashFunction;
import com.hashvis.model.table.Row;
abstract class OpenAddressing extends CollisionStrategyController {
  private Integer probeCount = 0;
  private Integer hashValue = null;
  private HashFunction hashFunc;
  private Row currentRow = null;
  
  abstract protected int handleBucketSelection(int hashValue, int probeCount);

  @Override
  public boolean useSeparateChaining() {return false;}
  @Override
  public void setHashFunctionFields(List<HashFunction> hashFunctions) {hashFunc = hashFunctions.get(0);}
  @Override
  protected void uniqueInitalize(HashAction action){
    probeCount   = 0;
    hashValue    = null;
    currentRow   = null;
  }
  @Override
  protected Result firstStep() {
    if (hashValue == null){return handleHashing();}
    return null;
  }
  private Result handleHashing() {
    hashValue = hashFunc.compute(key, table.size());
    return new Result("Hash value: " + hashValue, 0);
  }
  @Override
  protected ArrayList<String> initalizePseudocode(){	
    ArrayList<String> pseudocode = new ArrayList<String>();
    pseudocode.add("step = 0 ; i = base =hash(k,n)");
    return pseudocode;
  }
  @Override
	protected ArrayList<String> caseInsert(){
		ArrayList<String> pseudocode = new ArrayList<String>();
		pseudocode.add("if (keycount == size of HT) stop insertion");
		pseudocode.add("while (HT[i] != EMPTY)");
		pseudocode.add(" if (HT[i] == DELETED) mark the suitable space");
    pseudocode.add(" if HT[i] == key stop insertion");
    pseudocode.add(" step++");
    pseudocode.add(" if step == size of HT stop insertion");
		pseudocode.add(getcurrent_ResolverType());
		pseudocode.add("insert key at suitable space ");
		return pseudocode;
	}
  @Override
	protected ArrayList<String> caseDelete(){
		ArrayList<String> pseudocode = new ArrayList<String>();
		pseudocode.add("while (HT[i] != EMPTY)");
		pseudocode.add(" if (HT[i] == key) HT[i] = DELETED ; break");
		pseudocode.add(" step++");
    pseudocode.add(" if step == size of HT, stop deletion");
		pseudocode.add(getcurrent_ResolverType());
		return pseudocode;
	}
  @Override
	protected ArrayList<String> caseSearch(){
		ArrayList<String> pseudocode = new ArrayList<String>();
		pseudocode.add("while (HT[i] != EMPTY)");
		pseudocode.add(" if (HT[i] == key) return 'found at index i'");
		pseudocode.add(" step++");
    pseudocode.add(" if step == size of HT, stop searching");
		pseudocode.add(getcurrent_ResolverType());
		return pseudocode;
	}
  @Override 
  protected Result searching(){
    if (currentRow == null){
      if (probeCount == table.size()){probeCount=0;return null;}
      currentRow = table.getRow(handleBucketSelection(hashValue,probeCount));
      probeCount++;
      return new Result("Accessing bucket index " + currentRow.getIndex(), 0);
    }
    int caset = searching_bound(currentRow);
    /// calling searching_bound to check if the action meet the requiment to stop
    switch(caset){
      case 1 -> {currentRow=null;return null;}
      case 2 -> {return processFoundItem(currentRow);}
    }
    currentRow=null;
    return new Result("Checking item: " + " No match", 0);
  }
}