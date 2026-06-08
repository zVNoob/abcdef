package com.hashvis.model.collision;

import java.util.ArrayList;
import java.util.List;

import com.hashvis.model.hashfunc.*;
import com.hashvis.model.table.Table;

/**
 * Defines the contract for hash table collision resolution strategies.
 * Implementations provide step-by-step visualization of insert, search,
 * and delete operations, returning pseudocode line numbers and status
 * messages for each step.
 */
public interface CollisionResolver {
  /**
   * The type of hash table operation to visualize.
   */
  public enum HashAction {
    INSERT, SEARCH, DELETE
  }

  /**
   * The data type of the key being operated on.
   */
  public enum DataType {
    STRING, INTEGER
  }

  /**
   * Holds the result of a single visualization step.
   *
   * @param message    the status message to display to the user
   * @param currentLine the current pseudocode line number, or -1 to signal
   *                    that visualization is complete
   */
  public record Result(String message, int currentLine) {
  }

  /**
   * Returns whether this strategy uses separate chaining layout.
   * Open addressing strategies return false (wrapping layout);
   * separate chaining returns true (one row per bucket).
   *
   * @return true if each bucket is displayed as its own row
   */
  boolean useSeparateChaining();

  /**
   * Returns the hash function fields required by this collision strategy.
   * The views use these to let the user configure the hash functions.
   *
   * @param dataType the data type of keys (INTEGER or STRING)
   * @return a list of hash function instances
   */
  default List<HashFunction> getHashFunctionFields(DataType dataType) {
    ArrayList<HashFunction> result = new ArrayList<HashFunction>();
    HashFunction f = switch (dataType) {
      case INTEGER -> new HashFunctionNumber();
      case STRING -> new HashFunctionString();
      default -> throw new IllegalArgumentException("Unsupported data type: " + dataType);
    };
    result.add(f);
    return result;
  }

  /**
   * Sets the hash function fields from user-configured values.
   *
   * @param fields the hash functions to use for this strategy
   */
  void setHashFunctionFields(List<HashFunction> fields);

  /**
   * Initializes the resolver for a new operation and returns the pseudocode
   * lines for the chosen action.
   *
   * @param action the operation to visualize (INSERT, SEARCH, DELETE)
   * @param key    the key to operate on
   * @param table  the hash table model
   * @return the pseudocode lines describing the algorithm
   */
  List<String> getAlgorithmAndInitalize(HashAction action, String key, Table table);

  /**
   * Advances the visualization by one step and returns the result.
   *
   * @return the result containing a status message and the current
   *         pseudocode line number, or -1 when the operation is complete
   */
  Result nextStep();
}