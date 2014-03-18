package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.List;
import java.util.EnumSet;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NounSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.NounTransition;

public enum NounState {
  A(true, true, EnumSet.allOf(NounSuffix.class)) {
    @Override
    public NounState nextState(final NounSuffix suffix) {
      switch (suffix) {
        case S8: case S11: case S13:
          return B;
        case S9: case S16:
          return C;
        case S18:
          return D;
        case S10: case S17:
          return E;
        case S12: case S14:
          return F;
        case S15:
          return G;
        case S2: case S3: case S4: case S5: case S6:
          return H;
        case S7:
          return K;
        case S1:
          return L;
        case S19:
          return M;
        default:
          return null;
      }
    }
  },

  B(false, true, EnumSet.of(NounSuffix.S1,
                            NounSuffix.S2,
                            NounSuffix.S3,
                            NounSuffix.S4,
                            NounSuffix.S5)) {
    @Override
    public NounState nextState(final NounSuffix suffix) {
      switch(suffix) {
        case S2: case S3: case S4: case S5:
          return H;
        case S1:
          return L;
        default:
          return null;
      }
    }
  },

  C(false, false, EnumSet.of(NounSuffix.S6, NounSuffix.S7)) {
    @Override
    public NounState nextState(final NounSuffix suffix) {
      switch(suffix) {
        case S6:
          return H;
        case S7:
          return K;
        default:
          return null;
      }
    }
  },

  D(false, false, EnumSet.of(NounSuffix.S10, NounSuffix.S13)) {
    @Override
    public NounState nextState(final NounSuffix suffix) {
      switch(suffix) {
        case S13:
          return B;
        case S10:
          return E;
        default:
          return null;
      }
    }
  },

  E(false, true, EnumSet.of(NounSuffix.S1,
                            NounSuffix.S2,
                            NounSuffix.S3,
                            NounSuffix.S4,
                            NounSuffix.S5,
                            NounSuffix.S6,
                            NounSuffix.S7,
                            NounSuffix.S18)) {
    @Override
    public NounState nextState(final NounSuffix suffix) {
      switch(suffix) {
        case S18:
          return D;
        case S2: case S3: case S4: case S5: case S6:
          return H;
        case S7:
          return K;
        case S1:
          return L;
        default:
          return null;
      }
    }
  },

  F(false, false, EnumSet.of(NounSuffix.S6, NounSuffix.S7, NounSuffix.S18)) {
    @Override
    public NounState nextState(final NounSuffix suffix) {
      switch(suffix) {
        case S18:
          return D;
        case S6:
          return H;
        case S7:
          return K;
        default:
          return null;
      }
    }
  },

  G(false, true, EnumSet.of(NounSuffix.S1,
                            NounSuffix.S2,
                            NounSuffix.S3,
                            NounSuffix.S4,
                            NounSuffix.S5,
                            NounSuffix.S18)) {
    @Override
    public NounState nextState(final NounSuffix suffix) {
      switch(suffix) {
        case S18:
          return D;
        case S2: case S3: case S4: case S5:
          return H;
        case S1:
          return L;
        default:
          return null;
      }
    }
  },

  H(false, true, EnumSet.of(NounSuffix.S1)) {
    @Override
    public NounState nextState(final NounSuffix suffix) {
      switch(suffix) {
        case S1:
          return L;
        default:
          return null;
      }
    }
  },

  K(false, true, EnumSet.noneOf(NounSuffix.class)) {
    @Override
    public NounState nextState(final NounSuffix suffix) { return null; }
  },

  L(false, true, EnumSet.of(NounSuffix.S18)) {
    @Override
    public NounState nextState(final NounSuffix suffix) {
      switch(suffix) {
        case S18:
          return D;
        default:
          return null;
      }
    }
  },

  M(false, true, EnumSet.of(NounSuffix.S1,
                            NounSuffix.S2,
                            NounSuffix.S3,
                            NounSuffix.S4,
                            NounSuffix.S5,
                            NounSuffix.S6,
                            NounSuffix.S6,
                            NounSuffix.S7)) {
    @Override
    public NounState nextState(final NounSuffix suffix) {
      switch(suffix) {
        case S2: case S3: case S4: case S5: case S6:
          return H;
        case S7:
          return K;
        case S1:
          return L;
        default:
          return null;
      }
    }
  };

  private boolean initialState;
  private boolean finalState;
  private EnumSet<NounSuffix> suffixes;

  private NounState(final boolean initialState,
                    final boolean finalState,
                    final EnumSet<NounSuffix> suffixes) {
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

  public static NounState getInitialState() {
    for(NounState state : values()) {
      if(state.initialState())
        return state;
    }

    return null;
  }

  public EnumSet<NounSuffix> suffixes() {
    return this.suffixes;
  }

  public EnumSet<NounState> possibleStates(final String word) {
    EnumSet<NounState> states;

    states = EnumSet.noneOf(NounState.class);

    for(NounSuffix suffix : suffixes()) {
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
                             final List<NounTransition> transitions,
                             final String rollbackWord,
                             final boolean marked) {

    for(NounSuffix suffix : suffixes()) {
      if(suffix.match(word)) {
        transitions.add(new NounTransition(this, nextState(suffix),
            word, suffix, rollbackWord, marked));
      }
    }
  }

  public abstract NounState nextState(NounSuffix suffix);
}
