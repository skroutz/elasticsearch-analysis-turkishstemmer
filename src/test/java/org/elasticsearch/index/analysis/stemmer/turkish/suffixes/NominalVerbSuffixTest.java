package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NominalVerbSuffixTest {

  @Test
  public void testMatchSuffix() {
    Assert.assertEquals(NominalVerbSuffix.S4.match("satıyorsunuz"), true);
  }

  @Test
  public void testOptionalLetter() {
    Assert.assertEquals(NominalVerbSuffix.S1.optionalLetter("satıy"), 'y');
    Assert.assertEquals(NominalVerbSuffix.S1.optionalLetter("satıyor"), '\0');
    Assert.assertEquals(NominalVerbSuffix.S6.optionalLetter("satıyor"), '\0');
  }

  @Test
  public void testCheckHarmony() {
    Assert.assertTrue(NominalVerbSuffix.S1.checkHarmony());
  }

  @Test
  public void testRemoveSuffix() {
    Assert.assertEquals(NominalVerbSuffix.S4.removeSuffix("satıyorsunuz"), "satıyor");
  }

}
