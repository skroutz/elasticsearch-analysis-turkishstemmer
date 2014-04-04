package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.EnumSet;
import java.util.List;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.Suffix;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.Transition;

public enum NominalVerbState implements State {
  A(true, false, EnumSet.allOf(NominalVerbSuffix.class)) {
    @Override
    public State nextState(final Suffix suffix) {
      switch((NominalVerbSuffix) suffix) {
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
    public State nextState(final Suffix suffix) {
      switch((NominalVerbSuffix) suffix) {
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
    public State nextState(final Suffix suffix) {
      switch((NominalVerbSuffix) suffix) {
        case S10: case S12: case S13: case S14:
          return F;
        default:
          return null;
      }
    }
  },

  D(false, false, EnumSet.of(NominalVerbSuffix.S12, NominalVerbSuffix.S13)) {
    @Override
    public State nextState(final Suffix suffix) {
      switch((NominalVerbSuffix) suffix) {
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
    public State nextState(final Suffix suffix) {
      switch((NominalVerbSuffix) suffix) {
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
    public State nextState(final Suffix suffix) {
      return null;
    }
  },

  G(false, false, EnumSet.of(NominalVerbSuffix.S14)) {
    @Override
    public State nextState(final Suffix suffix) {
      switch((NominalVerbSuffix) suffix) {
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
    public State nextState(final Suffix suffix) {
      switch((NominalVerbSuffix) suffix) {
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
