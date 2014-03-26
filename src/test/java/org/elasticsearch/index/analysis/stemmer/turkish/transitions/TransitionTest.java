package org.elasticsearch.index.analysis.stemmer.turkish.transitions;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.analysis.stemmer.turkish.states.DerivationalState;
import org.elasticsearch.index.analysis.stemmer.turkish.states.NominalVerbState;
import org.elasticsearch.index.analysis.stemmer.turkish.states.NounState;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NounSuffix;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TransitionTest {

  private Transition transition;

  @DataProvider(name = "nominalVerbTransitions")
  public Object[][] nominalVerbTransitionsProvider() {
    Transition similarTransition = new Transition(NominalVerbState.A,
                                                  NominalVerbState.B,
                                                  "someWord",
                                                  NominalVerbSuffix.S2,
                                                  false);

    Transition differentTransition = new Transition(NominalVerbState.C,
                                                    NominalVerbState.D,
                                                    "someWord",
                                                    NominalVerbSuffix.S2,
                                                    false);

    List<Transition> initialTransitions = new ArrayList<Transition>();

    initialTransitions.add(similarTransition);
    initialTransitions.add(differentTransition);

    List<Transition> similarTransitions = new ArrayList<Transition>();

    similarTransitions.add(similarTransition);

    return new Object[][] { { initialTransitions, similarTransitions } };

  }

  @Test(dataProvider = "nominalVerbTransitions")
  public void similarNominalVerbTransitions(List<Transition> transitions,
                                            List<Transition> similarTransitions) {

    transition = new Transition(NominalVerbState.A,
                                NominalVerbState.B,
                                "aword",
                                NominalVerbSuffix.S1,
                                false);

    Assert.assertEquals(transition.similarTransitions(transitions),
                        similarTransitions);
  }

  @DataProvider(name = "nounTransitions")
  public Object[][] nounTransitionsProvider() {
    Transition similarTransition = new Transition(NounState.A,
                                                  NounState.B,
                                                  "someWord",
                                                  NounSuffix.S2,
                                                  false);

    Transition differentTransition = new Transition(NounState.C,
                                                    NounState.D,
                                                    "someWord",
                                                    NounSuffix.S2,
                                                    false);

    List<Transition> initialTransitions = new ArrayList<Transition>();

    initialTransitions.add(similarTransition);
    initialTransitions.add(differentTransition);

    List<Transition> similarTransitions = new ArrayList<Transition>();

    similarTransitions.add(similarTransition);

    return new Object[][] { { initialTransitions, similarTransitions } };

  }

  @Test(dataProvider = "nounTransitions")
  public void similarNounTransitions(List<Transition> transitions,
                                     List<Transition> similarTransitions) {

    transition = new Transition(NounState.A,
                                NounState.B,
                                "aword",
                                NounSuffix.S1,
                                false);

    Assert.assertEquals(transition.similarTransitions(transitions),
        similarTransitions);
  }

  @DataProvider(name = "derivationalTransitions")
  public Object[][] derivationalTransitionsProvider() {
    Transition similarTransition = new Transition(DerivationalState.A,
                                                  DerivationalState.B,
                                                  "someWord",
                                                  DerivationalSuffix.S1,
                                                  false);

    Transition differentTransition = new Transition(DerivationalState.A,
                                                    null,
                                                    "someWord",
                                                    DerivationalSuffix.S1,
                                                    false);

    List<Transition> initialTransitions = new ArrayList<Transition>();

    initialTransitions.add(similarTransition);
    initialTransitions.add(differentTransition);

    List<Transition> similarTransitions = new ArrayList<Transition>();

    similarTransitions.add(similarTransition);

    return new Object[][] { { initialTransitions, similarTransitions } };

  }

  @Test(dataProvider = "derivationalTransitions")
  public void similarDerivationalTransitions(List<Transition> transitions,
      List<Transition> similarTransitions) {

    transition = new Transition(DerivationalState.A,
                                DerivationalState.B,
                                "aword",
                                DerivationalSuffix.S1,
                                false);

    Assert.assertEquals(transition.similarTransitions(transitions),
        similarTransitions);
  }
}
