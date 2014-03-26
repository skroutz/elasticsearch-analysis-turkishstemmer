package org.elasticsearch.index.analysis.stemmer.turkish.transitions;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.analysis.stemmer.turkish.states.State;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.Suffix;

public class Transition {
  public State   startState;
  public State   nextState;
  public String  word;
  public Suffix  suffix;
  public boolean marked;

  public Transition(final State startState,
                    final State nextState,
                    final String word,
                    final Suffix suffix,
                    final boolean marked) {

    this.startState = startState;
    this.nextState = nextState;
    this.word = word;
    this.suffix = suffix;
    this.marked = false;
  }

  public List<Transition> similarTransitions(final List<Transition> transitions) {

    List<Transition> similars;
    similars = new ArrayList<Transition>();

    for(Transition transition : transitions) {
      if(this.startState == transition.startState
          && this.nextState == transition.nextState)
        similars.add(transition);
    }

    return similars;
  }

  @Override
  public String toString() {
    return String.format("%s(%s) -> %s", this.startState,
        this.suffix, this.nextState);
  }
}
