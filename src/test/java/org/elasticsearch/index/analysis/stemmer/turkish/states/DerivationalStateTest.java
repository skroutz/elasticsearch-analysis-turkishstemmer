package org.elasticsearch.index.analysis.stemmer.turkish.states;

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
}
