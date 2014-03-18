package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.NominalVerbTransition;
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
  public void testPossibleStates() {
    Assert.assertEquals(NominalVerbState.A.possibleStates("satıyorsunuz"),
                        EnumSet.of(NominalVerbState.B, NominalVerbState.D));
  }

  @Test
  public void testAddTransactions() {
    List<NominalVerbTransition> transitions = new ArrayList<NominalVerbTransition>();

    NominalVerbState.A.addTransitions("satıyorsunuz", transitions, null, false);

    Assert.assertEquals(transitions.size(), 3);

    NominalVerbTransition transition = transitions.get(0);

    Assert.assertEquals(transition.startState, NominalVerbState.A);
    Assert.assertEquals(transition.nextState, NominalVerbState.B);
    Assert.assertEquals(transition.suffix, NominalVerbSuffix.S4);
    Assert.assertNull(transition.rollbackWord);

    transition = transitions.get(1);
    Assert.assertEquals(transition.startState, NominalVerbState.A);
    Assert.assertEquals(transition.nextState, NominalVerbState.D);
    Assert.assertEquals(transition.suffix, NominalVerbSuffix.S9);
    Assert.assertNull(transition.rollbackWord);

    transition = transitions.get(2);
    Assert.assertEquals(transition.startState, NominalVerbState.A);
    Assert.assertEquals(transition.nextState, NominalVerbState.B);
    Assert.assertEquals(transition.suffix, NominalVerbSuffix.S3);
    Assert.assertNull(transition.rollbackWord);
  }
}
