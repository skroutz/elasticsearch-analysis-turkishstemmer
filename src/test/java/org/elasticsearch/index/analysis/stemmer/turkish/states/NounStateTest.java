package org.elasticsearch.index.analysis.stemmer.turkish.states;

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
}
