package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DerivationalSuffixTest {

  @Test
  public void testMatchSuffix() {
    Assert.assertEquals(DerivationalSuffix.S1.match("gozlu"), true);
  }
}
