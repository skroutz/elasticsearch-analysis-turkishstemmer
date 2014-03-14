package org.elasticsearch.index.analysis.stemmer.turkish.transitions;

import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.analysis.stemmer.turkish.states.NounState;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NounSuffix;

public class NounTransition extends Transition<NounState, NounSuffix> {

  public NounTransition(final NounState startState, final NounState nextState,
      final String word, final NounSuffix suffix, final String rollbackWord,
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

  public List<NounTransition> similarTransitions(
      final List<NounTransition> transitions) {

    List<NounTransition> similars;
    similars = new ArrayList<NounTransition>();

    for(NounTransition transition : transitions) {
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
