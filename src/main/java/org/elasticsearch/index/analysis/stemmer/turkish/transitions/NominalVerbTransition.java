package org.elasticsearch.index.analysis.stemmer.turkish.transitions;

import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.analysis.stemmer.turkish.states.NominalVerbState;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;

public class NominalVerbTransition extends
    Transition<NominalVerbState, NominalVerbSuffix> {

  public NominalVerbTransition(final NominalVerbState startState,
      final NominalVerbState nextState, final String word,
      final NominalVerbSuffix suffix, final String rollbackWord,
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

  public List<NominalVerbTransition> similarTransitions(
      final List<NominalVerbTransition> transitions) {

    List<NominalVerbTransition> similars;
    similars = new ArrayList<NominalVerbTransition>();

    for(NominalVerbTransition transition : transitions) {
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
