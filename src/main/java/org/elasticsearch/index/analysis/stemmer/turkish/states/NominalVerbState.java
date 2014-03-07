package org.elasticsearch.index.analysis.stemmer.turkish.states;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;

public enum NominalVerbState implements State {
  A(true, false) {
    @Override
    public State nextState(final NominalVerbSuffix suffix) {
      switch (suffix) {
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

  B(false, true) {
    @Override
    public State nextState(final NominalVerbSuffix suffix) {
      switch(suffix) {
        case S14:
          return F;
        default:
          return null;
      }
    }
  },

  C(false, true) {
    @Override
    public State nextState(final NominalVerbSuffix suffix) {
      switch(suffix) {
        case S10: case S12: case S13: case S14:
          return F;
        default:
          return null;
      }
    }
  },

  D(false, false) {
    @Override
    public State nextState(final NominalVerbSuffix suffix) {
      switch(suffix) {
        case S12: case S13:
          return F;
        default:
          return null;
      }
    }
  },

  E(false, true) {
    @Override
    public State nextState(final NominalVerbSuffix suffix) {
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

  F(false, true) {
    @Override
    public State nextState(final NominalVerbSuffix suffix) { return null; }
  },

  G(false, false) {
    @Override
    public State nextState(final NominalVerbSuffix suffix) {
      switch(suffix) {
        case S14:
          return F;
        default:
          return null;
      }
    }
  },

  H(false, false) {
    @Override
    public State nextState(final NominalVerbSuffix suffix) {
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

  private NominalVerbState(final boolean initialState, final boolean finalState) {
    this.initialState = initialState;
    this.finalState = finalState;
  }

  public boolean initialState() {
    return this.initialState;
  }

  public boolean finalState() {
    return this.finalState;
  }

  public abstract State nextState(NominalVerbSuffix suffix);

}
