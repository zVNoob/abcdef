package com.hashvis.model.collision;

import java.util.List;

import com.hashvis.model.hashfunc.HashFunction;
import com.hashvis.model.table.Item;
import com.hashvis.model.table.Row;
import java.util.ArrayList;
public class SeparateChaining extends CollisionStrategyController {
  private Item currentItem = null;
  private HashFunction hashFunc;
  private Integer hashValue = null;
  private Row currentRow = null;
  @Override
  public boolean useSeparateChaining() {
    return true;
  }
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
    currentItem  = null;
    hashValue    = null;
    currentRow   = null;
  }
  @Override
  protected Result firstStep(){
    if (hashValue == null)
      return handleHashing();
    if (currentRow == null)
      return handleBucketSelection();
    return null;
  }
  // Resolution steps
  private Result handleBucketSelection() {
    currentRow = table.getRow(hashValue);
    return new Result("Accessing bucket index " + hashValue, 0);
  }
  @Override
  protected Result searching() {
    // If we don't have an item yet, or we just finished one, get the next
    if (currentItem == null )
      currentItem = currentRow.nextItem();
    if (currentItem == null) {
      return handleFinalization();
    }

    // Check if this is the item we are looking for
    if (currentItem.getName().equals(key))
      return processFoundItem();

    // Otherwise, prepare to move to the next item in the next call
    Item itemToHighlight = currentItem;
    currentItem = null; // Reset so next call to handleTraversal calls nextItem()
    return new Result("Checking item: " + itemToHighlight.getName() + " (No match)", 0);
  }
  @Override
  protected Result processInsertion(){
    return new Result("Key not found. Inserted " + key + " into bucket " + hashValue, -1);
  }
  private Result processFoundItem() {
    if (action == HashAction.INSERT) {
      return new Result("Error: Duplicate key " + key, -1);
    } else if (action == HashAction.DELETE) {
      currentRow.removeItem(currentItem);
      return new Result("Deleted key " + key, -1);
    } else {
      return new Result("Found key " + key, -1);
    }
  }
  private Result handleFinalization() {
    if (action == HashAction.INSERT) {
      currentRow.addItem(key);
      return null;
    }
    return new Result("Error: Key " + key + " not found in table", -1);
  }
  protected Result handleHashing() {
  	hashValue = hashFunc.compute(key, table.size());
  	return new Result("Hash value: " + hashValue, 0);
  }
  @Override
  public void setHashFunctionFields(List<HashFunction> hashFunctions) {
    hashFunc = hashFunctions.get(0);
  }
}
