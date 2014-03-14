package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NounSuffix;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NounSuffixTest {

  @Test
  public void testMatchSuffix() {
    Assert.assertEquals(NounSuffix.S1.match("bebekler"), true);
    Assert.assertEquals(NounSuffix.S2.match("bulurum"), true);
  }

  @Test
  public void testOptionalLetter() {
    Assert.assertEquals(NounSuffix.S17.optionalLetter("buluruy"), 'y');
    Assert.assertEquals(NounSuffix.S1.optionalLetter("bulurum"), '\0');
  }

  @Test
  public void testCheckHarmony() {
    Assert.assertTrue(NounSuffix.S1.checkHarmony());
    Assert.assertFalse(NounSuffix.S18.checkHarmony());
  }

  @Test
  public void testRemoveSuffix() {
    Assert.assertEquals(NounSuffix.S1.removeSuffix("bebekler"), "bebek");
    Assert.assertEquals(NounSuffix.S2.removeSuffix("bulurum"), "buluru");
  }

}
