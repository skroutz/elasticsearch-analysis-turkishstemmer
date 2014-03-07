package org.elasticsearch.index.analysis.stemmer.turkish.states;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;

public enum DerivationalState implements State {
  A(true, false) {
    @Override
    public State nextState(final DerivationalSuffix suffix) {
      switch(suffix) {
        case S1:
          return B;
        default:
          return null;
      }
    }
  },

  B(false, true) {
    @Override
    public State nextState(final DerivationalSuffix suffix) {
      return null;
    }
  };

  private boolean initialState;
  private boolean finalState;

  private DerivationalState(final boolean initialState, final boolean finalState) {
    this.initialState = initialState;
    this.finalState = finalState;
  }

  public boolean initialState() {
    return this.initialState;
  }

  public boolean finalState() {
    return this.finalState;
  }

  public abstract State nextState(DerivationalSuffix suffix);

}
