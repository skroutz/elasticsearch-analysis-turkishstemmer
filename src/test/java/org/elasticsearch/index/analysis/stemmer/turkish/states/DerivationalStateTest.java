package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.EnumSet;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;
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
  public void testPossibleStates() {
    Assert.assertEquals(DerivationalState.A.possibleStates("gozlu"),
                        EnumSet.of(DerivationalState.B));
    Assert.assertEquals(DerivationalState.B.possibleStates("telefonu"),
                        EnumSet.noneOf(DerivationalState.class));
  }

}
