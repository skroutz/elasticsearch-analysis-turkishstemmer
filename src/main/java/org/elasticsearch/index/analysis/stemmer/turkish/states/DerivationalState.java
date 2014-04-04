package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.EnumSet;
import java.util.List;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.Suffix;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.Transition;

public enum DerivationalState implements State {
  A(true, false, EnumSet.of(DerivationalSuffix.S1)) {
    @Override
    public State nextState(final Suffix suffix) {
      switch((DerivationalSuffix) suffix) {
        case S1:
          return B;
        default:
          return null;
      }
    }
  },

  B(false, true, EnumSet.noneOf(DerivationalSuffix.class)) {
    @Override
    public State nextState(final Suffix suffix) {
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

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean initialState() {
    return this.initialState;
  }

  /**
   * {@inheritDoc}
   */
  @Override
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void addTransitions(final String word,
                             final List<Transition> transitions,
                             final boolean marked) {

    for(Suffix suffix : suffixes()) {
      if(suffix.match(word)) {
        transitions.add(new Transition(this, nextState(suffix),
            word, suffix, marked));
      }
    }
  }
}
