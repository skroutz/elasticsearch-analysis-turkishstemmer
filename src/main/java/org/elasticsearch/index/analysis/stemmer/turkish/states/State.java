package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.List;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.Suffix;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.Transition;

public interface State {

  /**
   * Checks if the state is an initial state.
   *
   * @return whether or not this state is initial
   */
  boolean initialState();
  /**
   * Checks if the state is final.
   *
   * @return whether or not this state is final
   */
  boolean finalState();
  /**
   * Adds possible transitions from the current state to other states
   * about a word to a given list.
   *
   * @param word a word to search transitions for
   * @param transitions the initial list to add transitions
   * @param marked whether to mark the transitions as marked
   */
  void addTransitions(String word, List<Transition> transitions, boolean marked);

  public abstract State nextState(Suffix suffix);

}
