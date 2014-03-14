package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.DerivationalSuffix;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DerivationalSuffixTest {

  @Test
  public void testMatchSuffix() {
    Assert.assertEquals(DerivationalSuffix.S1.match("gozlu"), true);
  }

  @Test
  public void testOptionalLetter() {
    Assert.assertEquals(DerivationalSuffix.S1.optionalLetter("gozlu"), '\0');
  }

  @Test
  public void testCheckHarmony() {
    Assert.assertTrue(DerivationalSuffix.S1.checkHarmony());
  }

  @Test
  public void testRemoveSuffix() {
    Assert.assertEquals(DerivationalSuffix.S1.removeSuffix("gozlu"), "goz");
  }

}
