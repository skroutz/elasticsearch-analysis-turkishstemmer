package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NounSuffix;
import org.elasticsearch.index.analysis.stemmer.turkish.transitions.NounTransition;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NounStateTest {

  @Test
  public void testNextState() {
    Assert.assertEquals(NounState.A.nextState(NounSuffix.S1),
        NounState.L);
    Assert.assertEquals(NounState.K.nextState(NounSuffix.S1),
        null);
  }

  @Test
  public void testSuffixes() {
    Assert.assertTrue(NounState.A.suffixes().contains(NounSuffix.S1));
    Assert.assertFalse(NounState.B.suffixes().contains(NounSuffix.S11));
  }

  @Test
  public void testGetInitialState() {
    Assert.assertEquals(NounState.getInitialState(), NounState.A);
  }

  @Test
  public void testPossibleStates() {
    Assert.assertEquals(NounState.A.possibleStates("bebekler"),
                        EnumSet.of(NounState.L));
  }

  @Test
  public void testAddTransactions() {
    List<NounTransition> transitions = new ArrayList<NounTransition>();

    NounState.A.addTransitions("bebekler", transitions, null, false);

    Assert.assertEquals(transitions.size(), 1);

    NounTransition transition = transitions.get(0);

    Assert.assertEquals(transition.startState, NounState.A);
    Assert.assertEquals(transition.nextState, NounState.L);
    Assert.assertEquals(transition.suffix, NounSuffix.S1);
    // This is final state show the input word is set as rollback word
    Assert.assertEquals(transition.rollbackWord, "bebekler");
  }

}
