package com.hashvis.model.collision;

import java.util.ArrayList;
import java.util.List;

import com.hashvis.model.table.Item;
import com.hashvis.model.table.Row;
import com.hashvis.model.table.Table;
abstract class CollisionStrategyController implements CollisionResolver{
  	protected String key;
  	protected Table table;
	private Row mark=null;
	private boolean ghost=false;
	private int keyCount=0;
	private  boolean checkpoint=true;
	protected  HashAction action;

	abstract protected String 	getcurrent_ResolverType();
	abstract protected void 	uniqueInitalize(HashAction action);
	abstract protected Result  	firstStep();
	abstract protected Result   searching();
	abstract protected ArrayList<String> caseInsert();
	abstract protected ArrayList<String> caseDelete();
	abstract protected ArrayList<String> caseSearch();
	abstract protected ArrayList<String> initalizePseudocode();

  	protected ArrayList<String> getPseudocode(HashAction action) {
    	ArrayList<String> pseudocode = new ArrayList<String>();
		pseudocode.addAll(initalizePseudocode());
        switch (action) {
            case (HashAction.INSERT) -> {pseudocode.addAll(caseInsert());}
            case (HashAction.DELETE) -> {pseudocode.addAll(caseDelete());}
            case (HashAction.SEARCH) -> {pseudocode.addAll(caseSearch());}
            default  -> {return new ArrayList<String>();}
        }
    	return pseudocode;
  	}
	@Override
	public List<String> getAlgorithmAndInitalize(HashAction action, String key, Table table) {
		this.key = key;
		this.table = table;
		this.action=action;
		checkpoint=true;
		return getAlgorithm(action);
	}
	protected ArrayList<String> getAlgorithm(HashAction action) {
		checkpoint=true;
		mark      =null;
    	ghost     =false;
		uniqueInitalize(action);
	    return getPseudocode(action);
	}
	@Override
  	public Result nextStep() {
		if(keyCount==table.size() && action == HashAction.INSERT && !useSeparateChaining()){return new Result("Error: Table is full", -1);}
    	Result tmp = firstStep();
		if(tmp != null){return tmp;}
		if(checkpoint){
			tmp = searching();
			if(tmp != null){return tmp;}
		}
		checkpoint=false;
		return processInsertion();
  	}
	protected Result processInsertion(){
		if(mark==null){return new Result("Can't insert " + key + " key into the hash table " , -1);}
		if(ghost){mark.removeItem(mark.getItems().get(0));}
		mark.addItem(key);
		keyCount++;
		return new Result("Inserted " + key + " into bucket " + mark.getIndex(), -1);
	}
	protected int searching_bound(Row row) {
		Item item=null;
		try {
			item = row.getItems().get(0);
		} catch (Exception e) {
			if(mark==null){mark = row;}
			return 1;
		}
		if(item==null){return 1;}
		if (item.isGhosted()){
			ghost=true;
			if(mark==null){mark = row;}
			return 0;
		}
		if (item.getName().equals(key)) {
			return 2;
		}
		return 0;
	}
	protected Result processFoundItem(Row row) {
    switch (action){
      case (HashAction.INSERT) -> { return new Result("Error: Duplicate key " + key, -1);}
      case (HashAction.DELETE) -> {
        Item item = row.getItems().get(0);
		item.ghost();
        keyCount--;
        return new Result("Deleted key " + key, -1);
      }  
      default-> { return new Result("Found key " + key, -1);}
    }  
  }
}