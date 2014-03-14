package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.EnumSet;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;
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

}
