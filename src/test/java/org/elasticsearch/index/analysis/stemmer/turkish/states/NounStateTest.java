package org.elasticsearch.index.analysis.stemmer.turkish.states;

import java.util.EnumSet;
import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NounSuffix;
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

}
