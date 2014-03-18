package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.List;
import java.util.EnumSet;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.DerivationalTransition;

public enum DerivationalState {
  A(true, false, EnumSet.of(DerivationalSuffix.S1)) {
    @Override
    public DerivationalState nextState(final DerivationalSuffix suffix) {
      switch(suffix) {
        case S1:
          return B;
        default:
          return null;
      }
    }
  },

  B(false, true, EnumSet.noneOf(DerivationalSuffix.class)) {
    @Override
    public DerivationalState nextState(final DerivationalSuffix suffix) {
      return null;
    }
  };

  private boolean initialState;
  private boolean finalState;
  private EnumSet<DerivationalSuffix> suffixes;

  private DerivationalState(final boolean initialState,
                            final boolean finalState,
                            final EnumSet<DerivationalSuffix> suffixes) {
    this.initialState = initialState;
    this.finalState = finalState;
    this.suffixes = suffixes;
  }

  public boolean initialState() {
    return this.initialState;
  }

  public boolean finalState() {
    return this.finalState;
  }

  public static DerivationalState getInitialState() {
    for(DerivationalState state : values()) {
      if(state.initialState())
        return state;
    }

    return null;
  }

  public EnumSet<DerivationalSuffix> suffixes() {
    return this.suffixes;
  }

  public EnumSet<DerivationalState> possibleStates(final String word) {
    EnumSet<DerivationalState> states;

    states = EnumSet.noneOf(DerivationalState.class);

    for(DerivationalSuffix suffix : suffixes()) {
      if(suffix.match(word)) {
        states.add(nextState(suffix));
      }
    }

    return states;
  }

  /**
   * Adds possible transitions from the current state to other states
   * about a word to a given list.
   *
   * @param word a word to search transitions for
   * @param transitions the initial list to add transitions
   * @param rollbackWord a given rollback word
   * @param marked whether to mark the transitions as marked
   */
  public void addTransitions(final String word,
      final List<DerivationalTransition> transitions, final String rollbackWord,
      final boolean marked) {

    for(DerivationalSuffix suffix : suffixes()) {
      if(suffix.match(word)) {
        transitions.add(new DerivationalTransition(this, nextState(suffix),
            word, suffix, rollbackWord, marked));
      }
    }
  }

  public abstract DerivationalState nextState(DerivationalSuffix suffix);

}
