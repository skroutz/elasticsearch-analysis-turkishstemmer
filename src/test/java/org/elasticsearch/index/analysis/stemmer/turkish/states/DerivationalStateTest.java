package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.Transition;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DerivationalStateTest {

  @Test
  public void testNextState() {
    Assert.assertEquals(DerivationalState.A.nextState(DerivationalSuffix.S1),
        DerivationalState.B);
    Assert.assertEquals(DerivationalState.B.nextState(DerivationalSuffix.S1),
        null);
  }

  @Test
  public void testSuffixes() {
    Assert.assertTrue(DerivationalState.A.suffixes().contains(DerivationalSuffix.S1));
    Assert.assertFalse(DerivationalState.B.suffixes().contains(DerivationalSuffix.S1));
  }

  @Test
  public void testGetInitialState() {
    Assert.assertEquals(DerivationalState.getInitialState(), DerivationalState.A);
  }

  @Test
  public void testAddTransactions() {
    List<Transition> transitions = new ArrayList<Transition>();

    DerivationalState.A.addTransitions("gozlu", transitions, false);

    Assert.assertEquals(transitions.size(), 1);

    Transition transition = transitions.get(0);

    Assert.assertEquals(transition.startState, DerivationalState.A);
    Assert.assertEquals(transition.nextState, DerivationalState.B);
    Assert.assertEquals(transition.suffix, DerivationalSuffix.S1);
  }

}
