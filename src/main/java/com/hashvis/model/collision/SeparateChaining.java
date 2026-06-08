package com.hashvis.model.collision;

import java.util.List;

import com.hashvis.model.hashfunc.HashFunction;
import com.hashvis.model.table.Item;
import com.hashvis.model.table.Row;
import java.util.ArrayList;
/**
 * Collision resolution strategy using separate chaining.
 * Each bucket holds a chain of items; collisions are resolved by
 * appending to the chain. The traversal follows the linked chain
 * within a single bucket.
 */
public class SeparateChaining extends CollisionStrategyController {
  private Item currentItem = null;
  private HashFunction hashFunc;
  private Integer hashValue = null;
  private Row currentRow = null;
  private ArrayList<String> traversal;
  @Override
  public boolean useSeparateChaining() {
    return true;
  }
  @Override
  public void setHashFunctionFields(List<HashFunction> hashFunctions) {hashFunc = hashFunctions.get(0);}
  @Override 
  protected ArrayList<String> initalizePseudocode(){
    ArrayList<String> pseudocode = new ArrayList<String>();
    pseudocode.add("i=hash(k,n), chain = HT_SC[i]");
    return pseudocode;
  }
  @Override
  protected String getcurrent_ResolverType(){
    return "chain = chain.next";
  }
  @Override
  protected ArrayList<String> caseInsert(){
    ArrayList<String> pseudocode = new ArrayList<String>();
    pseudocode.add("while(chain != null)");
    pseudocode.add("if chain.value == key, stop insertion");
    pseudocode.add(getcurrent_ResolverType());
    pseudocode.add("insert key at the end of chain");
    return pseudocode;
  }
  @Override
  protected ArrayList<String> caseDelete(){
    ArrayList<String> pseudocode = new ArrayList<String>();
    pseudocode.add("while(chain != null)");
    pseudocode.add("if chain.value == key, delete key");
    pseudocode.add(getcurrent_ResolverType());
    pseudocode.add("can't find key");
    return pseudocode; 
  }
  @Override
  protected ArrayList<String> caseSearch(){
    ArrayList<String> pseudocode = new ArrayList<String>();
    pseudocode.add("while(chain != null)");
    pseudocode.add("if chain.value == key, return searching");
    pseudocode.add(getcurrent_ResolverType());
    pseudocode.add("can't find key");
    return pseudocode;
  }
  @Override
  protected void uniqueInitalize(HashAction action){
    this.action  = action;
    traversal    = new ArrayList<>();
    currentItem  = null;
    hashValue    = null;
    currentRow   = null;
  }
  @Override
  protected Result firstStep(){
    if (hashValue == null){return handleHashing();}
    return null;
  }
  @Override 
  protected Item currentbucketValue(Row row){
    currentItem=row.nextItem();
    if(currentItem!=null){traversal.add(currentItem.getName());}
    return currentItem;
  }
  // Resolution steps
  protected Row handleBucketSelection(int hashValue, int probeCount) {
    Row row = table.getRow(hashValue);
    return row;
  }
  @Override 
  protected Result searching(){
    if (currentRow == null){
      currentRow = handleBucketSelection(hashValue,0);
      return new Result("Acessing row " + currentRow.getIndex(), 0);
    }
    int caset = searching_bound(currentRow);
    /// calling searching_bound to check if the action meet the requiment to stop
    switch(caset){
      case 1 -> {return null;}
      case 2 -> {return processFoundItem(currentRow);}
    }
    return new Result("Checking item: " + currentItem.getName() + " No match", 0);
  }
  @Override 
  protected String   collisonTraversal(){
    if(traversal.size()<=0){
      return "";
    }
    return  String.join("-> ", traversal);
  }
  @Override
  protected Result processFoundItem(Row row) {
    switch (action){
      case HashAction.INSERT -> {return new Result("Error: Duplicate key " + key +"<br/>Traversal : "+ collisonTraversal(), -1);} 
      case HashAction.DELETE -> {
        row.removeItem(currentItem);
        return new Result("Deleted key " + key +"<br/>Traversal : "+ collisonTraversal(), -1);
      } 
      default ->{return new Result("Found key " + key +"<br/>Traversal : "+ collisonTraversal(), -1);}
    }
  }
  protected Result handleHashing() {
  	hashValue = hashFunc.compute(key, table.size());
  	return new Result("Hash value: " + hashValue, 0);
  }
}
