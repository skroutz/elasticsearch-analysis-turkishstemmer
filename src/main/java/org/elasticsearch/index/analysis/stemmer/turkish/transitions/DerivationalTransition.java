package org.elasticsearch.index.analysis.stemmer.turkish.transitions;

import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.analysis.stemmer.turkish.states.DerivationalState;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;

public class DerivationalTransition extends
    Transition<DerivationalState, DerivationalSuffix> {

  public DerivationalTransition(final DerivationalState startState,
      final DerivationalState nextState, final String word,
      final DerivationalSuffix suffix, final String rollbackWord,
      final boolean marked) {

    this.startState = startState;
    this.nextState = nextState;
    this.word = word;
    this.suffix = suffix;
    this.marked = false;
    if(rollbackWord != null) {
      this.rollbackWord = rollbackWord;
    } else if(startState.finalState()) {
      this.rollbackWord = word;
    }
  }

  public List<DerivationalTransition> similarTransitions(
      final List<DerivationalTransition> transitions) {

    List<DerivationalTransition> similars;
    similars = new ArrayList<DerivationalTransition>();

    for(DerivationalTransition transition : transitions) {
      if(this.startState == transition.startState
          && this.nextState == transition.nextState)
        similars.add(transition);
    }

    return similars;
  }

  @Override
  public String toString() {
    return String.format("%s(%s) -> %s (rollback: %s)", this.startState,
        this.suffix, this.nextState, this.rollbackWord);
  }
}
