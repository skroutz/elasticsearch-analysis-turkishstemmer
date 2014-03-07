package org.elasticsearch.index.analysis.stemmer.turkish.suffixes;

import org.elasticsearch.index.analysis.stemmer.turkish.suffixes.NominalVerbSuffix;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NominalVerbSuffixTest {

  @Test
  public void testMatchSuffix() {
    Assert.assertEquals(NominalVerbSuffix.S4.match("satıyorsunuz"), true);
  }
}
