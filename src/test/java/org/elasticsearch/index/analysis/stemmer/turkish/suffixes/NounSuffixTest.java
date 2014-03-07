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
}
