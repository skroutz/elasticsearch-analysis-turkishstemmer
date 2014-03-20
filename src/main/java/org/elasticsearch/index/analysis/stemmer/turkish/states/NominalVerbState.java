package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.List;
import java.util.EnumSet;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.NominalVerbTransition;

public enum NominalVerbState {
  A(true, false, EnumSet.allOf(NominalVerbSuffix.class)) {
    @Override
    public NominalVerbState nextState(final NominalVerbSuffix suffix) {
      switch(suffix) {
        case S1: case S2: case S3: case S4:
          return B;
        case S5:
          return C;
        case S6: case S7: case S8: case S9:
          return D;
        case S10:
          return E;
        case S12: case S13: case S14: case S15:
          return F;
        case S11:
          return H;
        default:
          return null;
      }
    }

  },

  B(false, true, EnumSet.of(NominalVerbSuffix.S14)) {
    @Override
    public NominalVerbState nextState(final NominalVerbSuffix suffix) {
      switch(suffix) {
        case S14:
          return F;
        default:
          return null;
      }
    }
  },

  C(false, true, EnumSet.of(NominalVerbSuffix.S10,
                            NominalVerbSuffix.S12,
                            NominalVerbSuffix.S13,
                            NominalVerbSuffix.S14)) {
    @Override
    public NominalVerbState nextState(final NominalVerbSuffix suffix) {
      switch(suffix) {
        case S10: case S12: case S13: case S14:
          return F;
        default:
          return null;
      }
    }
  },

  D(false, false, EnumSet.of(NominalVerbSuffix.S12, NominalVerbSuffix.S13)) {
    @Override
    public NominalVerbState nextState(final NominalVerbSuffix suffix) {
      switch(suffix) {
        case S12: case S13:
          return F;
        default:
          return null;
      }
    }
  },

  E(false, true, EnumSet.of(NominalVerbSuffix.S1,
                            NominalVerbSuffix.S2,
                            NominalVerbSuffix.S3,
                            NominalVerbSuffix.S4,
                            NominalVerbSuffix.S5,
                            NominalVerbSuffix.S14)) {
    @Override
    public NominalVerbState nextState(final NominalVerbSuffix suffix) {
      switch(suffix) {
        case S1: case S2: case S3: case S4: case S5:
          return G;
        case S14:
          return F;
        default:
          return null;
      }
    }
  },

  F(false, true, EnumSet.noneOf(NominalVerbSuffix.class)) {
    @Override
    public NominalVerbState nextState(final NominalVerbSuffix suffix) {
      return null;
    }
  },

  G(false, false, EnumSet.of(NominalVerbSuffix.S14)) {
    @Override
    public NominalVerbState nextState(final NominalVerbSuffix suffix) {
      switch(suffix) {
        case S14:
          return F;
        default:
          return null;
      }
    }
  },

  H(false, false, EnumSet.of(NominalVerbSuffix.S1,
                             NominalVerbSuffix.S2,
                             NominalVerbSuffix.S3,
                             NominalVerbSuffix.S4,
                             NominalVerbSuffix.S5,
                             NominalVerbSuffix.S14)) {
    @Override
    public NominalVerbState nextState(final NominalVerbSuffix suffix) {
      switch(suffix) {
        case S14:
          return F;
        case S1: case S2: case S3: case S4: case S5:
          return G;
        default:
          return null;
      }
    }
  };

  private boolean initialState;
  private boolean finalState;
  private EnumSet<NominalVerbSuffix> suffixes;

  private NominalVerbState(final boolean initialState,
                           final boolean finalState,
                           final EnumSet<NominalVerbSuffix> suffixes) {
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

  public EnumSet<NominalVerbSuffix> suffixes() {
    return this.suffixes;
  }

  public static NominalVerbState getInitialState() {
    for(NominalVerbState state : values()) {
      if(state.initialState())
        return state;
    }

    return null;
  }

  public EnumSet<NominalVerbState> possibleStates(final String word) {
    EnumSet<NominalVerbState> states;

    states = EnumSet.noneOf(NominalVerbState.class);

    for(NominalVerbSuffix suffix : suffixes()) {
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
                             final List<NominalVerbTransition> transitions,
                             final String rollbackWord,
                             final boolean marked) {

    for(NominalVerbSuffix suffix : suffixes()) {
      if(suffix.match(word)) {
        transitions.add(new NominalVerbTransition(this, nextState(suffix),
            word, suffix, rollbackWord, marked));
      }
    }
  }

  public abstract NominalVerbState nextState(NominalVerbSuffix suffix);

}