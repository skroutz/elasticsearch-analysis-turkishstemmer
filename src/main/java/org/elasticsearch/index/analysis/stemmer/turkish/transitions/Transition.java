package org.elasticsearch.index.analysis.stemmer.turkish.transitions;

public abstract class Transition<E,V> {
  public E startState;
  public E nextState;
  public String word;
  public V suffix;
  public boolean marked;
  public String rollbackWord;

}
