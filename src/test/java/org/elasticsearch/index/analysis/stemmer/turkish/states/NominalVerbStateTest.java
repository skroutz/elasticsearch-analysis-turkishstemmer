package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.Transition;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NominalVerbStateTest {

  @Test
  public void testNextState() {
    Assert.assertEquals(NominalVerbState.A.nextState(NominalVerbSuffix.S1),
        NominalVerbState.B);
    Assert.assertEquals(NominalVerbState.F.nextState(NominalVerbSuffix.S1),
        null);
  }

  @Test
  public void testSuffixes() {
    Assert.assertTrue(NominalVerbState.A.suffixes().contains(NominalVerbSuffix.S1));
    Assert.assertFalse(NominalVerbState.B.suffixes().contains(NominalVerbSuffix.S11));
  }

  @Test
  public void testGetInitialState() {
    Assert.assertEquals(NominalVerbState.getInitialState(), NominalVerbState.A);
  }

  @Test
  public void testAddTransactions() {
    List<Transition> transitions = new ArrayList<Transition>();

    NominalVerbState.A.addTransitions("satıyorsunuz", transitions, false);

    Assert.assertEquals(transitions.size(), 3);

    Transition transition = transitions.get(0);

    Assert.assertEquals(transition.startState, NominalVerbState.A);
    Assert.assertEquals(transition.nextState, NominalVerbState.B);
    Assert.assertEquals(transition.suffix, NominalVerbSuffix.S4);

    transition = transitions.get(1);
    Assert.assertEquals(transition.startState, NominalVerbState.A);
    Assert.assertEquals(transition.nextState, NominalVerbState.D);
    Assert.assertEquals(transition.suffix, NominalVerbSuffix.S9);

    transition = transitions.get(2);
    Assert.assertEquals(transition.startState, NominalVerbState.A);
    Assert.assertEquals(transition.nextState, NominalVerbState.B);
    Assert.assertEquals(transition.suffix, NominalVerbSuffix.S3);
  }
}
