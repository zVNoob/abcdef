package com.hashvis.model.collision;

import java.util.ArrayList;
import java.util.List;

import com.hashvis.model.table.Item;
import com.hashvis.model.table.Row;
import com.hashvis.model.table.Table;

/**
 * Abstract base controller for collision resolution strategies.
 * Provides the common step-by-step visualization flow: hash, probe,
 * check bounds, and insert/delete/search. Subclasses implement the
 * strategy-specific details such as bucket traversal and pseudocode.
 */
abstract class CollisionStrategyController implements CollisionResolver {
	/** The key being operated on. */
  	protected String key;
  	/** The hash table model. */
  	protected Table table;
	/** The current operation type. */
	protected HashAction action;

	private Row mark = null;
	private boolean ghost = false;
	private int keyCount = 0;
	private boolean checkpoint = true;

	/** Initializes strategy-specific state before a new operation. */
	abstract protected void uniqueInitalize(HashAction action);
	/** Performs the first step (typically hashing) and returns its result. */
	abstract protected Result firstStep();
	/** Searches for a slot or key during probing. */
	abstract protected Result searching();
	/** Returns a string describing the traversal path so far. */
	abstract protected String collisonTraversal();
	/** Returns the current item value in the given row (bucket or chain). */
	abstract protected Item currentbucketValue(Row row);
	/** Returns the pseudocode line describing the bucket index formula. */
	abstract protected String getcurrent_ResolverType();
	abstract protected ArrayList<String> caseInsert();
	abstract protected ArrayList<String> caseDelete();
	abstract protected ArrayList<String> caseSearch();
	abstract protected ArrayList<String> initalizePseudocode();

	/**
	 * Builds the full pseudocode list by combining initialization lines
	 * with action-specific lines.
	 *
	 * @param action the operation type
	 * @return the complete pseudocode as a list of strings
	 */
  	protected ArrayList<String> getPseudocode(HashAction action) {
    	ArrayList<String> pseudocode = new ArrayList<String>();
		pseudocode.addAll(initalizePseudocode());
        switch (action) {
            case HashAction.INSERT -> {pseudocode.addAll(caseInsert());}
            case HashAction.DELETE -> {pseudocode.addAll(caseDelete());}
            case HashAction.SEARCH -> {pseudocode.addAll(caseSearch());}
            default  -> {return new ArrayList<String>();}
        }
    	return pseudocode;
  	}
	@Override
	public List<String> getAlgorithmAndInitalize(HashAction action, String key, Table table) {
		this.key = key;
		this.table = table;
		this.action=action;
		return getAlgorithm(action);
	}
	/**
	 * Resets internal state and returns the pseudocode for the given action.
	 *
	 * @param action the operation type
	 * @return the pseudocode lines
	 */
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
	/**
	 * Completes an insertion: either inserts at the marked slot or reports
	 * failure if no suitable slot was found.
	 *
	 * @return the result with a status message and stop signal
	 */
	protected Result processInsertion() {
		if(mark==null){return new Result("Can't insert " + key + " key into the hash table "+"<br/>Traversal : "+ collisonTraversal(), -1);}
		if(ghost){mark.removeItem(mark.getItems().get(0));}
		mark.addItem(key);
		keyCount++;
		return new Result("Inserted " + key + " into bucket " + mark.getIndex() +"<br/>Traversal : "+ collisonTraversal(), -1);
	}
	/**
	 * Checks the current item in the given row against search criteria.
	 *
	 * @param row the row to inspect
	 * @return 0 if no match, 1 if empty slot found, 2 if key matches
	 */
	protected int searching_bound(Row row) {
		Item item=currentbucketValue(row);
		if(item==null){
			if(mark==null){mark = row;}
			return 1;
		}
		if (item.isGhosted()){
			ghost=true;
			if(mark==null){mark = row;}
			return 0;
		}
		if (item.getName().equals(key)) {return 2;}
		return 0;
	}
	/**
	 * Handles the case where the target key was found in the given row.
	 * Behaviour depends on the current action: reports duplicate on insert,
	 * ghosts the item on delete, or reports success on search.
	 *
	 * @param row the row containing the matched key
	 * @return the result with a status message and stop signal
	 */
	protected Result processFoundItem(Row row) {
		switch (action){
			case HashAction.INSERT -> { return new Result("Error: Duplicate key " + key +"<br/>Traversal : "+ collisonTraversal(), -1);}
			case HashAction.DELETE -> {
				Item item = row.getItems().get(0);
				item.ghost();
				keyCount--;
				return new Result("Deleted key " + key +"\nTraversal : "+ collisonTraversal(), -1);
			}  
			default-> { return new Result("Found key " + key +"<br/>Traversal : "+ collisonTraversal(), -1);}
		}  
 	}
}